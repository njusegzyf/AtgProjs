package cn.nju.seg.atg.model;

import cn.nju.seg.atg.model.constraint.Operator;
import cn.nju.seg.atg.util.ConstantValue;
import cn.nju.seg.atg.util.MathFunc;

/**
 * 单个区间内的线性拟合函数
 * @author zy
 *
 */
public class CLF {

	/**
	 * 用于线性拟合的两个坐标c1(x1,y1),c2(x2,y2)
	 */
	private Coodinate c1;
	private Coodinate c2;
	
	/**
	 * 比较运算符
	 */
	private Operator comparisonOperator;
	
	/**
	 * 拟合线段延长线与坐标轴的交点
	 */
	private double paramInExtensionCord;
	
	/**
	 * 初始化函数
	 * @param c1
	 * @param c2
	 * @param comparisonOperator
	 */
	public CLF(Coodinate c1, Coodinate c2, Operator comparisonOperator)
	{
		this.c1 = c1;
		this.c2 = c2;
		this.comparisonOperator = comparisonOperator;
	}
	
	/**
	 * 拟合线段在当前区间无解时，求拟合线段延长线与坐标轴的交点
	 * @return intersection_type
	 */
	public int getParamsInExtensionCord(){
		double x1 = this.c1.getX();
		double y1 = this.c1.getY();
		double x2 = this.c2.getX();
		double y2 = this.c2.getY();
		
		if (this.comparisonOperator == Operator.GT || 
				this.comparisonOperator == Operator.GE){
			//if: 斜率不为0
			if (Math.abs(y1 - y2) > Double.MIN_VALUE){
				if(y1 < 0 && y2 < 0){
					this.paramInExtensionCord = (x1*y2-x2*y1)/(y2-y1);
					return (this.paramInExtensionCord<x1)?ConstantValue.LEFT:ConstantValue.RIGHT;
				}
			}
		}
		else if (this.comparisonOperator == Operator.LT || 
				this.comparisonOperator == Operator.LE){
			if (Math.abs(y1 - y2) > Double.MIN_VALUE){
				if(y1 > 0 && y2 > 0){
					this.paramInExtensionCord = (x1*y2-x2*y1)/(y2-y1);
					return (this.paramInExtensionCord<x1)?ConstantValue.LEFT:ConstantValue.RIGHT;				
				}
			}
		}
		else if (this.comparisonOperator == Operator.EQ){
			if(Math.abs(y1 - y2) > Double.MIN_VALUE){
				this.paramInExtensionCord = (x1*y2-x2*y1)/(y2-y1);			
				return (this.paramInExtensionCord<x1)?ConstantValue.LEFT:ConstantValue.RIGHT;
			}
		}
		
		return ConstantValue.INSIDE;
	}
	
	/**
	 * 计算有效取值区间
	 * @return 一个区间
	 */
	public Interval getEffectiveInterval()
	{
		Interval interval = new Interval();
		
		if (this.comparisonOperator == Operator.GT || this.comparisonOperator == Operator.GE)
		{
			interval = getGreaterInterval();
		}
		else if (this.comparisonOperator == Operator.LE || this.comparisonOperator == Operator.LT)
		{
			interval = getLessInteval();
		}
		else if (this.comparisonOperator == Operator.EQ)
		{
			interval = getEqualInterval();
		}
		else if (this.comparisonOperator == Operator.NE)
		{
			interval = getUnequalInterval();
		}
		
		return interval;
	}
	
