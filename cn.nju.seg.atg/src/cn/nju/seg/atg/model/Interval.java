package cn.nju.seg.atg.model;

import java.util.ArrayList;
import java.util.List;

import cn.nju.seg.atg.util.ATG;

/**
 * 一个取值区间
 * @author ChengXin
 */
public class Interval {
	/**
	 * 左右边界值
	 */
	private double leftBoundary;
	private double rightBoundary;
	
	/**
	 * 无参初始化函数
	 * @param leftBoundary
	 * @param rightBoundary
	 */
	public Interval()
	{
		this.leftBoundary = -Double.MIN_VALUE;
		this.rightBoundary = Double.MAX_VALUE;
	}
	
	/**
	 * 带参初始化函数
	 * @param leftBoundary
	 * @param rightBoundary
	 */
	public Interval(double leftBoundary, double rightBoundary)
	{
		this.leftBoundary = leftBoundary;
		this.rightBoundary = rightBoundary;
	}
	
	/**
	 * 预测新的可用输入数据
	 * @return double
	 */
	public double getNewCoodinate()
	{
		return getGoldenSectionPoint();
	}
	
	/**
	 * 获取区间的黄金分割点
	 * @return double
	 */
	private double getGoldenSectionPoint()
	{
		double ratio = (Math.sqrt(5)-1)/(Math.sqrt(5)+1);
		return (ratio*(this.rightBoundary-this.leftBoundary)+this.leftBoundary);
	}
	
	@Override
	public String toString(){
		return "("+this.leftBoundary+","+this.rightBoundary+")";
	}
	
	/**
	 * 获取区间的随机点
	 * @return double
	 */
//	private double getRandomPoint()
//	{
//		double ratio = Math.random();
//		while(ratio == 0.0){
//			ratio = Math.random();
//		}
//		return (ratio*(this.rightBoundary-this.leftBoundary)+this.leftBoundary);
//	}
	
	public double getLeftBoundary() {
		return leftBoundary;
	}
	public void setLeftBoundary(double leftBoundary) {
		this.leftBoundary = leftBoundary;
	}
	public double getRightBoundary() {
		return rightBoundary;
	}
	public void setRightBoundary(double rightBoundary) {
		this.rightBoundary = rightBoundary;
	}
	
