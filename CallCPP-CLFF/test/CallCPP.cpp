/*
 * CallCPP.cpp
 *
 *  Created on: Jun 17, 2015
 *      Author: zy
 */

#include "cn_nju_seg_atg_callCPP_CallCPP.h"
//#include <limits>
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

int f_inst(int a, int b, char* path, ofstream &writer){
	writer<<"entry@f\n";
	if(writer<<"node1@f "<<b-10<<" expression@9\n",b > 10){
		writer<<"node2@f\n";
		return b - a;
	}else{
		writer<<"node3@f\n";
		return a - b;
	}
}
/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callTest
 * Signature: (IILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTest
  (JNIEnv *env, jobject, jint a, jint b, jstring pathFile){
	char* path = jstringTostring(env, pathFile);
	ofstream bFile(path);
	bFile<<"node1@test\n";
	int c; int temp;
	if((bFile<<"node2@test "<<a-1<<" expression@3\n",a>1) &&
			(((bFile<<"node2@test "<<b-10<<" expression@4\n",b<10) &&
			(bFile<<"node2 "<<a-b<<" expression@5\n",a>b)) ||
			(bFile<<"call@test\n",temp=f_inst(a,b,path,bFile),
			bFile<<"node2@test "<<temp-0<<" expression@6\n",temp>0))){
		bFile<<"node3@test\n";
		c = a + b;
	}
	bFile<<"node4@test\n";
	return c;
}



