/*
 * CallCPP.cpp
 *
 *  Created on: 2012-12-19
 *      Author: ChengXin
 */
#include <iostream>
#include <fstream>
#include <cmath>
#include <cstring>
#include <limits>
#include <stdlib.h>
#include <iomanip>
#include "../1-10/header/nr.h"
#include "cn_nju_seg_atg_callCPP_CallCPP.h"

using namespace std;

namespace {
	DP funcexp(DP funk(const DP), const DP x) {
		return funk(-log(x)) / x;
	}
	
	DP funcinf(DP funk(const DP), const DP x) {
		return funk(1.0 / x) / (x * x);
	}
	
	DP funcsql(DP funk(const DP), const DP aa, const DP x) {
		return 2.0 * x * funk(aa + x * x);
	}
	
	DP funcsqu(DP funk(const DP), const DP bb, const DP x) {
		return 2.0 * x * funk(bb - x * x);
	}

	DP funk(const DP x) {
		return 1.0 / sqrt(x);
	}
}

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

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBessi0
 * Signature: (DLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBessi0
  (JNIEnv *env, jobject, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

	bFile<<"node1\n";
	DP ax,ans,y;

	ax=fabs(x);

	if (bFile<<"node2 "<<(ax - 3.75)<<" ax<3.75\n", ax < 3.75) {
		bFile<<"node3\n";
		y=x/3.75;
		y*=y;
		ans=1.0+y*(3.5156229+y*(3.0899424+y*(1.2067492
			+y*(0.2659732+y*(0.360768e-1+y*0.45813e-2)))));
	} else {
		bFile<<"node4\n";
		y=3.75/ax;
		ans=(exp(ax)/sqrt(ax))*(0.39894228+y*(0.1328592e-1
			+y*(0.225319e-2+y*(-0.157565e-2+y*(0.916281e-2
			+y*(-0.2057706e-1+y*(0.2635537e-1+y*(-0.1647633e-1
			+y*0.392377e-2))))))));
	}
	bFile<<"node5\n";
	return ans;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBessi1
 * Signature: (DLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBessi1
  (JNIEnv *env, jobject, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";
	
	DP ax,ans,y;

	ax=fabs(x);

	if (bFile<<"node2 "<<(ax - 3.75)<<" ax<3.75\n", ax < 3.75) {
		bFile<<"node3\n";
		y=x/3.75;
		y*=y;
		ans=ax*(0.5+y*(0.87890594+y*(0.51498869+y*(0.15084934
			+y*(0.2658733e-1+y*(0.301532e-2+y*0.32411e-3))))));
	} else {
		bFile<<"node4\n";
		y=3.75/ax;
		ans=0.2282967e-1+y*(-0.2895312e-1+y*(0.1787654e-1
			-y*0.420059e-2));
		ans=0.39894228+y*(-0.3988024e-1+y*(-0.362018e-2
			+y*(0.163801e-2+y*(-0.1031555e-1+y*ans))));
		ans *= (exp(ax)/sqrt(ax));
	}
	bFile<<"node5\n";
	return x < 0.0 ? -ans : ans;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBessk0
 * Signature: (DLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBessk0
  (JNIEnv *env, jobject, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";
	
	DP y,ans;

	if (bFile<<"node2 "<<(x - 2.0)<<" x<=2.0\n", x <= 2.0) {
		bFile<<"node3\n";
		y=x*x/4.0;
		ans=(-log(x/2.0)*NR::bessi0(x))+(-0.57721566+y*(0.42278420
			+y*(0.23069756+y*(0.3488590e-1+y*(0.262698e-2
			+y*(0.10750e-3+y*0.74e-5))))));
	} else {
		bFile<<"node4\n";
		y=2.0/x;
		ans=(exp(-x)/sqrt(x))*(1.25331414+y*(-0.7832358e-1
			+y*(0.2189568e-1+y*(-0.1062446e-1+y*(0.587872e-2
			+y*(-0.251540e-2+y*0.53208e-3))))));
	}
	bFile<<"node5\n";
	return ans;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBessk1
 * Signature: (DLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBessk1
  (JNIEnv *env, jobject, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";
	
	DP y,ans;

	if (bFile<<"node2 "<<(x - 2.0)<<" x<=2.0\n", x <= 2.0) {
		bFile<<"node3\n";
		y=x*x/4.0;
		ans=(log(x/2.0)*NR::bessi1(x))+(1.0/x)*(1.0+y*(0.15443144
			+y*(-0.67278579+y*(-0.18156897+y*(-0.1919402e-1
			+y*(-0.110404e-2+y*(-0.4686e-4)))))));
	} else {
		bFile<<"node4\n";
		y=2.0/x;
		ans=(exp(-x)/sqrt(x))*(1.25331414+y*(0.23498619
			+y*(-0.3655620e-1+y*(0.1504268e-1+y*(-0.780353e-2
			+y*(0.325614e-2+y*(-0.68245e-3)))))));
	}
	bFile<<"node5\n";
	return ans;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBetai
 * Signature: (DDDLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBetai
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";
	
	DP bt, result;

	if ((bFile<<"node2 "<<(x - 0.0)<<" x<0.0\n", x < 0.0) || (bFile<<"node2 "<<(x - 1.0)<<" x>1.0\n", x > 1.0))
	{
		bFile<<"node3\n";
		printf("Bad x in routine betai");
	}
	else
	{
		if ((bFile<<"node4 "<<(x - 0.0)<<" x==0.0\n", x == 0.0) || (bFile<<"node4 "<<(x - 1.0)<<" x==1.0\n", x == 1.0))
		{
			bFile<<"node5\n";
			bt = 0.0;
		}
		else
		{
			bFile<<"node6\n";
			bt = exp(NR::gammln(a + b) - NR::gammln(a) - NR::gammln(b) + a * log(x) + b * log(1.0 - x));
		}
		if (bFile<<"node7 "<<(x - (a + 1.0) / (a + b + 2.0))<<" x<(a+1.0)/(a+b+2.0)\n", x < (a + 1.0) / (a + b + 2.0))
		{
			bFile<<"node8\n";
			result = bt * NR::betacf(a, b, x) / a;
		}
		else
		{
			bFile<<"node9\n";
			result = 1.0 - bt * NR::betacf(b, a, 1.0 - x) / b;
		}
	}

	bFile<<"node10\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callFlmoon
 * Signature: (IIIDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callFlmoon
  (JNIEnv *env, jobject arg, jint n, jint nph, jint jd, jdouble frac, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream fFile(path);
	fFile<<setiosflags(ios::scientific);
	fFile<<setprecision(16);
	fFile<<"node1\n";
	
	const DP RAD = 3.141592653589793238 / 180.0;
	int i;
	DP am, as, c, t, t2, xtra;

	c = n + nph / 4.0;
	t = c / 1236.85;
	t2 = t * t;
	as = 359.2242 + 29.105356 * c;
	am = 306.0253 + 385.816918 * c + 0.010730 * t2;
	jd = 2415020 + 28 * n + 7 * nph;
	xtra = 0.75933 + 1.53058868 * c + ((1.178e-4) - (1.55e-7) * t) * t2;
	if ((fFile<<"node2 "<<(nph - 0)<<" nph==0\n", nph == 0) || (fFile<<"node2 "<<(nph - 2)<<" nph==2\n", nph == 2))
	{
		fFile<<"node3\n";
		xtra += (0.1734 - 3.93e-4 * t) * sin(RAD * as) - 0.4068 * sin(RAD * am);
		i = int(xtra >= 0.0 ? floor(xtra) : ceil(xtra - 1.0));
		jd += i;
		frac = xtra - i;
	}
	else if ((fFile<<"node4 "<<(nph - 1)<<" nph==1\n", nph == 1) || (fFile<<"node4 "<<(nph - 3)<<" nph==3\n", nph == 3))
	{
		fFile<<"node5\n";
		xtra += (0.1721 - 4.0e-4 * t) * sin(RAD * as) - 0.6280 * sin(RAD * am);
		i = int(xtra >= 0.0 ? floor(xtra) : ceil(xtra - 1.0));
		jd += i;
		frac = xtra - i;
	}
	else
	{
		fFile<<"node6\n";
		printf("nph is unknown in flmoon");
	}

	fFile<<"node7\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callGammp
 * Signature: (DDLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callGammp
  (JNIEnv *env, jobject arg, jdouble a, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream gFile(path);
	gFile<<setiosflags(ios::scientific);
	gFile<<setprecision(16);
	gFile<<"node1\n";
	
	DP gamser, gammcf, gln, result;

	if ((gFile<<"node2 "<<(x - 0.0)<<" x<0.0\n", x < 0.0) || (gFile<<"node2 "<<(a - 0.0)<<" a<=0.0\n", a <= 0.0))
	{
		gFile<<"node3\n";
		printf("Invalid arguments in routine gammp");
	}
	else
	{
		if (gFile<<"node4 "<<(x - a - 1.0)<<" x<a+1.0\n", x < a + 1.0) {
			gFile<<"node5\n";
			NR::gser(gamser, a, x, gln);
			result = gamser;
		} else {
			gFile<<"node6\n";
			NR::gser(gammcf, a, x, gln);
			result = 1.0 - gammcf;
		}
	}

	gFile<<"node7\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callGammq
 * Signature: (DDLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callGammq
  (JNIEnv *env, jobject arg, jdouble a, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream gFile(path);
	gFile<<setiosflags(ios::scientific);
	gFile<<setprecision(16);
	gFile<<"node1\n";
	
	DP gamser, gammcf, gln, result;

	if ((gFile<<"node2 "<<(x - 0.0)<<" x<0.0\n", x < 0.0) || (gFile<<"node2 "<<(a - 0.0)<<" a<=0.0\n", a <= 0.0))
	{
		gFile<<"node3\n";
		printf("Invalid arguments in routine gammq");
	}
	else
	{
		if (gFile<<"node4 "<<(x - a - 1.0)<<" x<a+1.0\n", x < a + 1.0) {
			gFile<<"node5\n";
			NR::gser(gamser, a, x, gln);
			result = 1.0 - gamser;
		} else {
			gFile<<"node6\n";
			NR::gser(gammcf, a, x, gln);
			result = gammcf;
		}
	}

	gFile<<"node7\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callGcf
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callGcf
  (JNIEnv *env, jobject arg, jdouble gammcf, jdouble a, jdouble x, jdouble gln, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream gFile(path);
	gFile<<setiosflags(ios::scientific);
	gFile<<setprecision(16);
	gFile<<"node1\n";
	
	const int ITMAX = 100;
	const DP EPS = numeric_limits < DP > ::epsilon();
	const DP FPMIN = numeric_limits < DP > ::min() / EPS;
	int i;
	DP an, b, c, d, del, h;

	gln = NR::gammln(a);
	b = x + 1.0 - a;
	c = 1.0 / FPMIN;
	d = 1.0 / b;
	h = d;
	for (i = 1; gFile<<"node2 "<<(i - ITMAX)<<" i<=ITMAX\n", i <= ITMAX; i++) {
		gFile<<"node3\n";
		an = -i * (i - a);
		b += 2.0;
		d = an * d + b;
		if (gFile<<"node4 "<<(fabs(d) - FPMIN)<<" fabs(d)<FPMIN\n", fabs(d) < FPMIN)
		{
			gFile<<"node5\n";
			d = FPMIN;
		}
		gFile<<"node6\n";
		c = b + an / c;
		if (gFile<<"node7 "<<(fabs(c) - FPMIN)<<" fabs(c)<FPMIN\n", fabs(c) < FPMIN)
		{
			gFile<<"node8\n";
			c = FPMIN;
		}
		gFile<<"node9\n";
		d = 1.0 / d;
		del = d * c;
		h *= del;
		if (gFile<<"node10 "<<(fabs(del - 1.0) - EPS)<<" fabs(del - 1.0)<=EPS\n", fabs(del - 1.0) <= EPS)
		{
			gFile<<"node11\n";
			i = ITMAX + 1;
		}
	}
	if (gFile<<"node12 "<<(i - ITMAX)<<" i>ITMAX\n", i > ITMAX)
	{
		gFile<<"node13\n";
		printf("a too large, ITMAX too small in gcf");
	}
	gFile<<"node14\n";
	gammcf = exp(-x + a * log(x) - gln) * h;

	gFile<<"node15\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callGser
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callGser
  (JNIEnv *env, jobject arg, jdouble gamser, jdouble a, jdouble x, jdouble gln, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream gFile(path);
	gFile<<setiosflags(ios::scientific);
	gFile<<setprecision(16);
	gFile<<"node1\n";
	
	const int ITMAX = 100;
	const DP EPS = numeric_limits < DP > ::epsilon();
	int n;
	DP sum, del, ap;

	gln = NR::gammln(a);
	if (gFile<<"node2 "<<(x - 0.0)<<" x<=0.0\n", x <= 0.0) {
		if (gFile<<"node3 "<<(x - 0.0)<<" x<0.0\n", x < 0.0)
		{
			gFile<<"node4\n";
			printf("x less than 0 in routine gser");
		}
		else
		{
			gFile<<"node5\n";
			gamser = 0.0;
		}
	} else {
		gFile<<"node6\n";
		ap = a;
		del = sum = 1.0 / a;
		for (n = 0; gFile<<"node7 "<<(n - ITMAX)<<" n<ITMAX\n", n < ITMAX; n++) {
			gFile<<"node8\n";
			++ap;
			del *= x / ap;
			sum += del;
			if (gFile<<"node9 "<<(fabs(del) - fabs(sum) * EPS)<<" fabs(del)<fabs(sum)*EPS\n", fabs(del) < fabs(sum) * EPS) {
				gFile<<"node10\n";
				gamser = sum * exp(-x + a * log(x) - gln);
				n = ITMAX;
			}
		}
		gFile<<"node11\n";
		printf("a too large, ITMAX too small in routine gser");
	}

	gFile<<"node12\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callMidexp
 * Signature: (DDILjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callMidexp
  (JNIEnv *env, jobject arg, jdouble aa, jdouble bb, jint n, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream mFile(path);
	mFile<<setiosflags(ios::scientific);
	mFile<<setprecision(16);
	mFile<<"node1\n";
	
	DP result = 0.0;

	DP x, tnm, sum, del, ddel, a, b;
	static DP s;
	int it, j;

	b = exp(-aa);
	a = 0.0;
	if (mFile<<"node2 "<<(n - 1)<<" n==1\n", n == 1) {
		mFile<<"node3\n";
		result = (s = (b - a) * funcexp(funk, 0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; mFile<<"node4 "<<(j - n + 1)<<" j<n-1\n", j < n - 1; j++)
		{
			mFile<<"node5\n";
			it *= 3;
		}
		mFile<<"node6\n";
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; mFile<<"node7 "<<(j - it)<<" j<it\n", j < it; j++) {
			mFile<<"node8\n";
			sum += funcexp(funk, x);
			x += ddel;
			sum += funcexp(funk, x);
			x += del;
		}
		mFile<<"node9\n";
		s = (s + (b - a) * sum / tnm) / 3.0;
		result = s;
	}

	mFile<<"node10\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callMidinf
 * Signature: (DDILjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callMidinf
  (JNIEnv *env, jobject arg, jdouble aa, jdouble bb, jint n, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream mFile(path);
	mFile<<setiosflags(ios::scientific);
	mFile<<setprecision(16);
	mFile<<"node1\n";
	
	DP result = 0.0;

	DP x, tnm, sum, del, ddel, b, a;
	static DP s;
	int it, j;

	b = 1.0 / aa;
	a = 1.0 / bb;
	if (mFile<<"node2 "<<(n - 1)<<" n==1\n", n == 1) {
		mFile<<"node3\n";
		result = (s = (b - a) * funcinf(funk, 0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; mFile<<"node4 "<<(j - n + 1)<<" j<n-1\n", j < n - 1; j++)
		{
			mFile<<"node5\n";
			it *= 3;
		}
		mFile<<"node6\n";
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; mFile<<"node7 "<<(j - it)<<" j<it\n", j < it; j++) {
			mFile<<"node8\n";
			sum += funcinf(funk, x);
			x += ddel;
			sum += funcinf(funk, x);
			x += del;
		}
		mFile<<"node9\n";
		result = (s = (s + (b - a) * sum / tnm) / 3.0);
	}

	mFile<<"node10\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callMidpnt
 * Signature: (DDILjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callMidpnt
  (JNIEnv *env, jobject arg, jdouble a, jdouble b, jint n, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream mFile(path);
	mFile<<setiosflags(ios::scientific);
	mFile<<setprecision(16);
	mFile<<"node1\n";
	
	DP result = 0.0;

	int it,j;
	DP x,tnm,sum,del,ddel;
	static DP s;

	if (mFile<<"node2 "<<(n - 1)<<" n==1\n", n == 1) {
		mFile<<"node3\n";
		result = (s=(b-a)*funk(0.5*(a+b)));
	} else {
		for(it=1,j=1; mFile<<"node4 "<<(j - n + 1)<<" j<n-1\n", j<n-1;j++) 
		{
			mFile<<"node5\n";
			it *= 3;
		}
		mFile<<"node6\n";
		tnm=it;
		del=(b-a)/(3.0*tnm);
		ddel=del+del;
		x=a+0.5*del;
		sum=0.0;
		for (j=0; mFile<<"node7 "<<(j - it)<<" j<it\n", j<it;j++) {
			mFile<<"node8\n";
			sum += funk(x);
			x += ddel;
			sum += funk(x);
			x += del;
		}
		mFile<<"node9\n";
		s=(s+(b-a)*sum/tnm)/3.0;
		result = s;
	}
	mFile<<"node10\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callMidsql
 * Signature: (DDILjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callMidsql
  (JNIEnv *env, jobject arg, jdouble aa, jdouble bb, jint n, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream mFile(path);
	mFile<<setiosflags(ios::scientific);
	mFile<<setprecision(16);
	mFile<<"node1\n";
	
	DP result = 0.0;

	DP x, tnm, sum, del, ddel, a, b;
	static DP s;
	int it, j;

	b = sqrt(bb - aa);
	a = 0.0;
	if (mFile<<"node2 "<<(n - 1)<<" n==1\n", n == 1) {
		mFile<<"node3\n";
		result = (s = (b - a) * funcsql(funk, aa, 0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; mFile<<"node4 "<<(j - n + 1)<<" j<n-1\n", j < n - 1; j++)
		{
			mFile<<"node5\n";
			it *= 3;
		}
		mFile<<"node6\n";
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; mFile<<"node7 "<<(j - it)<<" j<it\n", j < it; j++) {
			mFile<<"node8\n";
			sum += funcsql(funk, aa, x);
			x += ddel;
			sum += funcsql(funk, aa, x);
			x += del;
		}
		mFile<<"node9\n";
		s = (s + (b - a) * sum / tnm) / 3.0;
		result = s;
	}

	mFile<<"node10\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callMidsqu
 * Signature: (DDILjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callMidsqu
  (JNIEnv *env, jobject arg, jdouble aa, jdouble bb, jint n, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream mFile(path);
	mFile<<setiosflags(ios::scientific);
	mFile<<setprecision(16);
	mFile<<"node1\n";
	
	DP result = 0.0;

	DP x, tnm, sum, del, ddel, a, b;
	static DP s;
	int it, j;

	b = sqrt(bb - aa);
	a = 0.0;
	if (mFile<<"node2 "<<(n - 1)<<" n==1\n", n == 1) {
		mFile<<"node3\n";
		result = (s = (b - a) * funcsqu(funk, bb, 0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; mFile<<"node4 "<<(j - n + 1)<<" j<n-1\n", j < n - 1; j++)
		{
			mFile<<"node5\n";
			it *= 3;
		}
		mFile<<"node6\n";
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; mFile<<"node7 "<<(j - it)<<" j<it\n", j < it; j++) {
			mFile<<"node8\n";
			sum += funcsqu(funk, bb, x);
			x += ddel;
			sum += funcsqu(funk, bb, x);
			x += del;
		}
		mFile<<"node9\n";
		s = (s + (b - a) * sum / tnm) / 3.0;
		result = s;
	}

	mFile<<"node10\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callProbks
 * Signature: (DLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callProbks
  (JNIEnv *env, jobject arg, jdouble alam, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream pFile(path);
	pFile<<setiosflags(ios::scientific);
	pFile<<setprecision(16);
	pFile<<"node1\n";
	
	const DP EPS1 = 1.0e-6, EPS2 = 1.0e-16;
	int j;
	DP a2, fac = 2.0, sum = 0.0, term, termbf = 0.0, result = 1.0;

	a2 = -2.0 * alam * alam;
	for (j = 1; pFile<<"node2 "<<(j - 100)<<" j<=100\n", j <= 100; j++) {
		pFile<<"node3\n";
		term = fac * exp(a2 * j * j);
		sum += term;
		if ((pFile<<"node4 "<<(fabs(term) - EPS1 * termbf)<<" fabs(term)<=EPS1*termbf\n", fabs(term) <= EPS1 * termbf) || (pFile<<"node4 "<<(fabs(term) - EPS2 * sum)<<" fabs(term)<=EPS2*sum\n", fabs(term) <= EPS2 * sum))
		{
			pFile<<"node5\n";
			result = sum;
		}
		pFile<<"node6\n";
		fac = -fac;
		termbf = fabs(term);
	}
	pFile<<"node7\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callPythag
 * Signature: (DDLjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callPythag
  (JNIEnv *env, jobject arg, jdouble a, jdouble b, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream pFile(path);
	pFile<<setiosflags(ios::scientific);
	pFile<<setprecision(16);
	pFile<<"node1\n";
	
	DP absa, absb, result;

	absa = fabs(a);
	absb = fabs(b);
	if (pFile<<"node2 "<<(absa - absb)<<" absa>absb\n", absa > absb)
	{
		pFile<<"node3\n";
		result = absa * sqrt(1.0 + SQR(absb / absa));
	}
	else
	{
		pFile<<"node4\n";
		result = (absb == 0.0 ? 0.0 : absb * sqrt(1.0 + SQR(absa / absb)));
	}

	pFile<<"node5\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callRan0
 * Signature: (ILjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callRan0
  (JNIEnv *env, jobject arg, jint idum, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream rFile(path);
	rFile<<setiosflags(ios::scientific);
	rFile<<setprecision(16);
	rFile<<"node1\n";
	
	const int IA = 16807, IM = 2147483647, IQ = 127773;
	const int IR = 2836, MASK = 123459876;
	const DP AM = 1.0 / DP(IM);
	int k;
	DP ans;

	idum ^= MASK;
	k = idum / IQ;
	idum = IA * (idum - k * IQ) - IR * k;
	if (rFile<<"node2 "<<(idum - 0)<<" idum<0\n", idum < 0)
	{
		rFile<<"node3\n";
		idum += IM;
	}
	rFile<<"node4\n";
	ans = AM * idum;
	idum ^= MASK;
	
	rFile<<"node5\n";
	return ans;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callSphbes
 * Signature: (IDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callSphbes
  (JNIEnv *env, jobject arg, jint n, jdouble x, jdouble sj, jdouble sy, jdouble sjp, jdouble syp, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream sFile(path);
	sFile<<setiosflags(ios::scientific);
	sFile<<setprecision(16);
	sFile<<"node1\n";
	
	const DP RTPIO2 = 1.253314137315500251;
	DP factor, order, rj, rjp, ry, ryp;

	if ((sFile<<"node2 "<<(n - 0)<<" n<0\n", n < 0) || (sFile<<"node2 "<<(x - 0.0)<<" x<=0.0\n", x <= 0.0))
	{
		sFile<<"node3\n";
		printf("bad arguments in sphbes");
	}
	else
	{
		sFile<<"node4\n";
		order = n + 0.5;
		NR::bessjy(x, order, rj, ry, rjp, ryp);
		factor = RTPIO2 / sqrt(x);
		sj = factor * rj;
		sy = factor * ry;
		sjp = factor * rjp - sj / (2.0 * x);
		syp = factor * ryp - sy / (2.0 * x);
	}

	sFile<<"node5\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callTrapzd
 * Signature: (DDILjava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTrapzd
  (JNIEnv *env, jobject arg, jdouble a, jdouble b, jint n, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream tFile(path);
	tFile<<setiosflags(ios::scientific);
	tFile<<setprecision(16);
	tFile<<"node1\n";
	
	DP result = 0.0;

	DP x, tnm, sum, del;
	static DP s;
	int it, j;

	if (tFile<<"node2 "<<(n - 1)<<" n==1\n", n == 1) {
		tFile<<"node3\n";
		result = (s = 0.5 * (b - a) * (funk(a) + funk(b)));
	} else {
		for (it = 1, j = 1; tFile<<"node4 "<<(j - n + 1)<<" j<n-1\n", j < n - 1; j++)
		{
			tFile<<"node5\n";
			it <<= 1;
		}
		tFile<<"node6\n";
		tnm = it;
		del = (b - a) / tnm;
		x = a + 0.5 * del;
		for (sum = 0.0, j = 0; tFile<<"node7 "<<(j - it)<<" j<it\n", j < it; j++, x += del)
		{
			tFile<<"node8\n";
			sum += funk(x);
		}
		tFile<<"node9\n";
		s = 0.5 * (s + (b - a) * sum / tnm);
		result = s;
	}

	tFile<<"node10\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callZbrac
 * Signature: (DDLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callZbrac
  (JNIEnv *env, jobject arg, jdouble x1, jdouble x2, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream zFile(path);
	zFile<<setiosflags(ios::scientific);
	zFile<<setprecision(16);
	zFile<<"node1\n";
	
	bool result = false;

	const int NTRY = 50;
	const DP FACTOR = 1.6;
	int j;
	DP f1, f2;

	if (zFile<<"node2 "<<(x1 - x2)<<" x1==x2\n", x1 == x2)
	{
		zFile<<"node3\n";
		printf("Bad initial range in zbrac");
	}
	else {
		zFile<<"node4\n";
		f1 = funk(x1);
		f2 = funk(x2);
		for (j = 0; zFile<<"node5 "<<(j - NTRY)<<" j<NTRY\n", j < NTRY; j++) {
			if (zFile<<"node6 "<<(f1 * f2 - 0.0)<<" f1*f2<0.0\n", f1 * f2 < 0.0)
			{
				zFile<<"node7\n";
				result = true;
			}
			else {
				if (zFile<<"node8 "<<(fabs(f1) - fabs(f2))<<" fabs(f1)<fabs(f2)\n", fabs(f1) < fabs(f2))
				{
					zFile<<"node9\n";
					f1 = funk(x1 += FACTOR * (x1 - x2));
				}
				else
				{
					zFile<<"node10\n";
					f2 = funk(x2 += FACTOR * (x2 - x1));
				}
			}
		}
	}
	
	zFile<<"node11\n";
	return result;
}

