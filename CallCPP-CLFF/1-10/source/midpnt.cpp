#include "nr.h"

namespace {
	DP funk(const DP x) {
		return 1.0 / sqrt(x);
	}
}

DP midpnt(const DP a, const DP b, const int n) {
	DP result = 0.0;

	int it, j;
	DP x, tnm, sum, del, ddel;
	static DP s;

	if (n == 1) {
		result = (s = (b - a) * funk(0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; j < n - 1; j++)
			it *= 3;
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; j < it; j++) {
			sum += funk(x);
			x += ddel;
			sum += funk(x);
			x += del;
		}
		s = (s + (b - a) * sum / tnm) / 3.0;
		result = s;
	}

	return result;
}
