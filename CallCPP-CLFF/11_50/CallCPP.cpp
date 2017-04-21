/*
 * CallCPP.cpp
 *
 *  Created on: 2012-12-19
 *      Author: ChengXin
 */
#include <iostream>
#include <fstream>
#include <cstring>
#include <cmath>
#include <limits>
#include <stdlib.h>
#include <iomanip>
#include "../51_500/header/nr.h"
#include "cn_nju_seg_atg_callCPP_CallCPP.h"
//#include "adpcm.h"
//#include "crc.h"
using namespace std;

char* jstringTostring(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

/**
   for icrc
**/
typedef unsigned char uchar;
#define LOBYTE(x) ((uchar)((x) & 0xFF))
#define HIBYTE(x) ((uchar)((x) >> 8))
unsigned char lin[256] = "asdffeagewaHAFEFaeDsFEawFdsFaefaeerdjgp";

/**
    for encode
**/

/* common sampling rate for sound cards on IBM/PC */
#define SAMPLE_RATE 11025

#define PI 3141
#define SIZE 3
#define IN_END 4
/* COMPLEX STRUCTURE */

typedef struct {
    int real, imag;
} COMPLEX;

/* function prototypes for fft and filter functions */
void fft(COMPLEX *,int);
int fir_filter(int input,int *coef,int n,int *history);
int iir_filter(int input,int *coef,int n,int *history);
int gaussian(void);
int my_abs(int n);

void setup_codec(int),key_down(),int_enable(),int_disable();
int flags(int);

int getinput(void);
void sendout(int),flush();

//int encode(int,int);
void decode(int);
int filtez(int *bpl,int *dlt);
void upzero(int dlt,int *dlti,int *bli);
int filtep(int rlt1,int al1,int rlt2,int al2);
int quantl(int el,int detl);
/* int invqxl(int il,int detl,int *code_table,int mode); */
int logscl(int il,int nbl);
int scalel(int nbl,int shift_constant);
int uppol2(int al1,int al2,int plt,int plt1,int plt2);
int uppol1(int al1,int apl2,int plt,int plt1);
/* int invqah(int ih,int deth); */
int logsch(int ih,int nbh);
void reset();
int my_fabs(int n);
int my_cos(int n);
int my_sin(int n);

/* G722 C code */


/* 为了控制reset（）次数   */
int resetindex=0 ;

/* variables for transimit quadrature mirror filter here */
int tqmf[24];

/* QMF filter coefficients:
scaled by a factor of 4 compared to G722 CCITT recommendation */
int h[24] = {
    12,   -44,   -44,   212,    48,  -624,   128,  1448,
  -840, -3220,  3804, 15504, 15504,  3804, -3220,  -840,
  1448,   128,  -624,    48,   212,   -44,   -44,    12
};

int xl,xh;

/* variables for receive quadrature mirror filter here */
int accumc[11],accumd[11];

/* outputs of decode() */
int xout1,xout2;
int xs,xd;

/* variables for encoder (hi and lo) here */

int il,szl,spl,sl,el;

int qq4_code4_table[16] = {
     0,  -20456,  -12896,   -8968,   -6288,   -4240,   -2584,   -1200,
 20456,   12896,    8968,    6288,    4240,    2584,    1200,       0
};

int qq5_code5_table[32] = {
  -280,    -280,  -23352,  -17560,  -14120,  -11664,   -9752,   -8184,
 -6864,   -5712,   -4696,   -3784,   -2960,   -2208,   -1520,    -880,
 23352,   17560,   14120,   11664,    9752,    8184,    6864,    5712,
  4696,    3784,    2960,    2208,    1520,     880,     280,    -280
};
int qq6_code6_table[64] = {
  -136,    -136,    -136,    -136,  -24808,  -21904,  -19008,  -16704,
-14984,  -13512,  -12280,  -11192,  -10232,   -9360,   -8576,   -7856,
 -7192,   -6576,   -6000,   -5456,   -4944,   -4464,   -4008,   -3576,
 -3168,   -2776,   -2400,   -2032,   -1688,   -1360,   -1040,    -728,
 24808,   21904,   19008,   16704,   14984,   13512,   12280,   11192,
 10232,    9360,    8576,    7856,    7192,    6576,    6000,    5456,
  4944,    4464,    4008,    3576,    3168,    2776,    2400,    2032,
  1688,    1360,    1040,     728,     432,     136,    -432,    -136
};

int delay_bpl[6];

int delay_dltx[6];

int wl_code_table[16] = {
   -60,  3042,  1198,   538,   334,   172,    58,   -30,
  3042,  1198,   538,   334,   172,    58,   -30,   -60
};

int wl_table[8] = {
   -60,   -30,    58,   172,   334,   538,  1198,  3042
};

int ilb_table[32] = {
  2048,  2093,  2139,  2186,  2233,  2282,  2332,  2383,
  2435,  2489,  2543,  2599,  2656,  2714,  2774,  2834,
  2896,  2960,  3025,  3091,  3158,  3228,  3298,  3371,
  3444,  3520,  3597,  3676,  3756,  3838,  3922,  4008
};

int         nbl;                  /* delay line */
int         al1,al2;
int         plt,plt1,plt2;
int         rs;
int         dlt;
int         rlt,rlt1,rlt2;

/* decision levels - pre-multiplied by 8, 0 to indicate end */
int decis_levl[30] = {
   280,   576,   880,  1200,  1520,  1864,  2208,  2584,
  2960,  3376,  3784,  4240,  4696,  5200,  5712,  6288,
  6864,  7520,  8184,  8968,  9752, 10712, 11664, 12896,
 14120, 15840, 17560, 20456, 23352, 32767
};

int         detl;

/* quantization table 31 long to make quantl look-up easier,
last entry is for mil=30 case when wd is max */
int quant26bt_pos[31] = {
    61,    60,    59,    58,    57,    56,    55,    54,
    53,    52,    51,    50,    49,    48,    47,    46,
    45,    44,    43,    42,    41,    40,    39,    38,
    37,    36,    35,    34,    33,    32,    32
};

/* quantization table 31 long to make quantl look-up easier,
last entry is for mil=30 case when wd is max */
int quant26bt_neg[31] = {
    63,    62,    31,    30,    29,    28,    27,    26,
    25,    24,    23,    22,    21,    20,    19,    18,
    17,    16,    15,    14,    13,    12,    11,    10,
     9,     8,     7,     6,     5,     4,     4
};


int         deth;
int         sh;         /* this comes from adaptive predictor */
int         eh;

int qq2_code2_table[4] = {
  -7408,   -1616,   7408,  1616
};

int wh_code_table[4] = {
   798,   -214,    798,   -214
};


int         dh,ih;
int         nbh,szh;
int         sph,ph,yh,rh;

int         delay_dhx[6];

int         delay_bph[6];

int         ah1,ah2;
int         ph1,ph2;
int         rh1,rh2;

/* variables for decoder here */
int         ilr,yl,rl;
int         dec_deth,dec_detl,dec_dlt;

int         dec_del_bpl[6];

int         dec_del_dltx[6];

int     dec_plt,dec_plt1,dec_plt2;
int     dec_szl,dec_spl,dec_sl;
int     dec_rlt1,dec_rlt2,dec_rlt;
int     dec_al1,dec_al2;
int     dl;
int     dec_nbl,dec_yh,dec_dh,dec_nbh;

/* variables used in filtez */
int         dec_del_bph[6];

int         dec_del_dhx[6];

int         dec_szh;
/* variables used in filtep */
int         dec_rh1,dec_rh2;
int         dec_ah1,dec_ah2;
int         dec_ph,dec_sph;

int     dec_sh,dec_rh;

int     dec_ph1,dec_ph2;

/* G722 encode function two ints in, one 8 bit output */

/* put input samples in xin1 = first value, xin2 = second value */
/* returns il and ih stored together */

/* MAX: 1 */
int my_abs(int n)
{
  int m;

  if (n >= 0) m = n;
  else m = -n;
  return m;
}

/* MAX: 1 */
int my_fabs(int n)
{
  int f;

  if (n >= 0) f = n;
  else f = -n;
  return f;
}

int my_sin(int rad)
{
  int diff;
  int app=0;

  int inc = 1;

  /* MAX dependent on rad's value, say 50 */
  while (rad > 2*PI)
      rad -= 2*PI;
  /* MAX dependent on rad's value, say 50 */
  while (rad < -2*PI)
      rad += 2*PI;
   diff = rad;
   app = diff;
  diff = (diff * (-(rad*rad))) /
     ((2 * inc) * (2 * inc + 1));
  app = app + diff;
  inc++;
  /* REALLY: while(my_fabs(diff) >= my_abs0.00001) { */
  /* MAX: 1000 */
  while(my_fabs(diff) >= 1) {
    diff = (diff * (-(rad*rad))) /
	((2 * inc) * (2 * inc + 1));
    app = app + diff;
    inc++;
  }

  return app;
}


int my_cos(int rad)
{
  return (my_sin (PI / 2 - rad));
}


/* MAX: 1 */


/* decode function, result in xout1 and xout2 */

void decode(int input)
{
    int i;
    long int xa1,xa2;    /* qmf accumulators */
    int *h_ptr,*ac_ptr,*ac_ptr1,*ad_ptr,*ad_ptr1;

/* split transmitted word from input into ilr and ih */
    ilr = input & 0x3f;
    ih = input >> 6;

/* LOWER SUB_BAND DECODER */

/* filtez: compute predictor output for zero section */
    dec_szl = filtez(dec_del_bpl,dec_del_dltx);

/* filtep: compute predictor output signal for pole section */
    dec_spl = filtep(dec_rlt1,dec_al1,dec_rlt2,dec_al2);

    dec_sl = dec_spl + dec_szl;

/* invqxl: compute quantized difference signal for adaptive predic */
    dec_dlt = ((long)dec_detl*qq4_code4_table[ilr >> 2]) >> 15;

/* invqxl: compute quantized difference signal for decoder output */
    dl = ((long)dec_detl*qq6_code6_table[il]) >> 15;

    rl = dl + dec_sl;

/* logscl: quantizer scale factor adaptation in the lower sub-band */
    dec_nbl = logscl(ilr,dec_nbl);

/* scalel: computes quantizer scale factor in the lower sub band */
    dec_detl = scalel(dec_nbl,8);

/* parrec - add pole predictor output to quantized diff. signal */
/* for partially reconstructed signal */
    dec_plt = dec_dlt + dec_szl;

/* upzero: update zero section predictor coefficients */
    upzero(dec_dlt,dec_del_dltx,dec_del_bpl);

/* uppol2: update second predictor coefficient apl2 and delay it as al2 */
    dec_al2 = uppol2(dec_al1,dec_al2,dec_plt,dec_plt1,dec_plt2);

/* uppol1: update first predictor coef. (pole setion) */
    dec_al1 = uppol1(dec_al1,dec_al2,dec_plt,dec_plt1);

/* recons : compute recontructed signal for adaptive predictor */
    dec_rlt = dec_sl + dec_dlt;

/* done with lower sub band decoder, implement delays for next time */
    dec_rlt2 = dec_rlt1;
    dec_rlt1 = dec_rlt;
    dec_plt2 = dec_plt1;
    dec_plt1 = dec_plt;

/* HIGH SUB-BAND DECODER */

/* filtez: compute predictor output for zero section */
    dec_szh = filtez(dec_del_bph,dec_del_dhx);

/* filtep: compute predictor output signal for pole section */
    dec_sph = filtep(dec_rh1,dec_ah1,dec_rh2,dec_ah2);

/* predic:compute the predictor output value in the higher sub_band decoder */
    dec_sh = dec_sph + dec_szh;

/* invqah: in-place compute the quantized difference signal */
    dec_dh = ((long)dec_deth*qq2_code2_table[ih]) >> 15L ;

/* logsch: update logarithmic quantizer scale factor in hi sub band */
    dec_nbh = logsch(ih,dec_nbh);

/* scalel: compute the quantizer scale factor in the higher sub band */
    dec_deth = scalel(dec_nbh,10);

/* parrec: compute partially recontructed signal */
    dec_ph = dec_dh + dec_szh;

/* upzero: update zero section predictor coefficients */
    upzero(dec_dh,dec_del_dhx,dec_del_bph);

/* uppol2: update second predictor coefficient aph2 and delay it as ah2 */
    dec_ah2 = uppol2(dec_ah1,dec_ah2,dec_ph,dec_ph1,dec_ph2);

/* uppol1: update first predictor coef. (pole setion) */
    dec_ah1 = uppol1(dec_ah1,dec_ah2,dec_ph,dec_ph1);

/* recons : compute recontructed signal for adaptive predictor */
    rh = dec_sh + dec_dh;

/* done with high band decode, implementing delays for next time here */
    dec_rh2 = dec_rh1;
    dec_rh1 = rh;
    dec_ph2 = dec_ph1;
    dec_ph1 = dec_ph;

/* end of higher sub_band decoder */

/* end with receive quadrature mirror filters */
    xd = rl - rh;
    xs = rl + rh;

/* receive quadrature mirror filters implemented here */
    h_ptr = h;
    ac_ptr = accumc;
    ad_ptr = accumd;
    xa1 = (long)xd * (*h_ptr++);
    xa2 = (long)xs * (*h_ptr++);
/* main multiply accumulate loop for samples and coefficients */
    for(i = 0 ; i < 10 ; i++) {
        xa1 += (long)(*ac_ptr++) * (*h_ptr++);
        xa2 += (long)(*ad_ptr++) * (*h_ptr++);
    }
/* final mult/accumulate */
    xa1 += (long)(*ac_ptr) * (*h_ptr++);
    xa2 += (long)(*ad_ptr) * (*h_ptr++);

/* scale by 2^14 */
    xout1 = xa1 >> 14;
    xout2 = xa2 >> 14;

/* update delay lines */
    ac_ptr1 = ac_ptr - 1;
    ad_ptr1 = ad_ptr - 1;
    for(i = 0 ; i < 10 ; i++) {
        *ac_ptr-- = *ac_ptr1--;
        *ad_ptr-- = *ad_ptr1--;
    }
    *ac_ptr = xd;
    *ad_ptr = xs;

    return;
}

/* clear all storage locations */

void reset()
{
    int i;
    resetindex =0;
    detl = dec_detl = 32;   /* reset to min scale factor */
    deth = dec_deth = 8;
    nbl = al1 = al2 = plt1 = plt2 = rlt1 = rlt2 = 0;
    nbh = ah1 = ah2 = ph1 = ph2 = rh1 = rh2 = 0;
    dec_nbl = dec_al1 = dec_al2 = dec_plt1 = dec_plt2 = dec_rlt1 = dec_rlt2 = 0;
    dec_nbh = dec_ah1 = dec_ah2 = dec_ph1 = dec_ph2 = dec_rh1 = dec_rh2 = 0;

    for(i = 0 ; i < 6 ; i++) {
        delay_dltx[i] = 0;
        delay_dhx[i] = 0;
        dec_del_dltx[i] = 0;
        dec_del_dhx[i] = 0;
    }

    for(i = 0 ; i < 6 ; i++) {
        delay_bpl[i] = 0;
        delay_bph[i] = 0;
        dec_del_bpl[i] = 0;
        dec_del_bph[i] = 0;
    }

    for(i = 0 ; i < 23 ; i++) tqmf[i] = 0;

    for(i = 0 ; i < 11 ; i++) {
        accumc[i] = 0;
        accumd[i] = 0;
    }
    return;
}

/* filtez - compute predictor output signal (zero section) */
/* input: bpl1-6 and dlt1-6, output: szl */

int filtez(int *bpl,int *dlt)
{
    int i;
    long int zl;
    zl = (long)(*bpl++) * (*dlt++);
    /* MAX: 6 */
    for(i = 1 ; i < 6 ; i++)
        zl += (long)(*bpl++) * (*dlt++);

    return((int)(zl >> 14));   /* x2 here */
}

/* filtep - compute predictor output signal (pole section) */
/* input rlt1-2 and al1-2, output spl */

int filtep(int rlt1,int al1,int rlt2,int al2)
{
    long int pl,pl2;
    pl = 2*rlt1;
    pl = (long)al1*pl;
    pl2 = 2*rlt2;
    pl += (long)al2*pl2;
    return((int)(pl >> 15));
}

/* quantl - quantize the difference signal in the lower sub-band */
int quantl(int el,int detl)
{
    int ril,mil;
    long int wd,decis;

/* abs of difference signal */
    wd = my_abs(el);
/* determine mil based on decision levels and detl gain */
    /* MAX: 30 */
    for(mil = 0 ; mil < 30 ; mil++) {
        decis = (decis_levl[mil]*(long)detl) >> 15L;
        if(wd <= decis) break;
    }
/* if mil=30 then wd is less than all decision levels */
    if(el >= 0) ril = quant26bt_pos[mil];
    else ril = quant26bt_neg[mil];
    return(ril);
}

/* invqxl is either invqbl or invqal depending on parameters passed */
/* returns dlt, code table is pre-multiplied by 8 */

/*    int invqxl(int il,int detl,int *code_table,int mode) */
/*    { */
/*        long int dlt; */
/*       dlt = (long)detl*code_table[il >> (mode-1)]; */
/*        return((int)(dlt >> 15)); */
/*    } */

/* logscl - update log quantizer scale factor in lower sub-band */
/* note that nbl is passed and returned */

int logscl(int il,int nbl)
{
    long int wd;
    wd = ((long)nbl * 127L) >> 7L;   /* leak factor 127/128 */
    nbl = (int)wd + wl_code_table[il >> 2];
    if(nbl < 0) nbl = 0;
    if(nbl > 18432) nbl = 18432;
    return(nbl);
}

/* scalel: compute quantizer scale factor in lower or upper sub-band*/

int scalel(int nbl,int shift_constant)
{
    int wd1,wd2,wd3;
    wd1 = (nbl >> 6) & 31;
    wd2 = nbl >> 11;
    wd3 = ilb_table[wd1] >> (shift_constant + 1 - wd2);
    return(wd3 << 3);
}

/* upzero - inputs: dlt, dlti[0-5], bli[0-5], outputs: updated bli[0-5] */
/* also implements delay of bli and update of dlti from dlt */

void upzero(int dlt,int *dlti,int *bli)
{
    int i,wd2,wd3;
/*if dlt is zero, then no sum into bli */
    if(dlt == 0) {
      for(i = 0 ; i < 6 ; i++) {
        bli[i] = (int)((255L*bli[i]) >> 8L); /* leak factor of 255/256 */
      }
    }
    else {
      for(i = 0 ; i < 6 ; i++) {
        if((long)dlt*dlti[i] >= 0) wd2 = 128; else wd2 = -128;
        wd3 = (int)((255L*bli[i]) >> 8L);    /* leak factor of 255/256 */
        bli[i] = wd2 + wd3;
      }
    }
/* implement delay line for dlt */
    dlti[5] = dlti[4];
    dlti[4] = dlti[3];
    dlti[3] = dlti[2];
    dlti[1] = dlti[0];
    dlti[0] = dlt;
    return;
}

/* uppol2 - update second predictor coefficient (pole section) */
/* inputs: al1, al2, plt, plt1, plt2. outputs: apl2 */

int uppol2(int al1,int al2,int plt,int plt1,int plt2)
{
    long int wd2,wd4;
    int apl2;
    wd2 = 4L*(long)al1;
    if((long)plt*plt1 >= 0L) wd2 = -wd2;    /* check same sign */
    wd2 = wd2 >> 7;                  /* gain of 1/128 */
    if((long)plt*plt2 >= 0L) {
        wd4 = wd2 + 128;             /* same sign case */
    }
    else {
        wd4 = wd2 - 128;
    }
    apl2 = wd4 + (127L*(long)al2 >> 7L);  /* leak factor of 127/128 */

/* apl2 is limited to +-.75 */
    if(apl2 > 12288) apl2 = 12288;
    if(apl2 < -12288) apl2 = -12288;
    return(apl2);
}

/* uppol1 - update first predictor coefficient (pole section) */
/* inputs: al1, apl2, plt, plt1. outputs: apl1 */

int uppol1(int al1,int apl2,int plt,int plt1)
{
    long int wd2;
    int wd3,apl1;
    wd2 = ((long)al1*255L) >> 8L;   /* leak factor of 255/256 */
    if((long)plt*plt1 >= 0L) {
        apl1 = (int)wd2 + 192;      /* same sign case */
    }
    else {
        apl1 = (int)wd2 - 192;
    }
/* note: wd3= .9375-.75 is always positive */
    wd3 = 15360 - apl2;             /* limit value */
    if(apl1 > wd3) apl1 = wd3;
    if(apl1 < -wd3) apl1 = -wd3;
    return(apl1);
}

/* INVQAH: inverse adaptive quantizer for the higher sub-band */
/* returns dh, code table is pre-multiplied by 8 */

/*  int invqah(int ih,int deth) */
/*  { */
/*        long int rdh; */
/*        rdh = ((long)deth*qq2_code2_table[ih]) >> 15L ; */
/*        return((int)(rdh )); */
/*  } */

/* logsch - update log quantizer scale factor in higher sub-band */
/* note that nbh is passed and returned */

int logsch(int ih,int nbh)
{
    int wd;
    wd = ((long)nbh * 127L) >> 7L;       /* leak factor 127/128 */
    nbh = wd + wh_code_table[ih];
    if(nbh < 0) nbh = 0;
    if(nbh > 22528) nbh = 22528;
    return(nbh);
}


#ifndef Seoul_Mate

#endif

/*供encode计算节点能源函数值时用
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callGetEh
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callGetEh
  (JNIEnv *, jobject, jint xin1, jint xin2)
{
    
    int i;
    int *h_ptr,*tqmf_ptr,*tqmf_ptr1;
    long int xa,xb;
    int decis;

   /* if(resetindex == 0)  */{reset();resetindex++;}
/* transmit quadrature mirror filters implemented here */
    h_ptr = h;
    tqmf_ptr = tqmf;
    xa = (long)(*tqmf_ptr++) * (*h_ptr++);
    xb = (long)(*tqmf_ptr++) * (*h_ptr++);
/* main multiply accumulate loop for samples and coefficients */
    /* MAX: 10 */
    for(i = 0 ; i < 10 ; i++) {
        xa += (long)(*tqmf_ptr++) * (*h_ptr++);
        xb += (long)(*tqmf_ptr++) * (*h_ptr++);
    }
/* final mult/accumulate */
    xa += (long)(*tqmf_ptr++) * (*h_ptr++);
    xb += (long)(*tqmf_ptr) * (*h_ptr++);

/* update delay line tqmf */
    tqmf_ptr1 = tqmf_ptr - 2;
    /* MAX: 22 */
    for(i = 0 ; i < 22 ; i++) *tqmf_ptr-- = *tqmf_ptr1--;
    *tqmf_ptr-- = xin1;
    *tqmf_ptr = xin2;

/* scale outputs */
    xl = (xa + xb) >> 15;
    xh = (xa - xb) >> 15;

/* end of quadrature mirror filter code */

/* starting with lower sub band encoder */

/* filtez - compute predictor output section - zero section */
    szl = filtez(delay_bpl,delay_dltx);

/* filtep - compute predictor output signal (pole section) */
    spl = filtep(rlt1,al1,rlt2,al2);

/* compute the predictor output value in the lower sub_band encoder */
    sl = szl + spl;
    el = xl - sl;

/* quantl: quantize the difference signal */
    il = quantl(el,detl);

/* invqxl: computes quantized difference signal */
/* for invqbl, truncate by 2 lsbs, so mode = 3 */
    dlt = ((long)detl*qq4_code4_table[il >> 2]) >> 15;

/* logscl: updates logarithmic quant. scale factor in low sub band */
    nbl = logscl(il,nbl);

/* scalel: compute the quantizer scale factor in the lower sub band */
/* calling parameters nbl and 8 (constant such that scalel can be scaleh) */
    detl = scalel(nbl,8);

/* parrec - simple addition to compute recontructed signal for adaptive pred */
    plt = dlt + szl;

/* upzero: update zero section predictor coefficients (sixth order)*/
/* calling parameters: dlt, dlt1, dlt2, ..., dlt6 from dlt */
/*  bpli (linear_buffer in which all six values are delayed */
/* return params:      updated bpli, delayed dltx */
    upzero(dlt,delay_dltx,delay_bpl);

/* uppol2- update second predictor coefficient apl2 and delay it as al2 */
/* calling parameters: al1, al2, plt, plt1, plt2 */
    al2 = uppol2(al1,al2,plt,plt1,plt2);

/* uppol1 :update first predictor coefficient apl1 and delay it as al1 */
/* calling parameters: al1, apl2, plt, plt1 */
    al1 = uppol1(al1,al2,plt,plt1);

/* recons : compute recontructed signal for adaptive predictor */
    rlt = sl + dlt;

/* done with lower sub_band encoder; now implement delays for next time*/
    rlt2 = rlt1;
    rlt1 = rlt;
    plt2 = plt1;
    plt1 = plt;

/* high band encode */

    szh = filtez(delay_bph,delay_dhx);

    sph = filtep(rh1,ah1,rh2,ah2);

/* predic: sh = sph + szh */
    sh = sph + szh;
/* subtra: eh = xh - sh */
    eh = xh - sh;

    return eh;
}



