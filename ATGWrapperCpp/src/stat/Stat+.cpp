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

class StatCalculator
{
    static vector<int> values;
    static double sum;
    static double sumOfSquares;
    static double mean;
    static double deviation;
    static int count;
    
public:
	~StatCalculator(){
		clear();
	}

    static void clear()
    {
        values.clear();
        sum = 0;
        sumOfSquares = 0;
        mean = 0;
        deviation = 0;
        count = 0;
    }
    
    static int getMedian()
    {
        return values[values.size() / 2];
    }
    
    static double getMean(){
        return mean;
    }
    
    static double getStandardDeviation()
    {
        return deviation;
    }
    
    static int getMin(){
        return values[0];
    }
    
    static int getMax(){
        return values[count-1];
    }
    
    static int getCount(){
        return count;
    }
    
    static void addValue_inst(int val, char* path){
        ofstream bFile(path);
        bFile<<"entry(addValue)\n";
        bFile<<"node1\n";
        int index = binary_search(values.begin(),values.end(), val);
        if ((bFile<<"node2 "<<index-0<<" index>=0\n",index >= 0) &&
        (bFile<<"node2 "<<index-values.size()<<" index<values.size()\n",index < values.size())){
            bFile<<"node3\n";
            values.insert(values.begin()+index, val);
        }else if ((bFile<<"node4 "<<index-values.size()<<" index==values.size()\n",index == values.size())
        || (bFile<<"node4 "<<values.size()-0<<" values.size()==0\n",values.size() == 0)){
            bFile<<"node5\n";
            values.push_back(val);
        }else{
            bFile<<"node6\n";
            values.insert(values.begin()+(index * (-1)) - 1, val);
        }
        bFile<<"node7\n";
        count++;
        printf("stat \n");
        double currentVal = val;
        sum += currentVal;
        sumOfSquares += currentVal * currentVal;
        mean = sum / count;
        deviation = sqrt((sumOfSquares / count) - (mean * mean) );
        bFile<<"exit(addValue)\n";
    }
};
vector<int> StatCalculator::values;
double StatCalculator::sum = 0;
double StatCalculator::sumOfSquares = 0;
double StatCalculator::mean = 0;
double StatCalculator::deviation = 0;
int StatCalculator::count = 0;

void run_inst(int val, char* path) {
	ofstream bFile(path);
	bFile<<"node1\n";
    printf("adding value\n");
    bFile<<"call1\n";
    StatCalculator::addValue_inst(val, path);
    bFile<<"call2\n";
    StatCalculator::addValue_inst(val, path);
//    StatCalculator::addValue(val);
//    StatCalculator::addValue(val);
    //stat.addValue(val);
    if(bFile<<"node2 "<<StatCalculator::getMedian()-3<<
    " StatCalculator::getMedian()==3\n",StatCalculator::getMedian() == 3) {
    	bFile<<"node3\n";
        printf("median value is 3\n");
    } else {
    	bFile<<"node4\n";
        printf("median value is not 3\n");
    }
    if(bFile<<"node5 "<<StatCalculator::getStandardDeviation()-0.82915619758885<<
    " StatCalculator::getStandardDeviation()<=0.82915619758885\n",
    StatCalculator::getStandardDeviation() <= 0.82915619758885) {
    	bFile<<"node6\n";
        printf("std deviation is .10\n");
    } else {
    	bFile<<"node7\n";
        printf("std deviation not found\n");
    }
    bFile<<"node8\n";
}

int main(int argc, const char * argv[])
{
    stat(4);
    return 0;
}

