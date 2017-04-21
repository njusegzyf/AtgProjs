/*
 * SimExample.cpp
 *
 *  Created on: Jun 3, 2014
 *      Author: zy
 */
using namespace std;

double SimExample(double X, double Y, double Z){
	double U,W;
	U = (X-Y)*2;
	if(X>Y)
		W = U;
	else
		W = Y;
	if(W+Z>100)
		printf("Linear");
	else if(X*X + Z*Z >= 100)
		printf("Nonlinear: Quadratic");

	return 0.0;
}

