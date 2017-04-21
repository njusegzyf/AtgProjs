#include "nr.h"

namespace {
	DP funcinf(DP funk(const DP), const DP x) {
		return funk(1.0 / x) / (x * x);
	}

	DP funk(const DP x) {
		return 1.0 / sqrt(x);
	}
}

DP midinf(const DP aa, const DP bb, const int n) {
	DP result = 0.0;

	DP x, tnm, sum, del, ddel, b, a;
	static DP s;
	int it, j;

	b = 1.0 / aa;
	a = 1.0 / bb;
	if (n == 1) {
		result = (s = (b - a) * funcinf(funk, 0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; j < n - 1; j++)
			it *= 3;
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; j < it; j++) {
			sum += funcinf(funk, x);
			x += ddel;
			sum += funcinf(funk, x);
			x += del;
		}
		result = (s = (s + (b - a) * sum / tnm) / 3.0);
	}

	return result;
}
