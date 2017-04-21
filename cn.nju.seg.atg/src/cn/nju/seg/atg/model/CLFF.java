package cn.nju.seg.atg.model;

import java.util.ArrayList;
import java.util.List;

import cn.nju.seg.atg.model.constraint.Operator;
import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.ConstantValue;

/**
 * 简单条件分支对应的分段线性拟合函数曲线
 * @author zy
 *
 */
public class CLFF {
	
	/**
	 * 用于线性拟合的坐标序列
	 */
	private List<Coodinate> clist;
	
	/**
	 * 比较运算符
	 */
	private Operator comparisonOperator;
	
	/**
	 * 左边界
	 * <p>最左区间零点在当前搜索域左边时，将零点作为左预测边界
	 */
	private double leftBoundary;

	/**
	 * 右边界
	 * <p>最右区间零点在当前搜索域右边时，将零点作为右预测边界
	 */
	private double rightBoundary;
	
	/**
	 * 判断是否越界
	 */
	private boolean isLeftTransboundary, isRightTransboundary;
	
	/**
	 * 初始化函数
	 * @param clist
	 * @param comparisonOperator
	 */
	public CLFF(List<Coodinate> clist, Operator comparisonOperator){
		this.clist = clist;
		this.comparisonOperator = comparisonOperator;
		this.isLeftTransboundary = false;
		this.isRightTransboundary = false;
	}

	public Coodinate optimalValue(){
		Coodinate optimal = clist.get(0);
		double Dvalue = Double.MAX_VALUE;
		for(Coodinate c : clist){
			if(Math.abs(c.getY()) < Dvalue){
				Dvalue = Math.abs(c.getY());
				optimal = c;
			}
		}
		
		return optimal;
	}
	
	/**
	 * 求解可用取值区间
	 * <p>通过一组坐标做一元线性拟合，根据比较操作符确定可用取值区间
	 * @param cList
	 * @param comparisonOperator
	 * @return
	 */
	public List<Interval> getEffectiveIntervalList(){
		List<Interval> effectiveIntervalList = new ArrayList<Interval>();
		CLF tempCLF;
		
		int cListSize = clist.size();
		for (int i=0; i<(cListSize-1); i++){
			tempCLF = new CLF(clist.get(i), clist.get(i+1), comparisonOperator);
			if (tempCLF.getEffectiveInterval() != null){
				effectiveIntervalList.add(tempCLF.getEffectiveInterval());
			}
		}

		return effectiveIntervalList;
	}
	
	/**
	 * 拟合曲线延长线与x轴交点在相邻区间时，直接将零点作为预测的可行解       
	 * <p>只在分歧节点进行
	 */
	public List<Double> getParamsInExtensionCord(){
		List<Double> params = new ArrayList<Double>();
		CLF tempCLF;
				
		clist.add(0, new Coodinate(TestBuilder.leftSearchBoundary,0));
		clist.add(new Coodinate(TestBuilder.rightSearchBoundary,0));
		int cListSize = clist.size();
		
		for(int i = 1;i < cListSize-2; i++){		  
		    tempCLF = new CLF(clist.get(i), clist.get(i+1), comparisonOperator);
		    if(tempCLF.getParamsInExtensionCord() == ConstantValue.LEFT){
		    	double param = tempCLF.paramInExtensionCord();
		    	//交点必须在相邻区间内
		    	if(param > clist.get(i-1).getX()){
		    		params.add(param);
		    		//添加调整值
		    		if(ATG.NEED_REFINED_SEARCH)
		    		    addAdjustValue(param,clist.get(i).getX(),clist.get(i+1).getX(),params,ConstantValue.LEFT);               
		    	}
		    }
		    else if(tempCLF.getParamsInExtensionCord() == ConstantValue.RIGHT){
		    	double param = tempCLF.paramInExtensionCord();
		    	if(param < clist.get(i+2).getX()){
		    		params.add(param);
		    		if(ATG.NEED_REFINED_SEARCH)
		    		    addAdjustValue(param,clist.get(i).getX(),clist.get(i+1).getX(),params,ConstantValue.RIGHT);        
		    	}
		    }		    	
	    }
		
		clist.remove(cListSize-1);
		clist.remove(0);
		
		return params;
	}
	
	/**
	 * 对延长线零点值做可变调整
	 * <p>为预测值，应该判断是否有用，并在下次搜索之前删除冗余值
	 * @param (param, x1, x2, params, model)
	 */
	private void addAdjustValue(double param, double x1, double x2, List<Double> params, int model){
		if(CFGBuilder.parameterTypes[ATG.currentSearchParamIndex].equals("double")){
		    if(Math.abs(param-0.0) <= Double.MIN_VALUE){
			    double x = (model == ConstantValue.LEFT) ? x1 : x2;
			    params.add(x*ATG.ADJUST_RATIO_DOUBLE_ZERO);
			    params.add(-x*ATG.ADJUST_RATIO_DOUBLE_ZERO);
		    }
		    else{
			    if(x2-x1 > 1){
				    params.add(param+(x2-x1)*ATG.ADUJST_RATIO_DOUBLE);
				    params.add(param-(x2-x1)*ATG.ADUJST_RATIO_DOUBLE);
			    }
			    else{
				    params.add(param+ATG.ADUJST_RATIO_DOUBLE);
				    params.add(param-ATG.ADUJST_RATIO_DOUBLE);
			    }
		    }
		}
		else{
			params.add((double) ((Math.round(param)+ATG.ADJUST_GRANULARITY_INTEGER)));
			params.add((double) ((Math.round(param)-ATG.ADJUST_GRANULARITY_INTEGER)));
		}
	}
	
	/**
	 * 求拓展边界
	 */
	public void getNewBoundary(){
		CLF tempCLF = new CLF(clist.get(0), clist.get(1), comparisonOperator);
		if(tempCLF.getParamsInExtensionCord()==ConstantValue.LEFT){
    	    double param = tempCLF.paramInExtensionCord();
    	    if(param < TestBuilder.leftSearchBoundary){
    	    	this.isLeftTransboundary = true;
    	    	this.leftBoundary = param;
    	    }
    	}
		
		int cListSize = clist.size();
	    tempCLF = new CLF(clist.get(cListSize-2), clist.get(cListSize-1), comparisonOperator);
	    if(tempCLF.getParamsInExtensionCord()==ConstantValue.RIGHT){
	    	double param = tempCLF.paramInExtensionCord();
	    	if(param > TestBuilder.rightSearchBoundary){
	    		this.isRightTransboundary = true;
	    		this.rightBoundary = param;
	    	}
	    }
	}
	
	public List<Coodinate> getClist() {
		return clist;
	}

	public Operator getComparisonOperator() {
		return comparisonOperator;
	}
	
	public double getLeftBoundary() {
		return leftBoundary;
	}

	public double getRightBoundary() {
		return rightBoundary;
	}

	public boolean isLeftTransboundary() {
		return isLeftTransboundary;
	}

	public boolean isRightTransboundary() {
		return isRightTransboundary;
	}
}
