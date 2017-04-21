/*
 * Optimization.cpp
 *
 *  Created on: Feb 11, 2015
 *      Author: zy
 */
#include <cmath>

#define PI 3.14159265358979323846
#define E 2.7182818284590452354

  void beale(double x1, double x2) {
    if ((1.5 - x1 * (1.0 - x2)) == 0.0) {
      printf("Solved Beale constraint");
    }
  }

  void freudensteinRoth(double x1, double x2) {
    if ((-13.0 + x1 + ((5.0 - x2) * x2 - 2.0) * x2) + (-29.0 + x1 + ((x2 + 1.0) * x2 - 14.0) * x2) == 0.0) {
    	printf("Solved Freudenstein and Roth constraint");
    }
  }

  // This is public only because JPF keeps generating test cases for it
  // and it is highly annoying to remove them every time we regenerate them.
  double theta(double x1, double x2) {
    if(x1 > 0.0) {
      return atan(x2 / x1) / (2 * PI);
    } else if (x1 < 0.0) {
      return (atan(x2 / x1) / (2 * PI) + 0.5);
    }
    return 0.0;
  }

  void helicalValley(double x1, double x2, double x3) {
    if (10.0 * (x3 - 10.0 * theta(x1, x2)) == 0 && (10.0 * (sqrt(x1 * x1 + x2 * x2) - 1)) == 0.0 && x3 == 0.0) {
    	printf("Solved Helical Valley constraint");
    }
  }

  void powell(double x1, double x2) {
    if ((pow(10, 4) * x1 * x2 - 1.0) == 0.0 && (pow(E, -x1) + pow(E, -x2) - 1.0001) == 0.0) {
    	printf("Solved Powell constraint");
    }
  }

  void rosenbrock(double x1, double x2) {
    if (pow((1.0 - x1), 2) + 100.0 * (pow((x2 - x1 * x1), 2)) == 0.0) {
    	printf("Solved Rosenbrock consraint");
    }
  }

  void wood(double x1, double x2, double x3, double x4) {
    if ((10.0 * (x2 - x1 * x1)) == 0.0 && (1.0 - x1) == 0.0 && (sqrt(90) * (x4 - x3 * x3)) == 0.0
        && (1.0 - x3) == 0.0 && (sqrt(10) * (x2 + x4 - 2.0)) == 0.0 && (pow(10, -0.5) * (x2 - x4)) == 0.0) {
    	printf("Solved Wood constraint");
    }
  }


