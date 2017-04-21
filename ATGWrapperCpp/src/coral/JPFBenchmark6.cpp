/*
 * JPFBenchmark6.cpp
 *
 *  Created on: Feb 5, 2015
 *      Author: zy
 */
#include <cmath>
#include <limits>

void benchmark71(double a,double b, double c,double d, double e, double f, double g, double h, double i, double j, double k, double l) {
	if(sin(a) > sin(b) && sin(b) > sin(c) && sin(c) > sin(d) && sin(d) > sin(e) && sin(e) > sin(f) && sin(f) > sin(g) && sin(g) > sin(h) && sin(h) > sin(i) && sin(i) > sin(j) && sin(j) > sin(k) && sin(k) > sin(l) ) {
		printf("Solved 71");
	}
	return;
}

void benchmark72(double a,double b, double c,double d, double e, double f, double g) {
	if(sin(a) > sin(b) && sin(b) > sin(c) && sin(c) > sin(d) && sin(d) > sin(e) && sin(e) > sin(f) && sin(f) > sin(g)) {
		printf("Solved 72");
	}
	return;
}

void benchmark73(double a,double b, double c,double d, double e, double f, double g, double h, double i, double j) {
	if(sin(a) > sin(b) && sin(b) > sin(c) && sin(c) > sin(d) && sin(d) > sin(e) && sin(e) > sin(f) && sin(f) > sin(g) && sin(g) > sin(h) && sin(h) > sin(i) && sin(i) > sin(j)) {
		printf("Solved 73");
	}
	return;
}

void benchmark74(double a,double b, double c,double d, double e, double f, double g, double h) {
	if(sin(a) > sin(b) && sin(b) > sin(c) && sin(c) > sin(d) && sin(d) > sin(e) && sin(e) > sin(f) && sin(f) > sin(g) && sin(g) > sin(h)) {
		printf("Solved 74");
	}
	return;
}

void benchmark75(double a,double b, double c,double d, double e, double f, double g, double h, double i) {
	if(sin(a) > sin(b) && sin(b) > sin(c) && sin(c) > sin(d) && sin(d) > sin(e) && sin(e) > sin(f) && sin(f) > sin(g) && sin(g) > sin(h) && sin(h) > sin(i)) {
		printf("Solved 75");
	}
	return;
}

void benchmark76(double a,double b, double c,double d, double e, double f, double g, double h, double i) {
	if(a > b && b > c && c > d && d > e && e > f && f > g && g > h && h > i) {
		printf("Solved 76");
	}
	return;
}

void benchmark77(double a,double b, double c,double d, double e, double f, double g, double h, double i, double j) {
	if(a > b && b > c && c > d && d > e && e > f && f > g && g > h && h > i && i > j) {
		printf("Solved 77");
	}
	return;
}

