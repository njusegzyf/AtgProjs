/*
 * ArrayTest.cpp
 *
 *  Created on: Mar 15, 2016
 *      Author: zy
 */

void array_test(int low, int high, int step, double array[]){
    double min, max;

    min = array[low];
    max = array[low];
    int i = low + step;
    while(i < high){
    	if(max < array[i])
    		max = array[i];
    	if(min > array[i])
    		min = array[i];
    	i += step;
    }

    printf("min: %f, max : %f/n",min ,max);
}


