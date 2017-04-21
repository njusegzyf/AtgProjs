#include <cmath>
#include <limits>
#include "nr.h"
using namespace std;

namespace {
inline void mov3(DP &a, DP &b, DP &c, const DP d, const DP e, const DP f) {
	a = d;
	b = e;
	c = f;
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

inline DP f(const DP x) {
	return NR::bessj0(x);
}

inline DP df(const DP x) {
	return -NR::bessj1(x);
}
}

DP dbrent(const DP ax, const DP bx, const DP cx, const DP tol, DP &xmin) {
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
	fw = fv = fx = f(x);
	dw = dv = dx = df(x);
	for (iter = 0; iter < ITMAX; iter++) {
		xm = 0.5 * (a + b);
		tol1 = tol * fabs(x) + ZEPS;
		tol2 = 2.0 * tol1;
		if (fabs(x - xm) <= (tol2 - 0.5 * (b - a))) {
			xmin = x;
			result = fx;
			iter = ITMAX + 1;
		} else {
			if (fabs(e) > tol1) {
				d1 = 2.0 * (b - a);
				d2 = d1;
				if (dw > dx || dw < dx)
					d1 = (w - x) * dx / (dx - dw);
				if (dv > dx || dv < dx)
					d2 = (v - x) * dx / (dx - dv);
				u1 = x + d1;
				u2 = x + d2;
				ok1 = (a - u1) * (u1 - b) > 0.0 && dx * d1 <= 0.0;
				ok2 = (a - u2) * (u2 - b) > 0.0 && dx * d2 <= 0.0;
				olde = e;
				e = d;
				if (ok1 > 0 || ok2 > 0) {
					d = (ok1 > 0 && ok2 > 0) ?
							(fabs(d1) < fabs(d2) ? d1 : d2) :
							((ok1 > 0) ? d1 : d2);
					temp = d;

					d = (fabs(d) <= fabs(0.5 * olde)) ?
							((x + d - a < tol2 || b - x - d < tol2) ?
									SIGN(tol1, xm - x) : temp) :
							(0.5 * (e = (dx >= 0.0 ? a - x : b - x)));

				} else {
					d = 0.5 * (e = (dx >= 0.0 ? a - x : b - x));
				}
			} else {
				d = 0.5 * (e = (dx >= 0.0 ? a - x : b - x));
			}

			if (fabs(d) >= tol1) {
				u = x + d;
				fu = f(u);
			} else {
				u = x + SIGN(tol1, d);
				fu = f(u);
				if (fu > fx) {
					xmin = x;
					result = fx;
					iter = ITMAX + 1;
				}
			}
			if (fu <= fx) {
				du = df(u);
				if (fu <= fx) {
					(u >= x) ? (a = x) : (b = x);
					mov3(v, fv, dv, w, fw, dw);
					mov3(w, fw, dw, x, fx, dx);
					mov3(x, fx, dx, u, fu, du);
				} else {
					(u < x) ? (a = u) : (b = u);
					if (fu <= fw || w == x) {
						mov3(v, fv, dv, w, fw, dw);
						mov3(w, fw, dw, u, fu, du);
					} else if (fu < fv || v == x || v == w) {
						mov3(v, fv, dv, u, fu, du);
					}
				}
			}
		}
	}
	printf("Too many iterations in routine dbrent");


	result = (iter < ITMAX +1) ? 0.0 : result;

	return result;
}
