#include <cmath>
#include <limits>
#include "nr.h"
using namespace NR;

DP ei(double x)
{
	const int MAXIT=100;
	const DP EULER=0.577215664901533;
	const DP EPS=numeric_limits<DP>::epsilon();
	const DP FPMIN=numeric_limits<DP>::min()/EPS;
	int k;
	DP fact,prev,sum,term;
	DP result;

	if (x <= 0.0)
	{
		printf("Bad argument in ei");
	}
	else
	{
		if (x < FPMIN)
		{
			result = log(x)+EULER;
		}
		else
		{
			if (x <= -log(EPS))
			{
				sum=0.0;
				fact=1.0;
				for (k=1;k<=MAXIT;k++)
				{
					fact *= x/k;
					term=fact/k;
					sum += term;
					if (term < EPS*sum)
					{
						k = MAXIT+1;
					}
				}
				if (k > MAXIT)
				{
					printf("Series failed in ei");
				}
				result = sum+log(x)+EULER;
			} else {
				sum=0.0;
				term=1.0;
				for (k=1;k<=MAXIT;k++)
				{
					prev=term;
					term *= k/x;
					if (term < EPS)
					{
						k = MAXIT+1;
					}
					else
					{
						if (term < prev)
						{
							sum += term;
						}
						else
						{
						    sum -= prev;
							k = MAXIT+1;
						}
					}
				}
				result = exp(x)*(1.0+sum)/x;
			}
		}
	}
	return result;
}
