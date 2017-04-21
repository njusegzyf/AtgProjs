package cn.nju.seg.atg.visitor;

import java.util.List;

import cn.nju.seg.atg.parse.CFGBuilder;

/**
 * represent a function call
 * @author zy
 *
 */
public class Call{
	private CFGNode callNode;
	private CFGNode nextNode;
	private int offset = -1;
	private String functionId;
	
	public Call(String id, int offset){
		this.functionId = id;
		this.offset = offset;
	}
	
	/**
	 * 如果函数内既不存在分支节点也不存在其它函数调用，即不存在控制流变化，则不进入该函数
	 * @return
	 */
	public boolean isExistControlFlowJump() {
		Function f = CFGBuilder.allFunctions.get(functionId);
		if(f.isExistBranchNode())
			return true;
		if(f.isExistFunctionCall()){
			List<Call> calls = f.getFunctionCalls();
			for(Call call : calls){
				if(call.isExistControlFlowJump())
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 在CFG中跳过该函数调用
	 * @param f
	 */
	public void skipCall(Function f){
		callNode.addChild(nextNode);
		nextNode.replaceParent(f.getExit().getOffset(), callNode);
	}
	
	public CFGNode getCallNode() {
		return callNode;
	}

	public void setCallNode(CFGNode callNode) {
		this.callNode = callNode;
	}

	public CFGNode getNextNode() {
		return nextNode;
	}

	public void setNextNode(CFGNode nextNode) {
		this.nextNode = nextNode;
	}

	public int getOffset() {
		return offset;
	}

	public String getFunctionId() {
		return functionId;
	}
}
