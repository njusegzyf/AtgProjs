package cn.nju.seg.atg.visitor;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

import cn.nju.seg.atg.parse.CFGBuilder;

public class ReturnStatementVisitor extends ASTVisitor {
	public ReturnStatementVisitor() {

	}
	
	@Override
	public int visit(IASTStatement node) {
		if (node instanceof IASTReturnStatement) {
			IASTReturnStatement iars = (IASTReturnStatement)node;
			if(CFGBuilder.shouldVisitConditionalOperator){
				IASTConditionalExpression condExpression = null;
			    IASTExpression ie = iars.getReturnValue();
			    if(ie instanceof IASTUnaryExpression){
			    	ie = ((IASTUnaryExpression)ie).getOperand();
			    }
			    if(ie instanceof IASTConditionalExpression){
			    	condExpression = (IASTConditionalExpression)ie;
			    }
			    if(condExpression != null){
			    	IASTExpression logicalExpression = condExpression.getLogicalConditionExpression();
			    	IASTBinaryExpression iabe = null;
			    	if(logicalExpression instanceof IASTUnaryExpression){
			    		iabe = (IASTBinaryExpression)((IASTUnaryExpression)logicalExpression).getOperand();
			    	}
			    	else
			    		iabe = (IASTBinaryExpression)logicalExpression;
					int offsetTmp = condExpression.getFileLocation().getNodeOffset();
					CFGBuilder.currentNode.setSign(3);
					CFGBuilder.currentNode.setBinaryExpression(iabe);
					CFGBuilder.currentNode.setOffset(offsetTmp);
					CFGBuilder.currentNode.setNodeNumber(++CFGBuilder.nodeNumber);

					//设置左右分支节点
					offsetTmp = condExpression.getPositiveResultExpression().getFileLocation().getNodeOffset();
					CFGNode ifNode = new CFGNode();
					ifNode.setSign(4);
					ifNode.setOffset(offsetTmp);
					ifNode.setNodeNumber(++CFGBuilder.nodeNumber);
					CFGBuilder.currentNode.setIfChild(ifNode);
					ifNode.addParent(CFGBuilder.currentNode);
					
					offsetTmp = condExpression.getNegativeResultExpression().getFileLocation().getNodeOffset();
					CFGNode elseNode = new CFGNode();
					elseNode.setSign(4);
					elseNode.setOffset(offsetTmp);
					elseNode.setNodeNumber(++CFGBuilder.nodeNumber);
					CFGBuilder.currentNode.setElseChild(elseNode);
					elseNode.addParent(CFGBuilder.currentNode);
			    }else{
			    	int offsetTmp = iars.getFileLocation().getNodeOffset();
					CFGBuilder.currentNode.setSign(4);
					CFGBuilder.currentNode.setOffset(offsetTmp);
					CFGBuilder.currentNode.setNodeNumber(++CFGBuilder.nodeNumber);
			    }
			}else{
		    	int offsetTmp = iars.getFileLocation().getNodeOffset();
				CFGBuilder.currentNode.setSign(4);
				CFGBuilder.currentNode.setOffset(offsetTmp);
				CFGBuilder.currentNode.setNodeNumber(++CFGBuilder.nodeNumber);
		    
			}
			
			return PROCESS_ABORT;
		}
		return PROCESS_CONTINUE;
	}
}
