/*
 * JPFBenchmark2.cpp
 *
 *  Created on: Feb 5, 2015
 *      Author: zy
 */
#include <cmath>

  // asin(x) < cos(y)*cos(z) - atan(w)
  void benchmark11(double x, double y, double z, double w) {
	  if(asin(x) < cos(y) * cos(z) - atan(w)) {
          printf("Solved 11");
      }
	  return;
  }

  // (asin(x) * asin(y))-1 < atan(z) * atan(w)
  void benchmark12(double x, double y, double z, double w) {
	  if((asin(x) * asin(y)) - 1 < atan(z) * atan(w)) {
		  printf("Solved 12");
	  }
	  return;
  }

  // sin(y) * asin(x) < cos(y)*cos(z) - atan(w)
  void benchmark13(double x, double y, double z, double w) {
	if(sin(y) * asin(x) < cos(y)*cos(z) - atan(w)) {
      printf("Solved 13");
    }
	return;
  }

  // sin(y) * asin(x) - 300 < cos(y)*cos(z) - atan(w)
  void benchmark14(double x, double y, double z, double w) {
	if(sin(y) * asin(x) - 300 < cos(y)*cos(z) - atan(w)) {
      printf("Solved 14");
    }
	return;
  }

  // ((asin(1) * asin(cos(9*57)))-1) < (atan(0) * atan(0)) solution x=1,y=513,z=0,w=0
  void benchmark15(double x, double y, double z, double w) {
	  if(((asin(1) * asin(cos(9*57)))-1) < (atan(0) * atan(0))) {
		  printf("Solved 15");
      }
	  return;
  }

	//((((tan_(($V4-$V1))*cos_(sin_(($V4/$V5))))-atan_((($V2+20.0)+$V3)))+asin_(($V2-15.0))) < ((sin_(($V4*$V4))*cos_((($V1*$V4)*$V5)))-tan_((cos_((($V1*$V4)*$V1))+sin_($V4)))))
	void benchmark16(double x, double y, double z, double w, double v) {
		if(tan(w-x)*cos(sin(w/v)) - atan(y + 20 + z) + asin(y-15) < sin(w * w) * cos(x*w*v) - tan(cos(x*w*x)) + sin(w)){
            printf("Solved 16");
        }
		return;
	}

  // asin(x) * acos(x) < atan(x)
  void benchmark17(double x) {
	  if(asin(x) * acos(x) < atan(x)) {
          printf("Solved 17");
      }
	  return;
  }

  // (1+acos(x)) < asin(x)
  void benchmark18(double x) {
	  if((1+acos(x)) < asin(x)) {
          printf("Solved 18");
      }
	  return;
  }

  // 3*acos(x) < atan(y) + asin(z)
  void benchmark19(double x, double y, double z) {
	  if( 3*acos(x) < atan(y) + asin(z)) {
		  printf("Solved 19");
	  }
	  return;
  }

  // sin(sin((x*y)) < 0 && cos(2x) > 0.25
  void benchmark20(double x, double y) {
	  if(sin(sin(x*y)) < 0 && cos(2*x) > 0.25) {
		  printf("Solved 20");
	  }
	  return;
  }
