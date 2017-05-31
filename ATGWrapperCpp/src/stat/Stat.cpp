//
//  Stat.cpp
//
//  Created by zhou yan on 15/5/14.
//  Copyright (c) 2015�� zhou yan. All rights reserved.
//

#include <vector>
#include <algorithm>
#include <math.h>
using namespace std;

//class StatCalculator
//{
static vector<int> values = new vector<int>;
static double sum = 0.0;
static double sumOfSquares = 0.0;
static double mean = 0.0;
static double deviation = 0.0;
static int count = 0;

//public:
//	~StatCalculator(){
//		clear();
//	}

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

static void addValue(int val) {
  int index = binary_search(values.begin(), values.end(), val);
  if (index >= 0 && index < values.size()) {
    values.insert(values.begin() + index, val);
  } else if (index == values.size() || values.size() == 0) {
    values.push_back(val);
  } else {
    values.insert(values.begin() + (index * (-1)) - 1, val);
  }
  count++;
  printf("stat \n");
  double currentVal = val;
  sum += currentVal;
  sumOfSquares += currentVal * currentVal;
  mean = sum / count;
  deviation = sqrt((sumOfSquares / count) - (mean * mean));
}
//};
//vector<int> StatCalculator::values;
//double StatCalculator::sum = 0;
//double StatCalculator::sumOfSquares = 0;
//double StatCalculator::mean = 0;
//double StatCalculator::deviation = 0;
//int StatCalculator::count = 0;

void stat(int val) {
  printf("adding value\n");
//    StatCalculator::addValue(val);
//    StatCalculator::addValue(val);
//    StatCalculator::addValue(val);
//    StatCalculator::addValue(val);
  addValue(val);
  addValue(val);
  addValue(val);
  addValue(val);

  if ( /* StatCalculator:: */getMedian() == 3) {
    printf("median value is 3\n");
  } else {
    printf("median value is not 3\n");
  }
  if ( /* StatCalculator:: */getStandardDeviation() <= 0.82915619758885) {
    printf("std deviation is .10\n");
  } else {
    printf("std deviation not found\n");
  }

}

int main(int argc, const char * argv[]) {
  stat(4);
  return 0;
}

