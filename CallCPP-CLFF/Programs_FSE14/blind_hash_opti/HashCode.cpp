/*
 * HashCode.cpp
 *
 *  Created on: Feb 11, 2015
 *      Author: zy
 */

class EffectiveJavaHashCode {
private:
	int x;
    long y;
    int z;

public:
    EffectiveJavaHashCode(int a, long b, int c) {
	    x = a;
	    y = b;
	    z = c;
    }

    int hashCode() {
    	int h = x;
    	h = h * 31 + int(y ^ (y >> 32));
    	h = h * 31 + z;
    	return h;
    }
};

void testCollision1(int x1, long y1, int z1, int x2, long y2, int z2) {
    EffectiveJavaHashCode o1(x1, y1, z1);
    EffectiveJavaHashCode o2(x2, y2, z2);
    if (o1.hashCode() == o2.hashCode()) {
    	printf("Solved hash collision 1");
    }
}

void testCollision2(long y1, int z1, long y2, int z2) {
   	EffectiveJavaHashCode o1(1, y1, z1);
   	EffectiveJavaHashCode o2(2, y2, z2);
   	if (o1.hashCode() == o2.hashCode()) {
   		printf("Solved hash collision 2");
   	}
}

void testCollision3(long y1, long y2) {
	// The hashes can collide only if the lower 5 bits of z1/z2 match
   	EffectiveJavaHashCode o1(1234, y1, 3141);
   	EffectiveJavaHashCode o2(5678, y2, 3141);
   	if (o1.hashCode() == o2.hashCode()) {
   		printf("Solved hash collision 3");
   	}
}

void testCollision4(int x1, long y1, int z1) {
   	EffectiveJavaHashCode o1(1234, 6454505372016058754, 3141);
   	EffectiveJavaHashCode o2(x1, y1, z1);
   	if (o1.hashCode() == o2.hashCode()) {
   		printf("Solved hash collision 4");
   	}
}

void testCollision5(long y1, int z1) {
   	EffectiveJavaHashCode o1(1234, 6454505372016058754, 3141);
   	EffectiveJavaHashCode o2(5678, y1, z1);
   	if (o1.hashCode() == o2.hashCode()) {
   	    printf("Solved hash collision 5");
    }
}