	/**
	 * 为>和>=号计算可用区间
	 * @return 一个区间
	 */
	private Interval getGreaterInterval()
	{
		Interval interval = new Interval();
		double x1 = this.c1.getX();
		double y1 = this.c1.getY();
		double x2 = this.c2.getX();
		double y2 = this.c2.getY();
		
		//if: 斜率为0
		if (Math.abs(y1 - y2) <= Double.MIN_VALUE)
		{
			if (y1 >= 0 || y2 >= 0)
			{
				interval.setLeftBoundary(x1);
				interval.setRightBoundary(x2);
			}
			else if (y1 <= 0 || y2 <= 0)
			{
				interval = null;
			}
		}
		else	//斜率不为0
		{
			if (y1 >= 0)
			{
				if (y2 >= 0)
				{
					interval.setLeftBoundary(x1);
					interval.setRightBoundary(x2);
				}
				else
				{
					//计算零点
					double zeroCoodinate = (x1*y2-x2*y1)/(y2-y1);
					interval.setLeftBoundary(x1);
					interval.setRightBoundary(zeroCoodinate);
				}
			}
			else
			{
				if (y2 <= 0)
				{  					
					interval = null;
				}
				else
				{
					//计算零点
					double zeroCoodinate = (x1*y2-x2*y1)/(y2-y1);
					interval.setLeftBoundary(zeroCoodinate);
					interval.setRightBoundary(x2);
				}
			}
		}
		return interval;
	}
	
	/**
	 * 为<和<=计算可用区间
	 * @return 一个区间
	 */
	private Interval getLessInteval()
	{
		Interval interval = new Interval();
		double x1 = this.c1.getX();
		double y1 = this.c1.getY();
		double x2 = this.c2.getX();
		double y2 = this.c2.getY();
		
		if (Math.abs(y1 - y2) <= Double.MIN_VALUE)
		{
			if (y1 >= 0 || y2 >= 0)
			{
				interval = null;
			}
			else if (y1 <= 0 || y2 <= 0)
			{
				interval.setLeftBoundary(x1);
				interval.setRightBoundary(x2);
			}
		}
		else
		{
			if (y1 <= 0)
			{
				if (y2 <= 0)
				{
					interval.setLeftBoundary(x1);
					interval.setRightBoundary(x2);
				}
				else
				{
					//计算零点
					double zeroCoodinate = (x1*y2-x2*y1)/(y2-y1);
					interval.setLeftBoundary(x1);
					interval.setRightBoundary(zeroCoodinate);
				}
			}
			else
			{
				if (y2 >= 0)
				{   
					interval = null;
				}
				else
				{
					//计算零点
					double zeroCoodinate = (x1*y2-x2*y1)/(y2-y1);
					interval.setLeftBoundary(zeroCoodinate);
					interval.setRightBoundary(x2);
				}
			}
		}
		return interval;
	}
	
	/**
	 * 为==计算可用区间
	 * @return 一个区间
	 */
	private Interval getEqualInterval()
	{	
		return new Interval();
	}
	
	/**
	 * 为!=计算可用区间
	 * @return 一个区间
	 */
	private Interval getUnequalInterval()
	{
		Interval interval = new Interval();
		interval.setLeftBoundary(this.c1.getX());
		interval.setRightBoundary(this.c2.getX());
		return interval;
	}
	
	/**
	 * 打印拟合函数相关信息
	 */
	public void printCLF(){
		double x1 = this.c1.getX();
		double y1 = this.c1.getY();
		double x2 = this.c2.getX();
		double y2 = this.c2.getY();
		double coefficient = (y1 - y2) / (x1 - x2);
		double constant = y1 - coefficient*x1;
		double zeroCoodinate = (x1*y2-x2*y1)/(y2-y1);
		
		System.out.println("坐标点：("+x1+","+y1+"),("+x2+","+y2+"),拟合函数："+"y="+coefficient+"x+"+constant
				+",零点："+zeroCoodinate+",黄金分割点："+MathFunc.getGoldenSectionPoint(x1, x2));
	}
	
	public Coodinate getC1() {
		return this.c1;
	}

	public void setC1(Coodinate c1) {
		this.c1 = c1;
	}

	public Coodinate getC2() {
		return this.c2;
	}

	public void setC2(Coodinate c2) {
		this.c2 = c2;
	}

	public Operator getComparisonOperator() {
		return this.comparisonOperator;
	}

	public void setComparisonOperator(Operator comparisonOperator) {
		this.comparisonOperator = comparisonOperator;
	}

	public double paramInExtensionCord() {
		return this.paramInExtensionCord;
	}
}
