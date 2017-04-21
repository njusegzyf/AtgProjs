/*
 * WhileTest.cpp
 *
 *  Created on: Dec 25, 2014
 *      Author: zy
 */

#include <iostream>
using namespace std;

int sum(int n)
{
	int sum = 0;
	int i=0;
//	for (i=0; i<n; i++)
//	{
//		sum += i;
//	}

//	do{
//		do{
//			do{
//				i+=2;
//			}while(i<n/2);
//			i--;
//		}while(i<n-2);
//	}
//	while (i<n);

//	while (i<n)
//	{
//		sum += i;
//		while(i<40){
//			i--;
//		}
//		i++;
//		while(i>3){
//			i++;
//		}
//	}

//	if(i<n){
//		for (i=0; i<n; i++)
//		{
//			sum += i;
//		}
//		cout<<"test";
//	}else{
//		cout<<"test2";
//	}

	for (i=0; i<n; i++)
		for(int j=0; j<n; j++)
//	    if(i>5){
//	    	break;
//	    }
//	    else{
	    	i--;
//	    }



	return sum;
}
