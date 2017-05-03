package cn.nju.seg.atg.visitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import cn.nju.seg.atg.plugin.Activator;

/**
 * Useful functions for doing static code analysis on c/c++ AST
 * @author zy
 * 
 */
public final class AstUtils {
	public static class NameFinderVisitor extends ASTVisitor {
		public IASTName name;
		{
			this.shouldVisitNames = true;
		}

		@Override
		public int visit(IASTName name) {
			this.name = name;
			return PROCESS_ABORT;
		}
	}
	
	private static class FunctionNameFinderVisitor extends NameFinderVisitor {
		{
			this.shouldVisitExpressions = true;
		}
		
		@Override
		public int visit(IASTExpression expression) {
			if(expression instanceof IASTFieldReference) {
				this.name = ((IASTFieldReference) expression).getFieldName();
				return PROCESS_ABORT;
			}
			return super.visit(expression);
		}	
	}
	
	// Not instantiatable. All methods are static.
	private AstUtils() {
	}

	/**
	 * For a function call, tries to find a matching function declaration.
	 * Checks the argument count.
	 * 
	 * @param function name
	 * @param index
	 * 
	 * @return a generated declaration or null if not suitable
	 */
	public static IASTStandardFunctionDeclarator tryInferTypeFromFunctionCall(IASTName astName, IIndex index) {
		if (astName.getParent() instanceof IASTIdExpression && astName.getParent().getParent() instanceof IASTFunctionCallExpression
				&& astName.getParent().getPropertyInParent() == IASTFunctionCallExpression.FUNCTION_NAME) {//ARGUMENT or FUNCTION_NAME?
			IASTFunctionCallExpression call = (IASTFunctionCallExpression) astName.getParent().getParent();
			FunctionNameFinderVisitor visitor = new FunctionNameFinderVisitor();
			call.getFunctionNameExpression().accept(visitor);
			IASTName funcname = visitor.name;
			int expectedParametersNum = 0;
			int targetParameterNum = -1;
			for (IASTNode n : call.getChildren()) {
				if (n.getPropertyInParent() == IASTFunctionCallExpression.ARGUMENT) {
					expectedParametersNum++;
				}
			}
			targetParameterNum = expectedParametersNum;
			if (targetParameterNum == -1) {
				return null;
			}
			IBinding[] bindings;
			{
				IBinding binding = funcname.resolveBinding();
				if (binding instanceof IProblemBinding) {
					bindings = ((IProblemBinding) binding).getCandidateBindings();
				} else {
					bindings = new IBinding[] { binding };
				}
			}
			try {
				index.acquireReadLock();
				String sourceFileName = astName.getContainingFilename();
				
				Set<IIndexName> declSet = new HashSet<IIndexName>();
				// fill declSet with proper declarations
				for (IBinding b : bindings) {
					if (b instanceof IFunction) {
						IFunction f = (IFunction) b;
						if (f.getParameters().length == expectedParametersNum) {
							// Consider this overload
							//maybe a class method
							IIndexName[] decls = index.findDeclarations(b);
							declSet.addAll(Arrays.asList(decls));
						}
					}
				}
				HashMap<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<ITranslationUnit, IASTTranslationUnit>();
				for (IIndexName decl : declSet) {
					String declFileName = decl.getFileLocation().getFileName();
					//只考虑定义与调用位于同一源文件的函数调用
					if(!sourceFileName.equals(declFileName)){
					     continue;
					}
					
					ITranslationUnit tu = getTranslationUnitFromIndexName(decl);
					if (tu == null) {
						continue;
					}
					
					IASTTranslationUnit ast = null;
					if(astCache.containsKey(tu)) {
						ast = astCache.get(tu);
					} else {
						ast = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
						astCache.put(tu, ast);
					}
					
					IASTName name = (IASTName) ast.getNodeSelector(null).findEnclosingNode(decl.getNodeOffset(), decl.getNodeLength());
					IASTNode fdecl = name;
					while (fdecl instanceof IASTName) {
						fdecl = fdecl.getParent();
					}
					assert (fdecl instanceof IASTFunctionDeclarator);
					
					if(fdecl instanceof IASTStandardFunctionDeclarator){
						if(fdecl.getParent().getChildren().length>2){
							if(fdecl.getParent().getChildren()[2] instanceof IASTCompoundStatement){
								return (IASTStandardFunctionDeclarator)fdecl;
	                        }
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				Activator.log(e);
			} finally {
				index.releaseReadLock();
			}
		}
		return null;
	}

	private static ITranslationUnit getTranslationUnitFromIndexName(IIndexName decl) throws CoreException {
		IIndexFile file = decl.getFile();
		if (file != null) {
			return CoreModelUtil.findTranslationUnitForLocation(file.getLocation().getURI(), null);	
		}
		return null;
	}
	
	/**
	 * If the function definition belongs to a class, returns the class.
	 * Otherwise, returns null.
	 * 
	 * @param function
	 *        the function definition to check
	 * @param index
	 *        the index to use for name lookup
	 * @return Either a type specifier or null
	 */
	public IASTCompositeTypeSpecifier getCompositeTypeFromFunction(final IASTFunctionDefinition function, final IIndex index) {
		// return value to be set via visitor
		final IASTCompositeTypeSpecifier returnSpecifier[] = { null };
		final HashMap<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<ITranslationUnit, IASTTranslationUnit>();
		function.accept(new ASTVisitor() {
			{
				this.shouldVisitDeclarators = true;
				this.shouldVisitNames = true;
			}

			@Override
			public int visit(IASTName name) {
				if (!(name instanceof ICPPASTQualifiedName && name.getParent().getParent() == function))
					return PROCESS_CONTINUE;
				ICPPASTQualifiedName qname = (ICPPASTQualifiedName) name;
				// A qualified name may have 1 name, but in our case needs to
				// have 2.
				// The pre-last name is either a namespace or a class.
				if (qname.getChildren().length < 2) {
					return PROCESS_CONTINUE;
				}
				IASTName namePart = (IASTName) qname.getChildren()[qname.getChildren().length - 2];
				IBinding binding = namePart.resolveBinding();
				try {
					index.acquireReadLock();
					IIndexName[] declarations = index.findDeclarations(binding);
					// Check the declarations and use first suitable
					for (IIndexName decl : declarations) {
						ITranslationUnit tu = getTranslationUnitFromIndexName(decl);
						IASTTranslationUnit ast = null;
						if(astCache.containsKey(tu)) {
							ast = astCache.get(tu);
						} else {
							ast = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
							astCache.put(tu, ast);
						}
						IASTNode node = ast.getNodeSelector(null).findEnclosingNode(decl.getNodeOffset(), decl.getNodeLength());
						IASTCompositeTypeSpecifier specifier = getEnclosingCompositeTypeSpecifier(node);
						if (specifier != null) {
							returnSpecifier[0] = specifier;
							break;
						}
					}
				} catch (InterruptedException e) {
					return PROCESS_ABORT;
				} catch (CoreException e) {
					Activator.log(e);
					return PROCESS_ABORT;
				} finally {
					index.releaseReadLock();
				}
				return PROCESS_ABORT;
			}
		});
		return returnSpecifier[0];
	}
	
	public IASTCompositeTypeSpecifier getEnclosingCompositeTypeSpecifier(IASTNode node) {
		while (node != null && !(node instanceof IASTCompositeTypeSpecifier)) {
			node = node.getParent();
		}
		return (IASTCompositeTypeSpecifier) node;
	}
}
