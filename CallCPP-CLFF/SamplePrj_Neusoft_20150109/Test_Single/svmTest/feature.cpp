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
* \brief	This file about get feature(EOH + HAAR + HIST) data
*
* \author	2013/04/04, zhanglk : created.\n
****************************************************************************/

#include "stdafx.h"
#include "feature.h"
#include <fstream>
#include <math.h>


#define MEASURE_TIME 0

#define CELLS_PER_HOR_LINE ( ROI_IMG_WIDTH / CELL_SIZE)
#define CELLS_PER_VER_LINE ( ROI_IMG_HEIGHT/ CELL_SIZE)

#define SIGN(a,b) ((a) < (b) ? (1) : (0))

typedef unsigned char uchar;


double sum = 0.0;
int numS = 0;
double Avgtime = 0.0;

int gradMapVer[ ROI_IMG_HEIGHT * ROI_IMG_WIDTH ];
int gradMapHon[ ROI_IMG_HEIGHT * ROI_IMG_WIDTH ];
double HogCell[CELL_VECT_LENGTH];
TypeFeature AllIntMap[CELL_VECT_LENGTH][( CELLS_PER_VER_LINE + 1 ) * ( CELLS_PER_HOR_LINE + 1 )];
//LBP feature
int LBPIntMap[LBP_VECT_PER_BLOCK][( ROI_IMG_HEIGHT + 1 ) * ( ROI_IMG_WIDTH + 1 )];
int LBPmap[ROI_IMG_HEIGHT][ROI_IMG_WIDTH];
int LBPValueMap[ROI_IMG_HEIGHT][ROI_IMG_WIDTH];
int LBPCell[LBP_VECT_PER_BLOCK];
int uniform_patterns[LBP_VECT_PER_BLOCK-1] = {0, 1, 2, 3, 4, 6, 7, 8, 12, 14, 15, 16, 24, 28, 30, 31, 32, 48, 56, 60, 62, 63, 64, 96, 112, 120, 124, 126, 127, 128, 129, 131, 135, 143, 159, 191, 192, 193, 195, 199, 207, 223, 224, 225, 227, 231, 239, 240, 241, 243, 247, 248, 249, 251, 252, 253, 254, 255};

double tanValues[] = {0.0000000000,0.0174550652,0.0349207700,0.0524077801,0.0699268130,0.0874886648,0.1051042368,0.1227845627,0.1405408368,0.1583844427,0.1763269834,0.1943803121,0.2125565649,0.2308681947,0.2493280067,0.2679491966,0.2867453902,0.3057306863,0.3249197014,0.3443276188,0.3639702401,0.3838640412,0.4040262324,0.4244748232,0.4452286927,0.4663076660,0.4877325969,0.5095254583,0.5317094409,0.5543090612,0.5773502795,0.6008606299,0.6248693634,0.6494076053,0.6745085296,0.7002075517,0.7265425422,0.7535540651,0.7812856423,0.8097840498,0.8390996488,0.8692867564,0.9004040639,0.9325151069,0.9656887967,1.0000000232,1.0355303384,1.0723687361,1.1106125425,1.1503684366,1.1917536238,1.2348971897,1.2799416676,1.3270448594,1.3763819608,1.4281480498,1.4825610147,1.5398650134,1.6003345823,1.6642795397,1.7320508694,1.8040478222,1.8807265379,1.9626105843,2.0503039274,2.1445070143,2.2460368768,2.3558524790,2.4750869784,2.6050892032,2.7474775737,2.9042110504,3.0776837316,3.2708528387,3.4874146950,3.7320510962,4.0107812684,4.3314762666,4.7046305747,5.1445545754,5.6712825037,6.3137523681,7.1153708139,8.1443478689,9.5143664364,11.4300551879,14.3006708136,19.0811448773,28.6362719117,57.2900369699,-43093968.8414040130,-57.2898845988,-28.6362338073,-19.0811279334,-14.3006612759,-11.4300490782,-9.5143621888,-8.1443447440,-7.1153684178,-6.3137504716,-5.6712809646,-5.1445533007,-4.7046295011,-4.3314753495,-4.0107804754,-3.7320504034,-3.4874140841,-3.2708522957,-3.0776832456,-2.9042106125,-2.7474771770,-2.6050888418,-2.4750866476,-2.3558521750,-2.2460365962,-2.1445067545,-2.0503036859,-1.9626103591,-1.8807263273,-1.8040476247,-1.7320506838,-1.6642793647,-1.6003344170,-1.5398648569,-1.4825608663,-1.4281479088,-1.3763818264,-1.3270447312,-1.2799415451,-1.2348970726,-1.1917535115,-1.1503683287,-1.1106124388,-1.0723686363,-1.0355302422,-0.9999999304,-0.9656887070,-0.9325150201,-0.9004039799,-0.8692866749,-0.8390995697,-0.8097839730,-0.7812855675,-0.7535539923,-0.7265424713,-0.7002074825,-0.6745084621,-0.6494075393,-0.6248692989,-0.6008605667,-0.5773502176,-0.5543090006,-0.5317093814,-0.5095253998,-0.4877325394,-0.4663076095,-0.4452286371,-0.4244747684,-0.4040261784,-0.3838639880,-0.3639701875,-0.3443275669,-0.3249196501,-0.3057306355,-0.2867453400,-0.2679491468,-0.2493279574,-0.2308681458,-0.2125565164,-0.1943802639,-0.1763269355,-0.1583843951,-0.1405407895,-0.1227845156,-0.1051041899,-0.0874886181,-0.0699267663,-0.0524077335,-0.0349207235,-0.0174550188,-0.0000000464,};

