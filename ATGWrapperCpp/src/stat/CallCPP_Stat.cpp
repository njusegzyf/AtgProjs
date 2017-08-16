/*
 * CallCPP_Stat.cpp
 *
 * @author Zhang Yifan
 */

#include "../cn_nju_seg_atg_callCPP_CallCPP.h"

#include <math.h>
#include <malloc.h>

#include <iostream>
#include <vector>
#include <limits>
#include <iomanip>
#include <fstream>
#include <cstring>

#include <jni.h>

using namespace std;

char* jstringTostring(JNIEnv* env, jstring jstr) {
  char* rtn = NULL;
  jclass clsstring = env->FindClass("java/lang/String");
  jstring strencode = env->NewStringUTF("utf-8");
  jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
  jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
  jsize alen = env->GetArrayLength(barr);
  jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
  if (alen > 0) {
    rtn = (char*) malloc(alen + 1);
    memcpy(rtn, ba, alen);
    rtn[alen] = 0;
  }
  env->ReleaseByteArrayElements(barr, ba, 0);
  return rtn;
}

class StatCalculator {
public:
  static vector<int> values;
  private:
  static double sum;
  static double sumOfSquares;
  static double mean;
  static double deviation;
  static int count;

public:
  ~StatCalculator() {
    clear();
  }

  static void clear() {
    values.clear();
    sum = 0;
    sumOfSquares = 0;
    mean = 0;
    deviation = 0;
    count = 0;
  }

  static int getMedian() {
    return values[values.size() / 2];
  }

  static double getMean() {
    return mean;
  }

  static double getStandardDeviation() {
    return deviation;
  }

  static int getMin() {
    return values[0];
  }

  static int getMax() {
    return values[count - 1];
  }

  static int getCount() {
    return count;
  }

  static int binary_search(vector<int> &values, int key) {
    int low = 0;
    int high = values.size() - 1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      int midVal = values[mid];
      if (midVal < key)
        low = mid + 1;
      else if (midVal > key)
        high = mid - 1;
      else
        return mid;
    }
    return -(low + 1);
  }

  static void addValue_inst(int val, char* path, ofstream &writer) {
    writer << "entry@addValue\n";
    writer << "node1@addValue\n";
    int index = binary_search(values, val);
    if ((writer << "node2@addValue " << index - 0 << " expression@12\n", index >= 0)
        && (writer << "node2@addValue " << index - (int) values.size() << " expression@13\n", index < values.size())) {
      writer << "node3@addValue\n";
      values.insert(values.begin() + index, val);
    } else if ((writer << "node4@addValue " << index - (int) values.size() << " expression@17\n", index == values.size())
        || (writer << "node4@addValue " << values.size() - 0 << " expression@18\n", values.size() == 0)) {
      writer << "node5@addValue\n";
      values.push_back(val);
    } else {
      writer << "node6@addValue\n";
      values.insert(values.begin() + (index * (-1)) - 1, val);
    }
    writer << "node7@addValue\n";
    count++;
    printf("stat \n");
    double currentVal = val;
    sum += currentVal;
    sumOfSquares += currentVal * currentVal;
    mean = sum / count;
    deviation = sqrt((sumOfSquares / count) - (mean * mean));
    writer << "exit@addValue\n";
  }

  static void addValue1_inst(int val, char* path, ofstream &writer) {
    writer << "entry@addValue1\n";
    writer << "node1@addValue1\n";
    int index = binary_search(values, val);
    // printf("index %d\n", index);
    if ((writer << "node2@addValue1 " << index - 0 << " expression@12\n", index >= 0)
        && (writer << "node2@addValue1 " << index - (int) values.size() << " expression@13\n", index < values.size())) {
      writer << "node3@addValue1\n";
      values.insert(values.begin() + index, val);
    } else if ((writer << "node4@addValue1 " << index - (int) values.size() << " expression@17\n", index == values.size())
        || (writer << "node4@addValue1 " << values.size() - 0 << " expression@18\n", values.size() == 0)) {
      writer << "node5@addValue1\n";
      values.push_back(val);
    } else {
      writer << "node6@addValue1\n";
      values.insert(values.begin() + (index * (-1)) - 1, val);
    }
    writer << "node7@addValue1\n";
    count++;
    printf("stat \n");
    double currentVal = val;
    sum += currentVal;
    sumOfSquares += currentVal * currentVal;
    mean = sum / count;
    deviation = sqrt((sumOfSquares / count) - (mean * mean));
    writer << "exit@addValue1\n";
  }

  static void addValue2_inst(int val, char* path, ofstream &writer) {
    writer << "entry@addValue2\n";
    writer << "node1@addValue2\n";
    int index = binary_search(values, val);
    if ((writer << "node2@addValue2 " << index - 0 << " expression@26\n", index >= 0)
        && (writer << "node2@addValue2 " << index - (int) values.size() << " expression@27\n", index < values.size())) {
      writer << "node3@addValue2\n";
      values.insert(values.begin() + index, val);
    } else if ((writer << "node4@addValue2 " << index - (int) values.size() << " expression@31\n", index == values.size())
        || (writer << "node4@addValue2 " << values.size() - 0 << " expression@32\n", values.size() == 0)) {
      writer << "node5@addValue2\n";
      values.push_back(val);
    } else {
      writer << "node6@addValue2\n";
      values.insert(values.begin() + (index * (-1)) - 1, val);
    }
    writer << "node7@addValue2\n";
    count++;
    printf("stat \n");
    double currentVal = val;
    sum += currentVal;
    sumOfSquares += currentVal * currentVal;
    mean = sum / count;
    deviation = sqrt((sumOfSquares / count) - (mean * mean));
    writer << "exit@addValue2\n";
  }

  static void addValue3_inst(int val, char* path, ofstream &writer) {
    writer << "entry@addValue3\n";
    writer << "node1@addValue3\n";
    int index = binary_search(values, val);
    if ((writer << "node2@addValue3 " << index - 0 << " expression@40\n", index >= 0)
        && (writer << "node2@addValue3 " << index - (int) values.size() << " expression@41\n", index < values.size())) {
      writer << "node3@addValue3\n";
      values.insert(values.begin() + index, val);
    } else if ((writer << "node4@addValue3 " << index - (int) values.size() << " expression@45\n", index == values.size())
        || (writer << "node4@addValue3 " << values.size() - 0 << " expression@46\n", values.size() == 0)) {
      writer << "node5@addValue3\n";
      values.push_back(val);
    } else {
      writer << "node6@addValue3\n";
      values.insert(values.begin() + (index * (-1)) - 1, val);
    }
    writer << "node7@addValue3\n";
    count++;
    printf("stat \n");
    double currentVal = val;
    sum += currentVal;
    sumOfSquares += currentVal * currentVal;
    mean = sum / count;
    deviation = sqrt((sumOfSquares / count) - (mean * mean));
    writer << "exit@addValue3\n";
  }

  static void addValue4_inst(int val, char* path, ofstream &writer) {
    writer << "entry@addValue4\n";
    writer << "node1@addValue4\n";
    int index = binary_search(values, val);
    if ((writer << "node2@addValue4 " << index - 0 << " expression@54\n", index >= 0)
        && (writer << "node2@addValue4 " << index - (int) values.size() << " expression@55\n", index < values.size())) {
      writer << "node3@addValue4\n";
      values.insert(values.begin() + index, val);
    } else if ((writer << "node4@addValue4 " << index - (int) values.size() << " expression@59\n", index == values.size())
        || (writer << "node4@addValue4 " << values.size() - 0 << " expression@60\n", values.size() == 0)) {
      writer << "node5@addValue4\n";
      values.push_back(val);
    } else {
      writer << "node6@addValue4\n";
      values.insert(values.begin() + (index * (-1)) - 1, val);
    }
    writer << "node7@addValue4\n";
    count++;
    printf("stat \n");
    double currentVal = val;
    sum += currentVal;
    sumOfSquares += currentVal * currentVal;
    mean = sum / count;
    deviation = sqrt((sumOfSquares / count) - (mean * mean));
    writer << "exit@addValue4\n";
  }

};

