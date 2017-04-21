/*
 * test.cpp
 *
 *  Created on: Jul 6, 2016
 *      Author: zy
 */

int f(int a, int b){
	if(b > 10)
		return b - a;
	else
		return a - b;
}

int test(int a, int b){
	int c;
	if(a>1 && ((b<10 && a>b) || f(a,b)>0)){
		c = a + b;
	}
	return c;
}



