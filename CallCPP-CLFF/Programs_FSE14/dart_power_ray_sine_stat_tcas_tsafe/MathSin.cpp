/*
 * MathSin.cpp
 *
 *  Created on: Feb 12, 2015
 *      Author: zy
 */

//class MathSin {
//public:
    static int IEEE_MAX = 2047;
    static int IEEE_BIAS = 1023;
    static int IEEE_MANT = 52;
    static double sixth = 1.0/6.0;
    static double half = 1.0/2.0;
    static double mag52 = 1024.*1024.*1024.*1024.*1024.*4.;/*2**52*/
    static double magic = 1024.*1024.*1024.*1024.*1024.*4.;/*2**52*/

	static double P[] = {
		  -0.64462136749e-9,
		   0.5688203332688e-7,
		  -0.359880911703133e-5,
		   0.16044116846982831e-3,
		  -0.468175413106023168e-2,
		   0.7969262624561800806e-1,
		  -0.64596409750621907082,
		   0.15707963267948963959e1
    };

    static double _2_pi_hi; // = 2.0/Math.PI ;
    static double _2_pi_lo;
    static double pi2_lo;
    static double pi2_hi_hi;
    static double pi2_hi_lo;
    static double pi2_lo_hi;
    static double pi2_lo_lo;
    static double pi2_hi; // = Math.PI/2;
    static double pi2_lo2;

    static double X_EPS = (double)1e-4;

    double longBitsToDouble(long value){
    	double result;
    	memcpy(&result, &value, sizeof result);
    	return result;
    }

    long doubleToRawLongBits(double value){
    	long bits;
    	memcpy(&bits, &value, sizeof bits);
    	return bits;
    }

    double mysin(double x){
		double retval;
		double x_org;
		double x2;

		int md_b_sign;
		int xexp;
		int sign=0;
		int md_b_m1;
		int md_b_m2;
		// convert into the different parts
		//

		// x is symbolic.  We have to call
		// {@code doubleToRawLongBits} via the helper to build
		// a {@code FunctionExpressio} in the
	        // {@code ConcreteExecutionListener}.
		//long l_x = Double.doubleToRawLongBits(x);
        long l_x = helperdoubleToRawBits(x);
        //   <32> <20> <11> <1>
        // sign
        md_b_sign = (int) ((l_x >> 63) & 1);
        // exponent:
        xexp = (int)((l_x >> 52) & 0x7FF);
        int xexp0 = (int)((l_x >> 52) & 0x7FF);

        md_b_m2 = (int)(l_x & 0xFFFFFFFF);
        md_b_m1 = (int)((l_x >> 31) & 0xFFFFF);
        printf("input="+x);
        //printf("raw="+l_x);
        printf("sign="+md_b_sign);
        printf("exp="+xexp);
        printf("exp_raw="+xexp0);
        printf("exp (unbiased)="+(xexp-IEEE_BIAS));
        printf("m1="+md_b_m1);
        printf("m2="+md_b_m2);

//----------end-of-conversion------------
		if (IEEE_MAX == xexp){
            printf("NAN-on-INF");
            if( md_b_m1 >0 || md_b_m2 >0  ){
                printf("unnormalized");
                retval = x;
            }else{
                printf("NaN");
                retval = nan;
            }
		    return retval;
		}
		else if (0 == xexp){
			printf("+-0, denormal");
		    if( md_b_m1>0 || md_b_m2>0 ){	/* denormal	*/
			    printf("denormal");
			    x2 = x*x;	/* raise underflow		*/
			    return x - x2;	/* compute x		*/
			}
		    else{			/* +/-0.0		*/
			    printf("+-0");
			    return x;	/* => result is argument	*/
		    }
		}
	    else if( xexp <= (IEEE_BIAS - IEEE_MANT - 2) ){ /* very small;  */
			printf("very small");
			return x;
		}else if( xexp <= (IEEE_BIAS - IEEE_MANT/4) ){ /* small */
			printf("small");
            return x*(1.0-x*x*sixth);
				/* x**4 < epsilon of x        */
        }

		if (md_b_sign == 1){
			x = -x;
			sign = 1;
		}
		x_org = x;
        printf("CURRENT\n\n");

		if (xexp < IEEE_BIAS){
			printf("less-than pi/2");
			;
		}else if (xexp <= (IEEE_BIAS + IEEE_MANT)){
			printf("must bring into range...");
			double xm;
			double x3 =0.0;
			double x4 =0.0;
			double x5 =0.0;
			double x6 =0.0;
			double a1=0.0;
			double a2=0.0;
            int bot2;
            double xn_d;
            double md; // should be bit union

            xm = floor(x * _2_pi_hi + half);
		    printf("xm (int) = " + xm);
            xn_d = xm + mag52;

	        printf("xn_d = " + xn_d);
	        // C: bot2 = xn.b.m2 & 3u;
            // bot2 is the lower 3 bits of M2
            long l_xn = doubleToRawLongBits(xn_d);

            int xn_m2 = (int)(l_xn & 0xFFFFFFFF);
            bot2 = xn_m2 & 3;
	        printf("bot2 = " + bot2);

            /*
             * Form xm * (pi/2) exactly by doing:
             *      (x3,x4) = xm * pi2_hi
             *      (x5,x6) = xm * pi2_lo
             */
	        //>>>>>>>>>>>>>>>>>>>>>                split(a1,a2,xm);
	        printf("splitting: "+xm);
	        long l_x1 = doubleToRawLongBits(xm);

	        //   <32> <20> <11> <1>
	        // sign
	        int md_b_sign1 = (int) ((l_x1 >> 63) & 1);
	        // exponent:
	        int xexp1 = (int)((l_x1 >> 52) & 0x7FF);
	        int md_b_m21 = (int)(l_x1 & 0xFFFFFFFF);
	        int md_b_m11 = (int)((l_x1 >> 31) & 0xFFFFF);
	        printf("raw="+l_x1);
	        printf("sign="+md_b_sign1);
	        printf("exp="+xexp1);
	        printf("exp (unbiased)="+(xexp1-IEEE_BIAS));
	        printf("m1="+md_b_m11);
	        printf("m2="+md_b_m21);

	        // 	md.b.m2 &= 0xfc000000u;		\
	        // md_b_m2 = (int)(l_x1 & 0xFFFFFFFF);
	        l_x1 &= (long)0xFC000000L;
	        a1 = longBitsToDouble(l_x1);
	        // 	lo = (v) - hi;	/* bot 26 bits */
	        a2 = xm - a1;

	        printf("in split: a1="+a1);
	        printf("in split: a2="+a2);

	        //>>>>>>>>>>> exactmul2(x3,x4, xm,a1,a2, pi2_hi,pi2_hi_hi,pi2_hi_lo);
	        x3 = (xm)*(pi2_hi);
		    x4 = (((a1*pi2_hi_hi-x3)+a1*pi2_hi_lo)+pi2_hi_hi*a2)+a2*pi2_hi_lo;;

	        //>>>>>>>>>>>  exactmul2(x5,x6, xm,a1,a2, pi2_lo,pi2_lo_hi,pi2_lo_lo);
            x5 = (xm)*(pi2_lo);
	        x6 = (((a1*pi2_lo_hi-x5)+a1*pi2_lo_lo)+pi2_lo_hi*a2)+a2*pi2_lo_lo;;
            x = ((((x - x3) - x4) - x5) - x6) - xm*pi2_lo2;

	        //++++++++++++++++++++++++++++++++++++++++++++++

            if(bot2==0){
            	if (x < 0.0) {
                    x = -x;
                    //sign ^= 1;
                    if (sign ==1)
                        sign = 0;
                    else
                        sign = 1;
                }
            }else if(bot2==1){
            	if( x < 0.0 ){
                    x = pi2_hi + x;
                }else{
                    x = pi2_hi - x;
                }
            }else if(bot2==2){
            	 if (x < 0.0) {
                    x = -x;
                }else{
                    //sign ^= 1;
                    if (sign ==1)
                        sign = 0;
                    else
                        sign = 1;
                }
            }else if(bot2==3){
            	 // sign ^= 1;
                if (sign ==1)
                    sign = 0;
                else
                	sign = 1;

                if( x < 0.0 ){
                    x = pi2_hi + x;
                }else{
                    x = pi2_hi - x;
                }
            }
        }else {
		    printf("T_LOSS ");
		    retval = 0.0;
		    if (sign == 1)
			    retval = -retval;
		    return retval;
	    }

//---------everything between 0..pi/2
	    x = x * _2_pi_hi;
        if (x > X_EPS){
            printf("x > EPS");
            x2 = x*x;
            x *= ((((((((P)[0]*(x2) + (P)[1])*(x2) + (P)[2])*(x2) + (P)[3])*(x2) + (P)[4])*(x2) + (P)[5])*(x2) + (P)[6])*(x2) + (P)[7]);
        }else {
            printf("x <= EPS");
            x *= pi2_hi;              /* x = x * (pi/2)               */
	    }
        if (sign==1) x = -x;
            printf("final return");
        return x;
	}

    void MathSin(){
    //#define MD(v,hi,lo) md.i.i1 = hi; md.i.i2 = lo; v = md.d;

    //	  MD(    pi_hi, 0x400921FBuL,0x54442D18uL);/* top 53 bits of PI	*/
    // pi_hi = Double.longBitsToDouble((long)0x400921FB54442D18L);
    // printf("pi_hi = " + pi_hi);
    //	  MD(    pi_lo, 0x3CA1A626uL,0x33145C07uL);/* next 53 bits of PI*/
    // pi_lo = Double.longBitsToDouble((long)0x3CA1A62633145C07L);
    // printf("pi_lo = " + pi_lo);

    //	  MD(   pi2_hi, 0x3FF921FBuL,0x54442D18uL);/* top 53 bits of PI/2 */
    pi2_hi = longBitsToDouble((long)0x3FF921FB54442D18L);
    printf("pi2_hi = " + pi2_hi);
    //	  MD(   pi2_lo, 0x3C91A626uL,0x33145C07uL);/* next 53 bits of PI/2*/
    pi2_lo = longBitsToDouble((long)0x3C91A62633145C07L);
    printf("pi2_lo = " + pi2_lo);


    //	  MD(  pi2_lo2, 0xB91F1976uL,0xB7ED8FBCuL);/* next 53 bits of PI/2*/
    pi2_lo2 = longBitsToDouble((long)0xB91F1976B7ED8FBCL);
    printf("pi2_lo2 = " + pi2_lo2);

    //	  MD( _2_pi_hi, 0x3FE45F30uL,0x6DC9C883uL);/* top 53 bits of 2/pi */
    _2_pi_hi = longBitsToDouble((long)0x3FE45F306DC9C883L);
    printf("_2_pi_hi = " + _2_pi_hi);
    //	  MD( _2_pi_lo, 0xBC86B01EuL,0xC5417056uL);/* next 53 bits of 2/pi*/
    _2_pi_lo = longBitsToDouble((long)0xBC86B01EC5417056L);
    printf("_2_pi_lo = " + _2_pi_lo);

    //>>>>>	  split(pi2_hi_hi,pi2_hi_lo,pi2_hi);
    double a1,a2;
    double xm;
    xm=pi2_hi;
    printf("splitting: "+xm);
    long l_x1 = doubleToRawLongBits(xm);

    	//   <32> <20> <11> <1>
    	// sign
    int md_b_sign1 = (int) ((l_x1 >> 63) & 1);
    	// exponent:
    int xexp1 = (int)((l_x1 >> 52) & 0x7FF);
    int md_b_m21 = (int)(l_x1 & 0xFFFFFFFF);
    int md_b_m11 = (int)((l_x1 >> 31) & 0xFFFFF);
    printf("raw="+l_x1);
    printf("sign="+md_b_sign1);
    printf("exp="+xexp1);
    printf("exp (unbiased)="+(xexp1-IEEE_BIAS));
    printf("m1="+md_b_m11);
    printf("m2="+md_b_m21);

    // 	md.b.m2 &= 0xfc000000u;		\
    // md_b_m2 = (int)(l_x1 & 0xFFFFFFFF);
    l_x1 &= (long)0xFC000000L;
    a1 = longBitsToDouble(l_x1);
    // 	lo = (v) - hi;	/* bot 26 bits */
    a2 = xm - a1;

    printf("in split: a1="+a1);
    printf("in split: a2="+a2);
    pi2_hi_hi=a1;
    pi2_hi_lo=a2;

    //>>>>>	  split(pi2_lo_hi,pi2_lo_lo,pi2_lo);
    xm=pi2_lo;
    printf("splitting: "+xm);
    // xm is a concrete value; no need to invoke the helper (pdinges)
    l_x1 = doubleToRawLongBits(xm);
    //l_x1 = MathSin.helperdoubleToRawBits(xm);
    	//   <32> <20> <11> <1>
    	// sign
    md_b_sign1 = (int) ((l_x1 >> 63) & 1);
    	// exponent:
    xexp1 = (int)((l_x1 >> 52) & 0x7FF);
    md_b_m21 = (int)(l_x1 & 0xFFFFFFFF);
    md_b_m11 = (int)((l_x1 >> 31) & 0xFFFFF);
    printf("raw="+l_x1);
    printf("sign="+md_b_sign1);
    printf("exp="+xexp1);
    printf("exp (unbiased)="+(xexp1-IEEE_BIAS));
    printf("m1="+md_b_m11);
    printf("m2="+md_b_m21);

    // 	md.b.m2 &= 0xfc000000u;		\
    // md_b_m2 = (int)(l_x1 & 0xFFFFFFFF);
    l_x1 &= (long)0xFC000000L;
    a1 = longBitsToDouble(l_x1);
    // 	lo = (v) - hi;	/* bot 26 bits */
    a2 = xm - a1;

    printf("in split: a1="+a1);
    printf("in split: a2="+a2);

    pi2_lo_hi=a1;
    pi2_lo_lo=a2;
    }

    long helperdoubleToRawBits(double xm) {
    	return doubleToRawLongBits(xm);
    }

    //====================================================
    void TESTIT(double arg){
    double ms = mysin(arg);
    printf("SIN("+arg+"):\t"+sin(arg) +"\tmysin: "+ms);
    }

//    void main() {
//    	// TODO Auto-generated method stub
//    	MathSin sin = new MathSin();
//    	if(sin.mysin(0.0) == 0.0) {
//    		printf("it is zero");
//    	}
//    }
//};

