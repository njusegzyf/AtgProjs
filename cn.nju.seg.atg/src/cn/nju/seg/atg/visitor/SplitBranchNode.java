package cn.nju.seg.atg.visitor;

import java.util.List;

import cn.nju.seg.atg.model.constraint.Operator;

/**
 * 从复合约束中分离出的简单子约束，记录其分别取真与取假时的后继节点
 * @author zy
 *
 */
public 	class SplitBranchNode{
	private CFGNode node = null;
	private Operator innerLogicalOp = null;
	private List<Operator> outerLogicalOps = null;
	private int index = -1;
	
	/**
	 * 
	 * @param a split branch node
	 * @param inner logical operator
	 * @param outter logical operator
	 */
	public SplitBranchNode(CFGNode node, Operator op, List<Operator> ops){
		this.node = node;
		this.innerLogicalOp = op;
		this.outerLogicalOps = ops;
		this.index = node.getSplitIndex();
	}

	public CFGNode getNode() {
		return node;
	}
	
	/**
	 * 获取节点中约束成立时的后继节点
	 * @return node index
	 * <p>返回－1表示原节点的true分支
	 */
	public int getTrueNextNodeIndex(){
		int Tindex = index;
		if(this.innerLogicalOp==Operator.AND){
			return ++Tindex;
		}
		else if(this.innerLogicalOp==Operator.OR){
			Tindex++;
			for(int i = this.outerLogicalOps.size();i > 0; i--){
				Tindex++;
				if(this.outerLogicalOps.get(i-1)==Operator.OR){
					Tindex++;
				}else if(this.outerLogicalOps.get(i-1)==Operator.AND){
					return Tindex;
				}
			}
			return -1;
		}
		else
			return -1;
	}
	
	/**
	 * 获取节点中约束不成立时的后继节点
	 * @return node index
	 * <p>返回－1表示原节点的false分支
	 */
	public int getFalseNextNodeIndex(){
		int Findex = index;
		if(this.innerLogicalOp==Operator.AND){
			Findex++;
			for(int i = this.outerLogicalOps.size();i > 0; i--){
				Findex++;
				if(this.outerLogicalOps.get(i-1)==Operator.AND){
					Findex++;
				}else if(this.outerLogicalOps.get(i-1)==Operator.OR){
					return Findex;
				}
			}
			return -1;
		}
		else if(this.innerLogicalOp==Operator.OR){
			return ++Findex;
		}
		else
			return -1;
	}
	
	@Override
	public String toString(){
		return "[constraint:"+node.getBinaryExpression().toString()
				+", True next node index:"+this.getTrueNextNodeIndex()+", False next node index:"+this.getFalseNextNodeIndex()+"]";
	}
}
