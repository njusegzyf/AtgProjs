#include <cmath>
#include "nr.h"
using namespace std;

namespace {
	DP funk(const DP x) {
		return 1.0 / sqrt(x);
	}
}

bool zbrac(DP &x1, DP &x2) {
	bool result = false;

	const int NTRY = 50;
	const DP FACTOR = 1.6;
	int j;
	DP f1, f2;

	if (x1 == x2)
		printf("Bad initial range in zbrac");
	else {
		f1 = funk(x1);
		f2 = funk(x2);
		for (j = 0; j < NTRY; j++) {
			if (f1 * f2 < 0.0)
				result = true;
			else {
				if (fabs(f1) < fabs(f2))
					f1 = funk(x1 += FACTOR * (x1 - x2));
				else
					f2 = funk(x2 += FACTOR * (x2 - x1));
			}
		}
	}
	return result;
}
