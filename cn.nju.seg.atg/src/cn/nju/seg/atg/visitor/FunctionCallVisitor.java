package cn.nju.seg.atg.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.dom.ast.IFunction;

import cn.nju.seg.atg.parse.CFGBuilder;

/**
 * a visitor for function call in C or C++ programs.
 * @author zy
 *
 */
public class FunctionCallVisitor extends ASTVisitor{
	private IIndex index = null;
    private Function function;
	private List<Function> newFunctionCalls;
	
	public FunctionCallVisitor (IIndex index, Function function){
		this.index = index;
		this.function = function;
		this.newFunctionCalls = new ArrayList<Function>();
	}
	
	public List<Function> getNewFunctionCalls() {
		return this.newFunctionCalls;
	}

	@Override
  public int visit(IASTName name) {
		//visit the target source file under test, and find all of the function calls
		if(name.isReference()){	
			String fcType = null;
			IBinding b = name.resolveBinding();
			IType type = (b instanceof IFunction) ? ((IFunction)b).getType() : null;
			if(type != null){
//				System.out.println("Referencing:" + name +",type:" + ASTTypeUtil.getType(type));
				fcType = ASTTypeUtil.getType(type);
			}		
			//return a function declaration
			IASTStandardFunctionDeclarator iasfd = AstUtils.tryInferTypeFromFunctionCall(name, this.index);
			if(iasfd != null){
				String fcName = iasfd.getName().getRawSignature();
				//get the location where call is in
				int offset = iasfd.getFileLocation().getNodeOffset();
				Function fc = new Function(fcName, fcType, offset, iasfd);
//				if(name.getParent().getParent().getParent() instanceof IASTExpression){
//				    IASTExpression expression = removeBrackets((IASTExpression)name.getParent().getParent().getParent());
//                    if(expression instanceof IASTBinaryExpression){
//					    fc.setCallExpression((IASTBinaryExpression)expression);
//				    }
//				}
				if(!CFGBuilder.allFunctions.containsKey(fc.getFunctionId()))
				    CFGBuilder.allFunctions.put(fc.getFunctionId(), fc);
				this.function.addFunctionCall(fc.getFunctionId(), name.getFileLocation().getNodeOffset());
				this.newFunctionCalls.add(fc);
			}		
		}

		return PROCESS_CONTINUE;
	}
	
	/**
	 * 去除约束条件的外层括号
	 * @param iae
	 * @return 无外层括号的约束条件
	 */
//	private static IASTExpression removeBrackets(IASTExpression iae)
//	{
//		while(iae instanceof IASTUnaryExpression)
//		{
//			iae = (IASTExpression) iae.getChildren()[0];
//		}
//		return iae;
//	}
}