vector<int> StatCalculator::values;
double StatCalculator::sum = 0;
double StatCalculator::sumOfSquares = 0;
double StatCalculator::mean = 0;
double StatCalculator::deviation = 0;
int StatCalculator::count = 0;

static void skip() {
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callStat
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callStat(JNIEnv *env, jobject,
    jint val,
    jstring pathFile) {

  char* path = jstringTostring(env, pathFile);
  ofstream bFile(path);

  bFile<<"node1@stat\n";
  printf("adding value %d\n", val);
  // bFile<<"call@stat\n";
  StatCalculator::addValue1_inst(val,path,bFile);
  // bFile<<"call@stat\n";
  StatCalculator::addValue2_inst(val,path,bFile);
  // bFile<<"call@stat\n";
  StatCalculator::addValue3_inst(val,path,bFile);
  // bFile<<"call@stat\n";
  StatCalculator::addValue4_inst(val,path,bFile);

  if(bFile << "node2@stat "<< StatCalculator::getMedian()-3 << " expression@2\n",StatCalculator::getMedian() == 3) {
    bFile << "node3@stat\n";
    printf("median value is 3\n");
  } else {
    bFile<<"node4@stat\n";
    printf("median value is not 3\n");
  }
  if(bFile<<"node5@stat "<<StatCalculator::getStandardDeviation()-0.82915619758885<< " expression@4\n",StatCalculator::getStandardDeviation() <= 0.82915619758885) {
    bFile<<"node6@stat\n";
    printf("std deviation is .10\n");
  } else {
    bFile<<"node7@stat\n";
    printf("std deviation not found\n");
  }
  bFile<<"exit@stat\n";

  delete[] path;
}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    prepareStat
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_prepareStat(JNIEnv *, jobject) {
  StatCalculator::clear();
}

