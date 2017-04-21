#include "nr.h"

namespace {
	DP funk(const DP x) {
		return 1.0 / sqrt(x);
	}
}

DP trapzd(const DP a, const DP b, const int n) {
	DP result = 0.0;

	DP x, tnm, sum, del;
	static DP s;
	int it, j;

	if (n == 1) {
		result = (s = 0.5 * (b - a) * (funk(a) + funk(b)));
	} else {
		for (it = 1, j = 1; j < n - 1; j++)
			it <<= 1;
		tnm = it;
		del = (b - a) / tnm;
		x = a + 0.5 * del;
		for (sum = 0.0, j = 0; j < it; j++, x += del)
			sum += funk(x);
		s = 0.5 * (s + (b - a) * sum / tnm);
		result = s;
	}

	return result;
}
