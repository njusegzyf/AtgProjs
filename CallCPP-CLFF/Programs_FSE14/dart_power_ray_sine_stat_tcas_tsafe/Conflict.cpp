/*
 * Conflict.cpp
 *
 *  Created on: Nov 18, 2015
 *      Author: zy
 */
#include <cstring>
#include <iostream>
using namespace std;
#define PI 3.14159265358979323846

	static double degToRad = PI/180.0;
    static double g = 68443.0;

    double conflict(double psi1, double vA, double vC, double xC0, double yC0, double psiC, double bank_ang) {
        string PATH("");
        double dmin = 999;
        double dmst = 2;
        double psiA = psi1 * degToRad;
        int signA = 1;
        int signC = 1;

        if (psiA < 0) {
            PATH += "psiA<0\n";
            signA = -1;
        } else {
            PATH += "psiA>=0\n";
        }
        double rA = pow(vA, 2.0) / tan(bank_ang*degToRad) / g;
        double rC = pow(vC, 2.0) / tan(bank_ang*degToRad) / g;

        double t1 = abs(psiA) * rA / vA;
        double dpsiC = signC * t1 * vC/rC;
        double xA = signA*rA*(1-cos(psiA));
        double yA = rA*signA*sin(psiA);

        double xC = xC0 + signC*rC* (cos(psiC)-cos(psiC+dpsiC));
        double yC = yC0 - signC*rC*(sin(psiC)-sin(psiC+dpsiC));

        double xd1 = xC - xA;
        double yd1 = yC - yA;

        double d = sqrt(pow(xd1, 2.0) + pow(yd1, 2.0));
        double minsep;

        // min sep in turn
        if (d < dmin) {
            PATH += "d < dmin\n";
            dmin = d;
        } else {
            PATH += "d >= dmin\n";
        }

        if (dmin < dmst) {
            PATH += "dmin < dmst\n";
            minsep = dmin;
        } else {
            PATH += "dmin >= dmst\n";
            minsep = dmst;
        }
        cout<<PATH+">>> PATH: "<<endl;
        return minsep;
    }



