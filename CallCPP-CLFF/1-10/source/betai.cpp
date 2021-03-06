#include <cmath>
#include "nr.h"
using namespace NR;

DP betai(const DP a, const DP b, const DP x) {
	DP bt, result;

	if (x < 0.0 || x > 1.0)
		printf("Bad x in routine betai");
	else
	{
		if (x == 0.0 || x == 1.0)
			bt = 0.0;
		else
			bt = exp(gammln(a + b) - gammln(a) - gammln(b) + a * log(x) + b * log(1.0 - x));
		if (x < (a + 1.0) / (a + b + 2.0))
			result = bt * betacf(a, b, x) / a;
		else
			result = 1.0 - bt * betacf(b, a, 1.0 - x) / b;
	}

	return result;
}
