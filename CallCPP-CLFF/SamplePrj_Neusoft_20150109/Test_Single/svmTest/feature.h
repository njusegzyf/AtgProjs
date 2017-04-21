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
* \brief	This file about get feature(EOH + HAAR + HIST) data and related
*            image DS definitions
*
*
* \author	2013/04/04, zhanglk : created.\n
****************************************************************************/



#ifndef _FEATURE_H_
#define _FEATURE_H_


#define IMG_WIDTH   32
#define IMG_HEIGHT  64

#define ROI_IMG_WIDTH			(32)
#define ROI_IMG_HEIGHT			(64)//chaily 

#define CELL_VECT_LENGTH		(9)
#define CELL_NUMBER_PER_BLOCK	(4)
#define CELL_SIZE				(8)
#define BLOCK_SIZE				(16)

#define ANGLE_INTERVAL			(180 / CELL_VECT_LENGTH)

#define EOH_VECT_PER_BLOCK		(CELL_VECT_LENGTH * (CELL_VECT_LENGTH - 1) / 2)

#define EOH_VECT_NUM			(((ROI_IMG_HEIGHT - BLOCK_SIZE )/CELL_SIZE + 1) * (( ROI_IMG_WIDTH - \
								BLOCK_SIZE )/CELL_SIZE + 1) * EOH_VECT_PER_BLOCK)

#define HAAR_WAVE_NUM			(ROI_IMG_WIDTH * ROI_IMG_HEIGHT/4)

#define HIST_BIN_NUM			(32)

#define HIST_BIN_STEP			(256/HIST_BIN_NUM)

#define BLOCK_NUM				( ( ROI_IMG_HEIGHT - BLOCK_SIZE ) / CELL_SIZE + 1 ) * ( ( ROI_IMG_WIDTH - \
								BLOCK_SIZE ) / CELL_SIZE + 1 )

#define HOG_VECT_PER_BLOCK		(4 * CELL_VECT_LENGTH)

#define HOG_VECT_NUM			(BLOCK_NUM * HOG_VECT_PER_BLOCK)

#define NUM_CELLS				(ROI_IMG_HEIGHT/CELL_SIZE*ROI_IMG_WIDTH/CELL_SIZE)

#define HOG_RFM_VECT_NUM		(NUM_CELLS*(NUM_CELLS-1)/2)

#define LBP_VECT_PER_BLOCK		(59)

#define LBP_VECT_NUM			(BLOCK_NUM * LBP_VECT_PER_BLOCK)

#define ISS_VECT_NUM			(BLOCK_NUM * (BLOCK_NUM-1) / 2)

#define HALF_BLOCK_NUM			( ( ROI_IMG_HEIGHT / 2 - BLOCK_SIZE ) / CELL_SIZE + 1 ) * ( ( ROI_IMG_WIDTH - \
								BLOCK_SIZE ) / CELL_SIZE + 1)

#define HALF_FEATURE_NUM		(HALF_BLOCK_NUM * (HOG_VECT_PER_BLOCK + LBP_VECT_PER_BLOCK))

#define ALL_FERATURE_NUM		(HOG_VECT_NUM + LBP_VECT_NUM)

#define MINE					(10)
#define MATHPI					(3.1415927)


typedef struct _neq_Image {
    int  depth;
    int  width;
    int  height;
    char *imageData;
    int  widthStep;
} NeqImage;

int createNeqImageHeader(NeqImage* img, int width, int height, int depth, unsigned char* data);

typedef double TypeFeature;

typedef double TypeCalHOG;

void getFeatureData( NeqImage *img,TypeFeature *ptrFeature);

extern double max_sum;

#endif