/*
 * CallCPP.cpp
 *
 *  Created on: Feb 7, 2015
 *      Author: zy
 */
#include "cn_nju_seg_atg_callCPP_CallCPP.h"
#include <cmath>
#include <limits>
#include <iomanip>
#include <fstream>
#include <cstring>
#include <malloc.h>
#include <assert.h>
#include <iostream>
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

void abort() {
//    assert(false);
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callDart
 * Signature: (IILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callDart
  (JNIEnv *env, jobject, jint x, jint y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	if (bFile<<"node1 "<<x*x*x-0<<" x*x*x>0\n",x*x*x > 0){
			if((bFile<<"node2 "<<x-0<<" x>0\n",x>0) && (bFile<<"node2 "<<y-10<<" y==10\n",y==10)){
			    bFile<<"node3\n";
				abort();
			}
		} else {
			if ((bFile<<"node4 "<<x-0<<" x>0\n",x>0) && (bFile<<"node4 "<<y-20<<" y==20\n",y==20)){
			    bFile<<"node5\n";
				abort();
			}
		}
	    bFile<<"node6\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callPower
 * Signature: (IILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callPower
  (JNIEnv *env, jobject, jint x, jint y, jstring pathFile){
	char* file = jstringTostring(env, pathFile);
	ofstream bFile(file);
	bFile<<"node1\n";
	int path = 0;
	if (bFile<<"node2 "<<x-0<<" x>0\n",x > 0) {
		if (bFile<<"node3 "<<y-x*x<<" y==x*x\n",y == x*x) {
			bFile<<"node4\n";
//			printf("Solved S0");
			path = 1;
		}
		else {
			bFile<<"node5\n";
//			printf("Solved S1");
			path = 2;
		}
		if (bFile<<"node6 "<<y-8<<" y>8\n",y > 8) {
		//if (x > 1 && y > 3) {
			if (bFile<<"node7 "<<path-1<<" path==1\n",path == 1){
			    bFile<<"node8\n";
//				printf("Solved S0;S3");
			}
			if (bFile<<"node9 "<<path-2<<" path==2\n",path == 2){
			    bFile<<"node10\n";
//				printf("Solved S1;S3");
			}
		}
		else {
			if (bFile<<"node11 "<<path-1<<" path==1\n",path == 1){
			    bFile<<"node12\n";
//				printf("Solved S0;S4");
			}
			if (bFile<<"node13 "<<path-2<<" path==2\n",path == 2){
			    bFile<<"node14\n";
//				printf("Solved S1;S4");
			}
		}
	}
	bFile<<"node15\n";
}

#define PI 3.14159265358979323846
double degToRad = PI/180.0;
double g = 68443.0;
/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callConflict
 * Signature: (DDDDDDDLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callConflict
  (JNIEnv *env, jobject, jdouble psi1, jdouble vA, jdouble vC, jdouble xC0, jdouble yC0, jdouble psiC, jdouble bank_ang, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";
    string PATH("");
    double dmin = 999;
    double dmst = 2;
    double psiA = psi1 * degToRad;
    int signA = 1;
    int signC = 1;

    if (bFile<<"node2 "<<psiA-0<<" psiA<0\n",psiA < 0) {
        bFile<<"node3\n";
        PATH += "psiA<0\n";
        signA = -1;
    } else {
        bFile<<"node4\n";
        PATH += "psiA>=0\n";
    }
    bFile<<"node5\n";
    double rA = pow(vA, 2.0) / tan(bank_ang*degToRad) / g;
    double rC = pow(vC, 2.0) / tan(bank_ang*degToRad) / g;

    double t1 = abs(psiA) * rA / vA;
    double dpsiC = signC * t1 * vC/rC;
    double xA = signA*rA*(1-cos(psiA));
    double yA = rA*signA*sin(psiA);

    double xC = xC0 + signC*rC* (cos(psiC)-cos(psiC+dpsiC));
    double yC = yC0 - signC*rC*(sin(psiC)-sin(psiC+dpsiC));

    double xd1 = xC - xA;
    double yd1 = yC - yA;

    double d = sqrt(pow(xd1, 2.0) + pow(yd1, 2.0));
    double minsep;

    // min sep in turn
    if (bFile<<"node6 "<<d-dmin<<" d<dmin\n",d < dmin) {
        bFile<<"node7\n";
        PATH += "d < dmin\n";
        dmin = d;
    } else {
        bFile<<"node8\n";
        PATH += "d >= dmin\n";
    }

    if (bFile<<"node9 "<<dmin-dmst<<" dmin<dmst\n",dmin < dmst) {
        bFile<<"node10\n";
        PATH += "dmin < dmst\n";
        minsep = dmin;
    } else {
        bFile<<"node11\n";
        PATH += "dmin >= dmst\n";
        minsep = dmst;
    }
    bFile<<"node12\n";
    cout<<PATH+">>> PATH: "<<endl;
    bFile<<"node13\n";
    return minsep;
}

double twoPi = PI * 2;
double deg = PI / 180;
double gacc = 32.0;
double normAngle(double angle) {
	if (angle < -PI) {
		return angle + twoPi;
	}
	if (angle > PI) {
		return angle - twoPi;
	}
	return angle;
}
/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callTurnLogic
 * Signature: (DDDDDDDDLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTurnLogic
  (JNIEnv *env, jobject, jdouble x0, jdouble y0, jdouble gspeed, jdouble x1, jdouble y1, jdouble x2, jdouble y2, jdouble dt, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";
	double dx = x0 - x1;
	double dy = y0 - y1;
	if ((bFile<<"node2 "<<dx-0<<" dx==0\n",dx == 0) && (bFile<<"node2 "<<dy-0<<" dy==0\n",dy == 0)){
	    bFile<<"node3\n";
		return 0.0;
	}
	bFile<<"node4\n";
	double instHdg = 90 * deg - atan2(dy, dx);
	if (bFile<<"node5 "<<instHdg-0.<<" instHdg<0.\n",instHdg < 0.){
		bFile<<"node6\n";
		instHdg += 360 * deg;
	}
	if (bFile<<"node7 "<<instHdg-2*PI<<" instHdg>2*PI\n",instHdg > 2 * PI){
		bFile<<"node8\n";
		instHdg -= 360 * deg;
	}

	bFile<<"node9\n";
	dx = x1 - x2;
	dy = y1 - y2;
	if ((bFile<<"node10 "<<dx-0<<" dx==0\n",dx == 0) && (bFile<<"node10 "<<dy-0<<" dy==0\n",dy == 0)){
		bFile<<"node11\n";
		return 0.0;
	}
	bFile<<"node12\n";
	double instHdg0 = 90 * deg - atan2(dy, dx);
	if (bFile<<"node13 "<<instHdg0-0.<<" instHdg0<0.\n",instHdg0 < 0.){
		bFile<<"node14\n";
		instHdg0 += 360 * deg;
	}
	if (bFile<<"node15 "<<instHdg0-2*PI<<" instHdg0>2*PI\n",instHdg0 > 2 * PI){
		bFile<<"node16\n";
		instHdg0 -= 360 * deg;
	}

	bFile<<"node17\n";
//	double hdg_diff = normAngle(instHdg - instHdg0);
	double angle = instHdg - instHdg0;
	double hdg_diff;
	if (bFile<<"node18 "<<angle+PI<<" angle<-PI\n",angle < -PI) {
		bFile<<"node19\n";
		hdg_diff = angle + twoPi;
	}
	else if (bFile<<"node20 "<<angle-PI<<" angle>PI\n",angle > PI) {
		bFile<<"node21\n";
		hdg_diff = angle - twoPi;
	}
	else {
		bFile<<"node22\n";
		hdg_diff = angle;
	}
	bFile<<"node23\n";
	double phi = atan2(hdg_diff * gspeed, gacc * dt);
	bFile<<"node24\n";
	return phi / deg;
}

static int IEEE_MAX = 2047;
static int IEEE_BIAS = 1023;
static int IEEE_MANT = 52;
static double sixth = 1.0/6.0;
static double half = 1.0/2.0;
static double mag52 = 1024.*1024.*1024.*1024.*1024.*4.;/*2**52*/

static double P[] = {
	  -0.64462136749e-9,
	   0.5688203332688e-7,
	  -0.359880911703133e-5,
	   0.16044116846982831e-3,
	  -0.468175413106023168e-2,
	   0.7969262624561800806e-1,
	  -0.64596409750621907082,
	   0.15707963267948963959e1
};

static double _2_pi_hi; // = 2.0/Math.PI ;
static double _2_pi_lo;
static double pi2_lo;
static double pi2_hi_hi;
static double pi2_hi_lo;
static double pi2_lo_hi;
static double pi2_lo_lo;
static double pi2_hi; // = Math.PI/2;
static double pi2_lo2;
static double X_EPS = (double)1e-4;

double longBitsToDouble(long value){
	double result;
	memcpy(&result, &value, sizeof result);
	return result;
}
long doubleToRawLongBits(double value){
	long bits;
	memcpy(&bits, &value, sizeof bits);
	return bits;
}
long helperdoubleToRawBits(double xm) {
	return doubleToRawLongBits(xm);
}
void MathSin(){
    //#define MD(v,hi,lo) md.i.i1 = hi; md.i.i2 = lo; v = md.d;

    //	  MD(    pi_hi, 0x400921FBuL,0x54442D18uL);/* top 53 bits of PI	*/
    // pi_hi = Double.longBitsToDouble((long)0x400921FB54442D18L);
    // printf("pi_hi = " + pi_hi);
    //	  MD(    pi_lo, 0x3CA1A626uL,0x33145C07uL);/* next 53 bits of PI*/
    // pi_lo = Double.longBitsToDouble((long)0x3CA1A62633145C07L);
    // printf("pi_lo = " + pi_lo);

    //	  MD(   pi2_hi, 0x3FF921FBuL,0x54442D18uL);/* top 53 bits of PI/2 */
    pi2_hi = longBitsToDouble((long)0x3FF921FB54442D18L);
//    printf("pi2_hi = " + pi2_hi);
    //	  MD(   pi2_lo, 0x3C91A626uL,0x33145C07uL);/* next 53 bits of PI/2*/
    pi2_lo = longBitsToDouble((long)0x3C91A62633145C07L);
//    printf("pi2_lo = " + pi2_lo);


    //	  MD(  pi2_lo2, 0xB91F1976uL,0xB7ED8FBCuL);/* next 53 bits of PI/2*/
    pi2_lo2 = longBitsToDouble((long)0xB91F1976B7ED8FBCL);
//    printf("pi2_lo2 = " + pi2_lo2);

    //	  MD( _2_pi_hi, 0x3FE45F30uL,0x6DC9C883uL);/* top 53 bits of 2/pi */
    _2_pi_hi = longBitsToDouble((long)0x3FE45F306DC9C883L);
//    printf("_2_pi_hi = " + _2_pi_hi);
    //	  MD( _2_pi_lo, 0xBC86B01EuL,0xC5417056uL);/* next 53 bits of 2/pi*/
    _2_pi_lo = longBitsToDouble((long)0xBC86B01EC5417056L);
//    printf("_2_pi_lo = " + _2_pi_lo);

    //>>>>>	  split(pi2_hi_hi,pi2_hi_lo,pi2_hi);
    double a1,a2;
    double xm;
    xm=pi2_hi;
//    printf("splitting: "+xm);
    long l_x1 = doubleToRawLongBits(xm);

    	//   <32> <20> <11> <1>
    	// sign
    int md_b_sign1 = (int) ((l_x1 >> 63) & 1);
    	// exponent:
    int xexp1 = (int)((l_x1 >> 52) & 0x7FF);
    int md_b_m21 = (int)(l_x1 & 0xFFFFFFFF);
    int md_b_m11 = (int)((l_x1 >> 31) & 0xFFFFF);
//    printf("raw="+l_x1);
//    printf("sign="+md_b_sign1);
//    printf("exp="+xexp1);
//    printf("exp (unbiased)="+(xexp1-IEEE_BIAS));
//    printf("m1="+md_b_m11);
//    printf("m2="+md_b_m21);

    // 	md.b.m2 &= 0xfc000000u;		\
    // md_b_m2 = (int)(l_x1 & 0xFFFFFFFF);
    l_x1 &= (long)0xFC000000L;
    a1 = longBitsToDouble(l_x1);
    // 	lo = (v) - hi;	/* bot 26 bits */
    a2 = xm - a1;

//    printf("in split: a1="+a1);
//    printf("in split: a2="+a2);
    pi2_hi_hi=a1;
    pi2_hi_lo=a2;

    //>>>>>	  split(pi2_lo_hi,pi2_lo_lo,pi2_lo);
    xm=pi2_lo;
//    printf("splitting: "+xm);
    // xm is a concrete value; no need to invoke the helper (pdinges)
    l_x1 = doubleToRawLongBits(xm);
    //l_x1 = MathSin.helperdoubleToRawBits(xm);
    	//   <32> <20> <11> <1>
    	// sign
    md_b_sign1 = (int) ((l_x1 >> 63) & 1);
    	// exponent:
    xexp1 = (int)((l_x1 >> 52) & 0x7FF);
    md_b_m21 = (int)(l_x1 & 0xFFFFFFFF);
    md_b_m11 = (int)((l_x1 >> 31) & 0xFFFFF);
//    printf("raw="+l_x1);
//    printf("sign="+md_b_sign1);
//    printf("exp="+xexp1);
//    printf("exp (unbiased)="+(xexp1-IEEE_BIAS));
//    printf("m1="+md_b_m11);
//    printf("m2="+md_b_m21);

    // 	md.b.m2 &= 0xfc000000u;		\
    // md_b_m2 = (int)(l_x1 & 0xFFFFFFFF);
    l_x1 &= (long)0xFC000000L;
    a1 = longBitsToDouble(l_x1);
    // 	lo = (v) - hi;	/* bot 26 bits */
    a2 = xm - a1;

//    printf("in split: a1="+a1);
//    printf("in split: a2="+a2);

    pi2_lo_hi=a1;
    pi2_lo_lo=a2;
    }
/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callMysin
 * Signature: (DLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callMysin
  (JNIEnv *env, jobject, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";

	MathSin();
	double retval;
	double x_org;
	double x2;

	int md_b_sign;
	int xexp;
	int sign=0;
	int md_b_m1;
	int md_b_m2;
	// convert into the different parts
	//

	// x is symbolic.  We have to call
	// {@code doubleToRawLongBits} via the helper to build
	// a {@code FunctionExpressio} in the
        // {@code ConcreteExecutionListener}.
	//long l_x = Double.doubleToRawLongBits(x);
    long l_x = helperdoubleToRawBits(x);
    //   <32> <20> <11> <1>
    // sign
    md_b_sign = (int) ((l_x >> 63) & 1);
    // exponent:
    xexp = (int)((l_x >> 52) & 0x7FF);
    int xexp0 = (int)((l_x >> 52) & 0x7FF);

    md_b_m2 = (int)(l_x & 0xFFFFFFFF);
    md_b_m1 = (int)((l_x >> 31) & 0xFFFFF);
//    printf("input=%lf",x);
//    printf("sign=%d",md_b_sign);
//    printf("exp=%d",xexp);
//    printf("exp_raw=%d",xexp0);
//    printf("exp (unbiased)=%d",(xexp-IEEE_BIAS));
//    printf("m1=%d",md_b_m1);
//    printf("m2=%d",md_b_m2);

//----------end-of-conversion------------
	if (bFile<<"node2 "<<IEEE_MAX-xexp<<" IEEE_MAX==xexp\n",IEEE_MAX == xexp){
		bFile<<"node3\n";
//        printf("NAN-on-INF");
        if((bFile<<"node4 "<<md_b_m1-0<<" md_b_m1>0\n",md_b_m1 >0) || (bFile<<"node4 "<<md_b_m2-0<<" md_b_m2>0\n",md_b_m2 >0)){
            bFile<<"node5\n";
//        	printf("unnormalized");
            retval = x;
        }else{
        	bFile<<"node6\n";
//            printf("NaN");
            unsigned long nan[2]={0xffffffff, 0x7fffffff};    // code representing a NaN
            retval = *(double*)nan;
        }
        bFile<<"node7\n";
	    return retval;
	}
	else if (bFile<<"node8 "<<0-xexp<<" 0==xexp\n",0 == xexp){
		bFile<<"node9\n";
//		printf("+-0, denormal");
	    if((bFile<<"node10 "<<md_b_m1-0<<" md_b_m1>0\n",md_b_m1 >0) || (bFile<<"node10 "<<md_b_m2-0<<" md_b_m2>0\n",md_b_m2 >0)){	/* denormal	*/
	    	bFile<<"node11\n";
//	    	printf("denormal");
		    x2 = x*x;	/* raise underflow		*/
		    bFile<<"node12\n";
		    return x - x2;	/* compute x		*/
		}
	    else{			/* +/-0.0		*/
	    	bFile<<"node13\n";
//	    	printf("+-0");
	    	bFile<<"node14\n";
		    return x;	/* => result is argument	*/
	    }
	}
    else if(bFile<<"node15 "<<xexp-(IEEE_BIAS-IEEE_MANT-2)<<" xexp<=(IEEE_BIAS-IEEE_MANT-2)\n",xexp <= (IEEE_BIAS - IEEE_MANT - 2) ){ /* very small;  */
    	bFile<<"node16\n";
//    	printf("very small");
    	bFile<<"node17\n";
		return x;
	}else if(bFile<<"node18 "<<xexp-(IEEE_BIAS-IEEE_MANT/4)<<" xexp<=(IEEE_BIAS-IEEE_MANT/4)\n",xexp <= (IEEE_BIAS - IEEE_MANT/4) ){ /* small */
		bFile<<"node19\n";
//		printf("small");
		bFile<<"node20\n";
		return x*(1.0-x*x*sixth);
			/* x**4 < epsilon of x        */
    }

	if (bFile<<"node21 "<<md_b_sign-1<<" md_b_sign==1\n",md_b_sign == 1){
		bFile<<"node22\n";
		x = -x;
		sign = 1;
	}
	bFile<<"node23\n";
	x_org = x;
//    printf("CURRENT\n\n");

	if (bFile<<"node24 "<<xexp-IEEE_BIAS<<" xexp<IEEE_BIAS\n",xexp < IEEE_BIAS){
		bFile<<"node25\n";
//		printf("less-than pi/2");
		;
	}else if (bFile<<"node26 "<<xexp-(IEEE_BIAS+IEEE_MANT)<<" xexp<=(IEEE_BIAS+IEEE_MANT)\n",xexp <= (IEEE_BIAS + IEEE_MANT)){
		bFile<<"node27\n";
//		printf("must bring into range...");
		double xm;
		double x3 =0.0;
		double x4 =0.0;
		double x5 =0.0;
		double x6 =0.0;
		double a1=0.0;
		double a2=0.0;
        int bot2;
        double xn_d;
        double md; // should be bit union

        xm = floor(x * _2_pi_hi + half);
//	    printf("xm (int) = %lf",xm);
        xn_d = xm + mag52;

//        printf("xn_d = %lf", xn_d);
        // C: bot2 = xn.b.m2 & 3u;
        // bot2 is the lower 3 bits of M2
        long l_xn = doubleToRawLongBits(xn_d);

        int xn_m2 = (int)(l_xn & 0xFFFFFFFF);
        bot2 = xn_m2 & 3;
//        printf("bot2 = %d", bot2);

        /*
         * Form xm * (pi/2) exactly by doing:
         *      (x3,x4) = xm * pi2_hi
         *      (x5,x6) = xm * pi2_lo
         */
        //>>>>>>>>>>>>>>>>>>>>>                split(a1,a2,xm);
//        printf("splitting: %lf",xm);
        long l_x1 = doubleToRawLongBits(xm);

        //   <32> <20> <11> <1>
        // sign
        int md_b_sign1 = (int) ((l_x1 >> 63) & 1);
        // exponent:
        int xexp1 = (int)((l_x1 >> 52) & 0x7FF);
        int md_b_m21 = (int)(l_x1 & 0xFFFFFFFF);
        int md_b_m11 = (int)((l_x1 >> 31) & 0xFFFFF);
//        printf("raw=%ld",l_x1);
//        printf("sign=%d",md_b_sign1);
//        printf("exp=%d",xexp1);
//        printf("exp (unbiased)=%d",(xexp1-IEEE_BIAS));
//        printf("m1=%d",md_b_m11);
//        printf("m2=%d",md_b_m21);

        // 	md.b.m2 &= 0xfc000000u;		\
        // md_b_m2 = (int)(l_x1 & 0xFFFFFFFF);
        l_x1 &= (long)0xFC000000L;
        a1 = longBitsToDouble(l_x1);
        // 	lo = (v) - hi;	/* bot 26 bits */
        a2 = xm - a1;

//        printf("in split: a1=%e",a1);
//        printf("in split: a2=%e",a2);

        //>>>>>>>>>>> exactmul2(x3,x4, xm,a1,a2, pi2_hi,pi2_hi_hi,pi2_hi_lo);
        x3 = (xm)*(pi2_hi);
	    x4 = (((a1*pi2_hi_hi-x3)+a1*pi2_hi_lo)+pi2_hi_hi*a2)+a2*pi2_hi_lo;;

        //>>>>>>>>>>>  exactmul2(x5,x6, xm,a1,a2, pi2_lo,pi2_lo_hi,pi2_lo_lo);
        x5 = (xm)*(pi2_lo);
        x6 = (((a1*pi2_lo_hi-x5)+a1*pi2_lo_lo)+pi2_lo_hi*a2)+a2*pi2_lo_lo;;
        x = ((((x - x3) - x4) - x5) - x6) - xm*pi2_lo2;

        //++++++++++++++++++++++++++++++++++++++++++++++

        if(bFile<<"node28 "<<bot2-0<<" bot2==0\n",bot2==0){
        	if (bFile<<"node29 "<<x-0.0<<" x<0.0\n",x < 0.0) {
        		bFile<<"node30\n";
                x = -x;
                //sign ^= 1;
                if (bFile<<"node31 "<<sign-1<<" sign==1\n",sign ==1){
                    bFile<<"node32\n";
                	sign = 0;
                }
                else{
                    bFile<<"node33\n";
                	sign = 1;
                }
            }
        }else if(bFile<<"node34 "<<bot2-1<<" bot2==1\n",bot2==1){
        	if(bFile<<"node35 "<<x-0.0<<" x<0.0\n",x < 0.0){
        		bFile<<"node36\n";
                x = pi2_hi + x;
            }else{
            	bFile<<"node37\n";
                x = pi2_hi - x;
            }
        }else if(bFile<<"node38 "<<bot2-2<<" bot2==2\n",bot2==2){
        	 if (bFile<<"node39 "<<x-0.0<<" x<0.0\n",x < 0.0) {
        		bFile<<"node40\n";
                x = -x;
            }else{
                //sign ^= 1;
                if (bFile<<"node41 "<<sign-1<<" sign==1\n",sign ==1){
                	bFile<<"node42\n";
                	sign = 0;
                }
                else{
                	bFile<<"node43\n";
                	sign = 1;
                }
            }
        }else if(bFile<<"node44 "<<bot2-3<<" bot2==3\n",bot2==3){
        	 // sign ^= 1;
            if (bFile<<"node45 "<<sign-1<<" sign==1\n",sign ==1){
            	bFile<<"node46\n";
            	sign = 0;
            }
            else{
            	bFile<<"node47\n";
            	sign = 1;
            }

            if(bFile<<"node48 "<<x-0.0<<" x<0.0\n",x < 0.0){
            	bFile<<"node49\n";
                x = pi2_hi + x;
            }else{
            	bFile<<"node50\n";
                x = pi2_hi - x;
            }
        }
    }else {
    	bFile<<"node51\n";
//	    printf("T_LOSS ");
	    retval = 0.0;
	    if (bFile<<"node52 "<<sign-1<<" sign==1\n",sign ==1){
	    	bFile<<"node53\n";
	    	retval = -retval;
	    }
	    bFile<<"node54\n";
	    return retval;
    }

//---------everything between 0..pi/2
	bFile<<"node55\n";
    x = x * _2_pi_hi;
    if (bFile<<"node56 "<<x-X_EPS<<" x>X_EPS\n",x > X_EPS){
    	bFile<<"node57\n";
//        printf("x > EPS");
        x2 = x*x;
        x *= ((((((((P)[0]*(x2) + (P)[1])*(x2) + (P)[2])*(x2) + (P)[3])*(x2) + (P)[4])*(x2) + (P)[5])*(x2) + (P)[6])*(x2) + (P)[7]);
    }else {
    	bFile<<"node58\n";
//        printf("x <= EPS");
        x *= pi2_hi;              /* x = x * (pi/2)               */
    }
    if (bFile<<"node59 "<<sign-1<<" sign==1\n",sign ==1){
    	bFile<<"node60\n";
    	x = -x;
    }
    bFile<<"node61\n";
//    printf("final return");
    bFile<<"node62\n";
    return x;
}
