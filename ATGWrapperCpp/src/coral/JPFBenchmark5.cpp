/*
 * JPFBenchmark5.cpp
 *
 *  Created on: Feb 5, 2015
 *      Author: zy
 */
#include <cmath>

  // e ^ (x + y) == e ^ z
  void benchmark47(double x,double y,double z) {
	  if(exp(x + y) == exp(z)) {
		  printf("Solved 47");
	  }
	  return;
  }

  // x + y != z
  void benchmark48(double x,double y, double z) {
	  if(x + y != z) {
		  printf("Solved 48");
	  }
	  return;
  }

  //x^2 + 3*sqrt(y) < x*y && x < y ^ 2 && x + y < 50 //556 possible integer solutions
  void benchmark49(double x,double y) {
	  if(pow(x,2) + 3*sqrt(y) < x*y && x < pow(y,2) && x + y < 50) {
		  printf("Solved 49");
	  }
	  return;	
  }

  //x^2 + 3*sqrt(y) < x*y && x < y ^ 2 && x + y < 50 && x = -13 + y //18 possible integer solutions
  void benchmark50(double x,double y) {
	  if(pow(x,2) + 3*sqrt(y) < x*y && x < pow(y,2) && x + y < 50 && x == -13 + y) {
		  printf("Solved 50");
	  }
	  return;
  }

  //x ^ tan(y) + z < x * atan(z) && sin(y) + cos(y) + tan(y) >= x - z
  void benchmark52(double x,double y, double z) {
	 if(pow(x,tan(y)) + z < x * atan(z) && sin(y) + cos(y) + tan(y) >= x - z) {
		printf("Solved 52");
	 }
	 return;
  }

  //x ^ tan(y) + z < x * atan(z) && sin(y) + cos(y) + tan(y) >= x - z && atan(x) + atan(y) > y
  void benchmark53(double x,double y, double z) {
	  if(pow(x,tan(y)) + z < x * atan(z) && sin(y) + cos(y) + tan(y) >= x - z && atan(x) + atan(y) > y) {
		  printf("Solved 53");
	  }
	  return;
  }

  //x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t)
  void benchmark56(double x,double y, double z,double w, double t) {
	  if(x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t)) {
		  printf("Solved 56");
	  }
	  return;
  }

  //x + y > z / w && sqrt(x) > z / y && z*2 + w*3 + x*7 < pow(y,6) && z + w > x + y && w < x/y
  void benchmark61(double x,double y, double z,double w) {
	  if(x + y > z / w && sqrt(x) > z / y && z*2 + w*3 + x*7 < pow(y,6) && z + w > x + y && w < x/y) {
		  printf("Solved 61");
	  }
	  return;
  }

  //x + y > z / w && sqrt(x) > z / y && z*2 + w*3 + x*7 < pow(y,6) && z + w > x + y && w < x/y && x > (w+y-z)
  void benchmark62(double x,double y, double z,double w) {
	  if(x + y > z / w && sqrt(x) > z / y && z*2 + w*3 + x*7 < pow(y,6) && z + w > x + y && w < x/y && x > (w+y-z)) {
		  printf("Solved 62");
	  }
	  return;
  }

  //sin(a) > sin(b) > sin(c) > sin(d) > sin(e) > sin(f) > sin(g) > sin(h) > sin(i) > sin(j) > sin(k) > sin(l) > sin(m) > sin(n) > sin(o) > sin(p) > sin(q) > sin(r) > sin(s) > sin(t) > sin(u) > sin(v) > sin(x) > sin(z)
  void benchmark70(double a,double b, double c,double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n, double o, double p, double q, double r, double s, double t, double u, double v, double x, double z) {
	  if(sin(a) > sin(b) && sin(b) > sin(c) && sin(c) > sin(d) && sin(d) > sin(e) && sin(e) > sin(f) && sin(f) > sin(g) && sin(g) > sin(h) && sin(h) > sin(i) && sin(i) > sin(j) && sin(j) > sin(k) && sin(k) > sin(l) && sin(l) > sin(m) && sin(m) > sin(n) && sin(n) > sin(o) && sin(o) > sin(p) && sin(p) > sin(q) && sin(q) > sin(r) && sin(r) > sin(s) && sin(s) > sin(t) && sin(t) > sin(u) && sin(u) > sin(v) && sin(v) > sin(x) && sin(x) > sin(z)) {
		  printf("Solved 70");
	  }
	  return;
  }
