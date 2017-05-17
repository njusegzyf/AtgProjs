/*
 * TurnLogic.cpp
 *
 *  Created on: Feb 12, 2015
 *      Author: zy
 */

#include <cmath>

#define PI 3.14159265358979323846
#define TOLERANCE 0.0000001

//class TurnLogic {
//private:
	static double twoPi = PI * 2;
	static double deg = PI / 180;
	static double gacc = 32.0;

	// Calc only 1st component: phi: the heading change
	double turnLogic(double x0, double y0, double gspeed, double x1, double y1, double x2, double y2, double dt) {
		double dx = x0 - x1;
		double dy = y0 - y1;
		if (dx == 0 && dy == 0)
        // if (abs(dx) <= TOLERANCE && abs(dy) <= TOLERANCE)
        // if (dx <= TOLERANCE && dx >= -TOLERANCE && dy <= TOLERANCE && dy >= -TOLERANCE)
		// if (fabs(dx) + fabs(dy) <= TOLERANCE)
			return 0.0;
		double instHdg = 90 * deg - atan2(dy, dx);
		if (instHdg < 0.) instHdg += 360 * deg;
		if (instHdg > 2 * PI) instHdg -= 360 * deg;

		dx = x1 - x2;
		dy = y1 - y2;
		if (dx == 0 && dy == 0)
			return 0.0;
		double instHdg0 = 90 * deg - atan2(dy, dx);
		if (instHdg0 < 0.) instHdg0 += 360 * deg;
		if (instHdg0 > 2 * PI) instHdg0 -= 360 * deg;

//		double hdg_diff = normAngle(instHdg - instHdg0);
		double angle = instHdg - instHdg0;
		double hdg_diff;
		if (angle < -PI) {
			hdg_diff = angle + twoPi;
		}
		else if (angle > PI) {
			hdg_diff = angle - twoPi;
		}
		else {
			hdg_diff = angle;
		}

		double phi = atan2(hdg_diff * gspeed, gacc * dt);
		return phi / deg;
	}

//	double normAngle(double angle) {
//		if (angle < -PI) {
//			return angle + twoPi;
//		}
//		if (angle > PI) {
//			return angle - twoPi;
//		}
//		return angle;
//	}
//};


