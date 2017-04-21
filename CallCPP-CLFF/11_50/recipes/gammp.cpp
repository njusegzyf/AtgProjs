#include <cmath>
#include <limits>
#include "nr.h"
using namespace NR;

DP gammp(const DP a, const DP x)
{
	DP gamser,gammcf,gln;
	DP result;
	const int ITMAX=100;
	const DP EPS=numeric_limits<DP>::epsilon();
	int n;
	DP sum,del,ap;
	const DP FPMIN=numeric_limits<DP>::min()/EPS;
	int i;
	DP an,b,c,d,h;

	if (x < 0.0 || a <= 0.0)
	{
		printf("Invalid arguments in routine gammp");
	}
	else
	{
		if (x < a+1.0)
		{
			gln=gammln(a);
			if (x <= 0.0)
			{
				if (x < 0.0)
				{
					printf("x less than 0 in routine gser");
				}
				gamser=0.0;
			}
			else
			{
				ap=a;
				del=sum=1.0/a;
				for (n=0;n<ITMAX;n++)
				{
					++ap;
					del *= x/ap;
					sum += del;
					if (fabs(del) < fabs(sum)*EPS)
					{
						gamser=sum*exp(-x+a*log(x)-gln);
					}
				}
				printf("a too large, ITMAX too small in routine gser");
			}
			result = gamser;
		} else
		{
			gln=gammln(a);
			b=x+1.0-a;
			c=1.0/FPMIN;
			d=1.0/b;
			h=d;
			for (i=1;i<=ITMAX;i++)
			{
				an = -i*(i-a);
				b += 2.0;
				d=an*d+b;
				if (fabs(d) < FPMIN)
				{
					d=FPMIN;
				}
				c=b+an/c;
				if (fabs(c) < FPMIN)
				{
					c=FPMIN;
				}
				d=1.0/d;
				del=d*c;
				h *= del;
				if (fabs(del-1.0) <= EPS)
				{
					i = ITMAX+1;
				}
			}
			if (i > ITMAX)
			{
				printf("a too large, ITMAX too small in gcf");
			}
			gammcf=exp(-x+a*log(x)-gln)*h;

			result = 1.0-gammcf;
		}
	}
	return result;
}

