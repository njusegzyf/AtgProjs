package cn.nju.seg.atg.visitor;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.ASTEqualsInitializer;

import cn.nju.seg.atg.parse.CFGBuilder;
/**
 * a visitor for conditional operator in C or C++ programs.
 * @author zy
 *
 */
@SuppressWarnings("restriction")
public class ConditionalOperatorVisitor {
    public static IASTConditionalExpression exist(IASTNode node){
    	IASTConditionalExpression condExpression = null;
	    
	    //在表达式语句中
	    if(node instanceof IASTExpressionStatement){
		    IASTExpressionStatement expression = (IASTExpressionStatement)node;
		    //赋值语句
		    if(expression.getExpression() instanceof IASTBinaryExpression){
			    IASTBinaryExpression bExpression = (IASTBinaryExpression)expression.getExpression();
			    if(bExpression.getOperand2() instanceof IASTConditionalExpression){
				    condExpression = (IASTConditionalExpression)bExpression.getOperand2();
		    	}
		    }
		    //非赋值语句
		    else if(expression.getExpression() instanceof IASTConditionalExpression){
			    condExpression = (IASTConditionalExpression)expression.getExpression();
		    }
    	}
	    //在声明语句中
	    else if(node instanceof IASTDeclarationStatement){
		    IASTDeclarationStatement decl = (IASTDeclarationStatement)node;
		    if(decl.getDeclaration() instanceof IASTSimpleDeclaration){
		        IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration)decl.getDeclaration();
		        IASTDeclarator[] declarator = simpleDecl.getDeclarators();
		        if(declarator.length>0 && declarator[0].getInitializer() instanceof ASTEqualsInitializer){
		    	    ASTEqualsInitializer initializer = (ASTEqualsInitializer)declarator[0].getInitializer();
		            if(initializer != null && initializer.getInitializerClause() instanceof IASTConditionalExpression){
		        	    condExpression = (IASTConditionalExpression)initializer.getInitializerClause();
		            }
		        }
		    }
	    }
	    
	    return condExpression;
    }
    
    public static void visitConditionalOperator(IASTConditionalExpression condExpression){
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
		ifNode.setSign(0);
		ifNode.setOffset(offsetTmp);
		ifNode.setNodeNumber(++CFGBuilder.nodeNumber);
		CFGBuilder.currentNode.setIfChild(ifNode);
		ifNode.addParent(CFGBuilder.currentNode);
		CFGBuilder.terminalNodes.add(ifNode);
		
		offsetTmp = condExpression.getNegativeResultExpression().getFileLocation().getNodeOffset();
		CFGNode elseNode = new CFGNode();
		elseNode.setSign(1);
		elseNode.setOffset(offsetTmp);
		elseNode.setNodeNumber(++CFGBuilder.nodeNumber);
		CFGBuilder.currentNode.setElseChild(elseNode);
		elseNode.addParent(CFGBuilder.currentNode);
		CFGBuilder.terminalNodes.add(elseNode);
	}
}
