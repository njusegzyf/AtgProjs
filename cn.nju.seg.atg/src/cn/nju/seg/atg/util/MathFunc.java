package cn.nju.seg.atg.util;

import java.util.Arrays;
import java.util.List;

/**
 * @version 0.1
 * 
 * @author zy
 * @author Zhang Yifan
 */
public class MathFunc {
    /**
     * 在给定数的周围生成一个随机数
     * @param seed
     * @param boundary
     * @return
     */
	public static double getRandomFromSeed(double seed, double boundary){
		double random;
		double leftBoundary = seed - boundary;
		double rightBoundary = seed + boundary;
		random = rightBoundary - Math.random()*(rightBoundary - leftBoundary);
		
		return random;
	}
	
	/**
	 * 在指定区间内生成一个随机数
	 * @param leftBoundary
	 * @param rightBoundary
	 * @return
	 */
	public static double getRandom(double leftBoundary, double rightBoundary){
		double random = rightBoundary - Math.random()*(rightBoundary - leftBoundary);
		return random;
	}
	
	/**
	 * 求指定区域黄金分割点
	 * @param leftBoundary
	 * @param rightBoundary
	 * @return
	 */
	public static double getGoldenSectionPoint(double leftBoundary, double rightBoundary)
	{
		double ratio = (Math.sqrt(5)-1)/(Math.sqrt(5)+1);
		return (ratio*(rightBoundary-leftBoundary)+leftBoundary);
	}
	
	/**
	 * 
	 * @param times
	 * @return
	 */
	public static double getAverage(List<Double> times){
		double sum = 0;
		for (double time : times) sum += time;
		
		return (sum / times.size());
	}
	
	public static int getMax(int[] array)
	{
		int max = array[0];
		for (int i=1; i<array.length; i++)
		{
			if (max < array[i])
				max = array[i];
		}
		
		return max;
	}
	
	public static double getAverage(double[] array)
	{
		double sum = array[0];
		for (int i=1; i<array.length; i++) sum += array[i];
		
		return (sum / array.length);
	}
	
	public static double getAverage(int[] array)
	{
		int sum = array[0];
		for (int i=1; i<array.length; i++) sum += array[i];
		
		// @since 0.1, cast sum to double, otherwise it performs an integer division 
		return (double)sum / array.length;
		
		// return Arrays.stream(array).average().getAsDouble();
	}
	
	public static double getMin(double[] array)
	{
		double min = array[0];
		for (int i=1; i<array.length; i++)
		{
			if (min > array[i])
				min = array[i];
		}
		
		return min;
	}
	
	public static double getSTDEVPA(double[] array){
		double count = 0;
		double aver = getAverage(array);
		for(double a : array){
			count += Math.pow(a-aver, 2);
		}
		
		return Math.sqrt(count/array.length);
	}
}
