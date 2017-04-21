/*
 * JPFBenchmark3.cpp
 *
 *  Created on: Feb 5, 2015
 *      Author: zy
 */
#include <cmath>

  // cos(x*y) < 0 && sin(2x) > 0.25
	void benchmark21(double x, double y) {
		if( cos(x*y) < 0 && sin(2*x) > 0.25) {
			printf("Solved 21");
		}
		return;
	}

  // (sin_(cos_(($V1*$V2))) < cos_(sin_(($V2*$V3)))) &
  // ((sin_((($V4*2.0)-$V2))/(cos_((($V6*2.0)+$V7))+1.0)) == (cos_((($V3*2.0)+$V1))/(sin_((($V4*2.0)+$V5))+1.0))))
	void benchmark22(double x, double y, double z, double w, double v, double t, double q) {
		if((sin(cos(x*y)) < cos(sin(y*z))) &&
		   sin(w*2.0 -y)/(cos(t*2.0+q)+1.0) == (cos(z*2.0+x)/(sin(w*2.0+v)+1.0))) {
			printf("Solved 22");
		}
		return;
	}

  // (sin(2x - y)/(cos(2y + x) + 1) = cos(2z + x)/(sin(2w + y) - 1) &
  // sin(x*y*z*w) > 0 &
  // cos(x*y*z*w) < 0
  void benchmark23(double x, double y, double z, double w) {
	  if(sin(2*x - y)/(cos(2*y + x) + 1) == cos(2*z + x)/(sin(2*w + y) - 1) &&
		 sin(x*y*z*w) > 0 && cos(x*y*z*w) < 0) {
          printf("Solved 23");
      }
	  return;
  }

  //  sin(cos(x*y)) < cos(sin(x*z)) &
  // (sin(2w - y)/(cos(2y + v) + 1) = cos(2z + x)/(sin(2w + v) - 1)
  void benchmark25(double x, double y, double z, double w, double v) {
	  if(sin(cos(x*y)) < cos(sin(x*z)) && (sin(2*w - y)/(cos(2*y + v) + 1) == cos(2*z + x)/(sin(2*w + v) - 1))) {
		  printf("Solved 25");
      }
	  return;
  }

  // sin(cos(x*y)) < cos(sin(x*z))
  // (sin(2w - y)/(cos(2y + v) + 1) = cos(2z + x)/(sin(2w + v) - 1)
  // sin(x*y*z*w) > 0 && cos(x*y*z*w) < 0
  void benchmark26(double x, double y, double z, double w, double v) {
      if(sin(cos(x*y)) < cos(sin(x*z)) && (sin(2*w - y)/(cos(2*y + v) + 1) == cos(2*z + x)/(sin(2*w + v) - 1)) &&  sin(x*y*z*w) > 0 && cos(x*y*z*w) < 0 ) {
          printf("Solved 26");
      }
      return;
  }

  // sin(x*cos(y*sin(z))) > cos(x*sin(y*cos(z))) && sin(cos(x*y)) < cos(sin(x*y))
  void benchmark27(double x, double y, double z) {
	if(sin(x*cos(y*sin(z))) > cos(x*sin(y*cos(z))) && sin(cos(x*y)) < cos(sin(x*y))) {
        printf("Solved 27");
    }
	return;
  }

  void benchmark29(double x) {
	  if(exp(x) > 5){
		  printf("Solved 29");
	  }
	  return;
  }

  void benchmark32(double x) {
	  if(sqrt(x) > 5) {
		  printf("Solved 32");
	  }
	  return;
  }

  // sqrt(sin($V1)) > sqrt(cos($V1))
  void benchmark33(double x) {
	  if(sqrt(sin(x)) > sqrt(cos(x))) {
		  printf("Solved 33");
	  }
	  return;
  }

  // sqrt(sin($V1)) < sqrt(cos($V1))
  void benchmark34(double x) {
	  if(sqrt(sin(x)) < sqrt(cos(x))) {
		  printf("Solved 34");
	  }
	  return;
  }
