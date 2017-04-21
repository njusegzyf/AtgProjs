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
#include <iomanip>
#include "../51_500/header/nr.h"
#include "cn_nju_seg_atg_callCPP_CallCPP.h"

using namespace std;

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

namespace {
	inline void shft3(DP &a, DP &b, DP &c, const DP d) {
		a = b;
		b = c;
		c = d;
	}

	inline DP f(const DP x) {
		return (x - 10) * (x - 20) - 30;
	}
	
	inline DP func(const DP x) {
		return (x - 10) * (x - 20) - 30;
	}

	inline void mov3(DP &a, DP &b, DP &c, const DP d, const DP e, const DP f)
	{
		a=d; b=e; c=f;
	}

	inline DP f0(const DP x)
	{
		return NR::bessj0(x);
	}

	inline DP f1(const DP x)
	{
		return -NR::bessj1(x);
	}
}

void beschb(const DP x, DP &gam1, DP &gam2, DP &gampl, DP &gammi);
DP chebev(const DP a, const DP b, Vec_I_DP &c, const int m, const DP x);

void beschb(const DP x, DP &gam1, DP &gam2, DP &gampl, DP &gammi)
{
	const int NUSE1=7, NUSE2=8;
	static const DP c1_d[7] = {
		-1.142022680371168e0,6.5165112670737e-3,
		3.087090173086e-4,-3.4706269649e-6,6.9437664e-9,
		3.67795e-11,-1.356e-13};
	static const DP c2_d[8] = {
		1.843740587300905e0,-7.68528408447867e-2,
		1.2719271366546e-3,-4.9717367042e-6,-3.31261198e-8,
		2.423096e-10,-1.702e-13,-1.49e-15};
	DP xx;
	static Vec_DP c1(c1_d,7),c2(c2_d,8);

	xx=8.0*x*x-1.0;
	gam1=chebev(-1.0,1.0,c1,NUSE1,xx);
	gam2=chebev(-1.0,1.0,c2,NUSE2,xx);
	gampl= gam2-x*gam1;
	gammi= gam2+x*gam1;
}

