#include <cmath>
#include <limits>
#include "nr.h"
using namespace std;

namespace {
	inline void shft3(double &a, double &b, double &c, const double d) {
		a = b;
		b = c;
		c = d;
	}

	inline double f(const double x) {
		return (x - 10) * (x - 20) - 30;
	}

	inline float SIGN(const double &a, const float &b){
		return b >= 0 ? (a >= 0 ? a : -a) : (a >= 0 ? -a : a);
	}
}

double brent(const double ax, const double bx, const double cx, const double tol) {
	double result;                                                  //node1
	const int ITMAX = 100;
	const double CGOLD = 0.3819660;
	const double ZEPS = numeric_limits < double > ::epsilon() * 1.0e-3;
	int iter;
	double a, b, d = 0.0, etemp, fu, fv, fw, fx;
	double p, q, r, tol1, tol2, u, v, w, x, xm;
	double e = 0.0;

	a = (ax < cx ? ax : cx);
	b = (ax > cx ? ax : cx);
	x = w = v = bx;
	result = fw = fv = fx = f(x);
	for (iter = 0; iter < ITMAX; iter++) {                          //node2
		xm = 0.5 * (a + b);                                         //node3
		tol2 = 2.0 * (tol1 = tol * fabs(x) + ZEPS);
		if (fabs(x - xm) <= (tol2 - 0.5 * (b - a))) {               //node4
			result = fx;                                            //node5
		} else {
			if (fabs(e) > tol1) {                                   //node6
				r = (x - w) * (fx - fv);                            //node7
				q = (x - v) * (fx - fw);
				p = (x - v) * q - (x - w) * r;
				q = 2.0 * (q - r);
				if (q > 0.0)                                        //node8
					p = -p;                                         //node9
				q = fabs(q);                                        //node10
				etemp = e;
				e = d;
				if (fabs(p) >= fabs(0.5 * q * etemp) ||             //node11
						p <= q * (a - x) || p >= q * (b - x))
					d = CGOLD * (e = (x >= xm ? a - x : b - x));    //node12
				else {
					d = p / q;                                      //node13
					u = x + d;
					if (u - a < tol2 || b - u < tol2)               //node14
						d = SIGN(tol1, xm - x);                     //node15
				}
			} else {
				d = CGOLD * (e = (x >= xm ? a - x : b - x));        //node16
			}
			u = (fabs(d) >= tol1 ? x + d : x + SIGN(tol1, d));      //node17
			fu = f(u);
			if (fu <= fx) {                                         //node18
				if (u >= x)                                         //node19
					a = x;                                          //node20
				else
					b = x;                                          //node21
				shft3(v, w, x, u);                                  //node22
				shft3(fv, fw, fx, fu);
			} else {
				if (u < x)                                          //node23
					a = u;                                          //node24
				else
					b = u;                                          //node25
				if (fu <= fw || w == x) {                           //node26
					v = w;                                          //node27
					w = u;
					fv = fw;
					fw = fu;
				} else if (fu <= fv || v == x || v == w) {          //node28
					v = u;                                          //node29
					fv = fu;
				}
			}
		}
	}
	return fx;                                                      //node30
}
/*(infeasible paths)
Path2:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(T)->node19(T)->node20->node22->node2->node30
Path3:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(T)->node19(F)->node21->node22->node2->node30
Path4:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(F)->node23(T)->node24->node26(T)->node27->node2->node30
Path5:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(F)->node23(T)->node24->node26(F)->node28(T)->node29->node2->node30
Path6:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(F)->node23(T)->node24->node26(F)->node28(F)->node2->node30
Path7:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(F)->node23(F)->node25->node26(T)->node27->node2->node30
Path8:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(F)->node23(F)->node25->node26(F)->node28(T)->node29->node2->node30
Path9:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(T)->node12->node17->node18(F)->node23(F)->node25->node26(F)->node28(F)->node2->node30
Path10:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(T)->node19(T)->node20->node22->node2->node30
Path11:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(T)->node19(F)->node21->node22->node2->node30
Path12:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(T)->node24->node26(T)->node27->node2->node30
Path13:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(T)->node24->node26(F)->node28(T)->node29->node2->node30
Path14:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(T)->node24->node26(F)->node28(F)->node2->node30
Path15:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(F)->node25->node26(T)->node27->node2->node30
Path16:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(F)->node25->node26(F)->node28(T)->node29->node2->node30
Path17:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(F)->node25->node26(F)->node28(F)->node2->node30
Path18:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(T)->node19(T)->node20->node22->node2->node30
Path19:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(T)->node19(F)->node21->node22->node2->node30
Path20:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(T)->node24->node26(T)->node27->node2->node30
Path21:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(T)->node24->node26(F)->node28(T)->node29->node2->node30
Path22:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(T)->node24->node26(F)->node28(F)->node2->node30
Path23:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(F)->node25->node26(T)->node27->node2->node30
Path24:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(F)->node25->node26(F)->node28(T)->node29->node2->node30
Path25:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(T)->node9->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(F)->node25->node26(F)->node28(F)->node2->node30
Path29:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(T)->node12->node17->node18(F)->node23(T)->node24->node26(F)->node28(T)->node29->node2->node30
Path30:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(T)->node12->node17->node18(F)->node23(T)->node24->node26(F)->node28(F)->node2->node30
Path32:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(T)->node12->node17->node18(F)->node23(F)->node25->node26(F)->node28(T)->node29->node2->node30
Path33:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(T)->node12->node17->node18(F)->node23(F)->node25->node26(F)->node28(F)->node2->node30
Path34:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(T)->node19(T)->node20->node22->node2->node30
Path35:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(T)->node19(F)->node21->node22->node2->node30
Path36:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(T)->node24->node26(T)->node27->node2->node30
Path37:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(T)->node24->node26(F)->node28(T)->node29->node2->node30
Path38:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(T)->node24->node26(F)->node28(F)->node2->node30
Path39:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(F)->node25->node26(T)->node27->node2->node30
Path40:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(F)->node25->node26(F)->node28(T)->node29->node2->node30
Path41:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(T)->node15->node17->node18(F)->node23(F)->node25->node26(F)->node28(F)->node2->node30
Path42:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(T)->node19(T)->node20->node22->node2->node30
Path43:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(T)->node19(F)->node21->node22->node2->node30
Path44:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(T)->node24->node26(T)->node27->node2->node30
Path45:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(T)->node24->node26(F)->node28(T)->node29->node2->node30
Path46:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(T)->node24->node26(F)->node28(F)->node2->node30
Path47:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(F)->node25->node26(T)->node27->node2->node30
Path48:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(F)->node25->node26(F)->node28(T)->node29->node2->node30
Path49:node1->node2(T)->node3->node4(F)->node6(T)->node7->node8(F)->node10->node11(F)->node13->node14(F)->node17->node18(F)->node23(F)->node25->node26(F)->node28(F)->node2->node30
Path53:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17->node18(F)->node23(T)->node24->node26(F)->node28(T)->node29->node2->node30
Path54:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17->node18(F)->node23(T)->node24->node26(F)->node28(F)->node2->node30
Path56:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17->node18(F)->node23(F)->node25->node26(F)->node28(T)->node29->node2->node30
Path57:node1->node2(T)->node3->node4(F)->node6(F)->node16->node17->node18(F)->node23(F)->node25->node26(F)->node28(F)->node2->node30
Path58:node1->node2(F)->node30
