package cn.nju.seg.atg.model;

import cn.nju.seg.atg.model.constraint.BinaryExpression;

/**
 * 分支中的条件约束，用于条件覆盖结果存储
 * @author zy
 *
 */
public class Condition {
	
	/**
	 * 条件表达式信息
	 */
	private String info;
	
	/**
	 * 条件编号
	 */
	private int id;
	
	/**
	 * 所在分支节点编号
	 */
	private int nodeIndex;
	
	/**
	 * 条件所在分支节点名字
	 */
	private String nodeId;
	
	/**
	 * 条件约束
	 */
	private BinaryExpression constraint;
	
	/**
	 * 条件的True分支是否被覆盖
	 */
	private boolean TbranchCovered;
	
	/**
	 * 条件的False分支是否被覆盖
	 */
	private boolean FbranchCovered;
	
	/**
	 * 覆盖T分支的输入数据
	 */
	private double[] TcoveredInput;
	
	/**
	 * 覆盖F分支的输入数据
	 */
	private double[] FcoveredInput;
	
	/**
	 * 带参构造函数
	 * @param condition
	 * @param name
	 */
	public Condition(String info, String nodeId, int nodeIndex, int id, BinaryExpression constraint){
		this.info = info;
		this.nodeId = nodeId;
		this.nodeIndex = nodeIndex;
		this.id = id;
		this.constraint = constraint;
		this.TbranchCovered = false;
		this.FbranchCovered = false;
	}

	public boolean isCovered() {
		return TbranchCovered && FbranchCovered;
	}
	
	public boolean isTbranchCovered() {
		return TbranchCovered;
	}

	public boolean isFbranchCovered() {
		return FbranchCovered;
	}

	public double[] getTcoveredInput() {
		return TcoveredInput;
	}

	public void setTcoveredInput(double[] input) {
		this.TcoveredInput = new double[input.length];
		for(int i = 0;i < input.length; i++){
			this.TcoveredInput[i] = input[i];
		}
		this.TbranchCovered = true;
	}

	public double[] getFcoveredInput() {
		return FcoveredInput;
	}

	public void setFcoveredInput(double[] input) {
		this.FcoveredInput = new double[input.length];
		for(int i = 0;i < input.length; i++){
			this.FcoveredInput[i] = input[i];
		}
		this.FbranchCovered = true;
	}

	public String getInfo() {
		return info;
	}

	public String getNodeId() {
		return nodeId;
	}
	
	public int getNodeIndex() {
		return nodeIndex;
	}
	
	public int getId() {
		return id;
	}

	public BinaryExpression getConstraint() {
		return constraint;
	}
}
