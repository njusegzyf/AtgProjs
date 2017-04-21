package cn.nju.seg.atg.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import cn.nju.seg.atg.model.Condition;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.visitor.ArraySubscriptVisitor;
import cn.nju.seg.atg.visitor.CFGNode;
import cn.nju.seg.atg.visitor.FunctionCallVisitor;
import cn.nju.seg.atg.visitor.Function;
import cn.nju.seg.atg.visitor.FunctionVisitor;

/**
 * 借助CDT提供的AST，对目标函数进行静态分析，包括寻找函数调用链，构建每个相关函数的CFG并连接等。
 * <p>2015/5
 * 
 * @version 0.1 Move code that sets {@link cn.nju.seg.atg.parse.TestBuilder#targetNode} out.
 * @author zy
 * @author Zhang Yifan
 */
public abstract class AbstractAST {
	private IIndex index;
	
	/**
	 * a static parse process for the function under test.
	 * <p>get function calls, only consider these function calls that 
	 * in the same source file with the target function
	 * @param ifd
	 */
	protected void staticParse(IFunctionDeclaration ifd){
		findTargetFunction(ifd);
		//寻找与待测程序相关联的函数调用（仅考虑在同一文件中的函数）
		CFGBuilder.allFunctions = new HashMap<String, Function>();
    FunctionCallVisitor fCallVisitor = new FunctionCallVisitor(this.index, CFGBuilder.function);
		CFGBuilder.allFunctions.put(CFGBuilder.function.getFunctionId(), CFGBuilder.function);
		findFunctionCalls(fCallVisitor, CFGBuilder.function.getDeclarator());
	}
	
	/**
	 * 递归方式找出所有相关的函数调用
	 * @param visitor
	 * @param declarator
	 */
	private void findFunctionCalls(FunctionCallVisitor visitor, IASTStandardFunctionDeclarator declarator){
		visitor.shouldVisitNames = true;
		declarator.getParent().accept(visitor);
		
		ArraySubscriptVisitor visitor2 = new ArraySubscriptVisitor();
		visitor2.shouldVisitExpressions = true;
		declarator.getParent().accept(visitor2);
		
		List<Function> functionCalls = visitor.getNewFunctionCalls();
		if(functionCalls.size()>0){
			for(Function fc : functionCalls){
				visitor = new FunctionCallVisitor(this.index, fc);
				findFunctionCalls(visitor, fc.getDeclarator());
			}
		}
	}
	
	/**
	 * 找到目标函数
	 * @param ifd
	 */
	private void findTargetFunction(IFunctionDeclaration ifd){
		IASTTranslationUnit iatu = parse(ifd);
		FunctionVisitor expressionVisitor = new FunctionVisitor(CFGBuilder.funcName);
		//找到对应的declarator
		expressionVisitor.shouldVisitDeclarators = true;
		iatu.accept(expressionVisitor);
		IASTStandardFunctionDeclarator declarator = expressionVisitor.getDeclarator();
		//构建目标函数的对象实例
		String fcName = declarator.getName().getRawSignature();
		int offset = declarator.getFileLocation().getNodeOffset();
		String[] parameters = ifd.getParameterTypes();
		String parameter = "(";
		for(int i=0;i<parameters.length;i++){
			parameter = parameter.concat(parameters[i]);
			if(i < parameters.length-1)
				parameter = parameter + ", ";
		}
		parameter = parameter.concat(")");
		String fcType = ifd.getReturnType() + " "+parameter;
		CFGBuilder.function = new Function(fcName, fcType, offset, declarator);
	}
	
	/**
     * 借助CDT提供的AST,构建被测程序的CFG
     * @param ifd
     */
	protected void buildCFG(IFunctionDeclaration ifd, boolean visitFunCalls){
		ConditionCoverage.conditions = new ArrayList<Condition>();
		ConditionCoverage.allNodes = new HashMap<Integer, CFGNode>();
		/************************************************
		 | 借助CDT插件，遍历函数节点ifd，生成CFG树			    |
		 ***********************************************/
		if(!visitFunCalls){
		    findTargetFunction(ifd);
		    CFGBuilder.nodeIndex = 0;
		    CFGBuilder.function.simpleBuild();
		}else{
			CFGBuilder.function.printCallChain();
			CFGBuilder.nodeIndex = 0;
			//先构建所有相关函数的CFG,再连接
            int count = 0;
			for(Function function : CFGBuilder.allFunctions.values()){
				function.setFid(++count);
				function.complexBuild();
			}
			CFGBuilder.function.connectCFG();
			if(CFGBuilder.shouldBuildCCFG)
			    CFGBuilder.function.buildCCFG();
		}
	}
	
	private IASTTranslationUnit parse(IFunctionDeclaration ifd){
		try {
			CFGBuilder.funcName = ifd.getSignature();
			ATG.callFunctionName = CFGBuilder.funcName.substring(0, CFGBuilder.funcName.indexOf("("));
			
			// @since 0.1 Move the setting code out
	    // TestBuilder.targetNode = "node2@"+ATG.callFunctionName;
		} catch (final CModelException e) {
		  throw new IllegalStateException(e);
		}
		ITranslationUnit itu = ifd.getTranslationUnit();
		IASTTranslationUnit iatu = null;		
		try {
			this.index = CCorePlugin.getIndexManager().getIndex(
				ifd.getCProject(), 
				IIndexManager.ADD_DEPENDENCIES|IIndexManager.ADD_DEPENDENT
				);
			this.index.acquireReadLock();
			iatu = itu.getAST(this.index, ITranslationUnit.AST_SKIP_ALL_HEADERS);
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}finally {
			this.index.releaseReadLock();
		}
		return iatu;
	}
	
//	private IASTTranslationUnit parse(ITranslationUnit lwUnit) {
//		IASTTranslationUnit iatu = null;
//		try {
//			index = CCorePlugin.getIndexManager().getIndex(
//				lwUnit.getCProject(), 
//				IIndexManager.ADD_DEPENDENCIES|IIndexManager.ADD_DEPENDENT
//				);
//			index.acquireReadLock();
//			iatu = lwUnit.getAST(index, ITranslationUnit.AST_SKIP_ALL_HEADERS);
//		} catch (CoreException ce) {
//			ce.printStackTrace();
//		} catch (InterruptedException ie) {
//			ie.printStackTrace();
//		}finally {
//			index.releaseReadLock();
//		}
//		return iatu;
//	}
}