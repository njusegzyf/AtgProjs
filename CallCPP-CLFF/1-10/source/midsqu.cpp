#include <cmath>
#include "nr.h"
using namespace std;

namespace {
	DP funcsqu(DP funk(const DP), const DP bb, const DP x) {
		return 2.0 * x * funk(bb - x * x);
	}
	DP funk(const DP x) {
		return 1.0 / sqrt(x);
	}
}

DP midsqu(const DP aa, const DP bb, const int n) {
	DP result = 0.0;

	DP x, tnm, sum, del, ddel, a, b;
	static DP s;
	int it, j;

	b = sqrt(bb - aa);
	a = 0.0;
	if (n == 1) {
		result = (s = (b - a) * funcsqu(funk, bb, 0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; j < n - 1; j++)
			it *= 3;
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; j < it; j++) {
			sum += funcsqu(funk, bb, x);
			x += ddel;
			sum += funcsqu(funk, bb, x);
			x += del;
		}
		s = (s + (b - a) * sum / tnm) / 3.0;
		result = s;
	}

	return result;
}
