#include <cmath>
#include <limits>
#include "nr.h"
using namespace std;

void gser(DP &gamser, const DP a, const DP x, DP &gln) {
	const int ITMAX = 100;
	const DP EPS = numeric_limits < DP > ::epsilon();
	int n;
	DP sum, del, ap;

	gln = gammln(a);
	if (x <= 0.0) {
		if (x < 0.0)
			printf("x less than 0 in routine gser");
		else
			gamser = 0.0;
	} else {
		ap = a;
		del = sum = 1.0 / a;
		for (n = 0; n < ITMAX; n++) {
			++ap;
			del *= x / ap;
			sum += del;
			if (fabs(del) < fabs(sum) * EPS) {
				gamser = sum * exp(-x + a * log(x) - gln);
				n = ITMAX;
			}
		}
		printf("a too large, ITMAX too small in routine gser");
	}

	return;
}
