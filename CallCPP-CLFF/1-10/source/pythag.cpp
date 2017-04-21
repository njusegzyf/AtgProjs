#include <cmath>
#include "nr.h"
using namespace std;

DP pythag(const DP a, const DP b) {
	DP absa, absb, result;

	absa = fabs(a);
	absb = fabs(b);
	if (absa > absb)
		result = absa * sqrt(1.0 + SQR(absb / absa));
	else
		result = (absb == 0.0 ? 0.0 : absb * sqrt(1.0 + SQR(absa / absb)));

	return result;
}
