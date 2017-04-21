package cn.nju.seg.atg.util;

import java.math.BigDecimal;

/**
 * 
 * @author zy
 *
 */
public class GlobalRandomNumber {	
	/**
	 * 限制IEEE754 32位所表示的float型数指数的范围[low,high]
	 * @param low
	 * @param high
	 * @return float
	 */
	public static float generateRandomFloat(int low, int high){
		boolean flag = false;
		StringBuilder str = null;
		while(!flag){
			str = new StringBuilder(32);
			for(int i = 0; i < 32; i++){
		    	str.append(Math.random() > 0.5 ? "1" : "0");
		    }
			int exponent = Integer.valueOf(str.substring(1, 9), 2) - 127;
			if(exponent>=low && exponent<=high){
				flag = true;
			}
		}
        
		return binaryString2Float(str.toString());
	}
	
	/**
	 * 限制IEEE754 64位所表示的double型数指数的范围[low,high]
	 * @param low
	 * @param high
	 * @return double
	 */
	public static double generateRandomDouble(int low, int high){
		boolean flag = false;
		StringBuilder str = null;
		while(!flag){
			str = new StringBuilder(64);
			for(int i = 0; i < 64; i++){
		    	str.append(Math.random() > 0.5 ? "1" : "0");
		    }
			int exponent = Integer.valueOf(str.substring(1, 12), 2).intValue() - 1023;
			if(exponent>=low && exponent<=high){
				flag = true;
			}
		}
        
		return binaryString2Double(str.toString());
	}
	
	/**
	 * 在32位二进制表示的所有float型值中生成随机值
	 * @return float
	 */
	public static float generateRandomFloat(){
		StringBuilder str = new StringBuilder(32);
	    for(int i = 0; i < 32; i++){
	    	str.append(Math.random() > 0.5 ? "1" : "0");
	    }
	    
	    return binaryString2Float(str.toString());
	}
	
	/**
	 * 在64位二进制表示的所有double型值中生成随机值
	 * @return double
	 */
	public static double generateRandomDouble(){
		StringBuilder str = new StringBuilder(64);
	    for(int i = 0; i < 64; i++){
	    	str.append(Math.random() > 0.5 ? "1" : "0");
	    }
	    
	    return binaryString2Double(str.toString());
	}
	
    /**
     * 转换float二进制字符串为float值
     * @param binaryStr 二进制字符串
     * @return float值
     */
    public static float binaryString2Float(String binaryStr){
        BigDecimal result = new BigDecimal(0);
        char sign = binaryStr.charAt(0) == '0' ? '+' : '-';
        double exponent = Math.pow(2, Integer.valueOf(binaryStr.substring(1, 9), 2) - 127);
           
        String mantissa = binaryStr.substring(9);
        for(int n = 1; n <= 23; n++){
                if(mantissa.charAt(n-1) == '1'){
                        result = result.add(BigDecimal.valueOf(Math.pow(2, (-1)*n)));
                }
        }
        result = result.add(BigDecimal.valueOf(1));
        return (float)result.multiply(BigDecimal.valueOf(exponent)).multiply(BigDecimal.valueOf(sign == '-' ? (-1) : 1)).doubleValue();
    }
	
	/**
	 * 将64位二进制转换位double型实数值
	 * @param binaryStr
	 * @return double值
	 */
	private static double binaryString2Double(String binaryStr){
        BigDecimal result = new BigDecimal(0);
        char sign = binaryStr.charAt(0) == '0' ? '+' : '-';
        double exponent = Math.pow(2, Integer.valueOf(binaryStr.substring(1, 12), 2).intValue() - 1023);
       
        String mantissa = binaryStr.substring(12);
        for(int n = 1; n <= 52; n++){
                if(mantissa.charAt(n-1) == '1'){
                        result = result.add(BigDecimal.valueOf(Math.pow(2, (-1)*n)));
                }
        }
        result = result.add(BigDecimal.valueOf(1));
        return result.multiply(BigDecimal.valueOf(exponent)).multiply(BigDecimal.valueOf(sign == '-' ? (-1) : 1)).doubleValue();
    }
}