int binSearch_double(const double *Array,int start,int end,double key)
{
	int left,right;
	int mid;
	left=start;
	right=end;

	while (right - left > 1) 
	{ 
		mid=(left+right)/2;

		if (key<Array[mid]) 
		{
			right=mid;
		}
		else if(key>Array[mid])
		{
			left=mid;
		}
		else
			return mid;
	}

	return right;
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

void calcGradient( NeqImage *img)
{
	int ind1 = 0;
	int ind2 = 0;
	int ind3 = 0;

	for ( ind1 = 1 ; ind1 < ROI_IMG_HEIGHT-1; ind1++ )
	{
		for ( ind2 = 1; ind2 < ROI_IMG_WIDTH-1; ind2++ )
		{
			ind3 = ind1*ROI_IMG_WIDTH+ind2;

			gradMapVer[ind3] = ((uchar)img->imageData[ind3 + ROI_IMG_WIDTH] - (uchar)img->imageData[ind3 - ROI_IMG_WIDTH]);

			gradMapHon[ind3] = ((uchar)img->imageData[ind3+1] - (uchar)img->imageData[ind3-1]);
		}
	}

	int temp = (ROI_IMG_HEIGHT-1)*ROI_IMG_WIDTH;
	for ( ind2 = 0; ind2 < ROI_IMG_WIDTH; ind2++)
	{
		gradMapVer[ind2] = 0;
		gradMapHon[ind2] = 0;
		gradMapVer[temp+ind2] = 0;
		gradMapHon[temp+ind2] = 0;

	}

	for (ind1 = 1; ind1 < ROI_IMG_HEIGHT-1; ind1++)
	{
		gradMapVer[ind1*ROI_IMG_WIDTH] = 0;
		gradMapHon[ind1*ROI_IMG_WIDTH] = 0;
		gradMapVer[ind1*ROI_IMG_WIDTH+ROI_IMG_WIDTH-1] = 0;
		gradMapHon[ind1*ROI_IMG_WIDTH+ROI_IMG_WIDTH-1] = 0;
	}	
}

void getOrientGradvalue()
{
	int ind1 = 0;
	int ind2 = 0;
	int index = 0;
	int index2 = 0;
	double val = 0;
	double ratio = 1.0;
	int ind3,ind4,ind5,ind6,ind7,ind8;
	int org = 0;

	for (ind1 = 0;ind1 < CELL_VECT_LENGTH; ind1++)
	{
		for (ind2 = 0;ind2 < (CELLS_PER_HOR_LINE+1); ind2++)
		{
			AllIntMap[ind1][ind2] = 0;
		}
		for (ind2 = 1;ind2 < (CELLS_PER_VER_LINE+1); ind2++)
		{
			AllIntMap[ind1][ind2 * (CELLS_PER_HOR_LINE+1)] = 0;
		}
	}

	for ( ind1 = 0; ind1 < CELLS_PER_VER_LINE; ind1++)
	{
		for ( ind2 = 0; ind2 < CELLS_PER_HOR_LINE; ind2++)
		{
			memset(HogCell,0,sizeof(HogCell));

			for ( ind3 = ind1 * CELL_SIZE; ind3 < (ind1 + 1) * CELL_SIZE; ind3++ )
			{
				for ( ind4 = ind2 * CELL_SIZE; ind4 < (ind2 + 1) * CELL_SIZE; ind4++)
				{
					//if ( 0 == ind3 || 0 == ind4 || (ROI_IMG_WIDTH - 1) == ind4 || (ROI_IMG_HEIGHT - 1) == ind3)
					//{
					//	continue;
					//}
					index =  CalcGradientOrientation2(gradMapHon[ind3*ROI_IMG_WIDTH+ind4],gradMapVer[ind3*ROI_IMG_WIDTH+ind4],&val,&ratio);
					index2 = (1 + index) % CELL_VECT_LENGTH;

					HogCell[index]  += val * ratio;
					HogCell[index2] += val * ( 1 - ratio );
				}
			}

			ind5 = (ind1+1)*(CELLS_PER_HOR_LINE+1)+ind2+1;
			ind6 = ind5 - 1;
			ind7 = ind5 - (CELLS_PER_HOR_LINE+1);
			ind8 = ind7 - 1;

			for ( org = 0; org < CELL_VECT_LENGTH;org++)
			{
				AllIntMap[org][ind5] = AllIntMap[org][ind6] + AllIntMap[org][ind7] - AllIntMap[org][ind8] + HogCell[org];
			}	
		}
	}
}


void GetCellOrientHist(int ver, int hon, double *vect)
{
	int ind = 0;

	for (ind = 0; ind < CELL_VECT_LENGTH; ind++)
	{
		vect[ind] = AllIntMap[ind][ver*(CELLS_PER_HOR_LINE+1)+hon] +
			AllIntMap[ind][(ver+1)*(CELLS_PER_HOR_LINE+1)+hon+1] -
			AllIntMap[ind][(ver+1)*(CELLS_PER_HOR_LINE+1)+hon] - 
			AllIntMap[ind][ver*(CELLS_PER_HOR_LINE+1)+hon+1];
	}
}

void calcEOHVect(TypeFeature* ptrSrc,TypeFeature* ptrDst)
{
	int index = 0;
	TypeFeature sum = 0;
	int i,j;
	for (i=0;i<CELL_VECT_LENGTH;i++)
	{
		//for(j=0;j<CELL_VECT_LENGTH;j++)
		for(j=i+1;j<CELL_VECT_LENGTH;j++)
		{
			if(i != j)
			{
				ptrDst[index] = ptrSrc[i] / (ptrSrc[j] + MINE); 
				sum += ptrDst[index] * ptrDst[index];
				index++;
			}

		}

	}

	sum = sqrt(sum);
	for (int i=0;i< index ;i++)// L2 normalization
	{
		ptrDst[i] /= (sum + MINE);
	}

}

void calcFeature_EOH( NeqImage* img,TypeFeature* ptrFearture)
{
	int ind1 = 0;
	int ind2 = 0;
	double normPara = 0;

	int ind3 = 0;


	calcGradient(img);

	getOrientGradvalue();

	for (ind1 = 0; ind1 < ROI_IMG_HEIGHT-CELL_SIZE; ind1 += CELL_SIZE)
	{
		for (ind2 = 0; ind2 < ROI_IMG_WIDTH-CELL_SIZE; ind2 += CELL_SIZE)
		{
			TypeFeature tempVect[CELL_VECT_LENGTH] = {0};

			GetCellOrientHist(ind1,ind2,tempVect);

			GetCellOrientHist(ind1+CELL_SIZE,ind2,tempVect);
	
			GetCellOrientHist(ind1,ind2+CELL_SIZE,tempVect);

			GetCellOrientHist(ind1+CELL_SIZE,ind2+CELL_SIZE,tempVect);

			calcEOHVect(tempVect,ptrFearture);

			ptrFearture += EOH_VECT_PER_BLOCK;
		}

	}
}

void L2Norm(double* vec_src,double* vec_dst,int vec_len){
	double sum = 0.0;
	for (int i=0;i<vec_len;i++)
	{
		sum += vec_src[i]*vec_src[i];
	}
	sum = sqrt(sum);
	for (int i=0;i<vec_len;i++)
	{
		vec_dst[i] = vec_src[i] / (sum + MINE);
	}
}

double GetEntropy(double *vec)
{
	double entropy = 0.0;
	for (int i=0;i<CELL_VECT_LENGTH;i++)
	{
		entropy += -1 * (vec[i]+0.0001) * log(vec[i]+0.0001);
	}
	return entropy;
}

double X2Chi(double* vec1,double* vec2)
{
	double sum = 0.0;

	for (int i=0;i<CELL_VECT_LENGTH;i++)
	{
		sum += (vec1[i]-vec2[i])*(vec1[i]-vec2[i])/(vec1[i]+vec2[i]+MINE);
	}

	return sum;
}

void calcFeature_HOG(  NeqImage *img,TypeFeature* ptrFearture)
{
	int ind1 = 0;
	int ind2 = 0;
	//double entropy = 0.0;
	int ind3 = 0;
	int ind4 = 0;
	double tempVec1[9] = {0};
	double tempVec2[9] = {0};

	calcGradient(img);

	getOrientGradvalue();

	for (ind1 = 0; ind1 < CELLS_PER_VER_LINE-1; ind1 += 1)
	{
		for (ind2 = 0; ind2 < CELLS_PER_HOR_LINE-1; ind2 += 1)
		{
			//entropy = 0.0;

			GetCellOrientHist(ind1,ind2,ptrFearture);
			L2Norm(ptrFearture,ptrFearture,CELL_VECT_LENGTH);
			//entropy += GetEntropy(ptrFearture);
			ptrFearture += CELL_VECT_LENGTH;

			GetCellOrientHist(ind1+1,ind2,ptrFearture);
			L2Norm(ptrFearture,ptrFearture,CELL_VECT_LENGTH);
			//entropy += GetEntropy(ptrFearture);
			ptrFearture += CELL_VECT_LENGTH;

			GetCellOrientHist(ind1,ind2+1,ptrFearture);
			L2Norm(ptrFearture,ptrFearture,CELL_VECT_LENGTH);
			//entropy += GetEntropy(ptrFearture);
			ptrFearture += CELL_VECT_LENGTH;

			GetCellOrientHist(ind1+1,ind2+1,ptrFearture);
			L2Norm(ptrFearture,ptrFearture,CELL_VECT_LENGTH);
			//entropy += GetEntropy(ptrFearture);
			ptrFearture += CELL_VECT_LENGTH;

			//ptrFearture[0] = entropy;
			//ptrFearture++;
		}
	}

	//RFM features
	//for (ind1 = 0; ind1 < CELLS_PER_VER_LINE; ind1+=1)
	//{
	//	for (ind2 = 0; ind2 < CELLS_PER_HOR_LINE; ind2+=1)
	//	{
	//		GetCellOrientHist(ind1,ind2,tempVec1);

	//		for (ind3 = ind1;ind3 < CELLS_PER_VER_LINE; ind3++)
	//		{
	//			if (ind3 == ind1)
	//			{
	//				for (ind4 = ind2 + 1; ind4<CELLS_PER_HOR_LINE; ind4++)
	//				{
	//					GetCellOrientHist(ind3,ind4,tempVec2);
	//					(*ptrFearture) = X2Chi(tempVec1,tempVec2);
	//					ptrFearture++;
	//				}
	//			} 
	//			else
	//			{
	//				for (ind4 = 0; ind4<CELLS_PER_HOR_LINE; ind4++)
	//				{
	//					GetCellOrientHist(ind3,ind4,tempVec2);
	//					(*ptrFearture) = X2Chi(tempVec1,tempVec2);
	//					ptrFearture++;
	//				}
	//			}

	//		}
	//	}
	//}

	//ptrFearture -= HOG_RFM_VECT_NUM;

	//L2Norm(ptrFearture,ptrFearture,HOG_RFM_VECT_NUM);
}


/** A Modified version of 1D Haar Transform, used by the 2D Haar Transform function **/
void haar1(TypeFeature *vec, int n, int w)
{
	int i = 0;
	TypeFeature sqrt_2 = sqrt(2.0);
	TypeFeature *vecp = (TypeFeature*)malloc(sizeof(TypeFeature) * n);
	for( i = 0; i < n; i++ )
		vecp[i] = 0;

	w /= 2;

	for( i = 0; i < w; i++ )
	{
		vecp[i] = (vec[2*i] + vec[2*i+1])/sqrt_2;
		vecp[i+w] = (vec[2*i] - vec[2*i+1])/sqrt_2;
	}

	for( i = 0; i < (w*2); i++ )
		vec[i] = vecp[i];

	free(vecp);
}


/** The 2D Haar Transform **/
void haar2(TypeFeature **matrix, int rows, int cols)
{
	TypeFeature *temp_row = (TypeFeature*)malloc(sizeof(TypeFeature) * cols);
	TypeFeature *temp_col = (TypeFeature*)malloc(sizeof(TypeFeature) * rows);
	int i = 0,j = 0;
	int w = cols, h=rows;
	while(w>1 || h>1)
	{
		if(w>1)
		{
			for(i=0;i<h;i++)
			{
				for(j=0;j<cols;j++)
					temp_row[j] = matrix[i][j];

				haar1(temp_row,cols,w);

				for(j=0;j<cols;j++)
					matrix[i][j] = temp_row[j];
			}
		}

		if(h>1)
		{
			for(i=0;i<w;i++)
			{
				for(j=0;j<rows;j++)
					temp_col[j] = matrix[j][i];
				haar1(temp_col, rows, h);
				for(j=0;j<rows;j++)
					matrix[j][i] = temp_col[j];
			}
		}

		if(w>1)
			w/=2;
		if(h>1)
			h/=2;
	}

	free(temp_row);
	free(temp_col);
}



void calcFeature_HAAR( NeqImage *img,TypeFeature *ptrFearture)
{
	TypeFeature **imgData = (TypeFeature**) malloc(sizeof(TypeFeature *)*ROI_IMG_HEIGHT);//Allocate temporary memory
	for (int i=0;i<ROI_IMG_HEIGHT;i++)
	{
		imgData[i]=(TypeFeature *)malloc(sizeof(TypeFeature)*ROI_IMG_WIDTH);
	}

	//for( int i = 0; i < ROI_IMG_HEIGHT; i++ )  //�ϰ���
	//{  
	//	for( int j = 0; j < ROI_IMG_WIDTH; j++)  
	//	{  
	//		imgData[i][j]=cvGetReal2D(img,i,j);
	//	}  
	//}  

	// 2D Haar transform
	haar2(imgData,ROI_IMG_HEIGHT,ROI_IMG_WIDTH);

	double sum = 0.0;

	for(int i=0;i<ROI_IMG_HEIGHT/2;i++)
	{
		for(int j=0;j<ROI_IMG_WIDTH/2;j++){
			ptrFearture[i*ROI_IMG_WIDTH/2+j] = imgData[i][j];
			sum += imgData[i][j]*imgData[i][j];
		}
	}
	sum = sqrt(sum);

	for (int i=0;i<ROI_IMG_HEIGHT*ROI_IMG_WIDTH/4;i++)
	{
		ptrFearture[i] /= sum;
	}

	//Free the memory
	for (int i=0;i<ROI_IMG_HEIGHT;i++)
	{
		free(imgData[i]);
	}
	free(imgData);
}

void calcFeature_HIST( NeqImage *srcImg,TypeFeature *ptrFearture)
{
	int ind1 = 0;
	int ind2 = 0;
	int histCom[HIST_BIN_NUM] = {0};
	int width = srcImg->width;
	int height = srcImg->height;
	int widthStep = srcImg->widthStep;
	int totalPixlNum = ROI_IMG_HEIGHT * ROI_IMG_WIDTH;

	for( ind1 = 0; ind1 < height; ind1++ )
	{
		for( ind2 = 0; ind2 < width; ind2++ )
		{
			int bin = (int)(*((unsigned char *)srcImg->imageData + ind1 * widthStep + ind2)/HIST_BIN_STEP);
			histCom[bin]++;
		}
	}

	for ( ind1 = 0; ind1 < HIST_BIN_NUM; ind1++ )
	{
		ptrFearture[ind1] = histCom[ind1] *1.0/ totalPixlNum;
	}

}

int binSearch_int(const int *Array,int start,int end,int key)
{
	int left,right;
	int mid;
	left=start;
	right=end;

	while ( left <= right ) 
	{ 
		mid=(left+right)/2;

		if (key<Array[mid]) 
		{
			right=mid-1;
		}
		else if(key>Array[mid])
		{
			left=mid+1;
		}
		else
			return mid;
	}

	return LBP_VECT_PER_BLOCK - 1;
}

void calcFeature_LBP2( NeqImage *srcImg,double *ptrFearture)
{
	int ind1 = 0;
	int ind2 = 0;
	int ind5 = 0;
	int temp,ind3,ind4,iIndex;
	int ind6,ind7,ind8,ind9;

	for (ind1 = 0;ind1 < LBP_VECT_PER_BLOCK; ind1++)
	{
		for (ind2 = 0;ind2 < (ROI_IMG_WIDTH+1); ind2++)
		{
			LBPIntMap[ind1][ind2] = 0;
		}
		for (ind2 = 1;ind2 < ROI_IMG_HEIGHT+1; ind2++)
		{
			LBPIntMap[ind1][ind2 * (ROI_IMG_WIDTH + 1)] = 0;
		}
	}

	for (ind2 = 0; ind2 < ROI_IMG_WIDTH; ind2++)
	{
		LBPmap[0][ind2] = 0;
		LBPmap[ROI_IMG_HEIGHT-1][ind2] = 0;
	}
	for (ind1 = 1; ind1 < ROI_IMG_HEIGHT - 1; ind1++)
	{
		LBPmap[ind1][0] = 0;
		LBPmap[ind1][ROI_IMG_WIDTH-1] = 0;
	}

	for (ind1=1;ind1<ROI_IMG_HEIGHT-1;ind1++)
	{
		for (ind2=1;ind2<ROI_IMG_WIDTH-1;ind2++)
		{
			temp = (uchar)srcImg->imageData[ind1*ROI_IMG_WIDTH+ind2];

			LBPmap[ind1][ind2] = SIGN(temp,(uchar)srcImg->imageData[ind1*ROI_IMG_WIDTH+ind2+1])
				+ 2 * SIGN(temp,(uchar)srcImg->imageData[(ind1+1)*ROI_IMG_WIDTH+ind2+1])
				+ 4 * SIGN(temp,(uchar)srcImg->imageData[(ind1+1)*ROI_IMG_WIDTH+ind2])
				+ 8 * SIGN(temp,(uchar)srcImg->imageData[(ind1+1)*ROI_IMG_WIDTH+ind2-1])
				+ 16 * SIGN(temp,(uchar)srcImg->imageData[ind1*ROI_IMG_WIDTH+ind2-1])
				+ 32 * SIGN(temp,(uchar)srcImg->imageData[(ind1-1)*ROI_IMG_WIDTH+ind2-1])
				+ 64 * SIGN(temp,(uchar)srcImg->imageData[(ind1-1)*ROI_IMG_WIDTH+ind2])
				+ 128 * SIGN(temp,(uchar)srcImg->imageData[(ind1-1)*ROI_IMG_WIDTH+ind2+1]);

		}
	}

	for ( ind1 = 0; ind1 < CELLS_PER_VER_LINE; ind1++)
	{
		for ( ind2 = 0; ind2 < CELLS_PER_HOR_LINE; ind2++)
		{
			memset(LBPCell,0,sizeof(LBPCell));

			for ( ind3 = ind1 * CELL_SIZE; ind3 <  ind1 * CELL_SIZE + CELL_SIZE; ind3++)
			{
				for ( ind4 = ind2 * CELL_SIZE; ind4 < ind2 * CELL_SIZE + CELL_SIZE; ind4++)
				{
					iIndex = binSearch_int(uniform_patterns,0,57,LBPmap[ind3][ind4]);

					//LBPCell[iIndex] += LBPValueMap[ind3][ind4];
					LBPCell[iIndex] ++;
				}
			}

			ind6 = (ind1+1)*(CELLS_PER_HOR_LINE+1)+ind2+1;
			ind7 = ind6 - 1;
			ind8 = ind6 - (CELLS_PER_HOR_LINE+1);
			ind9 = ind8 - 1;

			for ( ind5 = 0; ind5 < LBP_VECT_PER_BLOCK; ind5++)
			{
				LBPIntMap[ind5][ind6] = LBPIntMap[ind5][ind7] + LBPIntMap[ind5][ind8] - LBPIntMap[ind5][ind9] + LBPCell[ind5];
			}
		}
	}

	for (ind1 = 0; ind1 < CELLS_PER_VER_LINE-1; ind1 ++)
	{
		for (ind2 = 0; ind2 < CELLS_PER_HOR_LINE-1; ind2 ++)
		{
			ind6 = ind1*(CELLS_PER_HOR_LINE+1)+ind2;
			ind7 = (ind1+2)*(CELLS_PER_HOR_LINE+1)+ind2+2;
			ind8 = ind7 - 2;
			ind9 = ind6 + 2;
			for (ind5 = 0; ind5 < LBP_VECT_PER_BLOCK; ind5++)
			{
				ptrFearture[ind5] = LBPIntMap[ind5][ind6] + LBPIntMap[ind5][ind7] - LBPIntMap[ind5][ind8] - LBPIntMap[ind5][ind9];
			}
			L2Norm(ptrFearture,ptrFearture,LBP_VECT_PER_BLOCK);
			ptrFearture += LBP_VECT_PER_BLOCK;
		}
	}
}

void GetBlockHist( NeqImage *srcImg,double *fearturevec,int ind1,int ind2)
{
	int widthStep = srcImg->widthStep;
	int bin = 0;

	for (int i=0;i<BLOCK_SIZE;i++)
	{
		for (int j=0;j<BLOCK_SIZE;j++)
		{
			bin = (int)(*((unsigned char *)srcImg->imageData + (ind1 + i) * widthStep + ind2 + j)/HIST_BIN_STEP);
			fearturevec[bin]++;
		}
	}
	for (int i=0;i<HIST_BIN_NUM;i++)
	{
		fearturevec[i] /= (BLOCK_SIZE * BLOCK_SIZE);
	}
}

void calcFeature_Entropy( NeqImage *srcImg,double *ptrFearture)
{
	int ind1 = 0;
	int ind2 = 0;
	int ind3 = 0;
	double blockHist[HIST_BIN_NUM] = {0.0};
	double entropy = 0.0;

	for (ind1 = 0; ind1 < ROI_IMG_HEIGHT-CELL_SIZE; ind1 += CELL_SIZE)
	{
		for (ind2 = 0; ind2 < ROI_IMG_WIDTH-CELL_SIZE; ind2 += CELL_SIZE)
		{
			entropy = 0.0;

			GetBlockHist(srcImg,blockHist,ind1,ind2);

			for (ind3 = 0; ind3 < HIST_BIN_NUM; ind3++)
			{
				entropy += -1 * (blockHist[ind3]+0.0001) * log(blockHist[ind3]+0.0001);
			}

			(*ptrFearture) = entropy;
			ptrFearture ++;
		}
	}
	ptrFearture -= BLOCK_NUM;
	
	L2Norm(ptrFearture,ptrFearture,BLOCK_NUM);
	
}

void calcFeature_ISS( NeqImage *srcImg,double *ptrFearture)
{
	int ind1 = 0;
	int ind2 = 0;
	int ind3 = 0;
	double blockHist[BLOCK_NUM][HIST_BIN_NUM];

	for (ind1=0;ind1<BLOCK_NUM;ind1++)
	{
		for (ind2=0;ind2<HIST_BIN_NUM;ind2++)
		{
			blockHist[ind1][ind2] = 0;
		}
	}

	for (ind1 = 0; ind1 < ROI_IMG_HEIGHT-CELL_SIZE; ind1 += CELL_SIZE)
	{
		for (ind2 = 0; ind2 < ROI_IMG_WIDTH-CELL_SIZE; ind2 += CELL_SIZE)
		{
			GetBlockHist(srcImg,blockHist[ind3],ind1,ind2);
			ind3++;
		}
	}

	for (ind1=0;ind1<BLOCK_NUM;ind1++)
	{
		for (ind2=ind1+1;ind2<BLOCK_NUM;ind2++)
		{
			double temp = 0.0;
			for (ind3=0;ind3<HIST_BIN_NUM;ind3++)
			{
				temp += abs(blockHist[ind1][ind3] - blockHist[ind2][ind3]);
			}
			(*ptrFearture) = temp;
			ptrFearture++;
		}
	}

	ptrFearture -= ISS_VECT_NUM;

	L2Norm(ptrFearture,ptrFearture,ISS_VECT_NUM);
}


void getFeatureData( NeqImage *img,TypeFeature *fearturevec)
{
	int hogVectNum = HOG_VECT_NUM;

#if MEASURE_TIME
	LARGE_INTEGER freq;  
	LARGE_INTEGER start_t, stop_t;  
	double exe_time;  
	QueryPerformanceFrequency(&freq); 
	QueryPerformanceCounter(&start_t);
#endif

	calcFeature_HOG(img,fearturevec);
	fearturevec += hogVectNum; 
	calcFeature_LBP2(img,fearturevec);
	//fearturevec += LBP_VECT_NUM;
	//calcFeature_ISS(img,fearturevec);

#if MEASURE_TIME
	QueryPerformanceCounter(&stop_t);  
	exe_time = 1e3*(stop_t.QuadPart-start_t.QuadPart)/freq.QuadPart;
	numS++;
	sum += exe_time;
	Avgtime = sum / numS;
	printf("%f ms\n",Avgtime);
#endif

}

int createNeqImageHeader(NeqImage* img, int width, int height, int depth, unsigned char* data){

    int recd = -1;

    if(width!=32 || height!=64 || depth!=1 || NULL==data) {

        img->depth = depth;
        img->width = width;
        img->height = height;
        img->widthStep = width;
        img->imageData = (char*)data;
        recd = 0;
    }

    return recd; 
}
