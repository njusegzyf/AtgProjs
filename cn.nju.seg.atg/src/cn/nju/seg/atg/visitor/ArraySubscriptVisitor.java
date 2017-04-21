package cn.nju.seg.atg.visitor;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;

/**
 * 访问程序中存在的数组下标表达式
 * <p>可直接插桩打印语句,得到程序中涉及到的数组元素
 * @author zy
 *
 */
public class ArraySubscriptVisitor extends ASTVisitor {
	
	public int visit(IASTExpression expression) {
		if(expression instanceof IASTArraySubscriptExpression){
			System.out.println(((IASTArraySubscriptExpression) expression).getArgument().getRawSignature());
		}
		return PROCESS_CONTINUE;
	}
}
