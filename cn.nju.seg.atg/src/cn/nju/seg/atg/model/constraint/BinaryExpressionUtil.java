package cn.nju.seg.atg.model.constraint;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

/**
 * 条件表达式相关操作
 * @author zhouyan
 *
 */
public class BinaryExpressionUtil {
	
	public static void main(String[] args) {
		//(a == 1) && (((b == 1) && (c == 1)) || (f() == 0))
		BinaryExpression e1 = new BinaryExpression(Operator.EQ,new IdExpression("a"),new IdExpression("1"));
	    BinaryExpression e2 = new BinaryExpression(Operator.EQ,new IdExpression("b"),new IdExpression("1"));
	    BinaryExpression e3 = new BinaryExpression(Operator.EQ,new IdExpression("c"),new IdExpression("1"));
	    BinaryExpression e4 = new BinaryExpression(Operator.EQ,new IdExpression("f()"),new IdExpression("0"));
	    BinaryExpression e5 = new BinaryExpression(Operator.AND,e2,e3);
	    BinaryExpression e6 = new BinaryExpression(Operator.OR,e5,e4);
	    BinaryExpression e7 = new BinaryExpression(Operator.AND,e1,e6);
	    e2.setParent(e5);
	    e3.setParent(e5);
	    e2.setProperty(ExprProperty.OPERAND_ONE);
	    e3.setProperty(ExprProperty.OPERAND_TWO);
	    e5.setParent(e6);
	    e4.setParent(e6);
	    e5.setProperty(ExprProperty.OPERAND_ONE);
	    e4.setProperty(ExprProperty.OPERAND_TWO);
	    e1.setParent(e7);
	    e6.setParent(e7);
	    e1.setProperty(ExprProperty.OPERAND_ONE);
	    e6.setProperty(ExprProperty.OPERAND_TWO);
	    System.out.println(e7.toString());
	    System.out.println(splitCompoundExpr(e4).toString());
	    System.out.println(splitCompoundExpr(e3).toString());
	    System.out.println(splitCompoundExpr(e2).toString());
	}
	
	/**
	 * 将复合表达式在含有函数调用的关系表达式处分离，生成新的复合表达式
	 * @param expr
	 * @return newExpr
	 */
	public static BinaryExpression splitCompoundExpr(Expression expr) {
		BinaryExpression newExpr = null, tmpParent = null;
			
		while(expr.getProperty()==ExprProperty.OPERAND_ONE){
			expr = expr.getParent();
		}
		BinaryExpression parent1 = (BinaryExpression) expr.getParent();
		Operator op1 = parent1.getOp();
		BinaryExpression leftOperand = (BinaryExpression) parent1.getOperand1();
		if (op1==Operator.AND){
			newExpr = leftOperand;
		}else{
			newExpr = reverseExpr(leftOperand);
		}
		tmpParent = (BinaryExpression) parent1.getParent();
		while(tmpParent != null){
			while(leftOperand.getProperty()==ExprProperty.OPERAND_ONE){
				leftOperand = (BinaryExpression) leftOperand.getParent();
			}
			tmpParent = (BinaryExpression) leftOperand.getParent();
			Operator op2 = tmpParent.getOp();
		    leftOperand = (BinaryExpression) tmpParent.getOperand1();
			if(op2==Operator.AND){
				newExpr = new BinaryExpression(Operator.AND, leftOperand, newExpr);
			}else{
				newExpr = new BinaryExpression(Operator.AND, reverseExpr(leftOperand), newExpr);
			}
			tmpParent = (BinaryExpression) tmpParent.getParent();
		}
		
		return newExpr;
	}
	
	/**
	 * 对一个条件表达式进行取反操作
	 * @param be
	 * @return
	 */
	private static BinaryExpression reverseExpr(BinaryExpression be) {
		BinaryExpression newExpr = new BinaryExpression();
		reverse(newExpr, be);
		return newExpr;
	}
	