DP chebev(const DP a, const DP b, Vec_I_DP &c, const int m, const DP x)
{
	DP d=0.0,dd=0.0,sv,y,y2;
	int j;

	if ((x-a)*(x-b) > 0.0) {
		//printf("x not in range in routine chebev");
		return -1;
	}
	y2=2.0*(y=(2.0*x-a-b)/(b-a));
	for (j=m-1;j>0;j--) {
		sv=d;
		d=y2*d-dd+c[j];
		dd=sv;
	}
	return y*d-dd+0.5*c[0];
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
 * Method:    callBessik
 * Signature: (DDDDDD)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBessik
  (JNIEnv *env, jobject arg, jdouble x, jdouble xnu, jdouble ri, jdouble rk, jdouble rip, jdouble rkp, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	bFile<<"node1\n";
	 
	const int MAXIT = 10000;
	const DP EPS = numeric_limits < DP > ::epsilon();
	const DP FPMIN = numeric_limits < DP > ::min() / EPS;
	const DP XMIN = 2.0, PI = 3.141592653589793;
	DP a, a1, b, c, d, del, del1, delh, dels, e, f, fact, fact2, ff, gam1, gam2,
			gammi, gampl, h, p, pimu, q, q1, q2, qnew, ril, ril1, rimu, rip1,
			ripl, ritemp, rk1, rkmu, rkmup, rktemp, s, sum, sum1, x2, xi, xi2,
			xmu, xmu2;
	int i, l, nl;

	if ((bFile<<"node2 "<<(x-0.0)<<" x<=0.0\n", x <= 0.0) || (bFile<<"node2 "<<(xnu-0.0)<<" xnu<0.0\n", xnu < 0.0)) {
		bFile<<"node3\n";
		//printf("bad arguments in bessik");
	}
	else {
		bFile<<"node4\n";
		nl = int(xnu + 0.5);
		xmu = xnu - nl;
		xmu2 = xmu * xmu;
		xi = 1.0 / x;
		xi2 = 2.0 * xi;
		h = xnu * xi;
		if (bFile<<"node5 "<<(h-FPMIN)<<" h<FPMIN\n", h < FPMIN) {
			bFile<<"node6\n";
			h = FPMIN;
		}
		bFile<<"node7\n";
		b = xi2 * xnu;
		d = 0.0;
		c = h;
		for (i = 0; bFile<<"node8 "<<(i-MAXIT)<<" i<MAXIT\n", i < MAXIT; i++) {
			bFile<<"node9\n";
			b += xi2;
			d = 1.0 / (b + d);
			c = b + 1.0 / c;
			del = c * d;
			h = del * h;
			if (bFile<<"node10 "<<(fabs(del - 1.0) - EPS)<<" fabs(del-1.0)<=EPS\n", fabs(del - 1.0) <= EPS) {
				bFile<<"node11\n";
				i = MAXIT;
			}
		}
		if (bFile<<"node12 "<<(i-MAXIT)<<" i>=MAXIT\n", i >= MAXIT) {
			bFile<<"node13\n";
			//printf("x too large in bessik; try asymptotic expansion");
		}
		else {
			bFile<<"node14\n";
			ril = FPMIN;
			ripl = h * ril;
			ril1 = ril;
			rip1 = ripl;
			fact = xnu * xi;
			for (l = nl - 1; bFile<<"node15 "<<(l-0)<<" l>=0\n", l >= 0; l--) {
				bFile<<"node16\n";
				ritemp = fact * ril + ripl;
				fact -= xi;
				ripl = fact * ritemp + ril;
				ril = ritemp;
			}
			bFile<<"node17\n";
			f = ripl / ril;
			if (bFile<<"node18 "<<(x-XMIN)<<" x<XMIN\n", x < XMIN) {
				bFile<<"node19\n";
				x2 = 0.5 * x;
				pimu = PI * xmu;
				fact = (fabs(pimu) < EPS ? 1.0 : pimu / sin(pimu));
				d = -log(x2);
				e = xmu * d;
				fact2 = (fabs(e) < EPS ? 1.0 : sinh(e) / e);
				beschb(xmu, gam1, gam2, gampl, gammi);
				ff = fact * (gam1 * cosh(e) + gam2 * fact2 * d);
				sum = ff;
				e = exp(e);
				p = 0.5 * e / gampl;
				q = 0.5 / (e * gammi);
				c = 1.0;
				d = x2 * x2;
				sum1 = p;
				for (i = 1; bFile<<"node20 "<<(i-MAXIT)<<" i<=MAXIT\n", i <= MAXIT; i++) {
					bFile<<"node21\n";
					ff = (i * ff + p + q) / (i * i - xmu2);
					c *= (d / i);
					p /= (i - xmu);
					q /= (i + xmu);
					del = c * ff;
					sum += del;
					del1 = c * (p - i * ff);
					sum1 += del1;
					if (bFile<<"node22 "<<(fabs(del) - fabs(sum) * EPS)<<" fabs(del)<fabs(sum)*EPS\n", fabs(del) < fabs(sum) * EPS) {
						bFile<<"node23\n";
						i = MAXIT;
					}
				}
				if (bFile<<"node24 "<<(i-MAXIT)<<" i>MAXIT\n", i > MAXIT) {
					bFile<<"node25\n";
					//printf("bessk series failed to converge");
				}
				else {
					bFile<<"node26\n";
					rkmu = sum;
					rk1 = sum1 * xi2;
				}
			} else {
				bFile<<"node27\n";
				b = 2.0 * (1.0 + x);
				d = 1.0 / b;
				h = delh = d;
				q1 = 0.0;
				q2 = 1.0;
				a1 = 0.25 - xmu2;
				q = c = a1;
				a = -a1;
				s = 1.0 + q * delh;
				for (i = 1; bFile<<"node28 "<<(i-MAXIT)<<" i<MAXIT\n", i < MAXIT; i++) {
					bFile<<"node29\n";
					a -= 2 * i;
					c = -a * c / (i + 1.0);
					qnew = (q1 - b * q2) / a;
					q1 = q2;
					q2 = qnew;
					q += c * qnew;
					b += 2.0;
					d = 1.0 / (b + a * d);
					delh = (b * d - 1.0) * delh;
					h += delh;
					dels = q * delh;
					s += dels;
					if (bFile<<"node30 "<<(fabs(dels / s) - EPS)<<" fabs(dels/s)<=EPS\n", fabs(dels / s) <= EPS) {
						bFile<<"node31\n";
						i = MAXIT;
					}
				}
				if (bFile<<"node32 "<<(i-MAXIT)<<" i>=MAXIT\n", i >= MAXIT) {
					bFile<<"node33\n";
					//printf("bessik: failure to converge in cf2");
				}
				else {
					bFile<<"node34\n";
					h = a1 * h;
					rkmu = sqrt(PI / (2.0 * x)) * exp(-x) / s;
					rk1 = rkmu * (xmu + x + 0.5 - h) * xi;
				}
			}
			bFile<<"node35\n";
			rkmup = xmu * xi * rkmu - rk1;
			rimu = xi / (f * rkmu - rkmup);
			ri = (rimu * ril1) / ril;
			rip = (rimu * rip1) / ril;
			for (i = 1; bFile<<"node36 "<<(i - nl)<<" i<=nl\n", i <= nl; i++) {
				bFile<<"node37\n";
				rktemp = (xmu + i) * xi2 * rk1 + rkmu;
				rkmu = rk1;
				rk1 = rktemp;
			}
			bFile<<"node38\n";
			rk = rkmu;
			rkp = xnu * xi * rkmu - rk1;
		}
	}

	bFile<<"node39\n";
	return;
 }
 
 /*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBetacf
 * Signature: (DDD)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBetacf
  (JNIEnv *env, jobject arg, jdouble a, jdouble b, jdouble x, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	//eFile<<setiosflags(ios::fixed);
	bFile<<setprecision(16);

	bFile<<"node1\n";
	const int MAXIT = 100;
	const DP EPS = numeric_limits < DP > ::epsilon();
	const DP FPMIN = numeric_limits < DP > ::min() / EPS;
	int m, m2;
	DP aa, c, d, del, h, qab, qam, qap;

	qab = a + b;
	qap = a + 1.0;
	qam = a - 1.0;
	c = 1.0;
	d = 1.0 - qab * x / qap;
	if (bFile<<"node2 "<<(fabs(d) - FPMIN)<<" fabs(d)<FPMIN\n", fabs(d) < FPMIN) {
		bFile<<"node3\n";
		d = FPMIN;
	}
	bFile<<"node4\n";
	d = 1.0 / d;
	h = d;
	for (m = 1; bFile<<"node5 "<<(m-MAXIT)<<" m<=MAXIT\n", m <= MAXIT; m++) {
		bFile<<"node6\n";
		m2 = 2 * m;
		aa = m * (b - m) * x / ((qam + m2) * (a + m2));
		d = 1.0 + aa * d;
		if (bFile<<"node7 "<<(fabs(d) - FPMIN)<<" fabs(d)<FPMIN\n", fabs(d) < FPMIN) {
			bFile<<"node8\n";
			d = FPMIN;
		}
		bFile<<"node9\n";
		c = 1.0 + aa / c;
		if (bFile<<"node10 "<<(fabs(c) - FPMIN)<<" fabs(c)<FPMIN\n", fabs(c) < FPMIN) {
			bFile<<"node11\n";
			c = FPMIN;
		}
		bFile<<"node12\n";
		d = 1.0 / d;
		h *= d * c;
		aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
		d = 1.0 + aa * d;
		if (bFile<<"node13 "<<(fabs(d) - FPMIN)<<" fabs(d)<FPMIN\n", fabs(d) < FPMIN) {
			bFile<<"node14\n";
			d = FPMIN;
		}
		bFile<<"node15\n";
		c = 1.0 + aa / c;
		if (bFile<<"node16 "<<(fabs(c) - FPMIN)<<" fabs(c)<FPMIN\n", fabs(c) < FPMIN) {
			bFile<<"node17\n";
			c = FPMIN;
		}
		bFile<<"node18\n";
		d = 1.0 / d;
		del = d * c;
		h *= del;
		if (bFile<<"node19 "<<(fabs(del - 1.0) - EPS)<<" fabs(del-1.0)<=EPS\n", fabs(del - 1.0) <= EPS) {
			bFile<<"node20\n";
			m=MAXIT+1;
		}
	}
	if (bFile<<"node21 "<<(m - MAXIT)<<" m>MAXIT\n", m > MAXIT) {
		bFile<<"node22\n";
	}
	bFile<<"node23\n";
	return h;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBrent
 * Signature: (DDDD)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBrent
  (JNIEnv *env, jobject arg, jdouble ax, jdouble bx, jdouble cx, jdouble tol, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

	bFile<<"node1\n";
	DP result;
	const int ITMAX = 100;
	const DP CGOLD = 0.3819660;
	const DP ZEPS = numeric_limits < DP > ::epsilon() * 1.0e-3;
	int iter;
	DP a, b, d = 0.0, etemp, fu, fv, fw, fx;
	DP p, q, r, tol1, tol2, u, v, w, x, xm;
	DP e = 0.0;

	a = (ax < cx ? ax : cx);
	b = (ax > cx ? ax : cx);
	x = w = v = bx;
	result = fw = fv = fx = f(x);
	for (iter = 0; bFile<<"node2 "<<(iter - ITMAX)<<" iter<ITMAX\n", iter < ITMAX; iter++) {
		bFile<<"node3\n";
		xm = 0.5 * (a + b);
		tol2 = 2.0 * (tol1 = tol * fabs(x) + ZEPS);
		if (bFile<<"node4 "<<(fabs(x - xm) - (tol2 - 0.5 * (b - a)))<<" fabs(x-xm)<=(tol2-0.5*(b-a))\n", fabs(x - xm) <= (tol2 - 0.5 * (b - a))) {
			bFile<<"node5\n";
			result = fx;
		} else {
			if (bFile<<"node6 "<<(fabs(e) - tol1)<<" fabs(e)>tol1\n", fabs(e) > tol1) {
				bFile<<"node7\n";
				r = (x - w) * (fx - fv);
				q = (x - v) * (fx - fw);
				p = (x - v) * q - (x - w) * r;
				q = 2.0 * (q - r);
				if (bFile<<"node8 "<<(q-0.0)<<" q>0.0\n", q > 0.0) {
					bFile<<"node9\n";
					p = -p;
				}
				bFile<<"node10\n";
				q = fabs(q);
				etemp = e;
				e = d;
				if ((bFile<<"node11 "<<(fabs(p) - fabs(0.5 * q * etemp))<<" fabs(p)>=fabs(0.5*q*etemp)\n", fabs(p) >= fabs(0.5 * q * etemp)) || (bFile<<"node11 "<<(p - q * (a - x))<<" p<=q*(a-x)\n", p <= q * (a - x)) || (bFile<<"node11 "<<(p - q * (b - x))<<" p>=q*(b-x)\n", p >= q * (b - x))) {
					bFile<<"node12\n";
					d = CGOLD * (e = (x >= xm ? a - x : b - x));
				}
				else {
					bFile<<"node13\n";
					d = p / q;
					u = x + d;
					if ((bFile<<"node14 "<<(u - a - tol2)<<" u-a<tol2\n", u - a < tol2) || (bFile<<"node14 "<<(b - u - tol2)<<" b-u<tol2\n", b - u < tol2)) {
						bFile<<"node15\n";
						d = SIGN(tol1, xm - x);
					}
				}
			} else {
				bFile<<"node16\n";
				d = CGOLD * (e = (x >= xm ? a - x : b - x));
			}
			bFile<<"node17\n";
			u = (fabs(d) >= tol1 ? x + d : x + SIGN(tol1, d));
			fu = f(u);
			if (bFile<<"node18 "<<(fu - fx)<<" fu<=fx\n", fu <= fx) {
				if (bFile<<"node19 "<<(u - x)<<" u>=x\n", u >= x) {
					bFile<<"node20\n";
					a = x;
				}
				else {
					bFile<<"node21\n";
					b = x;
				}
				bFile<<"node22\n";
				shft3(v, w, x, u);
				shft3(fv, fw, fx, fu);
			} else {
				if (bFile<<"node23 "<<(u - x)<<" u<x\n", u < x) {
					bFile<<"node24\n";
					a = u;
				}
				else {
					bFile<<"node25\n";
					b = u;
				}
				if ((bFile<<"node26 "<<(fu - fw)<<" fu<=fw\n", fu <= fw) || (bFile<<"node26 "<<(w - x)<<" w==x\n", w == x)) {
					bFile<<"node27\n";
					v = w;
					w = u;
					fv = fw;
					fw = fu;
				} else if ((bFile<<"node28 "<<(fu - fv)<<" fu<=fv\n", fu <= fv) || (bFile<<"node28 "<<(v - x)<<" v==x\n", v == x) || (bFile<<"node28 "<<(v - w)<<" v==w\n", v == w)) {
					bFile<<"node29\n";
					v = u;
					fv = fu;
				}
			}
		}
	}
	bFile<<"node30\n";
	return fx;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callZbrent
 * Signature: (DDD)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callZbrent
  (JNIEnv *env, jobject arg, jdouble x1, jdouble x2, jdouble tol, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream zFile(path);
	zFile<<setiosflags(ios::scientific);
	zFile<<setprecision(16);
	zFile<<"node1\n";
	
	DP result = 0.0;

	const int ITMAX = 100;
	const DP EPS = numeric_limits < DP > ::epsilon();
	int iter;
	DP a = x1, b = x2, c = x2, d, e, min1, min2;
	DP fa = func(a), fb = func(b), fc, p, q, r, s, tol1, xm;

	if (((zFile<<"node2 "<<(fa - 0.0)<<" fa>0.0\n", fa > 0.0) && (zFile<<"node2 "<<(fb - 0.0)<<" fb>0.0\n", fb > 0.0)) || ((zFile<<"node2 "<<(fa - 0.0)<<" fa<0.0\n", fa < 0.0) && (zFile<<"node2 "<<(fb - 0.0)<<" fb<0.0\n", fb < 0.0))) {
		zFile<<"node3\n";
		//printf("Root must be bracketed in zbrent\n");
	}
	else {
		zFile<<"node4\n";
		fc = fb;
		for (iter = 0; (zFile<<"node5 "<<(iter - ITMAX)<<" iter<ITMAX\n", iter < ITMAX); iter++) {
			if (((zFile<<"node6 "<<(fb - 0.0)<<" fb>0.0\n", fb > 0.0) && (zFile<<"node6 "<<(fc - 0.0)<<" fc>0.0\n", fc > 0.0)) || ((zFile<<"node6 "<<(fb - 0.0)<<" fb<0.0\n", fb < 0.0) && (zFile<<"node6 "<<(fc - 0.0)<<" fc<0.0\n", fc < 0.0))) {
				zFile<<"node7\n";
				c = a;
				fc = fa;
				e = d = b - a;
			}
			if (zFile<<"node8 "<<(fabs(fc) - fabs(fb))<<" fabs(fc)<fabs(fb)\n", fabs(fc) < fabs(fb)) {
				zFile<<"node9\n";
				a = b;
				b = c;
				c = a;
				fa = fb;
				fb = fc;
				fc = fa;
			}
			zFile<<"node10\n";
			tol1 = 2.0 * EPS * fabs(b) + 0.5 * tol;
			xm = 0.5 * (c - b);
			if ((zFile<<"node11 "<<(fabs(xm) - tol1)<<" fabs(xm)<=tol1\n", fabs(xm) <= tol1) || (zFile<<"node11 "<<(fb - 0.0)<<" fb==0.0\n", fb == 0.0)) {
				zFile<<"node12\n";
				result = b;
				iter = ITMAX;
			} else {
				if ((zFile<<"node13 "<<(fabs(e) - tol1)<<" fabs(e)>=tol1\n", fabs(e) >= tol1) && (zFile<<"node13 "<<(fabs(fa) - fabs(fb))<<" fabs(fa)>fabs(fb)\n", fabs(fa) > fabs(fb))) {
					zFile<<"node14\n";
					s = fb / fa;
					if (zFile<<"node15 "<<(a - c)<<" a==c\n", a == c) {
						zFile<<"node16\n";
						p = 2.0 * xm * s;
						q = 1.0 - s;
					} else {
						zFile<<"node17\n";
						q = fa / fc;
						r = fb / fc;
						p = s * (2.0 * xm * q * (q - r) - (b - a) * (r - 1.0));
						q = (q - 1.0) * (r - 1.0) * (s - 1.0);
					}
					if (zFile<<"node18 "<<(p - 0.0)<<" p>0.0\n", p > 0.0) {
						zFile<<"node19\n";
						q = -q;
					}
					zFile<<"node20\n";
					p = fabs(p);
					min1 = 3.0 * xm * q - fabs(tol1 * q);
					min2 = fabs(e * q);
					if (zFile<<"node21 "<<(2.0 * p - (min1 < min2 ? min1 : min2))<<" 2.0*p<(min1<min2?min1:min2)\n", 2.0 * p < (min1 < min2 ? min1 : min2)) {
						zFile<<"node22\n";
						e = d;
						d = p / q;
					} else {
						zFile<<"node23\n";
						d = xm;
						e = d;
					}
				} else {
					zFile<<"node24\n";
					d = xm;
					e = d;
				}
				zFile<<"node25\n";
				a = b;
				fa = fb;
				if (zFile<<"node26 "<<(fabs(d) - tol1)<<" fabs(d)>tol1\n", fabs(d) > tol1) {
					zFile<<"node27\n";
					b += d;
				}
				else {
					zFile<<"node28\n";
					b += SIGN(tol1, xm);
				}
				zFile<<"node29\n";
				fb = func(b);
			}
		}
	}

	zFile<<"node30\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callRan2
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callRan2
  (JNIEnv *env, jobject arg, jint idum, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream rFile(path);
	rFile<<"node1\n";
	DP result = 0.0;
	const int IM1 = 2147483563, IM2 = 2147483399;
	const int IA1 = 40014, IA2 = 40692, IQ1 = 53668, IQ2 = 52774;
	const int IR1 = 12211, IR2 = 3791, NTAB = 32, IMM1 = IM1 - 1;
	const int NDIV = 1 + IMM1 / NTAB;
	const DP EPS = 3.0e-16, RNMX = 1.0 - EPS, AM = 1.0 / DP(IM1);
	static int idum2 = 123456789, iy = 0;
	static Vec_INT iv(NTAB);
	int j, k;
	DP temp;

	if (rFile<<"node2 "<<idum-0<<" idum<=0\n",idum <= 0) {
		rFile<<"node3\n";
		idum = (idum == 0 ? 1 : -idum);
		idum2 = idum;
		for (j = NTAB + 7; rFile<<"node4 "<<j-0<<" j>=0\n",j >= 0; j--) {
			rFile<<"node5\n";
	        k = idum / IQ1;
			idum = IA1 * (idum - k * IQ1) - k * IR1;
			if (rFile<<"node6 "<<idum-0<<" idum<0\n",idum < 0){
				rFile<<"node7\n";
	            idum += IM1;
	        }
			if (rFile<<"node8 "<<j-NTAB<<" j<NTAB\n",j < NTAB){
				rFile<<"node9\n";
	            iv[j] = idum;
	        }
		}
		rFile<<"node10\n";
		iy = iv[0];
	}
	rFile<<"node11\n";
	k = idum / IQ1;
	idum = IA1 * (idum - k * IQ1) - k * IR1;
	if (rFile<<"node12 "<<idum-0<<" idum<0\n",idum < 0){
		rFile<<"node13\n";
		idum += IM1;
	}
	rFile<<"node14\n";
	k = idum2 / IQ2;
	idum2 = IA2 * (idum2 - k * IQ2) - k * IR2;
	if (rFile<<"node15 "<<idum2-0<<" idum2<0\n",idum2 < 0){
		rFile<<"node16\n";
		idum2 += IM2;
	}
	rFile<<"node17\n";
	j = iy / NDIV;
	iy = iv[j] - idum2;
	iv[j] = idum;
	if (rFile<<"node18 "<<iy-1<<" iy<1\n",iy < 1){
		rFile<<"node19\n";
		iy += IMM1;
	}
	if (rFile<<"node20 "<<(temp = AM * iy) - RNMX<<" (temp=AM*iy)>RNMX\n",(temp = AM * iy) > RNMX){
		rFile<<"node21\n";
		result = RNMX;
	}
	else{
		rFile<<"node22\n";
		result = temp;
	}
	rFile<<"node23\n";
    return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callRan3
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callRan3
  (JNIEnv *env, jobject arg, jint idum, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream rFile(path);
	rFile<<"node1\n";
	
	static int inext, inextp;
	static int iff = 0;
	const int MBIG = 1000000000, MSEED = 161803398, MZ = 0;
	const DP FAC = (1.0 / MBIG);
	static Vec_INT ma(56);
	int i, ii, k, mj, mk;

	if ((rFile<<"node2 "<<(idum - 0)<<" idum<0\n", idum < 0) || (rFile<<"node2 "<<(iff - 0)<<" iff==0\n",iff == 0)) {
		rFile<<"node3\n";
		iff = 1;
		mj = labs(MSEED - labs(idum));
		mj %= MBIG;
		ma[55] = mj;
		mk = 1;
		for (i = 1; (rFile<<"node4 "<<(i - 54)<<" i<=54\n", i <= 54); i++) {
			rFile<<"node5\n";
			ii = (21 * i) % 55;
			ma[ii] = mk;
			mk = mj - mk;
			if (rFile<<"node6 "<<(mk - int(MZ))<<" mk<int(MZ)\n", mk < int(MZ)) {
				rFile<<"node7\n";
				mk += MBIG;
			}
			rFile<<"node8\n";
			mj = ma[ii];
		}
		for (k = 0; (rFile<<"node9 "<<(k - 4)<<" k<4\n", k < 4); k++)
			for (i = 1; (rFile<<"node10 "<<(i - 55)<<" i<=55\n", i <= 55); i++) {
				rFile<<"node11\n";
				ma[i] -= ma[1 + (i + 30) % 55];
				if (rFile<<"node12 "<<(ma[i] - int(MZ))<<" ma[i]<int(MZ)\n", ma[i] < int(MZ)) {
					rFile<<"node13\n";
					ma[i] += MBIG;
				}
			}
		rFile<<"node14\n";
		inext = 0;
		inextp = 31;
		idum = 1;
	}
	rFile<<"node15\n";
	++inext;
	if (rFile<<"node16 "<<(inext - 56)<<" inext==56\n", inext == 56) {
		rFile<<"node17\n";
		inext = 1;
	}
	rFile<<"node18\n";
	++inextp;
	if (rFile<<"node19 "<<(inextp - 56)<<" inextp==56\n", inextp == 56) {
		rFile<<"node20\n";
		inextp = 1;
	}
	rFile<<"node21\n";
	mj = ma[inext] - ma[inextp];
	if (rFile<<"node22 "<<(mj - int(MZ))<<" mj<int(MZ)\n", mj < int(MZ)) {
		rFile<<"node23\n";
		mj += MBIG;
	}
	rFile<<"node24\n";
	ma[inext] = mj;
	rFile<<"node25\n";
	return mj * FAC;
}
 
/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callDbrent
 * Signature: (DDDDD)D
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callDbrent
  (JNIEnv *env, jobject arg, jdouble ax, jdouble bx, jdouble cx, jdouble tol, jdouble xmin, jstring pathFile)
{
	char* path = jstringTostring(env, pathFile);
	ofstream dFile(path);
	dFile<<setiosflags(ios::scientific);
	dFile<<setprecision(16);

	dFile<<"node1\n";
	DP result;
	const int ITMAX = 100;
	const DP ZEPS = numeric_limits < DP > ::epsilon() * 1.0e-3;
	bool ok1, ok2;
	int iter;
	DP a, b, d = 0.0, d1, d2, du, dv, dw, dx, e = 0.0;
	DP fu, fv, fw, fx, olde, tol1, tol2, u, u1, u2, v, w, x, xm, temp;

	a = (ax < cx ? ax : cx);
	b = (ax > cx ? ax : cx);
	x = w = v = bx;
	fw = fv = fx = f0(x);
	dw = dv = dx = f1(x);
	for (iter = 0; (dFile<<"node2 "<<(iter - ITMAX)<<" iter<ITMAX\n", iter < ITMAX); iter++) {
		dFile<<"node3\n";
		xm = 0.5 * (a + b);
		tol1 = tol * fabs(x) + ZEPS;
		tol2 = 2.0 * tol1;
		if (dFile<<"node4 "<<(fabs(x - xm) - (tol2 - 0.5 * (b - a)))<<" fabs(x-xm)<=(tol2-0.5*(b-a))\n", fabs(x - xm) <= (tol2 - 0.5 * (b - a))) {
			dFile<<"node5\n";
			xmin = x;
			result = fx;
			iter = ITMAX + 1;
		} else {
			if (dFile<<"node6 "<<(fabs(e) - tol1)<<" fabs(e)>tol1\n", fabs(e) > tol1) {
				dFile<<"node7\n";
				d1 = 2.0 * (b - a);
				d2 = d1;
				if ((dFile<<"node8 "<<(dw - dx)<<" dw>dx\n", dw > dx) || (dFile<<"node8 "<<(dw - dx)<<" dw<dx\n", dw < dx)) {
					dFile<<"node9\n";
					d1 = (w - x) * dx / (dx - dw);
				}
				if ((dFile<<"node10 "<<(dv - dx)<<" dv>dx\n", dv > dx) || (dFile<<"node10 "<<(dv - dx)<<" dv<dx\n", dv < dx)) {
					dFile<<"node11\n";
					d2 = (v - x) * dx / (dx - dv);
				}
				dFile<<"node12\n";
				u1 = x + d1;
				u2 = x + d2;
				ok1 = (a - u1) * (u1 - b) > 0.0 && dx * d1 <= 0.0;
				ok2 = (a - u2) * (u2 - b) > 0.0 && dx * d2 <= 0.0;
				olde = e;
				e = d;
				if ((dFile<<"node13 "<<(ok1 - 0)<<" ok1>0\n", ok1 > 0) || (dFile<<"node13 "<<(ok2 - 0)<<" ok2>0\n", ok2 > 0)) {
					dFile<<"node14\n";
					d = (ok1 > 0 && ok2 > 0) ?
							(fabs(d1) < fabs(d2) ? d1 : d2) :
							((ok1 > 0) ? d1 : d2);
					temp = d;

					d = (fabs(d) <= fabs(0.5 * olde)) ?
							((x + d - a < tol2 || b - x - d < tol2) ?
									SIGN(tol1, xm - x) : temp) :
							(0.5 * (e = (dx >= 0.0 ? a - x : b - x)));

				} else {
					dFile<<"node15\n";
					d = 0.5 * (e = (dx >= 0.0 ? a - x : b - x));
				}
			} else {
				dFile<<"node16\n";
				d = 0.5 * (e = (dx >= 0.0 ? a - x : b - x));
			}

			if (dFile<<"node17 "<<(fabs(d) - tol1)<<" fabs(d)>=tol1\n", fabs(d) >= tol1) {
				dFile<<"node18\n";
				u = x + d;
				fu = f0(u);
			} else {
				dFile<<"node19\n";
				u = x + SIGN(tol1, d);
				fu = f0(u);
				if (dFile<<"node20 "<<(fu - fx)<<" fu>fx\n", fu > fx) {
					dFile<<"node21\n";
					xmin = x;
					result = fx;
					iter = ITMAX + 1;
				}
			}
			if (dFile<<"node22 "<<(fu - fx)<<" fu<=fx\n", fu <= fx) {
				dFile<<"node23\n";
				du = f1(u);
				if (dFile<<"node24 "<<(fu - fx)<<" fu<=fx\n", fu <= fx) {
					dFile<<"node25\n";
					(u >= x) ? (a = x) : (b = x);
					mov3(v, fv, dv, w, fw, dw);
					mov3(w, fw, dw, x, fx, dx);
					mov3(x, fx, dx, u, fu, du);
				} else {
					dFile<<"node26\n";
					(u < x) ? (a = u) : (b = u);
					if ((dFile<<"node27 "<<(fu - fw)<<" fu<=fw\n", fu <= fw) || (dFile<<"node27 "<<(w - x)<<" w==x\n", w == x)) {
						dFile<<"node28\n";
						mov3(v, fv, dv, w, fw, dw);
						mov3(w, fw, dw, u, fu, du);
					} else if ((dFile<<"node29 "<<(fu - fv)<<" fu<fv\n", fu < fv) || (dFile<<"node29 "<<(v - x)<<" v==x\n", v == x) || (dFile<<"node29 "<<(v - w)<<" v==w\n", v == w)) {
						dFile<<"node30\n";
						mov3(v, fv, dv, u, fu, du);
					}
				}
			}
		}
	}
	dFile<<"node31\n";
	//printf("Too many iterations in routine dbrent");

	result = (iter < ITMAX + 1) ? 0.0 : result;

	dFile<<"node32\n";
	return result;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callSimExample
 * Signature: (D)V
 */
JNIEXPORT jdouble JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callSimExample
  (JNIEnv *env, jobject arg, jdouble X, jdouble Y, jdouble Z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream dFile(path);
	dFile<<"node1\n";
	double U,W;
	U = (X-Y)*2;
	if(dFile<<"node2 "<<X-Y<<" X>Y\n",X>Y){
		dFile<<"node3\n";
		W = U;
	}
	else{
	    dFile<<"node4\n";
		W = Y;
	}
	if(dFile<<"node5 "<<(W+Z)-100<<" W+Z>100\n",W+Z>100){
	    dFile<<"node6\n";
		printf("Linear");
	}
	else if(dFile<<"node7 "<<X*X+Z*Z-100<<" X*X+Z*Z>=100\n",X*X + Z*Z >= 100){
		dFile<<"node8\n";
		printf("Nonlinear: Quadratic");
	}

	dFile<<"node9\n";
	return 0.0;
}
