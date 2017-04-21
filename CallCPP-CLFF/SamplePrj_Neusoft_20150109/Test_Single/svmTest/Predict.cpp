/*****************************************************************************
*
* Neusoft Confidential Proprietary
*
* Copyright (c) 2013 Neusoft AAC;
* All Rights Reserved
*
*****************************************************************************
*
* THIS SOFTWARE IS PROVIDED BY NEUSOFT "AS IS" AND ANY EXPRESSED OR
* IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
* IN NO EVENT SHALL NEUSOFT OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
* INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
* HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
* STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
* IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
* THE POSSIBILITY OF SUCH DAMAGE.
*
****************************************************************************/
/**
* \brief	Pedestrain Detection implment
*
* \author	2013/04/04, zhanglk : created.\n
****************************************************************************/

#include "stdafx.h"
#include "Predict.h"
#include <fstream>
#include "feature.h"
#include "model.h"

#define WEIGHTED_FEATURE	(0)
#define BETA				(0.75)



bool prediction2(NeqImage* img,double thres)
{
	double	mulFeature_data[ALL_FERATURE_NUM];
	int		mulTypeFeatureNum = ALL_FERATURE_NUM;
	double	sum = 0;
	int ind;

	getFeatureData(img,mulFeature_data);

#if WEIGHTED_FEATURE
	for(int i=0;i<HOG_VECT_NUM;i++)
	{
		mulFeature_data[i] *= (1 - BETA);
	}
	for(int i=HOG_VECT_NUM;i<ALL_FERATURE_NUM;i++)
	{
		mulFeature_data[i] *= BETA;
	}
#endif


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