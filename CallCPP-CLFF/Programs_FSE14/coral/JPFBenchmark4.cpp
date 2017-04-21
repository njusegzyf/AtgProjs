/*
 * JPFBenchmark4.cpp
 *
 *  Created on: Feb 5, 2015
 *      Author: zy
 */
#include <cmath>

  //  1.0/sqrt(sin($V1)) > sqrt(cos(exp($V2)))
  void benchmark35(double x,double y) {
	  if(1.0/sqrt(sin(x)) > sqrt(cos(exp(y)))){
		  printf("Solved 35");
	  }
	  return;
  }

  // (atan2_($V1,$V2) == 1.0)
  void benchmark38(double x,double y) {
	  if(atan2(x,y) == 1.0) {
		  printf("Solved 38");
	  }
	  return;
  }

  // (pow_($V1,$V2) == 1.0)
  void benchmark39(double x,double y) {
	  if(pow(x,y) == 1.0) {
		  printf("Solved 39");
	  }
	  return;
  }

  // pow(x,2) == x + y
  void benchmark40(double x,double y) {
	  if(pow(x,2) == x + y) {
		  printf("Solved 40");
	  }
	  return;
  }

  // pow(x,2) == x + y & x >= -1 & y <=  2
  void benchmark41(double x,double y) {
	  if(pow(x,2) == x + y &&  x >= -1 && y <=  2 ) {
		  printf("Solved 41");
	  }
	  return;
  }

  // pow(x,y) > pow(y,x) & x > 1 & y <= 10
  void benchmark42(double x,double y) {
	  if(pow(x,y) > pow(y,x) && x > 1 && y <= 10) {
	      printf("Solved 42");
      }
	  return;
  }

  // pow(x,y) > pow(y,x) && exp(x,y) > exp(y,x) && y < x ^ 2
  void benchmark43(double x,double y) {
	  if(pow(x,y) > pow(y,x) && exp(y) > exp(x) && y < pow(x,2)) {
		  printf("Solved 43");
	  }
	  return;
  }

  // pow(x,y) > pow(y,x) && exp(x,y) < exp(y,x)
  void benchmark44(double x,double y) {
	  if(pow(x,y) > pow(y,x) && exp(y) < exp(x)) {
		  printf("Solved 44");
	  }
	  return;
  }

  // sqrt(exp(x+y)) < pow(z,x) && x > 0 && y > 1 && z > 1 && y <= x + 2
  void benchmark45(double x,double y,double z) {
      if(sqrt(exp(x+y)) < pow(z,x) && x > 0 && y > 1 && z > 1 && y <= x + 2) {
	      printf("Solved 45");
	  }
      return;
  }

  // sqrt(e^(x + z)) < z^x && x > 0 && y > 1 && z > 1 && y < 1 && y < x + 2 && w = x + 2
  void benchmark46(double x,double y, double z, double w) {
	  if(sqrt(exp(x+z)) < pow(z,x) && x > 0 && y > 1 && z > 1 && y < 1 && y < x + 2 && w == x + 2) {
		  printf("Solved 46");
	  }
	  return;
  }


