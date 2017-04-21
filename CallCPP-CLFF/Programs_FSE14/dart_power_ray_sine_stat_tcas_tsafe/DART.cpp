/*
 * DART.cpp
 *
 *  Created on: Feb 12, 2015
 *      Author: zy
 */
#include <assert.h>

void dart(int x, int y) {
	if (x*x*x > 0){
		if(x>0 && y==10)
		    abort();
	} else {
		if (x>0 && y==20)
			abort();
	}
}

void abort() {
    assert(false);
}