unsigned short icrc1(unsigned short crc, unsigned char onech)
{
	int i;
	unsigned short ans=(crc^onech << 8);

	for (i=0;i<8;i++) {
		if (ans & 0x8000)
			ans = (ans <<= 1) ^ 4129;
		else
			ans <<= 1;
	}
	return ans;
}


DP NR::gammln(const DP xx)
{
	int j;
	DP x,y,tmp,ser;
	static const DP cof[6]={76.18009172947146,-86.50532032941677,
		24.01409824083091,-1.231739572450155,0.1208650973866179e-2,
		-0.5395239384953e-5};

	y=x=xx;
	tmp=x+5.5;
	tmp -= (x+0.5)*log(tmp);
	ser=1.000000000190015;
	for (j=0;j<6;j++) ser += cof[j]/++y;
	return -tmp+log(2.5066282746310005*ser/x);
}

DP NR::bessi0(const DP x)
{
	DP ax,ans,y;

	if ((ax=fabs(x)) < 3.75) {
		y=x/3.75;
		y*=y;
		ans=1.0+y*(3.5156229+y*(3.0899424+y*(1.2067492
			+y*(0.2659732+y*(0.360768e-1+y*0.45813e-2)))));
	} else {
		y=3.75/ax;
		ans=(exp(ax)/sqrt(ax))*(0.39894228+y*(0.1328592e-1
			+y*(0.225319e-2+y*(-0.157565e-2+y*(0.916281e-2
			+y*(-0.2057706e-1+y*(0.2635537e-1+y*(-0.1647633e-1
			+y*0.392377e-2))))))));
	}
	return ans;
}

