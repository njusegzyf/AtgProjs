#include "nr.h"

DP gammq(const DP a, const DP x) {
	DP gamser, gammcf, gln, result;

	if (x < 0.0 || a <= 0.0)
		printf("Invalid arguments in routine gammq");
	else
	{
		if (x < a + 1.0) {
			gser(gamser, a, x, gln);
			result = 1.0 - gamser;
		} else {
			gcf(gammcf, a, x, gln);
			result = gammcf;
		}
	}

	return result;
}
