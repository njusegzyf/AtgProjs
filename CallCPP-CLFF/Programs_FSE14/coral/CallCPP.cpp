/*
 * CallCPP.cpp
 *
 *  Created on: 20161103
 *      Author: jackzhang
 */
#include "cn_nju_seg_atg_callCPP_CallCPP.h"
#include <math.h>
#include <cmath>
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

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark01
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark01
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

	if (bFile<<"node1@benchmark01 "<<sin(x)+cos(y)-1<<" expression@1\n",sin(x) + cos(y) > 1) 
	{
		bFile<<"node2@benchmark01\n";
	    	printf("Solved 01");
	}
	bFile<<"node3@benchmark01\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark02
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark02
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

    if (bFile<<"node1@benchmark02 "<<sin(x)-cos(y)-0.0000000001<<" expression@1\n",sin(x) - cos(y) < 0.0000000001) 
	{
    	bFile<<"node2@benchmark02\n";
    	printf("Solved 02");
    }
    bFile<<"node3@benchmark02\n";
    return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark03
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark03
  (JNIEnv * env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

    if (bFile<<"node1@benchmark03 "<<sin(x)-cos(y)-0<<" expression@1\n",sin(x) - cos(y) == 0) 
	{
    	bFile<<"node2@benchmark03\n";
    	printf("Solved 03");
    }
    bFile<<"node3benchmark03\n";
    return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark04
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark04
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

    if (bFile<<"node1@benchmark04 "<<exp(x)-0<<" expression@1\n",exp(x) > 0) {
    	bFile<<"node2@benchmark04\n";
    	printf("Solved 04");
    }
	bFile<<"node3@benchmark04\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark05
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark05
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if (bFile<<"node1@benchmark05 "<<sin(x)+sin(y)+sin(z)-4*cos(x)*cos(y)*cos(z)<<" expression@1\n",sin(x) + sin(y) + sin(z) == 4 * cos(x) * cos(y) * cos(z))
	{
    	bFile<<"node2@benchmark05\n";
    	printf("Solved 05");
    }
	bFile<<"node3@benchmark05\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark06
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark06
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

    if (bFile<<"node1@benchmark06 "<<cos(x)+cos(y)+cos(z)-4*sin(x/2)*sin(y/2)*sin(z/2)<<" expression@1\n",cos(x) + cos(y) + cos(z) > 4 * sin(x / 2) * sin(y / 2) * sin(z / 2))
    {
    	bFile<<"node2@benchmark06\n";
    	printf("Solved 06");
    }
	bFile<<"node3@benchmark06\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark07
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark07
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if (bFile<<"node1@benchmark07 "<<sin(2*x-y)/(cos(2*y+y)+1)-cos(2*z+x)/(sin(2*w+y)-1)<<" expression@1\n",sin(2*x-y)/(cos(2*y+y)+1)==cos(2*z+x)/(sin(2*w+y)-1))
    {
    	bFile<<"node2@benchmark07\n";
    	printf("Solved 07");
    }
	bFile<<"node3@benchmark07\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark08
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark08
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if (bFile<<"node1@benchmark08 "<<cos(3*x+2*y-z)*sin(z+x+y)-cos(z*x*y)<<" expression@1\n",cos(3*x+2*y-z)*sin(z+x+y)==cos(z*x*y))
	{
    	bFile<<"node2@benchmark08\n";
    	printf("Solved 08");
    }
	bFile<<"node3@benchmark08\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark09
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark09
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if (bFile<<"node1@benchmark09 "<<sin(cos(x*y))-cos(sin(x*y))<<" expression@1\n",sin(cos(x * y)) < cos(sin(x * y)))
	{
    	bFile<<"node2@benchmark09\n";
    	printf("Solved 09");
    }
	bFile<<"node3@benchmark09\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark10
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark10
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if (bFile<<"node1@benchmark10 "<<sin(x*cos(y*sin(z)))-cos(x*sin(y*cos(z)))<<" expression@1\n",sin(x * cos(y * sin(z))) > cos(x * sin(y * cos(z))))
	{
    	bFile<<"node2@benchmark10\n";
    	printf("Solved 10");
    }
	bFile<<"node3@benchmark10\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark11
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark11
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark11 "<<asin(x)-(cos(y)*cos(z)-atan(w))<<" expression@1\n",asin(x) < cos(y) * cos(z) - atan(w))
	{
	      bFile<<"node2@benchmark11\n";
          printf("Solved 11");
    }
	bFile<<"node3@benchmark11\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark12
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark12
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark12 "<<(asin(x)*asin(y))-1-atan(z)*atan(w)<<" expression@1\n",(asin(x) * asin(y)) - 1 < atan(z) * atan(w))
	{
	    bFile<<"node2@benchmark12\n";
		printf("Solved 12");
	}
	bFile<<"node3@benchmark12\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark13
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark13
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark13 "<<sin(y)*asin(x)-(cos(y)*cos(z)-atan(w))<<" expression@1\n",sin(y) * asin(x) < cos(y)*cos(z) - atan(w))
	{
	  bFile<<"node2@benchmark13\n";
      printf("Solved 13");
    }
	bFile<<"node3@benchmark13\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark14
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark14
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark14 "<<sin(y)*asin(x)-300-(cos(y)*cos(z)-atan(w))<<" expression@1\n",sin(y) * asin(x) - 300 < cos(y)*cos(z) - atan(w))
	{
	    bFile<<"node2@benchmark14\n";
		printf("Solved 14");
    }
	bFile<<"node3@benchmark14\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark15
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark15
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark15 "<<((asin(1)*asin(cos(9*57)))-1)-(atan(0)*atan(0))<<" expression@1\n", ((asin(1) * asin(cos(9*57)))-1) < (atan(0) * atan(0)))
	{
	    bFile<<"node2@benchmark15\n";
        printf("Solved 15");
    }
	bFile<<"node3@benchmark15\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark16
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark16
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jdouble v, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark16 "<<tan(w-x)*cos(sin(w/v))-atan(y+20+z)+asin(y-15)-(sin(w*w)*cos(x*w*v)-tan(cos(x*w*x))+sin(w))<<" expression@1\n",tan(w-x)*cos(sin(w/v)) - atan(y + 20 + z) + asin(y-15) < sin(w * w) * cos(x*w*v) - tan(cos(x*w*x)) + sin(w))
	{
	    bFile<<"node2@benchmark16\n";
		printf("Solved 16");
    }
	bFile<<"node3@benchmark16\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark17
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark17
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark17 "<<asin(x)*acos(x)-atan(x)<<" expression@1\n",asin(x) * acos(x) < atan(x)) 
	{
	      bFile<<"node2@benchmark17\n";
		  printf("Solved 17");
    }
	bFile<<"node3@benchmark17\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark18
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark18
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark18 "<<(1+acos(x))-asin(x)<<" expression@1\n",(1+acos(x)) < asin(x)) 
	 {
	      bFile<<"node2@benchmark18\n";
		  printf("Solved 18");
    }
	bFile<<"node3@benchmark18\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark19
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark19
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark19 "<<3*acos(x)-(atan(y)+asin(z))<<" expression@1\n",3*acos(x) < atan(y) + asin(z))
	  {
	      bFile<<"node2@benchmark19\n";
		  printf("Solved 19");
	  }
	bFile<<"node3@benchmark19\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark20
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark20
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if((bFile<<"node1@benchmark20 "<<sin(sin(x*y))-0<<" expression@2\n",sin(sin(x*y)) < 0) &&
		(bFile<<"node1@benchmark20 "<<cos(2*x)-0.25<<" expression@3\n",cos(2*x) > 0.25))
	  {
	      bFile<<"node2@benchmark20\n";
		  printf("Solved 20");
	  }
	bFile<<"node3@benchmark20\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark21
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark21
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark21 "<<cos(x*y)-0<<" expression@2\n",cos(x*y) < 0) &&
		(bFile<<"node1@benchmark21 "<<sin(2*x)-0.25<<" expression@3\n",sin(2*x) > 0.25))
	{
		bFile<<"node2@benchmark21\n";
		printf("Solved 21");
	}
	bFile<<"node3@benchmark21\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark22
 * Signature: (DDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark22
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jdouble v, jdouble t, jdouble q, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark22 "<<sin(cos(x*y))-cos(sin(y*z))<<" expression@2\n",sin(cos(x*y)) < cos(sin(y*z))) &&
	   (bFile<<"node1@benchmark22 "<<sin(w*2.0-y)/(cos(t*2.0+q)+1.0)-(cos(z*2.0+x)/(sin(w*2.0+v)+1.0))<<" expression@3\n",sin(w*2.0 -y)/(cos(t*2.0+q)+1.0) == (cos(z*2.0+x)/(sin(w*2.0+v)+1.0))))
	{
		bFile<<"node2@benchmark22\n";
		printf("Solved 22");
	}
	bFile<<"node3@benchmark22\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark23
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark23
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if((bFile<<"node1@benchmark23 "<<sin(2*x-y)/(cos(2*y+x)+1)-cos(2*z+x)/(sin(2*w+y)-1)<<" expression@2\n",sin(2*x - y)/(cos(2*y + x) + 1) == cos(2*z + x)/(sin(2*w + y) - 1)) &&
		 (bFile<<"node1@benchmark23 "<<sin(x*y*z*w)-0<<" expression@3n",sin(x*y*z*w) > 0) &&
		 (bFile<<"node1@benchmark23 "<<cos(x*y*z*w)-0<<" expression@4\n",cos(x*y*z*w) < 0)) 
	{
		  bFile<<"node2@benchmark23\n";
		  printf("Solved 23");
    }
	bFile<<"node3@benchmark23\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark25
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark25
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jdouble v, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if((bFile<<"node1@benchmark25 "<<sin(cos(x*y))-cos(sin(x*z))<<" expression@2\n",sin(cos(x*y)) < cos(sin(x*z)))
	  &&(bFile<<"node1@benchmark25 "<<(sin(2*w-y)/(cos(2*y+v)+1)-cos(2*z+x)/(sin(2*w+v)-1))<<" expression@3\n",(sin(2*w - y)/(cos(2*y + v) + 1) == cos(2*z + x)/(sin(2*w + v) - 1))))
	{
		  bFile<<"node2@benchmark25\n";
		  printf("Solved 25");
    }
	bFile<<"node3@benchmark25\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark26
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark26
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jdouble v, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if((bFile<<"node1@benchmark26 "<<sin(cos(x*y))-cos(sin(x*z))<<" expression@2\n",sin(cos(x*y)) < cos(sin(x*z))) &&
    	(bFile<<"node1@benchmark26 "<<sin(2*w-y)/(cos(2*y+v)+1)-cos(2*z+x)/(sin(2*w+v)-1)<<" expression@3\n",sin(2*w - y)/(cos(2*y + v) + 1) == cos(2*z + x)/(sin(2*w + v) - 1)) &&
    	(bFile<<"node1@benchmark26 "<<sin(x*y*z*w)-0<<" expression@4\n",sin(x*y*z*w) > 0) &&
    	(bFile<<"node1@benchmark26 "<<cos(x*y*z*w)-0<<" expression@5\n",cos(x*y*z*w) < 0))
	{
    	bFile<<"node2@benchmark26\n";
    	printf("Solved 26");
    }
	bFile<<"node3@benchmark26\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark27
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark27
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark27 "<<sin(x*cos(y*sin(z)))-cos(x*sin(y*cos(z)))<<" expression@2\n",sin(x*cos(y*sin(z))) > cos(x*sin(y*cos(z)))) &&
		(bFile<<"node1@benchmark27 "<<sin(cos(x*y))-cos(sin(x*y))<<" expression@3\n",sin(cos(x*y)) < cos(sin(x*y))))
	{
		bFile<<"node2@benchmark27\n";
		printf("Solved 27");
    }
	bFile<<"node3@benchmark27\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark29
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark29
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark29 "<<exp(x)-5<<" expression@1\n",exp(x) > 5)
	  {
		  bFile<<"node2@benchmark29\n";
		  printf("Solved 29");
	  }
	bFile<<"node3@benchmark29\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark32
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark32
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark32 "<<sqrt(x)-5<<" expression@1\n",sqrt(x) > 5)
	 {
		  bFile<<"node2@benchmark32\n";
		  printf("Solved 32");
	  }
	bFile<<"node3@benchmark32\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark33
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark33
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark33 "<<sqrt(sin(x))-sqrt(cos(x))<<" expression@1\n",sqrt(sin(x)) > sqrt(cos(x)))
	{
		bFile<<"node2@benchmark33\n";
		printf("Solved 33");
	}
	bFile<<"node3@benchmark33\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark34
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark34
  (JNIEnv *env, jobject arg, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

    if(bFile<<"node1@benchmark34 "<<sqrt(sin(x))-sqrt(cos(x))<<" expression@1\n",sqrt(sin(x)) < sqrt(cos(x)) )
	{
		bFile<<"node2@benchmark34\n";
		printf("Solved 34");
	}
	bFile<<"node3@benchmark34\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark35
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark35
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark35 "<<1.0/sqrt(sin(x))-sqrt(cos(exp(y)))<<" expression@1\n",1.0/sqrt(sin(x)) > sqrt(cos(exp(y))))
	{
		bFile<<"node2@benchmark35\n";
		printf("Solved 35");
	}
	bFile<<"node3@benchmark35\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark38
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark38
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	  if(bFile<<"node1@benchmark38 "<<atan2(x,y)-1.0<<" expression@1\n",atan2(x,y) == 1.0)
	  {
		  bFile<<"node2@benchmark38\n";
		  printf("Solved 38");
	  }
	bFile<<"node3@benchmark38\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark39
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark39
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark39 "<<pow(x,y)-1.0<<" expression@1\n",pow(x,y) == 1.0) 
	{
		bFile<<"node2@benchmark39\n";
		printf("Solved 39");
	}
	bFile<<"node3@benchmark39\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark40
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark40
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark40 "<<pow(x,2)-(x+y)<<" expression@1\n",pow(x,2) == x + y) 
	{
		bFile<<"node2@benchmark40\n";
		printf("Solved 40");
	}
	bFile<<"node3@benchmark40\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark41
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark41
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark41 "<<pow(x,2)-(x+y)<<" expression@2\n",pow(x,2) == x + y) &&
		(bFile<<"node1@benchmark41 "<<x+1<<" expression@3\n",x >= -1) &&
		(bFile<<"node1@benchmark41 "<<y-2<<" expression@4\n",y <= 2))
	{
		bFile<<"node2@benchmark41\n";
		printf("Solved 41");
	}
	bFile<<"node3@benchmark41\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark42
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark42
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark42 "<<pow(x,y)-pow(y,x)<<" expression@2\n",pow(x,y) > pow(y,x)) &&
		(bFile<<"node1@benchmark42 "<<x-1<<" expression@3\n",x > 1) &&
		(bFile<<"node1@benchmark42 "<<y-10<<" expression@4\n",y <= 10))
	{
		bFile<<"node2@benchmark42\n";
		printf("Solved 42");
    }
	bFile<<"node3@benchmark42\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark43
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark43
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark43 "<<pow(x,y)-pow(y,x)<<" expression@2\n",pow(x,y) > pow(y,x)) &&
		(bFile<<"node1@benchmark43 "<<exp(y)-exp(x)<<" expression@3\n",exp(y) > exp(x)) &&
		(bFile<<"node1@benchmark43 "<<y-pow(x,2)<<" expression@4\n",y < pow(x,2)))
	{
		bFile<<"node2@benchmark43\n";
		printf("Solved 43");
	}
	bFile<<"node3@benchmark43\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark44
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark44
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark44 "<<pow(x,y)-pow(y,x)<<" expression@2\n",pow(x,y) > pow(y,x)) &&
		(bFile<<"node1@benchmark44 "<<exp(y)-exp(x)<<" expression@3\n",exp(y) < exp(x)))
	{
		bFile<<"node2@benchmark44\n";
		printf("Solved 44");
	}
	bFile<<"node3@benchmark44\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark45
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark45
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if((bFile<<"node1@benchmark45 "<<sqrt(exp(x+y))-pow(z,x)<<" expression@2\n",sqrt(exp(x+y)) < pow(z,x)) &&
    	(bFile<<"node1@benchmark45 "<<x-0<<" expression@3\n",x > 0) &&
		(bFile<<"node1@benchmark45 "<<y-1<<" expression@4\n",y > 1) &&
    	(bFile<<"node1@benchmark45 "<<z-1<<" expression@5\n",z > 1) &&
		(bFile<<"node1@benchmark45 "<<y-(x+2)<<" expression@6\n",y <= x + 2))
	{
    	bFile<<"node2@benchmark45\n";
    	printf("Solved 45");
	 }
	bFile<<"node3@benchmark45\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark46
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark46
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark46 "<<sqrt(exp(x+z))-pow(z,x)<<" expression@2\n",sqrt(exp(x+z)) < pow(z,x))
		&&(bFile<<"node1@benchmark46 "<<x-0<<" expression@3\n",x > 0)
		&&(bFile<<"node1@benchmark46 "<<y-1<<" expression@4\n",y > 1)
		&&(bFile<<"node1@benchmark46 "<<z-1<<" expression@5\n",z > 1)
		&&(bFile<<"node1@benchmark46 "<<y-1<<" expression@6\n",y < 1)
		&&(bFile<<"node1@benchmark46 "<<y-(x+2)<<" expression@7\n",y < x + 2)
		&&(bFile<<"node1@benchmark46 "<<w-(x+2)<<" expression@8\n",w == x + 2))
	{
		bFile<<"node2@benchmark46\n";
		printf("Solved 46");
	}
	bFile<<"node3@benchmark46\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark47
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark47
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark47 "<<exp(x + y)-exp(z)<<" expression@1\n",exp(x + y) == exp(z))
	{
		bFile<<"node2@benchmark47\n";
		printf("Solved 47");
	}
	bFile<<"node3@benchmark47\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark48
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark48
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark48 "<<x+y-z<<" expression@1\n",x + y != z)
	{
		bFile<<"node2@benchmark48\n";
		printf("Solved 48");
	}
	bFile<<"node3@benchmark48\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark49
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark49
  (JNIEnv *env, jobject arg, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark49 "<<pow(x,2)+3*sqrt(y)-x*y<<" expression@2\n",pow(x,2) + 3*sqrt(y) < x*y) &&
		(bFile<<"node1@benchmark49 "<<x-pow(y,2)<<" expression@3\n",x < pow(y,2)) &&
		(bFile<<"node1@benchmark49 "<<x+y-50<<" expression@4\n",x + y < 50))
	{
		bFile<<"node2@benchmark49\n";
		printf("Solved 49");
	}
	bFile<<"node3@benchmark49\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark50
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark50
  (JNIEnv *env, jobject, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark50 "<<pow(x,2)+3*sqrt(y)-x*y<<" expression@2\n",pow(x,2) + 3*sqrt(y) < x*y) &&
		(bFile<<"node1@benchmark50 "<<x-pow(y,2)<<" expression@3\n",x < pow(y,2)) &&
		(bFile<<"node1@benchmark50 "<<x+y-50<<" expression@4\n",x + y < 50) &&
		(bFile<<"node1@benchmark50 "<<x-(-13+y)<<" expression@5\n",x == -13 + y))
	{
		bFile<<"node2@benchmark50\n";
		printf("Solved 50");
	}
	bFile<<"node3@benchmark50\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark52
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark52
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
    if((bFile<<"node1@benchmark52 "<<pow(x,tan(y))+z-x*atan(z)<<" expression@2\n",pow(x,tan(y)) + z < x * atan(z)) &&
      (bFile<<"node1@benchmark52 "<<sin(y)+cos(y)+tan(y)-(x-z)<<" expression@3\n",sin(y) + cos(y) + tan(y) >= x - z)  )
    {
    	bFile<<"node2@benchmark52\n";
    	printf("Solved 52");
	}
	bFile<<"node3@benchmark52\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark53
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark53
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark53 "<<pow(x,tan(y))+z-x*atan(z)<<" expression@2\n",pow(x,tan(y)) + z < x * atan(z)) &&
    	(bFile<<"node1@benchmark53 "<<sin(y)+cos(y)+tan(y)-(x-z)<<" expression@3\n",sin(y) + cos(y) + tan(y) >= x - z) &&
		(bFile<<"node1@benchmark53 "<<atan(x)+atan(y)-y<<" expression@4\n",atan(x) + atan(y) > y)) {
		bFile<<"node2@benchmark53\n";
		printf("Solved 53");
	}
	bFile<<"node3@benchmark53\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark56
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark56
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark56 "<<x*y+atan(z)*sin(w*t)-(x/y+z+tan(w+t))<<" expression@1\n",x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t)) 
	{
		bFile<<"node2@benchmark56\n";
		printf("Solved 56");
	}
	bFile<<"node3@benchmark56\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark61
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark61
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark61 "<<x+y-z/w<<" expression@2\n",x + y > z / w) &&
		(bFile<<"node1@benchmark61 "<<sqrt(x)-z/y<<" expression@3\n",sqrt(x) > z / y) &&
		(bFile<<"node1@benchmark61 "<<z*2+w*3+x*7-pow(y,6)<<" expression@4\n",z*2 + w*3 + x*7 < pow(y,6)) &&
		(bFile<<"node1@benchmark61 "<<z+w-(x+y)<<" expression@5\n",z + w > x + y) &&
		(bFile<<"node1@benchmark61 "<<w-x/y<<" expression@6\n",w < x/y))
	{
		bFile<<"node2@benchmark61\n";
		printf("Solved 61");
	}
	bFile<<"node3@benchmark61\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark62
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark62
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark62 "<<x+y-z/w<<" expression@2\n",x + y > z / w) &&
		(bFile<<"node1@benchmark62 "<<sqrt(x)-z/y<<" expression@3\n",sqrt(x) > z / y) &&
		(bFile<<"node1@benchmark62 "<<z*2+w*3+x*7-pow(y,6)<<" expression@4\n",z*2 + w*3 + x*7 < pow(y,6)) &&
		(bFile<<"node1@benchmark62 "<<z+w-(x+y)<<" expression@5\n",z + w > x + y) &&
		(bFile<<"node1@benchmark62 "<<w-x/y<<" expression@6\n",w < x/y) &&
		(bFile<<"node1@benchmark62 "<<x-(w+y-z)<<" expression@7\n",x > (w+y-z)))
	{
		bFile<<"node2@benchmark62\n";
		printf("Solved 62");
	}
	bFile<<"node3@benchmark62\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark70
 * Signature: (DDDDDDDDDDDDDDDDDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark70
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jdouble h, jdouble i, jdouble j, jdouble k, jdouble l, jdouble m, jdouble n, jdouble o , jdouble p, jdouble q, jdouble r, jdouble s, jdouble t, jdouble u, jdouble v, jdouble x, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark70 "<<sin(a)-sin(b)<<" expression@2\n",sin(a) > sin(b)) &&
		(bFile<<"node1@benchmark70 "<<sin(b)-sin(c)<<" expression@3\n",sin(b) > sin(c)) &&
		(bFile<<"node1@benchmark70 "<<sin(c)-sin(d)<<" expression@4\n",sin(c) > sin(d)) &&
		(bFile<<"node1@benchmark70 "<<sin(d)-sin(e)<<" expression@5\n",sin(d) > sin(e)) &&
		(bFile<<"node1@benchmark70 "<<sin(e)-sin(f)<<" expression@6\n",sin(e) > sin(f)) &&
		(bFile<<"node1@benchmark70 "<<sin(f)-sin(g)<<" expression@7\n",sin(f) > sin(g)) &&
		(bFile<<"node1@benchmark70 "<<sin(g)-sin(h)<<" expression@8\n",sin(g) > sin(h)) &&
		(bFile<<"node1@benchmark70 "<<sin(h)-sin(i)<<" expression@9\n",sin(h) > sin(i)) &&
		(bFile<<"node1@benchmark70 "<<sin(i)-sin(j)<<" expression@10\n",sin(i) > sin(j)) &&
		(bFile<<"node1@benchmark70 "<<sin(j)-sin(k)<<" expression@11\n",sin(j) > sin(k)) &&
		(bFile<<"node1@benchmark70 "<<sin(k)-sin(l)<<" expression@12\n",sin(k) > sin(l)) &&
		(bFile<<"node1@benchmark70 "<<sin(l)-sin(m)<<" expression@13\n",sin(l) > sin(m)) &&
		(bFile<<"node1@benchmark70 "<<sin(m)-sin(n)<<" expression@14\n",sin(m) > sin(n)) &&
		(bFile<<"node1@benchmark70 "<<sin(n)-sin(o)<<" expression@15\n",sin(n) > sin(o)) &&
		(bFile<<"node1@benchmark70 "<<sin(o)-sin(p)<<" expression@16\n",sin(o) > sin(p)) &&
		(bFile<<"node1@benchmark70 "<<sin(p)-sin(q)<<" expression@17\n",sin(p) > sin(q)) &&
		(bFile<<"node1@benchmark70 "<<sin(q)-sin(r)<<" expression@18\n",sin(q) > sin(r)) &&
		(bFile<<"node1@benchmark70 "<<sin(r)-sin(s)<<" expression@19\n",sin(r) > sin(s)) &&
		(bFile<<"node1@benchmark70 "<<sin(s)-sin(t)<<" expression@20\n",sin(s) > sin(t)) &&
		(bFile<<"node1@benchmark70 "<<sin(t)-sin(u)<<" expression@21\n",sin(t) > sin(u)) &&
		(bFile<<"node1@benchmark70 "<<sin(u)-sin(v)<<" expression@22\n",sin(u) > sin(v)) &&
		(bFile<<"node1@benchmark70 "<<sin(v)-sin(x)<<" expression@23\n",sin(v) > sin(x)) &&
		(bFile<<"node1@benchmark70 "<<sin(x)-sin(z)<<" expression@24\n",sin(x) > sin(z)))
	{
		bFile<<"node2@benchmark70\n";
		printf("Solved 70");
	}
	bFile<<"node3@benchmark70\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark71
 * Signature: (DDDDDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark71
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jdouble h, jdouble i, jdouble j, jdouble k, jdouble l, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark71 "<<sin(a)-sin(b)<<" expression@2\n",sin(a) > sin(b)) &&
		(bFile<<"node1@benchmark71 "<<sin(b)-sin(c)<<" expression@3\n",sin(b) > sin(c)) &&
		(bFile<<"node1@benchmark71 "<<sin(c)-sin(d)<<" expression@4\n",sin(c) > sin(d)) &&
		(bFile<<"node1@benchmark71 "<<sin(d)-sin(e)<<" expression@5\n",sin(d) > sin(e)) &&
		(bFile<<"node1@benchmark71 "<<sin(e)-sin(f)<<" expression@6\n",sin(e) > sin(f)) &&
		(bFile<<"node1@benchmark71 "<<sin(f)-sin(g)<<" expression@7\n",sin(f) > sin(g)) &&
		(bFile<<"node1@benchmark71 "<<sin(g)-sin(h)<<" expression@8\n",sin(g) > sin(h)) &&
		(bFile<<"node1@benchmark71 "<<sin(h)-sin(i)<<" expression@9\n",sin(h) > sin(i)) &&
		(bFile<<"node1@benchmark71 "<<sin(i)-sin(j)<<" expression@10\n",sin(i) > sin(j)) &&
		(bFile<<"node1@benchmark71 "<<sin(j)-sin(k)<<" expression@11\n",sin(j) > sin(k)) &&
		(bFile<<"node1@benchmark71 "<<sin(k)-sin(l)<<" expression@12\n",sin(k) > sin(l)))
	{
		bFile<<"node2@benchmark71\n";
		printf("Solved 71");
	}
	bFile<<"node3@benchmark71\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark72
 * Signature: (DDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark72
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark72 "<<sin(a)-sin(b)<<" expression@2\n",sin(a) > sin(b)) &&
		(bFile<<"node1@benchmark72 "<<sin(b)-sin(c)<<" expression@3\n",sin(b) > sin(c)) &&
		(bFile<<"node1@benchmark72 "<<sin(c)-sin(d)<<" expression@4\n",sin(c) > sin(d)) &&
		(bFile<<"node1@benchmark72 "<<sin(d)-sin(e)<<" expression@5\n",sin(d) > sin(e)) &&
		(bFile<<"node1@benchmark72 "<<sin(e)-sin(f)<<" expression@6\n",sin(e) > sin(f)) &&
		(bFile<<"node1@benchmark72 "<<sin(f)-sin(g)<<" expression@7\n",sin(f) > sin(g)))
	{
		bFile<<"node2@benchmark72\n";
		printf("Solved 72");
	}
	bFile<<"node3@benchmark72\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark73
 * Signature: (DDDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark73
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jdouble h, jdouble i, jdouble j, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark73 "<<sin(a)-sin(b)<<" expression@2\n",sin(a) > sin(b)) &&
		(bFile<<"node1@benchmark73 "<<sin(b)-sin(c)<<" expression@3\n",sin(b) > sin(c)) &&
		(bFile<<"node1@benchmark73 "<<sin(c)-sin(d)<<" expression@4\n",sin(c) > sin(d)) &&
		(bFile<<"node1@benchmark73 "<<sin(d)-sin(e)<<" expression@5\n",sin(d) > sin(e)) &&
		(bFile<<"node1@benchmark73 "<<sin(e)-sin(f)<<" expression@6\n",sin(e) > sin(f)) &&
		(bFile<<"node1@benchmark73 "<<sin(f)-sin(g)<<" expression@7\n",sin(f) > sin(g)) &&
		(bFile<<"node1@benchmark73 "<<sin(g)-sin(h)<<" expression@8\n",sin(g) > sin(h)) &&
		(bFile<<"node1@benchmark73 "<<sin(h)-sin(i)<<" expression@9\n",sin(h) > sin(i)) &&
		(bFile<<"node1@benchmark73 "<<sin(i)-sin(j)<<" expression@10\n",sin(i) > sin(j)))
	{
		bFile<<"node2@benchmark73\n";
		printf("Solved 73");
	}
	bFile<<"node3@benchmark73\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark74
 * Signature: (DDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark74
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jdouble h, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark74 "<<sin(a)-sin(b)<<" expression@2\n",sin(a) > sin(b)) &&
		(bFile<<"node1@benchmark74 "<<sin(b)-sin(c)<<" expression@3\n",sin(b) > sin(c)) &&
		(bFile<<"node1@benchmark74 "<<sin(c)-sin(d)<<" expression@4\n",sin(c) > sin(d)) &&
		(bFile<<"node1@benchmark74 "<<sin(d)-sin(e)<<" expression@5\n",sin(d) > sin(e)) &&
		(bFile<<"node1@benchmark74 "<<sin(e)-sin(f)<<" expression@6\n",sin(e) > sin(f)) &&
		(bFile<<"node1@benchmark74 "<<sin(f)-sin(g)<<" expression@7\n",sin(f) > sin(g)) &&
		(bFile<<"node1@benchmark74 "<<sin(g)-sin(h)<<" expression@8\n",sin(g) > sin(h)))
	{
		bFile<<"node2@benchmark74\n";
		printf("Solved 74");
	}
	bFile<<"node3@benchmark74\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark75
 * Signature: (DDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark75
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jdouble h, jdouble i, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark75 "<<sin(a)-sin(b)<<" expression@2\n",sin(a) > sin(b)) &&
		(bFile<<"node1@benchmark75 "<<sin(b)-sin(c)<<" expression@3\n",sin(b) > sin(c)) &&
		(bFile<<"node1@benchmark75 "<<sin(c)-sin(d)<<" expression@4\n",sin(c) > sin(d)) &&
		(bFile<<"node1@benchmark75 "<<sin(d)-sin(e)<<" expression@5\n",sin(d) > sin(e)) &&
		(bFile<<"node1@benchmark75 "<<sin(e)-sin(f)<<" expression@6\n",sin(e) > sin(f)) &&
		(bFile<<"node1@benchmark75 "<<sin(f)-sin(g)<<" expression@7\n",sin(f) > sin(g)) &&
		(bFile<<"node1@benchmark75 "<<sin(g)-sin(h)<<" expression@8\n",sin(g) > sin(h)) &&
		(bFile<<"node1@benchmark75 "<<sin(h)-sin(i)<<" expression@9\n",sin(h) > sin(i)))
	{
		bFile<<"node2@benchmark75\n";
		printf("Solved 75");
	}
	bFile<<"node3@benchmark75\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark76
 * Signature: (DDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark76
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jdouble h, jdouble i, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark76 "<<a-b<<" expression@2\n",a > b) &&
		(bFile<<"node1@benchmark76 "<<b-c<<" expression@3\n",b > c) &&
		(bFile<<"node1@benchmark76 "<<c-d<<" expression@4\n",c > d) &&
		(bFile<<"node1@benchmark76 "<<d-e<<" expression@5\n",d > e) &&
		(bFile<<"node1@benchmark76 "<<e-f<<" expression@6\n",e > f) &&
		(bFile<<"node1@benchmark76 "<<f-g<<" expression@7\n",f > g) &&
		(bFile<<"node1@benchmark76 "<<g-h<<" expression@8\n",g > h) &&
		(bFile<<"node1@benchmark76 "<<h-i<<" expression@9\n",h > i))
	{
		bFile<<"node2@benchmark76\n";
		printf("Solved 76");
	}
	bFile<<"node3@benchmark76\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark77
 * Signature: (DDDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark77
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jdouble f, jdouble g, jdouble h, jdouble i, jdouble j, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark77 "<<a-b<<" expression@2\n",a > b) &&
		(bFile<<"node1@benchmark77 "<<b-c<<" expression@3\n",b > c) &&
		(bFile<<"node1@benchmark77 "<<c-d<<" expression@4\n",c > d) &&
		(bFile<<"node1@benchmark77 "<<d-e<<" expression@5\n",d > e) &&
		(bFile<<"node1@benchmark77 "<<e-f<<" expression@6\n",e > f) &&
		(bFile<<"node1@benchmark77 "<<f-g<<" expression@7\n",f > g) &&
		(bFile<<"node1@benchmark77 "<<g-h<<" expression@8\n",g > h) &&
		(bFile<<"node1@benchmark77 "<<h-i<<" expression@9\n",h > i) &&
		(bFile<<"node1@benchmark77 "<<i-j<<" expression@10\n",i > j))
	{
		bFile<<"node2@benchmark77\n";
		printf("Solved 77");
	}
	bFile<<"node3@benchmark77\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark78
 * Signature: (DDDDDIIILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark78
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jdouble e, jint f, jint g, jint h, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark78 "<<0.0-(pow(((a*sin(((b*0.017453292519943295-c*0.017453292519943295)+(((((((pow(d,2.0)/(sin(e*0.017453292519943295)/cos(e*0.017453292519943295)))/68443.0)*0.0)/d)*-1.0)*a)/((pow(a,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)))))-(d*0.0)),2.0)+pow(((a*cos((((b*0.017453292519943295)-(c*0.017453292519943295))+(((((((pow(d,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)*0.0)/d)*-1.0)*a)/((pow(a,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)))))-d*1.0),2.0))<<" expression@2\n", 0.0 == (pow(((a*sin(((b*0.017453292519943295 - c*0.017453292519943295)+(((((((pow(d,2.0)/(sin(e*0.017453292519943295)/cos(e*0.017453292519943295)))/68443.0)*0.0)/d)*-1.0)*a)/((pow(a,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)))))-(d*0.0)),2.0)+pow(((a*cos((((b*0.017453292519943295)-(c*0.017453292519943295))+(((((((pow(d,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)*0.0)/d)*-1.0)*a)/((pow(a,2.0)/(sin((e*0.017453292519943295))/cos((e*0.017453292519943295))))/68443.0)))))-d*1.0),2.0)))
		&&(bFile<<"node1@benchmark78 "<<f-0<<" expression@3\n",f != 0)
		&&(bFile<<"node1@benchmark78 "<<h-0<<" expression@4\n",h != 0))
	{
		bFile<<"node2@benchmark78\n";
		printf("Solved 78");
	}
	bFile<<"node3@benchmark78\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark79
 * Signature: (DDDDILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark79
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jint e, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark79 "<<0.0-(pow(((a*sin(((0.017453292519943295*b)-(0.017453292519943295*c))))-(0.0*d)),2.0)+pow((a*cos((((0.017453292519943295*b)-(0.017453292519943295*c))+0.0))),2.0))<<" expression@2\n",0.0 == (pow(((a*sin(((0.017453292519943295*b)-(0.017453292519943295*c))))-(0.0*d)),2.0)+ pow((a*cos((((0.017453292519943295*b)-(0.017453292519943295*c))+0.0))),2.0)))
	   &&(bFile<<"node1@benchmark79 "<<e-0<<" expression@3\n",e != 0))
	{
		bFile<<"node2@benchmark79\n";
		printf("Solved 79");
	}
	bFile<<"node3@benchmark79\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark80
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark80
  (JNIEnv *env, jobject, jdouble a, jdouble b, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark80 "<<(1.5-a*(1-b))-0<<" expression@1\n",1.5 - a * (1 - b)) == 0)
	{
		bFile<<"node2@benchmark80\n";
		printf("Solved 80");
	}
	bFile<<"node3@benchmark80\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark81
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark81
  (JNIEnv *env, jobject, jdouble a, jdouble b, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark81 "<<(-13+a+((5-b)*b-2)*b)+(-29+a+((b+1)*b-14)*b)-0<<" expression@1\n",(-13 + a + ((5 - b) * b - 2) * b) + (-29 + a + ((b + 1) * b - 14) * b) == 0) 
	{
		bFile<<"node2node1@benchmark81\n";
		printf("Solved 81");
	}
	bFile<<"node3node1@benchmark81\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark82
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark82
  (JNIEnv *env, jobject, jdouble a, jdouble b, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark82 "<< pow(10, 4) * a * b - 1-0<<" expression@2\n", pow(10, 4) * a * b - 1==0 )
     &&(bFile<<"node1@benchmark82 "<<(exp(-a) + exp(-b) - 1.0001)-0<<" expression@3\n",(exp(-a) + exp(-b) - 1.0001)==0 ) )
	{
		bFile<<"node2@benchmark82\n";
		printf("Solved 82");
	}
	bFile<<"node3@benchmark82\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark83
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark83
  (JNIEnv *env, jobject, jdouble a, jdouble b, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if( bFile<<"node1@benchmark83 "<<pow((1-a),2)+100*(pow((b-a*a),2))-0<<" expression@1\n",pow((1 - a), 2) + 100 * (pow((b - a * a), 2))==0 )
	{
		bFile<<"node2@benchmark83\n";
		printf("Solved 83");
	}
	bFile<<"node3@benchmark83\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark84
 * Signature: (DDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark84
  (JNIEnv *env, jobject, jdouble a, jdouble b, jdouble c, jdouble d, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(
	(bFile<<"node1@benchmark84 "<<(10 * (b - a * a))-0<<" expression@2\n",(10 * (b - a * a)) == 0)
	&&(bFile<<"node1@benchmark84 "<< (1 - a)-0<<" expression@3\n", (1 - a) == 0)
	&&(bFile<<"node1@benchmark84 "<<(sqrt(90) * (d - c * c))-0<<" expression@4\n",(sqrt(90) * (d - c * c)) == 0)
	&&(bFile<<"node1@benchmark84 "<<(1 - c)-0<<" expression@5\n",(1 - c) == 0)
	&&(bFile<<"node1@benchmark84 "<<(sqrt(10) * (b + d - 2))-0<<" expression@6\n",(sqrt(10) * (b + d - 2)) == 0)
	&&(bFile<<"node1@benchmark84 "<<(pow(10, -0.5) * (b - d))-0<<" expression@7\n",(pow(10, -0.5) * (b - d)) == 0)
	)
	{
		bFile<<"node2@benchmark84\n";
		printf("Solved 84");
	}
	bFile<<"node3@benchmark84\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark91
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark91
  (JNIEnv *env, jobject, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark91 "<<sin(x)+sin(y)<<" expression@2\n",sin(x) == -sin(y)) &&
	   (bFile<<"node1@benchmark91 "<<sin(x)-0<<" expression@3\n",sin(x) > 0))
	{
		bFile<<"node2@benchmark91\n";
		printf("Solved 91");
	}
	bFile<<"node3@benchmark91\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark28
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark28
  (JNIEnv *env, jobject, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark28 "<<log(x)-2<<" expression@1\n",log(x) == 2) 
	{
		bFile<<"node2@benchmark28\n";
		printf("Solved 28");
	}
	bFile<<"node3@benchmark28\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark30
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark30
  (JNIEnv *env, jobject, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark30 "<<log10(x)-2<<" expression@1\n",log10(x) == 2)
	{
		bFile<<"node2@benchmark30\n";
		printf("Solved 30");
	}
	bFile<<"node3@benchmark30\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark31
 * Signature: (DLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark31
  (JNIEnv *env, jobject, jdouble x, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark31 "<<round(x)-5<<" expression@1\n",round(x) > 5) 
	{
		bFile<<"node2@benchmark31\n";
	    printf("Solved 31");
	}
	bFile<<"node3@benchmark31\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark36
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark36
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark36 "<<log10(z)*(1.0/sqrt(sin(x)))-sqrt(cos(exp(y)))<<" expression@1\n",log10(z)*(1.0/sqrt(sin(x))) == sqrt(cos(exp(y)))) 
	{
		bFile<<"node2@benchmark36\n";
	    printf("Solved 36");
    }
	bFile<<"node3@benchmark36\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark37
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark37
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(bFile<<"node1@benchmark37 "<<log10(tan(z))*(1.0/sqrt(sin(x)))-sqrt(cos(exp(y)))<<" expression@1\n",log10(tan(z))*(1.0/sqrt(sin(x))) == sqrt(cos(exp(y)))) 
	{
		bFile<<"node2@benchmark37\n";
		printf("Solved 37");
	}
	bFile<<"node3@benchmark37\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark51
 * Signature: (DDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark51
  (JNIEnv *env, jobject, jdouble x, jdouble y, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark51 "<<pow(x,2)+3*sqrt(y)-x*y<<" expression@2\n",pow(x,2) + 3*sqrt(y) < x*y) &&
		(bFile<<"node1@benchmark51 "<<x-pow(y,2)<<" expression@3\n",x < pow(y,2)) &&
		(bFile<<"node1@benchmark51 "<<x+y-50<<" expression@4\n",x + y < 50) &&
		(bFile<<"node1@benchmark51 "<<pow(x,x)-log10(y)<<" expression@5\n",pow(x,x) < log10(y)))
	{
		bFile<<"node2@benchmark51\n";
	    printf("Solved 51");
    }
	bFile<<"node3@benchmark51\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark54
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark54
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark54 "<<pow(x,tan(y))+z-x*atan(z)<<" expression@2\n",pow(x,tan(y)) + z < x * atan(z)) &&
		(bFile<<"node1@benchmark54 "<<sin(y)+cos(y)+tan(y)-(x-z)<<" expression@3\n",sin(y) + cos(y) + tan(y) >= x - z) &&
		(bFile<<"node1@benchmark54 "<<atan(x)+atan(y)-y<<" expression@4\n",atan(x) + atan(y) > y) &&
		(bFile<<"node1@benchmark54 "<<log(pow(x,tan(y)))-log(z)<<" expression@5\n",log(pow(x,tan(y))) < log(z)))
	{
		bFile<<"node2@benchmark54\n";
		printf("Solved 54");
	}
	bFile<<"node3@benchmark54\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark55
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark55
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);

	if(
			(bFile<<"node1@benchmark55 "<<atan(x)+atan(y)-y<<" expression@2\n",atan(x) + atan(y) > y)&&
			(bFile<<"node1@benchmark55 "<<sqrt(y+z)-sqrt(pow(x,(x-y)))<<" expression@3\n",sqrt(y+z) > sqrt(pow(x,(x-y))))&&
			(bFile<<"node1@benchmark55 "<<pow(x,tan(y))+z-x*atan(z)<<" expression@4\n",pow(x,tan(y)) + z < x * atan(z))&&
			(bFile<<"node1@benchmark55 "<<sin(y)+cos(y)+tan(y)-(x-z)<<" expression@5\n",sin(y) + cos(y) + tan(y) >= x - z)&&
	  	    (bFile<<"node1@benchmark55 "<<log(pow(x,tan(y)))-log(z)<<" expression@6\n",log(pow(x,tan(y))) < log(z)) )
	 {
		bFile<<"node2@benchmark55\n";
		printf("Solved 55");
	}
	bFile<<"node3@benchmark55\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark57
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark57
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark57 "<<x*y+atan(z)*sin(w*t)-(x/y+z+tan(w+t))<<" expression@2\n",x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t)) &&
	 (bFile<<"node1@benchmark57 "<<pow(log10(x),log10(y))-pow(log10(z+w+t),tan(w*t))<<" expression@3\n",pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t)))) 
	{
		bFile<<"node2@benchmark57\n";
		printf("Solved 57");
	}
	bFile<<"node3@benchmark57\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark58
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark58
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark58 "<<x*y+atan(z)*sin(w*t)-(x/y+z+tan(w+t))<<" expression@2\n",x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t)) &&
	(bFile<<"node1@benchmark58 "<<pow(log10(x),log10(y))-pow(log10(z+w+t),tan(w*t))<<" expression@3\n",pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t))) &&
	(bFile<<"node1@benchmark58 "<<tan(w*(x+y))+sin(t*(y+z))-(asin(x+y+z)+acos(x+y+z)+atan(x+y+z))<<" expression@4\n",tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z))) 
	{
		bFile<<"node2@benchmark58\n";
		printf("Solved 58");
	}
	bFile<<"node3@benchmark58\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark59
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark59
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark59 "<<x*y+atan(z)*sin(w*t)-(x/y+z+tan(w+t))<<" expression@2\n",x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t)) &&
	(bFile<<"node1@benchmark59 "<<pow(log10(x),log10(y))-pow(log10(z+w+t),tan(w*t))<<" expression@3\n",pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t))) &&
	(bFile<<"node1@benchmark59 "<<tan(w*(x+y))+sin(t*(y+z))-(asin(x+y+z)+acos(x+y+z)+atan(x+y+z))<<" expression@4\n",tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z)) &&
	(bFile<<"node1@benchmark59 "<<w-t*3/4<<" expression@5\n",w == t * 3 / 4)) 
	{
		bFile<<"node2@benchmark59\n";
		printf("Solved 59");
	}
	bFile<<"node3@benchmark59\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark60
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark60
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark60 "<<x*y+atan(z)*sin(w*t)-(x/y+z+tan(w+t))<<" expression@2\n",x * y + atan(z) * sin(w*t) > x/y + z + tan(w+t)) &&
		(bFile<<"node1@benchmark60 "<<pow(log10(x),log10(y))-pow(log10(z+w+t),tan(w*t))<<" expression@3\n",pow(log10(x),log10(y)) <= pow(log10(z+w+t),tan(w*t))) &&
		(bFile<<"node1@benchmark60 "<<tan(w*(x+y))+sin(t*(y+z))-(asin(x+y+z)+acos(x+y+z)+atan(x+y+z))<<" expression@4\n",tan(w*(x+y)) + sin(t*(y+z)) > asin(x+y+z) + acos(x+y+z) + atan(x+y+z)) &&
		(bFile<<"node1@benchmark60 "<<w-t*3/4<<"expression@5\n",w == t * 3 / 4) &&
		(bFile<<"node1@benchmark60 "<<x-(2*y-3*z)<<" expression@6\n",x < 2*y - 3*z))
	{
		bFile<<"node2@benchmark60\n";
		printf("Solved 60");
	}
	bFile<<"node3@benchmark60\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark63
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark63
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark63 "<<x+y-z/w<<" expression@2\n",x + y > z / w) &&
		(bFile<<"node1@benchmark63 "<<sqrt(x)-z/y<<" expression@3\n",sqrt(x) > z / y) &&
		(bFile<<"node1@benchmark63 "<<log(x*y)-log(t+w+z)<<" expression@4\n",log(x*y) > log(t+w+z)) &&
		(bFile<<"node1@benchmark63 "<<z*2+w*3+x*7-pow(y,6)<<" expression@5\n",z*2 + w*3 + x*7 < pow(y,6)) &&
		(bFile<<"node1@benchmark63 "<<z+w-x-y<<" expression@6\n",z + w > x + y) &&
		(bFile<<"node1@benchmark63 "<<w-x/y<<" expression@7\n",w < x/y) &&
		(bFile<<"node1@benchmark63 "<<x-(w+y-z)<<" expression@8\n",x > (w+y-z)) &&
		(bFile<<"node1@benchmark63 "<<log10(t*x)-sqrt(w*y*z)<<" expression@9\n",log10(t*x) < sqrt(w*y*z)))
	{
		bFile<<"node2@benchmark63\n";
		printf("Solved 63");
	}
	bFile<<"node3@benchmark63\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark64
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark64
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark64 "<<x+y-z/(w+t)<<" expression@2\n",x + y > z / (w + t)) &&
		(bFile<<"node1@benchmark64 "<<sqrt(x)-z/y<<" expression@3\n",sqrt(x) > z / y) &&
		(bFile<<"node1@benchmark64 "<<log(x*y)-log(t+w+z)<<" expression@4\n",log(x*y) > log(t+w+z)) &&
		(bFile<<"node1@benchmark64 "<<z*2+w*3+x*7-pow(y,t)<<" expression@5\n",z*2 + w*3 + x*7 < pow(y,t)) &&
		(bFile<<"node1@benchmark64 "<<z+w-x-y<<" expression@6\n",z + w > x + y) &&
		(bFile<<"node1@benchmark64 "<<w-x/y<<" expression@7\n",w < x/y) &&
		(bFile<<"node1@benchmark64 "<<x-(w+y-z)<<" expression@8\n",x > (w+y-z)) &&
		(bFile<<"node1@benchmark64 "<<log10(t*x)-sqrt(w*y*z)<<" expression@9\n",log10(t*x) < sqrt(w*y*z)) &&
		(bFile<<"node1@benchmark64 "<<x*(t+y)-log(w*z*3)<<" expression@10\n",x * (t + y) > log(w*z*3)))
	{
		bFile<<"node2@benchmark64\n";
		printf("Solved 64");
    }
	bFile<<"node3@benchmark64\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark65
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark65
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark65 "<<x+y-z/(w+t)<<" expression@2\n",x + y > z / (w + t)) &&
		(bFile<<"node1@benchmark65 "<<sqrt(x)-z/y<<" expression@3\n",sqrt(x) > z / y) &&
		(bFile<<"node1@benchmark65 "<<log(x*y)-log(t+w+z)<<" expression@4\n",log(x*y) > log(t+w+z)) &&
		(bFile<<"node1@benchmark65 "<<z*2+w*3+x*7-pow(y,t)<<" expression@5\n",z*2 + w*3 + x*7 < pow(y,t)) &&
		(bFile<<"node1@benchmark65 "<<z+w-x-y<<" expression@6\n",z + w > x + y) &&
		(bFile<<"node1@benchmark65 "<<w-x/y<<" expression@7\n",w < x/y) &&
		(bFile<<"node1@benchmark65 "<<x-(w+y-z)<<" expression@8\n",x > (w+y-z)) &&
		(bFile<<"node1@benchmark65 "<<log10(t*x)-sqrt(w*y*z)<<"expression@9\n",log10(t*x) < sqrt(w*y*z)) &&
		(bFile<<"node1@benchmark65 "<<x*cos(t+y)-log(w*z*3)<<" expression@10\n",x * cos(t + y) > log(w*z*3)))
	{
		bFile<<"node2@benchmark65\n";
		printf("Solved 65");
    }
	bFile<<"node3@benchmark65\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark66
 * Signature: (DDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark66
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark66 "<<x+y-(z+t)/(w+t)<<" expression@2\n",x + y > (z + t) / (w + t)) &&
		(bFile<<"node1@benchmark66 "<<sqrt(x)-z/y<<" expression@3\n",sqrt(x) > z / y) &&
		(bFile<<"node1@benchmark66 "<<log(x*y)-log(t+w+z)<<" expression@4\n",log(x*y) > log(t+w+z)) &&
		(bFile<<"node1@benchmark66 "<<z*2+w*3+x*7-pow(y,t)<<" expression@5\n",z*2 + w*3 + x*7 < pow(y,t)) &&
		(bFile<<"node1@benchmark66 "<<z+w-x-y<<" expression@6\n",z + w > x + y) &&
		(bFile<<"node1@benchmark66 "<<w-x/y<<" expression@7\n",w < x/y) &&
		(bFile<<"node1@benchmark66 "<<x-(w+y-z)<<" expression@8\n",x > (w+y-z)) &&
		(bFile<<"node1@benchmark66 "<<log10(t*x)-sqrt(w*y*z)<<" expression@9\n",log10(t*x) < sqrt(w*y*z)) &&
		(bFile<<"node1@benchmark66 "<<x*cos(t+y)-log(w*z*3)<<" expression@10\n",x * cos(t + y) > log(w*z*3))&&
		(bFile<<"node1@benchmark66 "<<cos(t)-cos(y)<<" expression@11\n",cos(t) > cos(y))) 
	{
		bFile<<"node2@benchmark66\n";
		printf("Solved 66");
    }
	bFile<<"node3@benchmark66\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark67
 * Signature: (DDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark67
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jdouble v, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if((bFile<<"node1@benchmark67 "<<x-y+tan(v)-(z+t)/(w+t)<<" expression@2\n",x - y + tan(v)> (z+ t) / (w + t)) &&
		(bFile<<"node1@benchmark67 "<<sqrt(x-t)-z/y<<" expression@3\n",sqrt(x-t) > z / y) &&
		(bFile<<"node1@benchmark67 "<<log(x*y)-log(t+w+z)<<" expression@4\n",log(x*y) > log(t+w+z)) &&
		(bFile<<"node1@benchmark67 "<<z*2+w*3+x*7-pow(y,t)*cos(v)<<" expression@5\n",z*2 + w*3 + x*7 < pow(y,t)*cos(v)) &&
		(bFile<<"node1@benchmark67 "<<z+w-(x+y)<<" expression@6\n",z + w > x + y) &&
		(bFile<<"node1@benchmark67 "<<w-x/y<<" expression@7\n",w < x/y) &&
		(bFile<<"node1@benchmark67 "<<x-(w+y-z)<<" expression@8\n",x > (w+y-z)) &&
		(bFile<<"node1@benchmark67 "<<log10(t*x)-sqrt(w*y*z)<<" expression@9\n",log10(t*x) < sqrt(w*y*z)) &&
		(bFile<<"node1@benchmark67 "<<x*cos(t+y)-log(w*z*3)<<" expression@10\n",x * cos(t + y) > log(w*z*3))&&
		(bFile<<"node1@benchmark67 "<<cos(t)*sin(v)-cos(y)<<" expression@11\n",cos(t) * sin(v) > cos(y)))
	{
		bFile<<"node2@benchmark67\n";
		printf("Solved 67");
    }
	bFile<<"node3@benchmark67\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark68
 * Signature: (DDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark68
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jdouble v, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(
			(bFile<<"node1@benchmark68 "<<z+w-(x+y)<<" expression@2\n",z + w > x + y)&&
			(bFile<<"node1@benchmark68 "<<x-(w+y-z)<<" expression@3\n",x > (w+y-z))&&
			(bFile<<"node1@benchmark68 "<<w-x/y<<" expression@4\n",w < x/y)&&
			(bFile<<"node1@benchmark68 "<<sqrt(x-t)-z/y<<" expression@5\n",sqrt(x-t) > z / y)&&
			(bFile<<"node1@benchmark68 "<<log(x*y)-log(t+w+z)<<" expression@6\n",log(x*y) > log(t+w+z))&&
			(bFile<<"node1@benchmark68 "<<log10(t*x)-sqrt(w*y*z)<<" expression@7\n",log10(t*x) < sqrt(w*y*z))&&
			(bFile<<"node1@benchmark68 "<<x-y+tan(v)-(z+t)/(w+t)<<" expression@8\n",x - y + tan(v)> (z+ t) / (w + t))&&
			(bFile<<"node1@benchmark68 "<<x*cos(t+y)-log(w*z*3)<<" expression@9\n",x * cos(t + y) > log(w*z*3))&&
			(bFile<<"node1@benchmark68 "<<z*2+w*3+x*7-pow(y,t)*cos(v)<<" expression@10\n",z*2 + w*3 + x*7 < pow(y,t)*cos(v))&&
			(bFile<<"node1@benchmark68 "<<cos(t)*sin(v)-cos(y)<<" expression@11\n",cos(t) * sin(v) > cos(y))
			) 
	{
		bFile<<"node2@benchmark68\n";
		printf("Solved 68");
    }
	bFile<<"node3@benchmark68\n";
	return;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callBenchmark69
 * Signature: (DDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callBenchmark69
  (JNIEnv *env, jobject, jdouble x, jdouble y, jdouble z, jdouble w, jdouble t, jdouble v, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<setiosflags(ios::scientific);
	bFile<<setprecision(16);
	if(
			(bFile<<"node1@benchmark69 "<<z+w-(x+y)<<" expression@2\n",z + w > x + y)&&
			(bFile<<"node1@benchmark69 "<<x-(w+y-z)<<" expression@3\n",x > (w+y-z))&&
			(bFile<<"node1@benchmark69 "<<w-x/y<<" expression@4\n",w < x/y)&&
			(bFile<<"node1@benchmark69 "<<sqrt(x-t)-z/y<<" expression@5\n",sqrt(x-t) > z / y)&&
			(bFile<<"node1@benchmark69 "<<log(x*y)-log(t+w+z)<<" expression@6\n",log(x*y) > log(t+w+z))&&
			(bFile<<"node1@benchmark69 "<<log10(t*x)-sqrt(w*y*z)<<" expression@7\n",log10(t*x) < sqrt(w*y*z))&&
			(bFile<<"node1@benchmark69 "<<x-y+tan(v)-(z+t)/(w+t)<<" expression@8\n",x - y + tan(v)> (z+ t) / (w + t))&&
			(bFile<<"node1@benchmark69 "<<x*cos(t+y)-log(w*z*3)<<" expression@9\n",x * cos(t + y) > log(w*z*3))&&
			(bFile<<"node1@benchmark69 "<<z*2+w*3+x*7-pow(y,t)*cos(v)<<" expression@10\n",z*2 + w*3 + x*7 < pow(y,t)*cos(v))&&
			(bFile<<"node1@benchmark69 "<<cos(t)*sin(v)-cos(y)<<" expression@11\n",cos(t) * sin(v) > cos(y))&&
			(bFile<<"node1@benchmark69 "<<sin(x*y)+sin(z*w)+sin(t*v)-(cos(x*y)+cos(z*w)+cos(t*v))<<" expression@12\n",sin(x*y) + sin(z*w) + sin(t*v) < cos(x*y) + cos(z*w) + cos(t*v))

	) {
		bFile<<"node2@benchmark69\n";
		printf("Solved 69");
    }
	bFile<<"node3@benchmark69\n";
	return;
}