//(0.0 == (pow_((($V1*sin_(((($V2*0.017453292519943295)-($V3*0.017453292519943295))+(((((((pow_($V4,2.0)/(sin_(($V5*0.017453292519943295))/cos_(($V5*0.017453292519943295))))/68443.0)*0.0)/$V4)*-1.0)*$V1)/((pow_($V1,2.0)/(sin_(($V5*0.017453292519943295))/cos_(($V5*0.017453292519943295))))/68443.0)))))-($V4*0.0)),2.0)+pow_((($V1*cos_(((($V2*0.017453292519943295)-($V3*0.017453292519943295))+(((((((pow_($V4,2.0)/(sin_(($V5*0.017453292519943295))/cos_(($V5*0.017453292519943295))))/68443.0)*0.0)/$V4)*-1.0)*$V1)/((pow_($V1,2.0)/(sin_(($V5*0.017453292519943295))/cos_(($V5*0.017453292519943295))))/68443.0)))))-($V4*1.0)),2.0)))
//AND(AND(,($V6 != 0)),($V8 != 0)))
void benchmark78(double a,double b, double c,double d, double e, int f, int g, int h) {
	if((0.0 == (pow(((a*sin(((b*0.017453292519943295 - c*0.017453292519943295)+(((((((pow(d,2.0)/(sin(e*0.017453292519943295)/cos(e*0.017453292519943295)))/68443.0)*0.0)/d)*-1.0)*a)/((pow(a,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)))))-(d*0.0)),2.0)+pow(((a*cos((((b*0.017453292519943295)-(c*0.017453292519943295))+(((((((pow(d,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)*0.0)/d)*-1.0)*a)/((pow(a,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)))))-d*1.0),2.0))) && f != 0 && h != 0) {
		printf("Solved 78");
	}
	return;
}

//AND((0.0 == (pow_((($V84*sin_(((0.017453292519943295*$V85)-(0.017453292519943295*$V86))))-(0.0*$V87)),2.0)+pow_(($V84*cos_((((0.017453292519943295*$V85)-(0.017453292519943295*$V86))+0.0))),2.0))),($V82 != 0))
void benchmark79(double a,double b, double c,double d, int e) {
	if((0.0 == (pow(((a*sin(((0.017453292519943295*b)-(0.017453292519943295*c))))-(0.0*d)),2.0)+ pow((a*cos((((0.017453292519943295*b)-(0.017453292519943295*c))+0.0))),2.0))) && e != 0) {
		printf("Solved 79");
	}
	return;
}

//(1.5 - x1 * (1 - x2)) == 0
void benchmark80(double a,double b) {
	if((1.5 - a * (1 - b)) == 0) {
		printf("Solved 80");
	}
	return;
}

//(-13 + x1 + ((5 - x2) * x2 - 2) * x2) + (-29 + x1 + ((x2 + 1) * x2 - 14) * x2) == 0
void benchmark81(double a,double b) {
	if((-13 + a + ((5 - b) * b - 2) * b) + (-29 + a + ((b + 1) * b - 14) * b) == 0) {
		printf("Solved 81");
	}
	return;
}

// (Pow(10, 4) * x1 * x2 - 1) == 0 && (Pow(E, -x1) + Pow(E, -x2) - 1.0001) == 0
// 张辉修改过 在计算机允许的情况下等价转换等式为不等式 JZ20160722
void benchmark82(double a,double b) {
	if(
		fabs((pow(10, 4) * a * b - 1))<= 0.03
		&&
		fabs((exp(-a) + exp(-b) - 1.0001)) <= 0.03
		)
	{
		printf("Solved 82");
	}
	return;
}

// Pow((1 - x1), 2) + 100 * (Pow((x2 - x1 * x1), 2)) == 0
// 张辉修改过 在计算机允许的情况下等价转换等式为不等式 JZ20160722
void benchmark83(double a,double b) {
	if(
			pow((1 - a), 2) + 100 * (pow((b - a * a), 2)) <= 0.03
	)
	{
		printf("Solved 83");
	}
	return;
}

//(10 * (x2 - x1 * x1)) == 0 && (1 - x1) == 0 && (sqrt(90) * (x4 - x3 * x3)) == 0 && (1 - x3) == 0 && (sqrt(10) * (x2 + x4 - 2)) == 0 && (Pow(10, -0.5) * (x2 - x4)) == 0
// 张辉修改过 在计算机允许的情况下等价转换等式为不等式 JZ20160722
void benchmark84(double a,double b, double c, double d) {
	if(
		 fabs((10 * (b - a * a))) <= 0.03
		 && fabs((1 - a)) <= 0.03
		 && fabs((sqrt(90) * (d - c * c))) <= 0.03
		 &&fabs((1 - c)) <= 0.03
		 && fabs((sqrt(10) * (b + d - 2))) <= 0.03
		 &&fabs((pow(10, -0.5) * (b - d))) <= 0.03
		 )
	{
		printf("Solved 84");
	}
	return;
}

void benchmark91(double x, double y){
	if(sin(x) == -sin(y) && sin(x) > 0){
	    printf("Solved 91");
	}
	return;
}
