#include <cmath>
#include <limits>
#include "nr.h"
using namespace std;

namespace {
inline void mov3(double &a, double &b, double &c, const double d, const double e, const double f) {
	a = d;
	b = e;
	c = f;
}

double NR::bessj0(const double x)
{
	double ax,z,xx,y,ans,ans1,ans2;

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

double NR::bessj1(const double x)
{
	double ax,z,xx,y,ans,ans1,ans2;

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

inline double f(const double x) {
	return NR::bessj0(x);
}

inline double df(const double x) {
	return -NR::bessj1(x);
}

inline float SIGN(const double &a, const float &b){
	return b >= 0 ? (a >= 0 ? a : -a) : (a >= 0 ? -a : a);
}
}

double dbrent(const double ax, const double bx, const double cx, const double tol, double &xmin) {
	double result;                                                     //node1
	const int ITMAX = 100;
	const double ZEPS = numeric_limits < double > ::epsilon() * 1.0e-3;
	bool ok1, ok2;
	int iter;
	double a, b, d = 0.0, d1, d2, du, dv, dw, dx, e = 0.0;
	double fu, fv, fw, fx, olde, tol1, tol2, u, u1, u2, v, w, x, xm, temp;

	a = (ax < cx ? ax : cx);
	b = (ax > cx ? ax : cx);
	x = w = v = bx;
	fw = fv = fx = f(x);
	dw = dv = dx = df(x);
	for (iter = 0; iter < ITMAX; iter++) {                             //node2
		xm = 0.5 * (a + b);                                            //node3
		tol1 = tol * fabs(x) + ZEPS;
		tol2 = 2.0 * tol1;
		if (fabs(x - xm) <= (tol2 - 0.5 * (b - a))) {                  //node4
			xmin = x;                                                  //node5
			result = fx;
			iter = ITMAX + 1;
		} else {
			if (fabs(e) > tol1) {                                      //node6
				d1 = 2.0 * (b - a);                                    //node7
				d2 = d1;
				if (dw > dx || dw < dx)                                //node8
					d1 = (w - x) * dx / (dx - dw);                     //node9
				if (dv > dx || dv < dx)                                //node10
					d2 = (v - x) * dx / (dx - dv);                     //node11
				u1 = x + d1;                                           //node12
				u2 = x + d2;
				ok1 = (a - u1) * (u1 - b) > 0.0 && dx * d1 <= 0.0;
				ok2 = (a - u2) * (u2 - b) > 0.0 && dx * d2 <= 0.0;
				olde = e;
				e = d;
				if (ok1 > 0 || ok2 > 0) {                              //node13
					d = (ok1 > 0 && ok2 > 0) ?                         //node14
							(fabs(d1) < fabs(d2) ? d1 : d2) :
							((ok1 > 0) ? d1 : d2);
					temp = d;

					d = (fabs(d) <= fabs(0.5 * olde)) ?
							((x + d - a < tol2 || b - x - d < tol2) ?
									SIGN(tol1, xm - x) : temp) :
							(0.5 * (e = (dx >= 0.0 ? a - x : b - x)));

				} else {
					d = 0.5 * (e = (dx >= 0.0 ? a - x : b - x));       //node15
				}
			} else {
				d = 0.5 * (e = (dx >= 0.0 ? a - x : b - x));           //node16
			}

			if (fabs(d) >= tol1) {                                     //node17
				u = x + d;                                             //node18
				fu = f(u);
			} else {
				u = x + SIGN(tol1, d);                                 //node19
				fu = f(u);
				if (fu > fx) {                                         //node20
					xmin = x;                                          //node21
					result = fx;
					iter = ITMAX + 1;
				}
			}
			if (fu <= fx) {                                            //node22
				du = df(u);                                            //node23
				if (fu <= fx) {                                        //node24
					(u >= x) ? (a = x) : (b = x);                      //node25
					mov3(v, fv, dv, w, fw, dw);
					mov3(w, fw, dw, x, fx, dx);
					mov3(x, fx, dx, u, fu, du);
				} else {
					(u < x) ? (a = u) : (b = u);                       //node26
					if (fu <= fw || w == x) {                          //node27
						mov3(v, fv, dv, w, fw, dw);                    //node28
						mov3(w, fw, dw, u, fu, du);
					} else if (fu < fv || v == x || v == w) {          //node29
						mov3(v, fv, dv, u, fu, du);                    //node30
					}
				}
			}
		}
	}
	printf("Too many iterations in routine dbrent");                   //node31
	result = (iter < ITMAX +1) ? 0.0 : result;

	return result;                                                     //node32
}
/*(infeasible paths)
Path2:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path3:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path4:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path5:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path6:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(F)->node2->node31->node32
Path7:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path8:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path9:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path10:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path11:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path12:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path13:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path14:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path15:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path16:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path17:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path18:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path19:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path20:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path21:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(F)->node2->node31->node32
Path22:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path23:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path24:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path25:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path26:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path27:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path28:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path29:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path30:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path31:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path32:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path33:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path34:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path35:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path36:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(F)->node2->node31->node32
Path37:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path38:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path39:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path40:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path41:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path42:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path43:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path44:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path45:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path46:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path47:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path48:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path49:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path50:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path51:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(F)->node2->node31->node32
Path52:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path53:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path54:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path55:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path56:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path57:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path58:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path59:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path60:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path61:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path62:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path63:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path64:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path65:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path66:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(T)->node18->node22(F)->node2->node31->node32
Path67:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path68:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path69:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path70:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path71:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path72:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path73:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path74:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path75:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path76:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path77:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path78:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path79:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path80:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path81:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(T)->node18->node22(F)->node2->node31->node32
Path82:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path83:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path84:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path85:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path86:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path87:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path88:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path89:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path90:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path91:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(T)->node11->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path93:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path94:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path95:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path97:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path98:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path99:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path100:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path101:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path102:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path103:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path104:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path105:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path106:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(T)->node14->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path108:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path109:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path110:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path112:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path113:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path114:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path115:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path116:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(T)->node21->node22(F)->node2->node31->node32
Path117:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path118:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path119:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path120:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path121:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10(F)->node12->node13(F)->node15->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path123:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path124:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path125:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(T)->node18->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path127:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(T)->node25->node2->node31->node32
Path128:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path129:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path130:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(T)->node21->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path133:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(T)->node28->node2->node31->node32
Path134:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(T)->node30->node2->node31->node32
Path135:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(F)->node22(T)->node23->node24(F)->node26->node27(F)->node29(F)->node2->node31->node32
Path136:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17(F)->node19->node20(F)->node22(F)->node2->node31->node32
Path137:node1->node2(F)->node31->node32
