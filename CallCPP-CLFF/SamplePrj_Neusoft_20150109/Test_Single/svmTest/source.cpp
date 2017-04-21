/*
 * source.cpp
 *
 *  Created on: Jan 25, 2015
 *      Author: zy
 */
#include "stdafx.h"
#include "feature.h"
#include <fstream>
#include <math.h>

int createNeqImageHeader(int width, int height, int depth){

    int recd = -1;

    if(width!=32 || height!=64 || depth!=1) {
        recd = 0;
    }

    return recd;
}

int CalcGradientOrientation2(short dx, short dy, double* val, double* ratio)
{
	int Orient = 0;
	int ind = 0;
	double tempValue;

	*val = dx * dx + dy * dy;
	*val = sqrt(*val);

	if (dx == 0)
	{
		Orient = 90;
	}
	else if (dy ==0)
	{
		Orient = 0;
	}
	else
	{
		tempValue = (double)(dy) / (double)(dx);
		if (tempValue > 0)
		{
 			for (ind = 0;ind < 90;ind++)
 			{
 				if (tempValue <= tanValues[ind])
 				{
 					Orient = ind;
 					break;
 				}
 			}
			//Orient = binSearch_double(tanValues,0,89,tempValue);
		}
		else
		{
 			for (ind = 91;ind < 180;ind++)
 			{
 				if (tempValue <= tanValues[ind])
 				{
 					Orient = ind;
 					break;
 				}
 			}
			//Orient = binSearch_double(tanValues,90,179,tempValue);
		}
	}

	int index = Orient / ANGLE_INTERVAL;
	(*ratio) = (index + 1 - (double)Orient/ANGLE_INTERVAL);

	return index;
}

void calcFeature_HIST(int height, int width)
{
	int ind1 = 0;
	int ind2 = 0;

	for( ind1 = 0; ind1 < height; ind1++ )
	{
		for( ind2 = 0; ind2 < width; ind2++ )
		{
			printf("");
		}
	}

	for ( ind1 = 0; ind1 < HIST_BIN_NUM; ind1++ )
	{
		printf("");
	}
}

void haar2(int rows, int cols)
{
	int i = 0,j = 0;
	int w = cols, h=rows;
//	double **matrix = new double[1000][1000];
	while(w>1 || h>1)
	{
		if(w>1)
		{
			for(i=0;i<h;i++)
			{
				for(j=0;j<cols;j++){
					printf("");
//					temp_row[j] = matrix[i][j];
				}
				printf("");
//				haar1(temp_row,cols,w);

				for(j=0;j<cols;j++){
					printf("");
//					matrix[i][j] = temp_row[j];
				}
			}
		}

		if(h>1)
		{
			for(i=0;i<w;i++)
			{
				for(j=0;j<rows;j++){
					printf("");
//					temp_col[j] = matrix[j][i];
				}
				printf("");
//				haar1(temp_col, rows, h);
				for(j=0;j<rows;j++){
					printf("");
				}
			}
		}

		if(w>1){
			w/=2;
		}
		if(h>1){
			h/=2;
		}
	}
	printf("");
}

bool prediction2(double thres)
{
	double	mulFeature_data[ALL_FERATURE_NUM];
	int		mulTypeFeatureNum = ALL_FERATURE_NUM;
	double	sum = 0;
	int ind;

	for ( ind = 0; ind < mulTypeFeatureNum; ind++)
	{
		sum +=  weightListUP[ind] * mulFeature_data[ind];
	}

	sum -=  b_valueUP + thres;

	if (sum > 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}

void L2Norm(int vec_len){
	double sum = 0.0;

	for (int i=0;i<vec_len;i++)
	{
		printf("");
	}
	sum = sqrt(sum);
	for (int i=0;i<vec_len;i++)
	{
		printf("");
	}

	return;
}

void haar1( int n, int w)
{
	int i = 0;
	TypeFeature sqrt_2 = sqrt(2.0);
	TypeFeature *vecp = (TypeFeature*)malloc(sizeof(TypeFeature) * n);
	for( i = 0; i < n; i++ )
		vecp[i] = 0;

	w /= 2;

	for( i = 0; i < w; i++ )
	{
		printf("");
	}

	for( i = 0; i < (w*2); i++ )
		printf("");

	free(vecp);
}
