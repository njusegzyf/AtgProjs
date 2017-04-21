/*
 * FunctionCallTest.cpp
 *
 *  Created on: Mar 27, 2015
 *      Author: zy
 */
using namespace std;

extern int test3(int a);
extern int test1(int a);


int test(int a){
 int b=2;
// if(a>1&&test1(a)>1){
//	 b+=1;
// }
 test1(a);
 test2();
// if(a>b)
//	 printf("hello");
 return b;
}

int test1(int a){
	test2();
	return a;
}

void test2(){
	test3();
}
void test3(int a,int b){
	if(a>b)
	  printf("test3");
}

int test_inst(int a, char* path){
	ofstream bFile(path);
	bFile<<"node1\n";
	int b=2;
	int temp;
	if((bFile<<"node2 "<<a-1<<" a>1\n",a>1)&&
	(bFile<<"call1\n",temp=test1_inst(a, path),
	bFile<<"node2 "<<temp-1<<" test1(a)>1\n",temp>1)){
		bFile<<"node3\n";
        b =1;
	}
	bFile<<"node4\n";
	return b;
}

int test1_inst(int a, char* path){
	ofstream bFile(path);
	bFile<<"entry(test1)\n";
	if(bFile<<"node1 "<<a-2<<" a>2\n",a>2){
		bFile<<"node2\n";
		a = 3;
	}
	bFile<<"node3\n";
	bFile<<"exit(test1)\n";
	return a;
}

int test2(int b){
	if(b==1)
		return 2;
	else
		return 3;
}

int test3(int b){
	return 1;
}

int f1(int a, int b){
	return a>b?a:b;
}

int f1_inst(int a, int b, char* path){
	ofstream bFile(path);
	return (bFile<<"node1 "<<a-b<<" a>b\n",a>b)
			?(bFile<<"node2\n",a):(bFile<<"node3\n",b);
}

int f2(int a, int b){
	int c = a>b?a:b;
	return c;
}

int f2_inst(int a, int b, char* path){
	ofstream bFile(path);
	int c = (bFile<<"node1 "<<a-b<<" a>b\n",a>b)
	?(bFile<<"node2\n",a):(bFile<<"node3\n",b);
	bFile<<"node4\n";
	return c;
}


