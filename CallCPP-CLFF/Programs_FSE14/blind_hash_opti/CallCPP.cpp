/*
 * CallCPP.cpp
 *
 *  Created on: Feb 7, 2015
 *      Author: zy
 */
#include "cn_nju_seg_atg_callCPP_CallCPP.h"
#include <math.h>
#include <limits>
#include <iomanip>
#include <fstream>
#include <cstring>
#include <malloc.h>
using namespace std;

char* jstringTostring(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

class EffectiveJavaHashCode {
private:
	int x;
    long y;
    int z;

public:
    EffectiveJavaHashCode(int a, long b, int c) {
	    x = a;
	    y = b;
	    z = c;
    }

    int hashCode() {
    	int h = x;
    	h = h * 31 + int(y ^ (y >> 32));
    	h = h * 31 + z;
    	return h;
    }
};

#define PI 3.14159265358979323846
#define E 2.7182818284590452354

double theta(double x1, double x2) {
  if(x1 > 0.0) {
    return atan(x2 / x1) / (2 * PI);
  } else if (x1 < 0.0) {
    return (atan(x2 / x1) / (2 * PI) + 0.5);
  }
  return 0.0;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callCommitEarly
 * Signature: (IILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callCommitEarly
  (JNIEnv *env, jobject, jint a, jint b, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<"node1\n";
    int c = a * b;
    if ((bFile<<"node2 "<<b-2<<" b>2\n",b > 2) && (bFile<<"node2 "<<b-c<<" b==c\n",b == c)) {
    	bFile<<"node3\n";
        printf("Solved early commitment");
    }
    bFile<<"node4\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    testCollision1
 * Signature: (IJIIJILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTestCollision1
  (JNIEnv *env, jobject, jint x1, jlong y1, jint z1, jint x2, jlong y2, jint z2, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<"node1\n";
	EffectiveJavaHashCode o1(x1, y1, z1);
	EffectiveJavaHashCode o2(x2, y2, z2);
	if (bFile<<"node2 "<<o1.hashCode()-o2.hashCode()<<" o1.hashCode()==o2.hashCode()\n",o1.hashCode() == o2.hashCode()) {
		bFile<<"node3\n";
		printf("Solved hash collision 1");
	}
	bFile<<"node4\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    testCollision2
 * Signature: (JIJILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTestCollision2
  (JNIEnv *env, jobject, jlong y1, jint z1, jlong y2, jint z2, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<"node1\n";
	EffectiveJavaHashCode o1(1, y1, z1);
	EffectiveJavaHashCode o2(2, y2, z2);
	if (bFile<<"node2 "<<o1.hashCode()-o2.hashCode()<<" o1.hashCode()==o2.hashCode()\n",o1.hashCode() == o2.hashCode()) {
		bFile<<"node3\n";
		printf("Solved hash collision 2");
	}
	bFile<<"node4\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    testCollision3
 * Signature: (JJLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTestCollision3
  (JNIEnv *env, jobject, jlong y1, jlong y2, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<"node1\n";
	EffectiveJavaHashCode o1(1234, y1, 3141);
	EffectiveJavaHashCode o2(5678, y2, 3141);
	if (bFile<<"node2 "<<o1.hashCode()-o2.hashCode()<<" o1.hashCode()==o2.hashCode()\n",o1.hashCode() == o2.hashCode()) {
		bFile<<"node3\n";
		printf("Solved hash collision 3");
	}
	bFile<<"node4\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    testCollision4
 * Signature: (IJILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTestCollision4
  (JNIEnv *env, jobject, jint x1, jlong y1, jint z1, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<"node1\n";
	EffectiveJavaHashCode o1(1234, 6454505372016058754, 3141);
	EffectiveJavaHashCode o2(x1, y1, z1);
	if (bFile<<"node2 "<<o1.hashCode()-o2.hashCode()<<" o1.hashCode()==o2.hashCode()\n",o1.hashCode() == o2.hashCode()) {
		bFile<<"node3\n";
		printf("Solved hash collision 4");
	}
	bFile<<"node4\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    testCollision5
 * Signature: (JILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTestCollision5
  (JNIEnv *env, jobject, jlong y1, jint z1, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<"node1\n";
	EffectiveJavaHashCode o1(1234, 6454505372016058754, 3141);
	EffectiveJavaHashCode o2(5678, y1, z1);
	if (bFile<<"node2 "<<o1.hashCode()-o2.hashCode()<<" o1.hashCode()==o2.hashCode()\n",o1.hashCode() == o2.hashCode()) {
		bFile<<"node3\n";
		printf("Solved hash collision 5");
	}
	bFile<<"node4\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    beale
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBeale
  (JNIEnv *env, jobject, jdouble x1, jdouble x2, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

    if (bFile<<"node1 "<<(1.5-x1*(1.0-x2))-0.0<<" (1.5-x1*(1.0-x2))==0.0\n",(1.5 - x1 * (1.0 - x2)) == 0.0) {
    	bFile<<"node2\n";
    	printf("Solved Beale constraint");
    }
    bFile<<"node3\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    freudensteinRoth
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callFreudensteinRoth
  (JNIEnv *env, jobject, jdouble x1, jdouble x2, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

    if (bFile<<"node1 "<<(-13.0+x1+((5.0-x2)*x2-2.0)*x2)+(-29.0+x1+((x2+1.0)*x2-14.0)*x2)-0.0<<
    		" (-13.0+x1+((5.0-x2)*x2-2.0)*x2)+(-29.0+x1+((x2+1.0)*x2-14.0)*x2)==0.0\n",
    		(-13.0 + x1 + ((5.0 - x2) * x2 - 2.0) * x2) + (-29.0 + x1 + ((x2 + 1.0) * x2 - 14.0) * x2) == 0.0) {
    	bFile<<"node2\n";
    	printf("Solved Freudenstein and Roth constraint");
    }
    bFile<<"node3\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    helicalValley
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callHelicalValley
  (JNIEnv *env, jobject, jdouble x1, jdouble x2, jdouble x3, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if (bFile<<"node1 "<<10.0*(x3-10.0*theta(x1,x2))-0<<" 10.0*(x3-10.0*theta(x1,x2))==0\n",(10.0 * (x3 - 10.0 * theta(x1, x2)) == 0) &&
    		(bFile<<"node1 "<<(10.0*(sqrt(x1*x1+x2*x2)-1))-0.0<<" (10.0*(sqrt(x1*x1+x2*x2)-1))==0.0\n",(10.0 * (sqrt(x1 * x1 + x2 * x2) - 1)) == 0.0) &&
    		(bFile<<"node1 "<<x3-0.0<<" x3==0.0\n",x3 == 0.0)) {
    	bFile<<"node2\n";
    	printf("Solved Helical Valley constraint");
    }
    bFile<<"node3\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    powell
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callPowell
  (JNIEnv *env, jobject, jdouble x1, jdouble x2, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if ((bFile<<"node1 "<<(pow(10,4)*x1*x2-1.0)-0.0<<" (pow(10,4)*x1*x2-1.0)==0.0\n",(pow(10, 4) * x1 * x2 - 1.0) == 0.0) &&
    		(bFile<<"node1 "<<(pow(E,-x1)+pow(E,-x2)-1.0001)-0.0<<" (pow(E,-x1)+pow(E,-x2)-1.0001)==0.0\n",(pow(E, -x1) + pow(E, -x2) - 1.0001) == 0.0)) {
    	bFile<<"node2\n";
    	printf("Solved Powell constraint");
    }
    bFile<<"node3\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    rosenbrock
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callRosenbrock
  (JNIEnv *env, jobject, jdouble x1, jdouble x2, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if (bFile<<"node1 "<<pow((1.0-x1),2)+100.0*(pow((x2-x1*x1),2))-0.0<<
    		" pow((1.0-x1),2)+100.0*(pow((x2-x1*x1),2))==0.0\n",
    		pow((1.0 - x1), 2) + 100.0 * (pow((x2 - x1 * x1), 2)) == 0.0) {
    	bFile<<"node2\n";
    	printf("Solved Rosenbrock consraint");
    }
    bFile<<"node3\n";
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    wood
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callWood
  (JNIEnv *env, jobject, jdouble x1, jdouble x2, jdouble x3, jdouble x4, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if ((bFile<<"node1 "<<(10.0*(x2-x1*x1))-0.0<<" (10.0*(x2-x1*x1))==0.0\n",(10.0 * (x2 - x1 * x1)) == 0.0) &&
    		(bFile<<"node1 "<<(1.0-x1)-0.0<<" (1.0-x1)==0.0\n",(1.0 - x1) == 0.0) &&
    		(bFile<<"node1 "<<(sqrt(90)*(x4-x3*x3))-0.0<<" (sqrt(90)*(x4-x3*x3))==0.0\n",(sqrt(90) * (x4 - x3 * x3)) == 0.0) &&
    		(bFile<<"node1 "<<(1.0-x3)-0.0<<" (1.0-x3)==0.0\n",(1.0 - x3) == 0.0) &&
    		(bFile<<"node1 "<<(sqrt(10)*(x2+x4-2.0))-0.0<<" (sqrt(10)*(x2+x4-2.0))==0.0\n",(sqrt(10) * (x2 + x4 - 2.0)) == 0.0) &&
    		(bFile<<"node1 "<<(pow(10,-0.5)*(x2-x4))-0.0<<" (pow(10,-0.5)*(x2-x4))==0.0\n",(pow(10, -0.5) * (x2 - x4)) == 0.0)) {
    	bFile<<"node2\n";
    	printf("Solved Wood constraint");
    }
    bFile<<"node3\n";
}
