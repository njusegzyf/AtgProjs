/*
 * JPFBenchmark1.cpp
 *
 *  Created on: Feb 5, 2015
 *      Author: zy
 */
#include <cmath>
#include<math>
#include<cstdio>

  // sin(x) + cos(y) > 1.0
  void benchmark01(double x, double y) {
    if (sin(x) + cos(y) > 1) {
      printf("Solved 01");
    }
    return;
  }

  // sin(x) - cos(y) < 0.0000000001
  void benchmark02(double x, double y) {
    if (sin(x) - cos(y) < 0.0000000001) {
    	printf("Solved 02");
    }
    return;
  }

  // sin(x) - cos(y) == 0.0
  void benchmark03(double x, double y) {
    if (sin(x) - cos(y) == 0) {
    	printf("Solved 03");
    }
    return;
  }

  // exp(x) > 0.0
  void benchmark04(double x) {
    if (exp(x) > 0) {
    	printf("Solved 04");
    }
    return;
  }

  // sin A + sin B + sin C = 4 * cos A * cos B * cos C
  void benchmark05(double x, double y, double z) {
    if (sin(x) + sin(y) + sin(z) == 4 * cos(x)
        * cos(y) * cos(z)) {
    	printf("Solved 05");
    }
    return;
  }

  // cos A + cos B + cos C > 4 sin A/2 sin B/2 sin C/2
  void benchmark06(double x, double y, double z) {
    if (cos(x) + cos(y) + cos(z) > 4 * sin(x / 2) * sin(y / 2) * sin(z / 2)) {
    	printf("Solved 06");
    }
    return;
  }

  // (sin(2x - y)/(cos(2y + y) + 1) = cos(2z + x)/(sin(2w + y) - 1)
  void benchmark07(double x, double y, double z, double w) {
    if (sin(2 * x - y) / (cos(2 * y + y) + 1) == cos(2 * z + x) / (sin(2 * w + y) - 1)) {
    	printf("Solved 07");
    }
    return;
  }

  // cos(3x+2y-z) * sin(z+x+y) == cos(z*x*y)
  void benchmark08(double x, double y, double z) {
    if (cos(3 * x + 2 * y - z) * sin(z + x + y) == cos(z * x * y)) {
    	printf("Solved 08");
    }
    return;
  }

  // sin(cos(x*y)) < cos(sin(x*y))
  void benchmark09(double x, double y) {
    if (sin(cos(x * y)) < cos(sin(x * y))) {
    	printf("Solved 09");
    }
    return;
  }

  // sin(x*cos(y*sin(z))) > cos(x*sin(y*cos(z)))
  void benchmark10(double x, double y, double z) {
    if (sin(x * cos(y * sin(z))) > cos(x * sin(y * cos(z)))) {
    	printf("Solved 10");
    }
    return;
  }