	/**
	 * 求两个区间的交集
	 * @param list1
	 * @param list2
	 * @return 交集
	 */
	public static List<Interval> getIntersection(List<Interval> list1, List<Interval> list2)
	{
		List<Interval> intersectionList = new ArrayList<Interval>();
		
		if (list1 != null && list2 != null && !list1.isEmpty() && !list2.isEmpty())
		{
			int listSize1 = list1.size();
			int listSize2 = list2.size();
			int index1 = 0;
			int index2 = 0;
			double tempLeft1;
			double tempLeft2;
			double tempRight1;
			double tempRight2;
			Interval tempInterval = new Interval();
			while(index1 < listSize1 && index2 < listSize2)
			{
				tempLeft1 = list1.get(index1).getLeftBoundary();
				tempLeft2 = list2.get(index2).getLeftBoundary();
				tempRight1 = list1.get(index1).getRightBoundary();
				tempRight2 = list2.get(index2).getRightBoundary();
				
				if (tempLeft1 > tempLeft2)
				{
					if (tempRight1 < tempRight2)
					{
						tempInterval = new Interval();
						tempInterval.setLeftBoundary(tempLeft1);
						tempInterval.setRightBoundary(tempRight1);
						intersectionList.add(tempInterval);
						index1++;
					}
					else if (tempRight1 > tempRight2)
					{
						if (tempLeft1 < tempRight2)
						{
							tempInterval = new Interval();
							tempInterval.setLeftBoundary(tempLeft1);
							tempInterval.setRightBoundary(tempRight2);
							intersectionList.add(tempInterval);
						}
						index2++;
					}
					else
					{
						tempInterval = new Interval();
						tempInterval.setLeftBoundary(tempLeft1);
						tempInterval.setRightBoundary(tempRight1);
						intersectionList.add(tempInterval);
						index1++;
						index2++;
					}
				}
				else if (tempLeft1 < tempLeft2)
				{
					if (tempRight1 > tempRight2)
					{
						tempInterval = new Interval();
						tempInterval.setLeftBoundary(tempLeft2);
						tempInterval.setRightBoundary(tempRight2);
						intersectionList.add(tempInterval);
						index2++;
					}
					else if (tempRight1 < tempRight2)
					{
						if (tempRight1 > tempLeft2)
						{
							tempInterval = new Interval();
							tempInterval.setLeftBoundary(tempLeft2);
							tempInterval.setRightBoundary(tempRight1);
							intersectionList.add(tempInterval);
						}
						index1++;
					}
					else
					{
						tempInterval = new Interval();
						tempInterval.setLeftBoundary(tempLeft2);
						tempInterval.setRightBoundary(tempRight2);
						intersectionList.add(tempInterval);
						index1++;
						index2++;
					}
				}
				else
				{
					if (tempRight1 > tempRight2)
					{
						tempInterval = new Interval();
						tempInterval.setLeftBoundary(tempLeft1);
						tempInterval.setRightBoundary(tempRight2);
						intersectionList.add(tempInterval);
						index2++;
					}
					else if (tempRight1 < tempRight2)
					{
						tempInterval = new Interval();
						tempInterval.setLeftBoundary(tempLeft1);
						tempInterval.setRightBoundary(tempRight1);
						intersectionList.add(tempInterval);
						index1++;
					}
					else
					{
						tempInterval = new Interval();
						tempInterval.setLeftBoundary(tempLeft1);
						tempInterval.setRightBoundary(tempRight1);
						intersectionList.add(tempInterval);
						index1++;
						index2++;
					}
				}
			}
		}
		else
		{
			intersectionList = null;
		}
		
		return intersectionList;
	}
	
	/**
	 * 求两个区间的并集
	 * @param list1
	 * @param list2
	 * @return 并集
	 */
	public static List<Interval> getUnion(List<Interval> list1, List<Interval> list2)
	{
		List<Interval> unionList = new ArrayList<Interval>();
		
		if (list1 == null || list1.size() == 0)
		{
			unionList = list2;
		}
		else if (list2 == null || list2.size() == 0)
		{
			unionList = list1;
		}
		else
		{
			unionList = getComplementary(getIntersection(getComplementary(list1), getComplementary(list2)));
		}
		
		return unionList;
	}
	
	/**
	 * 求区间的补集
	 * @param list
	 * @return 补集
	 */
	public static List<Interval> getComplementary(List<Interval> list)
	{
		List<Interval> complementaryList = new ArrayList<Interval>();

		Interval totalInterval = ATG.maxInterval();
		if (list != null && list.size() != 0)
		{
			int listSize = list.size();
			if (totalInterval.getLeftBoundary() < list.get(0).getLeftBoundary())
			{
				Interval temp = new Interval(totalInterval.getLeftBoundary(), list.get(0).getLeftBoundary());
				complementaryList.add(temp);
			}
			for (int i=0; i<(listSize-1); i++)
			{
				if (list.get(i).getRightBoundary() < list.get(i+1).getLeftBoundary())
				{
					Interval temp = new Interval(list.get(i).getRightBoundary(), list.get(i+1).getLeftBoundary());
					complementaryList.add(temp);
				}
			}
			if (totalInterval.getRightBoundary() > list.get(listSize-1).getRightBoundary())
			{
				Interval temp = new Interval(list.get(listSize-1).getRightBoundary(), totalInterval.getRightBoundary());
				complementaryList.add(temp);
			}
		}
		else
		{
			complementaryList.add(totalInterval);
		}
		
		return complementaryList;
	}
}
