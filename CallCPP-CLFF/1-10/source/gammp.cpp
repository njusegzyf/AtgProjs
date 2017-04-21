#include "nr.h"

DP gammp(const DP a, const DP x) {
	DP gamser, gammcf, gln, result;

	if (x < 0.0 || a <= 0.0)
		printf("Invalid arguments in routine gammp");
	else
	{
		if (x < a + 1.0) {
			gser(gamser, a, x, gln);
			result = gamser;
		} else {
			gcf(gammcf, a, x, gln);
			result = 1.0 - gammcf;
		}
	}

	return result;
}
