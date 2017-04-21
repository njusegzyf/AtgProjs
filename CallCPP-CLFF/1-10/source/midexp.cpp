#include <cmath>
#include "nr.h"
using namespace std;

namespace {
	DP funcexp(DP funk(const DP), const DP x) {
		return funk(-log(x)) / x;
	}

	DP funk(const DP x) {
		return 1.0 / sqrt(x);
	}
}

DP midexp(const DP aa, const DP bb, const int n) {
	DP result = 0.0;

	DP x, tnm, sum, del, ddel, a, b;
	static DP s;
	int it, j;

	b = exp(-aa);
	a = 0.0;
	if (n == 1) {
		result = (s = (b - a) * funcexp(funk, 0.5 * (a + b)));
	} else {
		for (it = 1, j = 1; j < n - 1; j++)
			it *= 3;
		tnm = it;
		del = (b - a) / (3.0 * tnm);
		ddel = del + del;
		x = a + 0.5 * del;
		sum = 0.0;
		for (j = 0; j < it; j++) {
			sum += funcexp(funk, x);
			x += ddel;
			sum += funcexp(funk, x);
			x += del;
		}
		s = (s + (b - a) * sum / tnm) / 3.0;
		result = s;
	}

	return result;
}