DP NR::bessj0(const DP x)
{
	DP ax,z,xx,y,ans,ans1,ans2;

	if ((ax=fabs(x)) < 8.0) {
		y=x*x;
		ans1=57568490574.0+y*(-13362590354.0+y*(651619640.7
			+y*(-11214424.18+y*(77392.33017+y*(-184.9052456)))));
		ans2=57568490411.0+y*(1029532985.0+y*(9494680.718
			+y*(59272.64853+y*(267.8532712+y*1.0))));
		ans=ans1/ans2;
	} else {
		z=8.0/ax;
		y=z*z;
		xx=ax-0.785398164;
		ans1=1.0+y*(-0.1098628627e-2+y*(0.2734510407e-4
			+y*(-0.2073370639e-5+y*0.2093887211e-6)));
		ans2 = -0.1562499995e-1+y*(0.1430488765e-3
			+y*(-0.6911147651e-5+y*(0.7621095161e-6
			-y*0.934945152e-7)));
		ans=sqrt(0.636619772/ax)*(cos(xx)*ans1-z*sin(xx)*ans2);
	}
	return ans;
}

DP NR::bessj1(const DP x)
{
	DP ax,z,xx,y,ans,ans1,ans2;

	if ((ax=fabs(x)) < 8.0) {
		y=x*x;
		ans1=x*(72362614232.0+y*(-7895059235.0+y*(242396853.1
			+y*(-2972611.439+y*(15704.48260+y*(-30.16036606))))));
		ans2=144725228442.0+y*(2300535178.0+y*(18583304.74
			+y*(99447.43394+y*(376.9991397+y*1.0))));
		ans=ans1/ans2;
	} else {
		z=8.0/ax;
		y=z*z;
		xx=ax-2.356194491;
		ans1=1.0+y*(0.183105e-2+y*(-0.3516396496e-4
			+y*(0.2457520174e-5+y*(-0.240337019e-6))));
		ans2=0.04687499995+y*(-0.2002690873e-3
			+y*(0.8449199096e-5+y*(-0.88228987e-6
			+y*0.105787412e-6)));
		ans=sqrt(0.636619772/ax)*(cos(xx)*ans1-z*sin(xx)*ans2);
		if (x < 0.0) ans = -ans;
	}
	return ans;
}

