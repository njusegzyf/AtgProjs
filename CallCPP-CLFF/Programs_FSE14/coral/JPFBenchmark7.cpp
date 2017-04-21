/*
 * JPFBenchmark7.cpp
 *
 *  Created on: Jun 6, 2016
 *      Author: zy
 */
#include <cmath>

  // log($V1) == 2.0
  void benchmark28(double x) {
	  if(log(x) == 2) {
		  printf("Solved 28");
	  }
	  return;
  }

  // log_10($v1) == 2.0
  void benchmark30(double x) {
	  if(log10(x) == 2){
		  printf("Solved 30");
	  }
	  return;
  }

  void benchmark31(double x) {
	  if(round(x) > 5) {
	      printf("Solved 31");
      }
	  return;
  }

  void benchmark36(double x,double y,double z) {
	  if(log10(z)*(1.0/sqrt(sin(x))) == sqrt(cos(exp(y)))) {
		  printf("Solved 36");
	  }
	  return;
  }

  // ((log10(tan_($V3))/(1.0/sqrt(sin_($V1)))) == sqrt(cos_(exp($V2))))
  void benchmark37(double x,double y,double z) {
	  if(log10(tan(z))*(1.0/sqrt(sin(x))) == sqrt(cos(exp(y)))) {
		  printf("Solved 37");
	  }
	  return;
  }

  //x^2 + 3*sqrt(y) < x*y && x < y ^ 2 && x + y < 50 && x = -13 + y && x ^ x < log10(y) //one integer solution
  void benchmark51(double x,double y) {
	  if(pow(x,2) + 3*sqrt(y) < x*y && x < pow(y,2) && x + y < 50 && pow(x,x) < log10(y)) {
          printf("Solved 51");
	  }
	  return;
  }

  //x ^ tan(y) + z < x * atan(z) && sin(y) + cos(y) + tan(y) >= x - z && atan(x) + atan(y) > y && log(x^tan(y)) < log(z)
  void benchmark54(double x,double y, double z) {
      if(pow(x,tan(y)) + z < x * atan(z) && sin(y) + cos(y) + tan(y) >= x - z && atan(x) + atan(y) > y && log(pow(x,tan(y))) < log(z)) {
  		  printf("Solved 54");
  	  }
      return;
  }

  //x ^ tan(y) + z < x * atan(z) &&  sin(y) + cos(y) + tan(y) >= x - z &&  atan(x) + atan(y) > y &&  log(x^tan(y)) < log(z) &&  sqrt(y+z) > sqrt(x^(x-y))
  void benchmark55(double x,double y, double z) {
  	  if(
  		atan(x) + atan(y) > y
  		&&
  		sqrt(y+z) > sqrt(pow(x,(x-y)))
  		&&
  	    pow(x,tan(y)) + z < x * atan(z)
	 	&&
	 	sin(y) + cos(y) + tan(y) >= x - z
  	  	&&
  	  	log(pow(x,tan(y))) < log(z)   )
  	  {
  		  printf("Solved 55");
  	  }
  	  return;
  }

  //x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t))
  void benchmark57(double x,double y, double z,double w, double t) {
  	  if(x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t))) {
  		  printf("Solved 57");
  	  }
      return;
  }

  //x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t)) && tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z)
  void benchmark58(double x,double y, double z,double w, double t) {
  	if(x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t)) && tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z)) {
  		printf("Solved 58");
  	}
  	return;
  }

  //x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t))	&& tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z) && w = t * 3 / 4
  void benchmark59(double x,double y, double z,double w, double t) {
  	if(x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t)) && tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z) && w == t * 3 / 4) {
  		printf("Solved 59");
  	}
  	return;
  }

  //x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t))	&& tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z) && w = t * 3 / 4 && x < 2y - 3z
  void benchmark60(double x,double y, double z,double w, double t) {
  	if(x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t) && pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t)) && tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z) && w == t * 3 / 4 && x < 2*y - 3*z) {
  		printf("Solved 60");
  	}
  	return;
  }

  //x + y > z / w && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,6) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z)
  void benchmark63(double x,double y, double z,double w, double t) {
	  if(x + y > z / w && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,6) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z)) {
	      printf("Solved 63");
	  }
	  return;
  }

  //x + y > z / (w + t) && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) &&	x * (t + y) > log(w*z*3)
  void benchmark64(double x,double y, double z,double w, double t) {
	  if(x + y > z / (w + t) && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) &&	x * (t + y) > log(w*z*3)) {
	      printf("Solved 64");
	  }
	  return;
  }

  //x + y > z / (w + t) && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3)
  void benchmark65(double x,double y, double z,double w, double t) {
	  if(x + y > z / (w + t) && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3)) {
		  printf("Solved 65");
	  }
	  return;
  }

  //x + y > (z+ t) / (w + t) && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3) && cos(t) > cos(y)
  void benchmark66(double x,double y, double z,double w, double t) {
	  if(x + y > (z+ t) / (w + t) && sqrt(x) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3) && cos(t) > cos(y)) {
		  printf("Solved 66");
	  }
	  return;
  }

  //x - y + tan(v)> (z+ t) / (w + t) && sqrt(x-t) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t)*cos(v) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3) && cos(t) * sin(v) > cos(y)
  void benchmark67(double x,double y, double z,double w, double t, double v) {
	  if(x - y + tan(v)> (z+ t) / (w + t) && sqrt(x-t) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t)*cos(v) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3) && cos(t) * sin(v) > cos(y)) {
		  printf("Solved 67");
	  }
	  return;
  }

  //x - y + tan(v)> (z+ t) / (w + t) && sqrt(x-t) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t)*cos(v) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3) && cos(t) * sin(v) > cos(y)
  void benchmark68(double x,double y, double z,double w, double t, double v) {
	  if(
			  z + w > x + y
			  && x > (w+y-z)
			  && w < x/y
			  && sqrt(x-t) > z / y
			  && log(x*y) > log(t+w+z)
			  && log10(t*x) < sqrt(w*y*z)
			  && x - y + tan(v)> (z+ t) / (w + t)
			  && x * cos(t + y) > log(w*z*3)
			  && z*2 + w*3 + x*7 < pow(y,t)*cos(v)
			  && cos(t) * sin(v) > cos(y)      )
	  {
		  printf("Solved 68");
	  }
	  return;
  }

  //x - y + tan(v)> (z+ t) / (w + t) && sqrt(x-t) > z / y && log(x*y) > log(t+w+z) && z*2 + w*3 + x*7 < pow(y,t)*cos(v) && z + w > x + y && w < x/y && x > (w+y-z) && log10(t*x) < sqrt(w*y*z) && x * cos(t + y) > log(w*z*3) && cos(t) * sin(v) > cos(y) && sin(x*y) + sin(z*w) + sin(t*v) < cos(x*y) + cos(z*w) + cos(t*v)
  void benchmark69(double x,double y, double z,double w, double t, double v) {
	  if(
			  z + w > x + y
			  && x > (w+y-z)
			  && w < x/y
			  && sqrt(x-t) > z / y
			  && log(x*y) > log(t+w+z)
			  && log10(t*x) < sqrt(w*y*z)
			  && x - y + tan(v)> (z+ t) / (w + t)
			  && x * cos(t + y) > log(w*z*3)
			  && z*2 + w*3 + x*7 < pow(y,t)*cos(v)
			  && cos(t) * sin(v) > cos(y)
			  && sin(x*y) + sin(z*w) + sin(t*v) < cos(x*y) + cos(z*w) + cos(t*v)   )
	  {
	      printf("Solved 69");
	  }
	  return;
  }

  }