	/**
	 * 递归取反
	 * <p>约定operand1和operand2应当同时为IdExpression
	 * @param newExpr
	 * @param oldExpr
	 */
	private static void reverse(BinaryExpression newExpr, BinaryExpression oldExpr) {
	    newExpr.setOffset(oldExpr.getOffset());
	    newExpr.setOp(Operator.getReverseOp(oldExpr.getOp()));
	    newExpr.setParent(oldExpr.getParent());
	    
	    Expression operand1 = oldExpr.getOperand1();
	    Expression operand2 = oldExpr.getOperand2();
	    
	    if(operand1 instanceof IdExpression){
	    	newExpr.setId(oldExpr.getId());
	    	newExpr.setOperand1(new IdExpression(operand1.toString()));
	    }else{
	    	BinaryExpression reverseOp1 = new BinaryExpression();
	    	reverseOp1.setParent(newExpr);
	    	newExpr.setOperand1(reverseOp1);
	    	reverseOp1.setProperty(ExprProperty.OPERAND_ONE);
	    	reverse(reverseOp1, (BinaryExpression) operand1);
	    }
		
	    if(operand2 instanceof IdExpression){
	    	newExpr.setOperand2(new IdExpression(operand2.toString()));
	    }else{
	    	BinaryExpression reverseOp2 = new BinaryExpression();
	    	reverseOp2.setParent(newExpr);
	    	newExpr.setOperand2(reverseOp2);
	    	reverseOp2.setProperty(ExprProperty.OPERAND_TWO);
	    	reverse(reverseOp2, (BinaryExpression) operand2);
	    }
	}
	
	/**
	 * 将CDT中的条件表达式转换为自定义的BinaryExpression类型
	 * @param iastExpr
	 * @return expr
	 */
	public static BinaryExpression translateExpr(IASTBinaryExpression iastExpr){
		BinaryExpression expr = new BinaryExpression();
		translate(iastExpr, expr);
		
		return expr;
	}
	
	private static void translate(IASTExpression iastExpr, BinaryExpression expr){
		IASTBinaryExpression iabe = removeBrackets(iastExpr);
		expr.setOp(translateOp(iabe.getOperator()));
		expr.setOffset(iabe.getFileLocation().getNodeOffset());
		if (isAtomicConstraint(iabe)){
			IdExpression operand1 = new IdExpression(iabe.getOperand1().getRawSignature());
			IdExpression operand2 = new IdExpression(iabe.getOperand2().getRawSignature());
			expr.setOperand1(operand1);
			expr.setOperand2(operand2);
			operand1.setParent(expr);
			operand2.setParent(expr);
		}else{
		    BinaryExpression  operand1, operand2;
			
		    operand1 = new BinaryExpression();
	        expr.setOperand1(operand1);
		    operand1.setParent(expr);
		    operand1.setProperty(ExprProperty.OPERAND_ONE);
		    translate(((IASTBinaryExpression) iabe).getOperand1(),operand1);
			
		    operand2 = new BinaryExpression();
		    expr.setOperand2(operand2);
		    operand2.setParent(expr);
		    operand2.setProperty(ExprProperty.OPERAND_TWO);
		    translate(((IASTBinaryExpression) iabe).getOperand2(),operand2);
		}
	}
	
	/**
	 * 判断一个约束条件是否为原子约束条件
	 * @param iabe
	 * @return true|false
	 */
	private static boolean isAtomicConstraint(IASTBinaryExpression iabe){
		int operator = iabe.getOperator();
		return operator!=IASTBinaryExpression.op_logicalAnd && operator!=IASTBinaryExpression.op_logicalOr;
	}

	/**
	 * 判断一个约束条件是否为原子约束条件
	 * @param be
	 * @return true|false
	 */
	public static boolean isAtomicConstraint(BinaryExpression be){
		Operator operator = be.getOp();
		return operator!=Operator.AND && operator!=Operator.OR;
	}
	
	
	/**
	 * 去除约束条件的外层括号
	 * @param iae
	 * @return 无外层括号的约束条件
	 */
	private static IASTBinaryExpression removeBrackets(IASTExpression iae){
		while(iae instanceof IASTUnaryExpression){
			iae = (IASTExpression) iae.getChildren()[0];
		}
		return (IASTBinaryExpression)iae;
	}
	
	/**
	 * 将IASTBinaryExpression.op转换为自定义的RelationalOp
	 * @param operator
	 * @return reverse op
	 */
	public static Operator translateOp(int operator) {
		switch(operator){
		case IASTBinaryExpression.op_logicalAnd:
			return Operator.AND;
		case IASTBinaryExpression.op_logicalOr:
			return Operator.OR;
		case IASTBinaryExpression.op_equals:
			return Operator.EQ;
		case IASTBinaryExpression.op_notequals:
			return Operator.NE;
		case IASTBinaryExpression.op_lessThan:
			return Operator.LT;
		case IASTBinaryExpression.op_greaterEqual:
			return Operator.GE;
		case IASTBinaryExpression.op_lessEqual:
			return Operator.LE;
		default:
			return Operator.GT;
		}
	}
}