bool is_leap_year(int year)
{
	ofstream lFile("/home/cx/atg_data/IsLeapYear.dat");
	lFile<<"node1\n";
	bool result;
	if ((lFile<<"node2 "<<(year%4)-0<<" (year%4)!=0\n",(year%4) != 0))
	{
		lFile<<"node3\n";
		result = false;
	}
	else if ((lFile<<"node4 "<<(year%400)-0<<" (year%400)==0\n",(year%400) == 0))
	{
		lFile<<"node5\n";
		result = true;
	}
	else if ((lFile<<"node6 "<<(year%100)-0<<" (year%100)==0\n",(year%100) == 0))
	{
		lFile<<"node7\n";
		result = false;
	}
	else
	{
		lFile<<"node8\n";
		result = true;
	}
	lFile<<"node9\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callTriangle
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTriangle
  (JNIEnv *env, jobject arg, jint a, jint b, jint c)
{
	ofstream tFile("/home/zy/atg_data/Triangle.dat");
	tFile<<"node1\n";
	int Tri_kind=0;	//初始化为非三角形
	if (((tFile<<"node2 "<<a-0<<" a>0\n",a>0) && (tFile<<"node2 "<<b-0<<" b>0\n",b>0) && (tFile<<"node2 "<<c-0<<" c>0\n",c>0)))
	{
		if (((tFile<<"node3 "<<a+b-c<<" a+b>c\n",a+b>c) && (tFile<<"node3 "<<a+c-b<<" a+c>b\n",a+c>b) && (tFile<<"node3 "<<b+c-a<<" b+c>a\n",b+c>a)))
		{
			if (((tFile<<"node4 "<<a-b<<" a==b\n",a==b) || (tFile<<"node4 "<<b-c<<" b==c\n",b==c)))
			{
				tFile<<"node5\n";
				Tri_kind=1;	//等边三角形
			}
			else
			{
				if ((tFile<<"node6 "<<a*a+b*b-c*c<<" a*a+b*b==c*c\n",a*a+b*b==c*c) || (tFile<<"node6 "<<a*a+c*c-b*b<<" a*a+c*c==b*b\n",a*a+c*c==b*b) || (tFile<<"node6 "<<b*b+c*c-a*a<<" b*b+c*c==a*a\n",b*b+c*c==a*a))
				{
					tFile<<"node7\n";
					Tri_kind=2;	//直角三角形
				}
				else
				{
					tFile<<"node8\n";
					Tri_kind=3;	//不等边三角形
				}
			}
		}
	}
	tFile<<"node9\n";
	tFile.close();
	return Tri_kind;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callIsLeapYear
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callIsLeapYear
  (JNIEnv *env, jobject arg, jint year)
{
	ofstream lFile("/home/zy/atg_data/IsLeapYear.dat");
	lFile<<"node1\n";
	bool result;
	if ((lFile<<"node2 "<<(year%4)-0<<" (year%4)!=0\n",(year%4) != 0))
	{
		lFile<<"node3\n";
		result = false;
	}
	else if ((lFile<<"node4 "<<(year%400)-0<<" (year%400)==0\n",(year%400) == 0))
	{
		lFile<<"node5\n";
		result = true;
	}
	else if ((lFile<<"node6 "<<(year%100)-0<<" (year%100)==0\n",(year%100) == 0))
	{
		lFile<<"node7\n";
		result = false;
	}
	else
	{
		lFile<<"node8\n";
		result = true;
	}
	lFile<<"node9\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callIsValidDate
 * Signature: (III)Z
 */
JNIEXPORT jboolean JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callIsValidDate
  (JNIEnv *env, jobject arg, jint day, jint month, jint year)
{
	ofstream vFile("/home/zy/atg_data/IsValidDate.dat");
	vFile<<"node1\n";
	bool valid = true;
	int month_length[13] = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	if ((vFile<<"node2 "<<is_leap_year(year)-1<<" is_leap_year(year)==1\n",is_leap_year(year) == 1))
	{
		vFile<<"node3\n";
		month_length[2] = 29;
	}
	if (((vFile<<"node4 "<<month-1<<" month<1\n",month<1) || (vFile<<"node4 "<<month-12<<" month>12\n",month>12)))
	{
		vFile<<"node5\n";
		valid = false;
	}
	else if (((vFile<<"node6 "<<day-1<<" day<1\n",day<1) || (vFile<<"node6 "<<day-month_length[month]<<" day>month_length[month]\n",day>month_length[month])))
	{
		vFile<<"node7\n";
		valid = false;
	}
	vFile<<"node8\n";
	return valid;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callCheckX
 * Signature: (D)Z
 */
JNIEXPORT jboolean JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callCheckX
  (JNIEnv *env, jobject arg, jdouble x)
{
	ofstream xFile("/home/zy/atg_data/CheckX.dat");
	xFile<<"node1\n";
	bool isPositive = true;
	x = x*x - 4*x;
	x = lround(x)%360;
	x = sin(x*3.14/180);
	if (xFile<<"node2 "<<x-0<<" x>0\n",x > 0)
	{
		if (xFile<<"node3 "<<x-0.5<<" x>0.5\n",x>0.5)
		{
			xFile<<"node4\n";
			isPositive = true;
		}
		else
		{
			xFile<<"node5\n";
			isPositive = false;
		}
	}
	else
	{
		if (xFile<<"node6 "<<x-(-0.5)<<" x>-0.5\n",x>-0.5)
		{
			xFile<<"node7\n";
			isPositive = false;
		}
		else
		{
			xFile<<"node8\n";
			isPositive = true;
		}
	}
	xFile<<"node9\n";
	return isPositive;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callGetRootOfQuadraticF
 * Signature: (FFF)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callGetRootOfQuadraticF
  (JNIEnv *env, jobject arg, jfloat a, jfloat b, jfloat c)
{
	ofstream fFile("/home/zy/atg_data/getRootOfQuadraticF.dat");
	fFile<<"node1\n";
	float d, r1, r2;
	float real, imag, n;

	if ((fFile<<"node2 "<<a-0<<" a>0\n",a > 0) || (fFile<<"node2 "<<a-0<<" a<0\n",a < 0))
	{
		fFile<<"node3\n";
		d = b * b - 4 * a * c;
		if (fFile<<"node4 "<<d-0<<" d<0\n",d < 0)
		{
			fFile<<"node5\n";
			printf("\nRoots are imaginary\n");
			real = -b / (2 * a);
			d = -d;
			n = pow((double) d, (double) 0.5);
			imag = n / (2 * a);
			printf("\nr1 = %7.2f + j%7.2f", real, imag);
			printf("\nr2 = %7.2f - j%7.2f", real, imag);
		}
		else if (fFile<<"node6 "<<d-0<<" d==0\n",d == 0)
		{
			fFile<<"node7\n";
			printf("\nRoots are real and equal\n");
			r1 = -b / (2 * a);
			printf("\nr1 = r2 = %7.2f", r1);
		}
		else
		{
			fFile<<"node8\n";
			printf("\nRoots are real and unequal\n");
			r1 = (-b + sqrt((double) d)) / (2 * a);
			r2 = (-b - sqrt((double) d)) / (2 * a);
			printf("\nr1 = %7.2f", r1);
			printf("\nr2 = %7.2f", r2);
		}
	}
	else
	{
		fFile<<"node9\n";
		printf("\nEquation is linear");
	}

	fFile<<"node10\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callSum
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callSum
  (JNIEnv *env, jobject arg, jint n)
{
	ofstream sFile("/home/zy/atg_data/Sum.dat");
	sFile<<"node1\n";
	int sum = 0;
	int i=0;
	for (i=0; (sFile<<"node2 "<<i-n<<" i<n\n",i<n); i++)
	{
		sFile<<"node3\n";
		sum += i;
	}

	sFile<<"node4\n";
	return sum;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBessi
 * Signature: (ID)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBessi
  (JNIEnv *env, jobject arg, jint n, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

	bFile<<"node1\n";
	const DP ACC=200.0;
	const int IEXP=numeric_limits<DP>::max_exponent/2;
	int j,k;
	DP bi,bim,bip,dum,tox,ans;

	if (bFile<<"node2 "<<n-2<<" n<2\n",n < 2)
	{
		bFile<<"node3\n";
//		printf("Index n less than 2 in bessi");
	}
	if (bFile<<"node4 "<<x*x - 8.0*numeric_limits<DP>::min()<<" x*x<=8.0*numeric_limits<DP>::min()\n",x*x <= 8.0*numeric_limits<DP>::min())
	{
		bFile<<"node5\n";
		return 0.0;
	}
	else 
	{
		bFile<<"node6\n";
		tox=2.0/fabs(x);
		bip=ans=0.0;
		bi=1.0;
		for (j=2*(n+int(sqrt(ACC*n)));(bFile<<"node7 "<<j-0<<" j>0\n",j>0);j--) 
		{
			bFile<<"node8\n";
			bim=bip+j*tox*bi;
			bip=bi;
			bi=bim;
			dum=frexp(bi,&k);
			if (bFile<<"node9 "<<k - IEXP<<" k>IEXP\n",k > IEXP) 
			{
				bFile<<"node10\n";
				ans=ldexp(ans,-IEXP);
				bi=ldexp(bi,-IEXP);
				bip=ldexp(bip,-IEXP);
			}
			if (bFile<<"node11 "<<j - n<<" j==n\n",j == n)
			{
				bFile<<"node12\n";
				ans=bip;
			}
		}
		bFile<<"node13\n";
		ans *= NR::bessi0(x)/bi;
		bFile<<"node14\n";
		return x < 0.0 && (n & 1) ? -ans : ans;
	}
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBessj
 * Signature: (ID)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBessj
  (JNIEnv *env, jobject arg, jint n, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream jFile(path);
	jFile<<setiosflags(ios::scientific);
	jFile<<setprecision(16);
	jFile<<"node1\n";
	const DP ACC=160.0;
	const int IEXP=numeric_limits<DP>::max_exponent/2;
	int jsum;
	int j,k,m;
       
	DP ax,bj,bjm,bjp,dum,sum,tox,ans;

	if (jFile<<"node2 "<<n-2<<" n<2\n",n < 2)
	{
		jFile<<"node3\n";
//		printf("Index n less than 2 in bessj");
	}
	jFile<<"node4\n";
	ax=fabs(x);
	if (jFile<<"node5 "<<ax*ax - 8.0*numeric_limits<DP>::min()<<" ax*ax<=8.0*numeric_limits<DP>::min()\n",ax*ax <= 8.0*numeric_limits<DP>::min())
	{
		jFile<<"node6\n";
		return 0.0;
	}
	else if (jFile<<"node7 "<<ax - DP(n)<<" ax>DP(n)\n",ax > DP(n))
	{
		jFile<<"node8\n";
		tox=2.0/ax;
		bjm=NR::bessj0(ax);
		bj=NR::bessj1(ax);
		for (j=1;(jFile<<"node9 "<<j-n<<" j<n\n",j<n);j++)
		{
			jFile<<"node10\n";
			bjp=j*tox*bj-bjm;
			bjm=bj;
			bj=bjp;
		}
		jFile<<"node11\n";
		ans=bj;
	}
	else
	{
		jFile<<"node12\n";
		tox=2.0/ax;
		m=2*((n+int(sqrt(ACC*n)))/2);
		jsum=-1;
		bjp=ans=sum=0.0;
		bj=1.0;
		for (j=m;(jFile<<"node13 "<<j-0<<" j>0\n",j>0);j--)
		{
		   
			jFile<<"node14\n";
			bjm=j*tox*bj-bjp;
			bjp=bj;
			bj=bjm;
			dum=frexp(bj,&k);
			if (jFile<<"node15 "<<k - IEXP<<" k>IEXP\n",k > IEXP)
			{
				jFile<<"node16\n";
				bj=ldexp(bj,-IEXP);
				bjp=ldexp(bjp,-IEXP);
				ans=ldexp(ans,-IEXP);
				sum=ldexp(sum,-IEXP);
			}
			if (jFile<<"node17 "<<jsum-0<<" jsum<0\n",jsum<0)
			{
				jFile<<"node18\n";
				sum += bj;
			}
			
			jFile<<"node19\n";
			jsum = -jsum;
			if (jFile<<"node20 "<<j-n<<" j==n\n",j == n)
			{
				jFile<<"node21\n";
				ans=bjp;
			}
			
		}
		jFile<<"node22\n";
		sum=2.0*sum-bj;
		ans /= sum;
	}
	jFile<<"node23\n";
	return x < 0.0 && (n & 1) ? -ans : ans;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callExpint
 * Signature: (ID)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callExpint
  (JNIEnv *env, jobject arg, jint n, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream eFile(path);
	eFile<<setiosflags(ios::scientific);
	eFile<<setprecision(16);
	eFile<<"node1@expint\n";
	const int MAXIT=100;
	const DP EULER=0.577215664901533;
	const DP EPS=numeric_limits<DP>::epsilon();
	const DP BIG=numeric_limits<DP>::max()*EPS;
	int i,ii,nm1;
	DP a,b,c,d,del,fact,h,psi,ans;

	nm1=n-1;
	if ((eFile<<"node2@expint "<<n-0<<" expression@3\n",n < 0) || (eFile<<"node2@expint "<<x-0.0<<" expression@4\n",x < 0.0)
			|| ((eFile<<"node2@expint "<<x-0.0<<" expression@5\n",x==0.0) && ((eFile<<"node2@expint "<<n-0<<" expression@6\n",n==0) || (eFile<<"node2@expint "<<n-1<<" expression@7\n",n==1))))
	{
		eFile<<"node3@expint\n";
	//	printf("bad arguments in expint");
	}
	else
	{
		if (eFile<<"node4@expint "<<n-0<<" expression@10\n",n == 0)
		{
			eFile<<"node5@expint\n";
			ans=exp(-x)/x;
		}
		else
		{
			if (eFile<<"node6@expint "<<x-0.0<<" expression@12\n",x == 0.0)
			{
				eFile<<"node7@expint\n";
				ans=1.0/nm1;
			}
			else
			{
				if (eFile<<"node8@expint "<<x-1.0<<" expression@14\n",x > 1.0)
				{
					eFile<<"node9@expint\n";
					b=x+n;
					c=BIG;
					d=1.0/b;
					h=d;
					for (i=1;(eFile<<"node10@expint "<<i-MAXIT<<" expression@16\n",i<=MAXIT);i++)
					{
						eFile<<"node11@expint\n";
						a = -i*(nm1+i);
						b += 2.0;
						d=1.0/(a*d+b);
						c=b+a/c;
						del=c*d;
						h *= del;
						if (eFile<<"node12@expint "<<fabs(del-1.0) - EPS<<" expression@18\n",fabs(del-1.0) <= EPS)
						{
							eFile<<"node13@expint\n";
							ans=h*exp(-x);
							i=MAXIT+1;
						}
					}
					eFile<<"node14@expint\n";
	//				printf("continued fraction failed in expint");
				}
				else
				{
					eFile<<"node15@expint\n";
					ans = (nm1!=0 ? 1.0/nm1 : -log(x)-EULER);
					fact=1.0;
					for (i=1;(eFile<<"node16@expint "<<i-MAXIT<<" expression@22\n",i<=MAXIT);i++)
					{
						eFile<<"node17@expint\n";
						fact *= -x/i;
						if ((eFile<<"node18@expint "<<i-nm1<<" expression@25\n",i > nm1) || (eFile<<"node18@expint "<<i-nm1<<" expression@26\n",i < nm1))
						{
							eFile<<"node19@expint\n";
							del = -fact/(i-nm1);
						}
						else
						{
							eFile<<"node20@expint\n";
							psi = -EULER;
							for (ii=1;(eFile<<"node21@expint "<<ii-nm1<<" expression@29\n",ii<=nm1);ii++)
							{
								eFile<<"node22@expint\n";
								psi += 1.0/ii;
							}
							eFile<<"node23@expint\n";
							del=fact*(-log(x)+psi);
						}
						eFile<<"node24@expint\n";
						ans += del;
						if (eFile<<"node25@expint "<<fabs(del) - fabs(ans)*EPS<<" expression@32\n",fabs(del) < fabs(ans)*EPS)
						{
							eFile<<"node26@expint\n";
							i=MAXIT+1;
						}
					}
					eFile<<"node27@expint\n";
	//				printf("series failed in expint");
				}
			}
		}
	}
	eFile<<"node28@expint\n";
	return ans;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBetacf
 * Signature: (DDD)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBetacf
  (JNIEnv *env, jobject arg, jdouble a, jdouble b, jdouble x)
{
	ofstream fFile("/home/cx/atg_data/betacf.dat");
	fFile<<"node1\n";
	const int MAXIT=100;
	const DP EPS=numeric_limits<DP>::epsilon();
	const DP FPMIN=numeric_limits<DP>::min()/EPS;
//	const DP EPS=10;
//	const DP FPMIN=100/EPS;
	int m,m2;
	DP aa,c,d,del,h,qab,qam,qap;

	qab=a+b;
	qap=a+1.0;
	qam=a-1.0;
	c=1.0;
	d=1.0-qab*x/qap;
	if (fFile<<"node2 "<<fabs(d) - FPMIN<<" fabs(d)<FPMIN\n",fabs(d) < FPMIN)
	{
		fFile<<"node3\n";
		d=FPMIN;
	}
	fFile<<"node4\n";
	d=1.0/d;
	h=d;
	for (m=1;(fFile<<"node5 "<<m-MAXIT<<" m<=MAXIT\n",m<=MAXIT);m++)
	{
		fFile<<"node6\n";
		m2=2*m;
		aa=m*(b-m)*x/((qam+m2)*(a+m2));
		d=1.0+aa*d;
		if (fFile<<"node7 "<<fabs(d) - FPMIN<<" fabs(d)<FPMIN\n",fabs(d) < FPMIN)
		{
			fFile<<"node8\n";
			d=FPMIN;
		}
		fFile<<"node9\n";
		c=1.0+aa/c;
		if (fFile<<"node10 "<<fabs(c) - FPMIN<<" fabs(c)<FPMIN\n",fabs(c) < FPMIN)
		{
			fFile<<"node11\n";
			c=FPMIN;
		}
		fFile<<"node12\n";
		d=1.0/d;
		h *= d*c;
		aa = -(a+m)*(qab+m)*x/((a+m2)*(qap+m2));
		d=1.0+aa*d;
		if (fFile<<"node13 "<<fabs(d) - FPMIN<<" fabs(d)<FPMIN\n",fabs(d) < FPMIN)
		{
			fFile<<"node14\n";
			d=FPMIN;
		}
		fFile<<"node15\n";
		c=1.0+aa/c;
		if (fFile<<"node16 "<<fabs(c) - FPMIN<<" fabs(c)<FPMIN\n",fabs(c) < FPMIN)
		{
			fFile<<"node17\n";
			c=FPMIN;
		}
		fFile<<"node18\n";
		d=1.0/d;
		del=d*c;
		h *= del;
		if (fFile<<"node19 "<<fabs(del-1.0) - EPS<<" fabs(del-1.0)<=EPS\n",fabs(del-1.0) <= EPS)
		{
			if (fFile<<"node20 "<<m - MAXIT<<" m>MAXIT\n",m > MAXIT)
			{
				fFile<<"node21\n";
				printf("a or b too big, or MAXIT too small in betacf");
			}
			fFile<<"node22\n";
			m = MAXIT+1;
		}
	}
	if (fFile<<"node23 "<<m - MAXIT<<" m>MAXIT\n",m > MAXIT)
	{
		fFile<<"node24\n";
		printf("a or b too big, or MAXIT too small in betacf");
	}
	fFile<<"node25\n";
	return h;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callCaldat
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callCaldat
  (JNIEnv *env, jobject arg, jint julian, jint mm, jint id, jint iyyy, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream tFile(path);
	tFile<<"node1\n";
	const int IGREG=2299161;
	int ja,jalpha,jb,jc,jd,je;

	if (tFile<<"node2 "<<julian - IGREG<<" julian>=IGREG\n",julian >= IGREG)
	{
		tFile<<"node3\n";
		jalpha=int((DP(julian-1867216)-0.25)/36524.25);
		ja=julian+1+jalpha-int(0.25*jalpha);
	}
	else if (tFile<<"node4 "<<julian - 0<<" julian<0\n",julian < 0)
	{
		tFile<<"node5\n";
		ja=julian+36525*(1-julian/36525);
	}
	else
	{
		tFile<<"node6\n";
		ja=julian;
	}
	tFile<<"node7\n";
	jb=ja+1524;
	jc=int(6680.0+(DP(jb-2439870)-122.1)/365.25);
	jd=int(365*jc+(0.25*jc));
	je=int((jb-jd)/30.6001);
	id=jb-jd-int(30.6001*je);
	mm=je-1;
	if (tFile<<"node8 "<<mm - 12<<" mm>12\n",mm > 12)
	{
		tFile<<"node9\n";
		mm -= 12;
	}
	tFile<<"node10\n";
	iyyy=jc-4715;
	if (tFile<<"node11 "<<mm - 2<<" mm>2\n",mm > 2)
	{
		tFile<<"node12\n";
		--iyyy;
	}
	if (tFile<<"node13 "<<iyyy - 0<<" iyyy<=0\n",iyyy <= 0)
	{
		tFile<<"node14\n";
		--iyyy;
	}
	if (tFile<<"node15 "<<julian - 0<<" julian<0\n",julian < 0)
	{
		tFile<<"node16\n";
		iyyy -= 100*(1-julian/36525);
	}

	tFile<<"node17\n";
	return 0.0;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callPlgndr
 * Signature: (IID)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callPlgndr
  (JNIEnv *env, jobject arg, jint l, jint m, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream rFile(path);
	rFile<<setiosflags(ios::scientific);
	rFile<<setprecision(16);
	rFile<<"node1\n";
	int i,ll;
	DP fact,pll,pmm,pmmp1,somx2;

	if ((rFile<<"node2 "<<m-0<<" m<0\n",m < 0) || (rFile<<"node2 "<<m - l<<" m>l\n",m > l) || (rFile<<"node2 "<<fabs(x) - 1.0<<" fabs(x)>1.0\n",fabs(x) > 1.0))
	{
		rFile<<"node3\n";
//		printf("Bad arguments in routine plgndr");
	}
	rFile<<"node4\n";
	pmm=1.0;
	if (rFile<<"node5 "<<m - 0<<" m>0\n",m > 0)
	{
		rFile<<"node6\n";
		somx2=sqrt((1.0-x)*(1.0+x));
		fact=1.0;
		for (i=1;(rFile<<"node7 "<<i-m<<" i<=m\n",i<=m);i++)
		{
			rFile<<"node8\n";
			pmm *= -fact*somx2;
			fact += 2.0;
		}
	}
	if (rFile<<"node9 "<<l - m<<" l==m\n",l == m)
	{
		rFile<<"node10\n";
		return pmm;
	}
	else
	{
		rFile<<"node11\n";
		pmmp1=x*(2*m+1)*pmm;
		if (rFile<<"node12 "<<l - (m+1)<<" l==(m+1)\n",l == (m+1))
		{
			rFile<<"node13\n";
			return pmmp1;
		}
		else
		{
			for (ll=m+2;(rFile<<"node14 "<<ll-l<<" ll<=l\n",ll<=l);ll++)
			{
				rFile<<"node15\n";
				pll=(x*(2*ll-1)*pmmp1-(ll+m-1)*pmm)/(ll-m);
				pmm=pmmp1;
				pmmp1=pll;
			}
			rFile<<"node16\n";
			return pll;
		}
	}
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callEi
 * Signature: (D)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callEi
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream eFile(path);
	eFile<<setiosflags(ios::scientific);
	eFile<<setprecision(16);
	eFile<<"node1\n";
	const int MAXIT=100;
	const DP EULER=0.577215664901533;
	const DP EPS=numeric_limits<DP>::epsilon();
	const DP FPMIN=numeric_limits<DP>::min()/EPS;
	int k;
	DP fact,prev,sum,term;
	DP result;

	if (eFile<<"node2 "<<(x-0.0)<<" x<=0.0\n",x <= 0.0)
	{
		eFile<<"node3\n";
		printf("Bad argument in ei");
	}
	else
	{
		if (eFile<<"node4 "<<(x-FPMIN)<<" x<FPMIN\n",x < FPMIN)
		{
			eFile<<"node5\n";
			result = log(x)+EULER;
		}
		else
		{
			if (eFile<<"node6 "<<(x - (-log(EPS)))<<" x<=-log(EPS)\n",x <= -log(EPS)) 
			{
				eFile<<"node7\n";
				sum=0.0;
				fact=1.0;
				for (k=1;(eFile<<"node8 "<<(k-MAXIT)<<" k<=MAXIT\n",k<=MAXIT);k++)
				{
					eFile<<"node9\n";
					fact *= x/k;
					term=fact/k;
					sum += term;
					if (eFile<<"node10 "<<(term - EPS*sum)<<" term<EPS*sum\n",term < EPS*sum)
					{
						eFile<<"node11\n";
						k = MAXIT+1;
					}
				}
				if (eFile<<"node12 "<<(k-MAXIT)<<" k>MAXIT\n",k > MAXIT)
				{
					eFile<<"node13\n";
					printf("Series failed in ei");
				}
				eFile<<"node14\n";
				result = sum+log(x)+EULER;
			} else {
				eFile<<"node15\n";
				sum=0.0;
				term=1.0;
				for (k=1;(eFile<<"node16 "<<(k-MAXIT)<<" k<=MAXIT\n",k<=MAXIT);k++)
				{
					eFile<<"node17\n";
					prev=term;
					term *= k/x;
					if (eFile<<"node18 "<<(term-EPS)<<" term<EPS\n",term < EPS)
					{
						eFile<<"node19\n";
						k = MAXIT+1;
					}
					else
					{
						if (eFile<<"node20 "<<(term-prev)<<" term<prev\n",term < prev)
						{
							eFile<<"node21\n";
							sum += term;
						}
						else
						{
							eFile<<"node22\n";
							sum -= prev;
							k = MAXIT+1;
						}
					}
				}
				eFile<<"node23\n";
				result = exp(x)*(1.0+sum)/x;
			}
		}
	}
	eFile<<"node24\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callGammp
 * Signature: (DD)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callGammp
  (JNIEnv *env, jobject arg, jdouble a, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream gFile(path);
	gFile<<setiosflags(ios::scientific);
	gFile<<setprecision(16);

	gFile<<"node1\n";
	DP gamser,gammcf,gln;
	DP result;
	const int ITMAX=100;
	const DP EPS=numeric_limits<DP>::epsilon();
	int n;
	DP sum,del,ap;
	const DP FPMIN=numeric_limits<DP>::min()/EPS;
	int i;
	DP an,b,c,d,h;

	if ((gFile<<"node2 "<<(x-0.0)<<" x<0.0\n",x < 0.0) || (gFile<<"node2 "<<(a-0.0)<<" a<=0.0\n",a <= 0.0))
	{
		gFile<<"node3\n";
		printf("Invalid arguments in routine gammp");
	}
	else
	{
		if (gFile<<"node4 "<<(x-(a+1.0))<<" x<a+1.0\n",x < a+1.0)
		{
			gFile<<"node5\n";
			gln=NR::gammln(a);
			if (gFile<<"node6 "<<(x-0.0)<<" x<=0.0\n",x <= 0.0)
			{
				if (gFile<<"node7 "<<(x-0.0)<<" x<0.0\n",x < 0.0)
				{
					gFile<<"node8\n";
					printf("x less than 0 in routine gser");
				}
				gFile<<"node9\n";
				gamser=0.0;
			}
			else
			{
				gFile<<"node10\n";
				ap=a;
				del=sum=1.0/a;
				for (n=0;(gFile<<"node11 "<<(n-ITMAX)<<" n<ITMAX\n",n<ITMAX);n++)
				{
					gFile<<"node12\n";
					++ap;
					del *= x/ap;
					sum += del;
					if (gFile<<"node13 "<<(fabs(del) - fabs(sum)*EPS)<<" fabs(del)<fabs(sum)*EPS\n",fabs(del) < fabs(sum)*EPS)
					{
						gFile<<"node14\n";
						gamser=sum*exp(-x+a*log(x)-gln);
					}
				}
				gFile<<"node15\n";
				printf("a too large, ITMAX too small in routine gser");
			}
			gFile<<"node16\n";
			result = gamser;
		} 
		else
		{
			gFile<<"node17\n";
			gln=NR::gammln(a);
			b=x+1.0-a;
			c=1.0/FPMIN;
			d=1.0/b;
			h=d;
			for (i=1;(gFile<<"node18 "<<(i-ITMAX)<<" i<=ITMAX\n",i<=ITMAX);i++)
			{
				gFile<<"node19\n";
				an = -i*(i-a);
				b += 2.0;
				d=an*d+b;
				if (gFile<<"node20 "<<(fabs(d) - FPMIN)<<" fabs(d)<FPMIN\n",fabs(d) < FPMIN)
				{
					gFile<<"node21\n";
					d=FPMIN;
				}
				gFile<<"node22\n";
				c=b+an/c;
				if (gFile<<"node23 "<<(fabs(c) - FPMIN)<<" fabs(c)<FPMIN\n",fabs(c) < FPMIN)
				{
					gFile<<"node24\n";
					c=FPMIN;
				}
				gFile<<"node25\n";
				d=1.0/d;
				del=d*c;
				h *= del;
				if (gFile<<"node26 "<<(fabs(del-1.0) - EPS)<<" fabs(del-1.0)<=EPS\n",fabs(del-1.0) <= EPS)
				{
					gFile<<"node27\n";
					i = ITMAX+1;
				}
			}
			if (gFile<<"node28 "<<(i-ITMAX)<<" i>ITMAX\n",i > ITMAX)
			{
				gFile<<"node29\n";
				printf("a too large, ITMAX too small in gcf");
			}
			gFile<<"node30\n";
			gammcf=exp(-x+a*log(x)-gln)*h;

			result = 1.0-gammcf;
		}
	}
	gFile<<"node31\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callJulday
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callJulday
  (JNIEnv *env, jobject arg, jint mm, jint id, jint iyyy, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream jFile(path);
	jFile<<"node1\n";
	const int IGREG=15+31*(10+12*1582);
	int ja,jul,jy=iyyy,jm;

	if (jFile<<"node2 "<<(jy-0)<<" jy==0\n",jy == 0)
	{
		jFile<<"node3\n";
		printf("julday: there is no year zero.");
	}
	if (jFile<<"node4 "<<(jy-0)<<" jy<0\n",jy < 0)
	{
		jFile<<"node5\n";
		++jy;
	}
	if (jFile<<"node6 "<<(mm-2)<<" mm>2\n",mm > 2)
	{
		jFile<<"node7\n";
		jm=mm+1;
	}
	else
	{
		jFile<<"node8\n";
		--jy;
		jm=mm+13;
	}
	jFile<<"node9\n";
	jul = int(floor(365.25*jy)+floor(30.6001*jm)+id+1720995);
	if (jFile<<"node10 "<<((id+31*(mm+12*iyyy)) - IGREG)<<" id+31*(mm+12*iyyy)>=IGREG\n",id+31*(mm+12*iyyy) >= IGREG)
	{
		jFile<<"node11\n";
		ja=int(0.01*jy);
		jul += 2-ja+int(0.25*ja);
	}
	jFile<<"node12\n";
	return jul;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callProbks
 * Signature: (D)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callProbks
  (JNIEnv *env, jobject arg, jdouble alam)
{
	ofstream pFile("/home/cx/atg_data/probks.dat");
	pFile<<"node1\n";
	const DP EPS1=1.0e-6,EPS2=1.0e-16;
	int j;
	DP a2,fac=2.0,sum=0.0,term,termbf=0.0;
	DP result = 1.0;

	a2 = -2.0*alam*alam;
	for (j=1;(pFile<<"node2 "<<(j-100)<<" j<=100\n",j<=100);j++)
	{
		pFile<<"node3\n";
		term=fac*exp(a2*j*j);
		sum += term;
		if ((pFile<<"node4 "<<(fabs(term) - EPS1*termbf)<<" fabs(term)<=EPS1*termbf\n",fabs(term) <= EPS1*termbf) || (pFile<<"node4 "<<(fabs(term) - EPS2*sum)<<" fabs(term)<=EPS2*sum\n",fabs(term) <= EPS2*sum))
		{
			pFile<<"node5\n";
			result = sum;
			j = 101;
		}
		else
		{
			pFile<<"node6\n";
			fac = -fac;
			termbf=fabs(term);
		}
	}
	pFile<<"node7\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callRan1
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callRan1
  (JNIEnv *env, jobject arg, jint idum)
{
	ofstream rFile("/home/cx/atg_data/ran1.dat");
	rFile<<"node1\n";
	const int IA=16807,IM=2147483647,IQ=127773,IR=2836,NTAB=32;
	const int NDIV=(1+(IM-1)/NTAB);
	const DP EPS=3.0e-16,AM=1.0/IM,RNMX=(1.0-EPS);
	static int iy=0;
	static Vec_INT iv(NTAB);
	int j,k;
	DP temp;
	DP result;

	if ((rFile<<"node2 "<<(idum-0)<<" idum<=0\n",idum <= 0) || (rFile<<"node2 "<<(iy-0)<<" iy==0\n",iy==0))
	{
		if (rFile<<"node3 "<<(idum - (-1))<<" idum>-1\n",idum > -1) 
		{
			rFile<<"node4\n";
			idum=1;
		}
		else 
		{
			rFile<<"node5\n";
			idum = -idum;
		}
		for (j=NTAB+7;(rFile<<"node6 "<<(j-0)<<" j>=0\n",j>=0);j--)
		{
			rFile<<"node7\n";
			k=idum/IQ;
			idum=IA*(idum-k*IQ)-IR*k;
			if (rFile<<"node8 "<<(idum-0)<<" idum<0\n",idum < 0)
			{
				rFile<<"node9\n";
				idum += IM;
			}
			if (rFile<<"node10 "<<(j-NTAB)<<" j<NTAB\n",j < NTAB)
			{
				rFile<<"node11\n";
				iv[j] = idum;
			}
		}
		rFile<<"node12\n";
		iy=iv[0];
	}
	rFile<<"node13\n";
	k=idum/IQ;
	idum=IA*(idum-k*IQ)-IR*k;
	if (rFile<<"node14 "<<(idum-0)<<" idum<0\n",idum < 0)
	{
		rFile<<"node15\n";
		idum += IM;
	}
	rFile<<"node16\n";
	j=iy/NDIV;
	iy=iv[j];
	iv[j] = idum;
	temp = AM*iy;
	if (rFile<<"node17 "<<(temp - RNMX)<<" temp>RNMX\n",temp > RNMX)
	{
		rFile<<"node18\n";
		result = RNMX;
	}
	else
	{
		rFile<<"node19\n";
		result = temp;
	}
	rFile<<"node20\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callEncode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callEncode
  (JNIEnv *, jobject, jint xin1, jint xin2)
{
    ofstream rFile("/home/cx/atg_data/encode.dat");
    rFile<<"node1\n";
    int i;
    int *h_ptr,*tqmf_ptr,*tqmf_ptr1;
    long int xa,xb;
    int decis;

    /*if(resetindex == 0) */{reset();resetindex++;}
/* transmit quadrature mirror filters implemented here */
    h_ptr = h;
    tqmf_ptr = tqmf;
    xa = (long)(*tqmf_ptr++) * (*h_ptr++);
    xb = (long)(*tqmf_ptr++) * (*h_ptr++);
/* main multiply accumulate loop for samples and coefficients */
    /* MAX: 10 */
    for(i = 0 ; (rFile<<"node2 "<<(i-10)<<" i<10\n",i < 10 ); i++) {
	rFile<<"node3\n";
        xa += (long)(*tqmf_ptr++) * (*h_ptr++);
        xb += (long)(*tqmf_ptr++) * (*h_ptr++);
    }
	rFile<<"node4\n";
/* final mult/accumulate */
    xa += (long)(*tqmf_ptr++) * (*h_ptr++);
    xb += (long)(*tqmf_ptr) * (*h_ptr++);

/* update delay line tqmf */
    tqmf_ptr1 = tqmf_ptr - 2;
    /* MAX: 22 */
    for(i = 0 ;(rFile<<"node5 "<<(i-22)<<" i<22\n", i < 22) ; i++) {
	rFile<<"node6\n";
	*tqmf_ptr-- = *tqmf_ptr1--;
    }
	rFile<<"node7\n";
    *tqmf_ptr-- = xin1;
    *tqmf_ptr = xin2;

/* scale outputs */
    xl = (xa + xb) >> 15;
    xh = (xa - xb) >> 15;

/* end of quadrature mirror filter code */

/* starting with lower sub band encoder */

/* filtez - compute predictor output section - zero section */
    szl = filtez(delay_bpl,delay_dltx);

/* filtep - compute predictor output signal (pole section) */
    spl = filtep(rlt1,al1,rlt2,al2);

/* compute the predictor output value in the lower sub_band encoder */
    sl = szl + spl;
    el = xl - sl;

/* quantl: quantize the difference signal */
    il = quantl(el,detl);

/* invqxl: computes quantized difference signal */
/* for invqbl, truncate by 2 lsbs, so mode = 3 */
    dlt = ((long)detl*qq4_code4_table[il >> 2]) >> 15;

/* logscl: updates logarithmic quant. scale factor in low sub band */
    nbl = logscl(il,nbl);

/* scalel: compute the quantizer scale factor in the lower sub band */
/* calling parameters nbl and 8 (constant such that scalel can be scaleh) */
    detl = scalel(nbl,8);

/* parrec - simple addition to compute recontructed signal for adaptive pred */
    plt = dlt + szl;

/* upzero: update zero section predictor coefficients (sixth order)*/
/* calling parameters: dlt, dlt1, dlt2, ..., dlt6 from dlt */
/*  bpli (linear_buffer in which all six values are delayed */
/* return params:      updated bpli, delayed dltx */
    upzero(dlt,delay_dltx,delay_bpl);

/* uppol2- update second predictor coefficient apl2 and delay it as al2 */
/* calling parameters: al1, al2, plt, plt1, plt2 */
    al2 = uppol2(al1,al2,plt,plt1,plt2);

/* uppol1 :update first predictor coefficient apl1 and delay it as al1 */
/* calling parameters: al1, apl2, plt, plt1 */
    al1 = uppol1(al1,al2,plt,plt1);

/* recons : compute recontructed signal for adaptive predictor */
    rlt = sl + dlt;

/* done with lower sub_band encoder; now implement delays for next time*/
    rlt2 = rlt1;
    rlt1 = rlt;
    plt2 = plt1;
    plt1 = plt;

/* high band encode */

    szh = filtez(delay_bph,delay_dhx);

    sph = filtep(rh1,ah1,rh2,ah2);

/* predic: sh = sph + szh */
    sh = sph + szh;
/* subtra: eh = xh - sh */
    eh = xh - sh;

/* quanth - quantization of difference signal for higher sub-band */
/* quanth: in-place for speed params: eh, deth (has init. value) */
    if(rFile<<"node8 "<<eh-0<<" eh>=0\n",eh >= 0) {
	rFile<<"node9\n";
        ih = 3;     /* 2,3 are pos codes */
    }
    else {
	rFile<<"node10\n";
        ih = 1;     /* 0,1 are neg codes */
    }
	rFile<<"node11\n";
    decis = (564L*(long)deth) >> 12L;
    if(rFile<<"node12 "<<my_abs(eh)-decis<<" my_abs(eh) > decis\n",my_abs(eh) > decis)
	{
	rFile<<"node13\n";
 	ih--;     /* mih = 2 case */
	}
	rFile<<"node14\n";
/* invqah: compute the quantized difference signal, higher sub-band*/
    dh = ((long)deth*qq2_code2_table[ih]) >> 15L ;

/* logsch: update logarithmic quantizer scale factor in hi sub-band*/
    nbh = logsch(ih,nbh);

/* note : scalel and scaleh use same code, different parameters */
    deth = scalel(nbh,10);

/* parrec - add pole predictor output to quantized diff. signal */
    ph = dh + szh;

/* upzero: update zero section predictor coefficients (sixth order) */
/* calling parameters: dh, dhi, bphi */
/* return params: updated bphi, delayed dhx */
    upzero(dh,delay_dhx,delay_bph);

/* uppol2: update second predictor coef aph2 and delay as ah2 */
/* calling params: ah1, ah2, ph, ph1, ph2 */
    ah2 = uppol2(ah1,ah2,ph,ph1,ph2);

/* uppol1:  update first predictor coef. aph2 and delay it as ah1 */
    ah1 = uppol1(ah1,ah2,ph,ph1);

/* recons for higher sub-band */
    yh = sh + dh;

/* done with higher sub-band encoder, now Delay for next time */
    rh2 = rh1;
    rh1 = yh;
    ph2 = ph1;
    ph1 = ph;

/* multiplex ih and il to get signals together */
	rFile<<"node15\n";
    return(il | (ih << 6));
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callIcrc
 * Signature: (SJSI)S
 */
JNIEXPORT jshort JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callIcrc
  (JNIEnv *, jobject, jshort crc, jlong len, jshort jinit, jint jrev)
{
ofstream rFile("/home/cx/atg_data/icrc.dat");
    rFile<<"node1\n";
  unsigned short icrc1(unsigned short crc, unsigned char onech);
  static unsigned short icrctb[256],init=0;
  static uchar rchr[256];
  unsigned short tmp1, tmp2, j,cword=crc;
  static uchar it[16]={0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15};

  if (rFile<<"node2 "<<init<<" !init\n",!init) {
	rFile<<"node3\n";
    init=1;
    for (j=0;(rFile<<"node4 "<<j-255<<" j<=255\n",j<=255);j++) {
	rFile<<"node5\n";
      icrctb[j]=icrc1(j << 8,(uchar)0);
      rchr[j]=(uchar)(it[j & 0xF] << 4 | it[j >> 4]);
    }
  }
  if (rFile<<"node6 "<<jinit<<" jinit>=0\n",jinit >= 0){rFile<<"node7\n"; cword=((uchar) jinit) | (((uchar) jinit) << 8);}
  else if (rFile<<"node8 "<<jrev<<" jrev<0\n",jrev < 0){rFile<<"node9\n";
    cword=rchr[HIBYTE(cword)] | rchr[LOBYTE(cword)] << 8;}
  for (j=1;(rFile<<"node10 "<<j-len<<" j<=len\n",j<=len);j++) {
    if (rFile<<"node11 "<<jrev<<" jrev<0\n",jrev < 0) {
	rFile<<"node12\n";
      tmp1 = rchr[lin[j]]^ HIBYTE(cword);
    }
    else {rFile<<"node13\n";
      tmp1 = lin[j]^ HIBYTE(cword);
    }
	rFile<<"node14\n";
    cword = icrctb[tmp1] ^ LOBYTE(cword) << 8;
  }
  if (rFile<<"node15 "<<jrev<<" jrev>=0\n",jrev >= 0) {
	rFile<<"node16\n";
    tmp2 = cword;
  }
  else {rFile<<"node17\n";
    tmp2 = rchr[HIBYTE(cword)] | rchr[LOBYTE(cword)] << 8;
  }
	rFile<<"node18\n";
  return (tmp2 );
}

