package cn.nju.seg.atg.visitor;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
// import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
// import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
// import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.internal.core.model.ASTStringUtil;

import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.util.ATG;

/**
 * 添加对while/do-while/break/continue/return/conditional operator的处理（by zy)
 * @author ChengXin
 * @author zy
 *
 */
@SuppressWarnings("restriction")
public class FunctionVisitor extends ASTVisitor {
	
//	private ArrayList<String> parameters;
	/**
	 * 由函数转成的CFG树的开始节点
	 */
	private CFGNode startNode;
	private String funcName;
	private IASTStandardFunctionDeclarator declartor;
	
	public IASTStandardFunctionDeclarator getDeclartor() {
		return this.declartor;
	}

	public FunctionVisitor(String funcName){
		this.startNode = new CFGNode();
		this.funcName = funcName;
	}
	
//	public String[] getParameters()
//	{
//		String[] tmps = new String[this.parameters.size()];
//		for(int i=0;i<this.parameters.size();i++)
//		{
//			tmps[i] = String.valueOf(this.parameters.get(i));
//		}
//		return tmps;
//	}
	
	public CFGNode getStartNode() {
		return this.startNode;
	}

	public int visit(IASTDeclarator node) {
		if(node instanceof IASTStandardFunctionDeclarator){
			IASTStandardFunctionDeclarator iasfd = (IASTStandardFunctionDeclarator)node;
			String tempName = iasfd.getName().getRawSignature();
			String[] parameters = ASTStringUtil.getParameterSignatureArray(iasfd);
			String parameter = "(";
			for(int i=0;i<parameters.length;i++)
			{
				parameter = parameter.concat(parameters[i]);
				if(i < parameters.length-1)
					parameter = parameter + ", ";
			}
			parameter = parameter.concat(")");
			tempName = tempName + parameter;
//			IASTParameterDeclaration[] iParameters = iasfd.getParameters();
//			this.parameters = new ArrayList<String>();
//			for(int i=0;i<iParameters.length;i++)
//			{
//				if(iParameters[i].getDeclarator().getName().getRawSignature().equals(iParameters[i].getDeclarator().getRawSignature()))
//				{
//					this.parameters.add(iParameters[i].getDeclarator().getRawSignature());
//				}
//			}
			if(tempName.compareTo(this.funcName)==0){
				this.declartor = iasfd;
				// 获取函数的输入参数的类型、参数个数
				CFGBuilder.parameterTypes = ASTStringUtil.getParameterSignatureArray(iasfd);
				ATG.NUM_OF_PARAM = CFGBuilder.parameterTypes.length;
			}
		}
		
		return PROCESS_CONTINUE;
	}
	
	public IASTStandardFunctionDeclarator getDeclarator(){
		return this.declartor;
	}
	
	public void build(IASTStandardFunctionDeclarator iasfd){
		int lastStatementOffset = -1;
		int firstSequenceStatementIndex = -1;
		int endStatementIndex = -1;
		
		IASTCompoundStatement iacs = (IASTCompoundStatement)iasfd.getParent().getChildren()[2];				
		IASTNode[] iacsChildren = iacs.getChildren();
		int n = iacsChildren.length;
		
		if(n>0){
			if(iacsChildren[0] instanceof IASTIfStatement){ }            //if
			else if(iacsChildren[0] instanceof IASTForStatement){ }      //for
			else if(iacsChildren[0] instanceof IASTWhileStatement){ }    //while
			else if(iacsChildren[0] instanceof IASTDoStatement){ }       //do
			else if(iacsChildren[0] instanceof IASTReturnStatement){ }   //return
			else{
				int offsetTmp = iacsChildren[0].getFileLocation().getNodeOffset();
				this.startNode.setSign(-1);
				this.startNode.setOffset(offsetTmp);
				CFGBuilder.nodeNumber++;
				this.startNode.setNodeNumber(CFGBuilder.nodeNumber);
			}
		}
		
		int countI = 0;
		for(int i=0; i<n; i++){
			/****************************
			* 		三元条件运算符  		*
			****************************/
			if(CFGBuilder.shouldVisitConditionalOperator){
			    IASTConditionalExpression condExpression = ConditionalOperatorVisitor.exist(iacsChildren[i]);
			    		
			    //处理三元条件运算符
			    if(condExpression!=null){
        			countI++;
		        	endStatementIndex = i;						
			        if(countI == 1){
				        lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
				        if(i==0){
				        	CFGBuilder.nodeNumber--;
					        this.startNode = CFGBuilder.currentNode;
				        }
				        else{
					        this.startNode.addChild(CFGBuilder.currentNode);
				            CFGBuilder.currentNode.addParent(this.startNode);
				        }
				        
				        ConditionalOperatorVisitor.visitConditionalOperator(condExpression);
                        
			        	firstSequenceStatementIndex = i+1;
			        }else{
			    	    lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
				 
				        if(firstSequenceStatementIndex != i){
					        CFGNode nextNode = new CFGNode();
					        int offsetTmp = iacsChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
					        nextNode.setSign(-1);
					        nextNode.setOffset(offsetTmp);
					        nextNode.setNodeNumber(++CFGBuilder.nodeNumber);
					        int terminalNodesNum = CFGBuilder.terminalNodes.size();
					        for(int k=0; k<terminalNodesNum; k++){
						        CFGBuilder.terminalNodes.get(k).addChild(nextNode);
						        nextNode.addParent(CFGBuilder.terminalNodes.get(k));
					        }
				        	CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
					
					        CFGBuilder.currentNode = new CFGNode();
					        CFGBuilder.currentNode.addParent(nextNode);
					        nextNode.addChild(CFGBuilder.currentNode);
				        }else{
					        CFGBuilder.currentNode = new CFGNode();
					        int terminalNodesNum = CFGBuilder.terminalNodes.size();
					        for(int k=0; k<terminalNodesNum; k++){
						        CFGBuilder.terminalNodes.get(k).addChild(CFGBuilder.currentNode);
						        CFGBuilder.currentNode.addParent(CFGBuilder.terminalNodes.get(k));
					        }
					        CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
				        }
				        
				        ConditionalOperatorVisitor.visitConditionalOperator(condExpression);
				        
			        	firstSequenceStatementIndex = i+1;
			        }
			    }
			}
			/****************************
			* 		If-Else语句			*
			****************************/
	        if(iacsChildren[i] instanceof IASTIfStatement){						
				countI++;
				endStatementIndex = i;						
				if(countI == 1){
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					if(i==0){
						this.startNode = CFGBuilder.currentNode;
					}
					else{
						this.startNode.addChild(CFGBuilder.currentNode);
					    CFGBuilder.currentNode.addParent(this.startNode);
					}

					IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
					ifStatementVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(ifStatementVisitor);
					
					firstSequenceStatementIndex = i+1;
				}else{
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					
					if(firstSequenceStatementIndex != i){
						CFGNode nextNode = new CFGNode();
						int offsetTmp = iacsChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
						nextNode.setSign(-1);
						nextNode.setOffset(offsetTmp);
						CFGBuilder.nodeNumber++;
						nextNode.setNodeNumber(CFGBuilder.nodeNumber);
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(nextNode);
							nextNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
						
						CFGBuilder.currentNode = new CFGNode();
						CFGBuilder.currentNode.addParent(nextNode);
						nextNode.addChild(CFGBuilder.currentNode);
					}else{
						CFGBuilder.currentNode = new CFGNode();
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(CFGBuilder.currentNode);
							CFGBuilder.currentNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
					}
					
					IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
					ifStatementVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(ifStatementVisitor);
					
					firstSequenceStatementIndex = i+1;
				}
			}else if(iacsChildren[i] instanceof IASTForStatement){
				/****************************
				* 			For语句			*
				****************************/
				countI++;
				endStatementIndex = i;
				CFGNode forNode;
				if(countI == 1){
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					
					if(i==0){
						this.startNode = CFGBuilder.currentNode;
					}
					else{
						this.startNode.addChild(CFGBuilder.currentNode);
					    CFGBuilder.currentNode.addParent(this.startNode);
					}

					CFGBuilder.terminalNodes.add(CFGBuilder.currentNode);
					forNode = CFGBuilder.currentNode;
					ForStatementVisitor forVisitor = new ForStatementVisitor();
					forVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(forVisitor);
					//处理break节点
					if(CFGBuilder.breakNodes.size()>0){
						for(CFGNode breakNode : CFGBuilder.breakNodes){
							CFGBuilder.terminalNodes.add(breakNode);
						}
					}
					CFGBuilder.breakNodes = new ArrayList<CFGNode>();
					//处理continue节点
					if(CFGBuilder.continueNodes.size()>0){
						for(CFGNode continueNode : CFGBuilder.continueNodes){
							forNode.addParent(continueNode);
							continueNode.addChild(forNode);
						}
					}
					CFGBuilder.continueNodes = new ArrayList<CFGNode>();
					firstSequenceStatementIndex = i+1;
				}else{
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					
					if(firstSequenceStatementIndex != i){
						CFGNode nextNode = new CFGNode();
						int offsetTmp = iacsChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
						nextNode.setSign(-1);
						nextNode.setOffset(offsetTmp);
						CFGBuilder.nodeNumber++;
						nextNode.setNodeNumber(CFGBuilder.nodeNumber);
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(nextNode);
							nextNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
						
						CFGBuilder.currentNode = new CFGNode();
						CFGBuilder.currentNode.addParent(nextNode);
						nextNode.addChild(CFGBuilder.currentNode);
						
						CFGBuilder.terminalNodes.add(CFGBuilder.currentNode);
						forNode = CFGBuilder.currentNode;
					}else{
						CFGBuilder.currentNode = new CFGNode();
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(CFGBuilder.currentNode);
							CFGBuilder.currentNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
						
						CFGBuilder.terminalNodes.add(CFGBuilder.currentNode);
						forNode = CFGBuilder.currentNode;
					}
					
					ForStatementVisitor forVisitor = new ForStatementVisitor();
					forVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(forVisitor);
					//处理break节点
					if(CFGBuilder.breakNodes.size()>0){
						for(CFGNode breakNode : CFGBuilder.breakNodes){
							CFGBuilder.terminalNodes.add(breakNode);
						}
					}
					CFGBuilder.breakNodes = new ArrayList<CFGNode>();
					//处理continue节点
					if(CFGBuilder.continueNodes.size()>0){
						for(CFGNode continueNode : CFGBuilder.continueNodes){
							forNode.addParent(continueNode);
							continueNode.addChild(forNode);
						}
					}
					CFGBuilder.continueNodes = new ArrayList<CFGNode>();
					firstSequenceStatementIndex = i+1;
				}
			}
			else if(iacsChildren[i] instanceof IASTWhileStatement){
				/****************************
				* 			While语句	    *
				****************************/
                countI++;
                endStatementIndex = i;
                CFGNode whileNode;
				if(countI == 1){
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					if(i==0){
						this.startNode = CFGBuilder.currentNode;
					}
					else{
						this.startNode.addChild(CFGBuilder.currentNode);
					    CFGBuilder.currentNode.addParent(this.startNode);
					}							
					CFGBuilder.terminalNodes.add(CFGBuilder.currentNode);
					whileNode = CFGBuilder.currentNode;
					WhileStatementVisitor whileVisitor = new WhileStatementVisitor();
					whileVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(whileVisitor);
					//处理break节点
					if(CFGBuilder.breakNodes.size()>0){
						for(CFGNode breakNode : CFGBuilder.breakNodes){
							CFGBuilder.terminalNodes.add(breakNode);
						}
					}
					CFGBuilder.breakNodes = new ArrayList<CFGNode>();
					//处理continue节点
					if(CFGBuilder.continueNodes.size()>0){
						for(CFGNode continueNode : CFGBuilder.continueNodes){
							whileNode.addParent(continueNode);
							continueNode.addChild(whileNode);
						}
					}
					CFGBuilder.continueNodes = new ArrayList<CFGNode>();
					firstSequenceStatementIndex = i+1;
				}else{
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					
					if(firstSequenceStatementIndex != i){
						CFGNode nextNode = new CFGNode();
						int offsetTmp = iacsChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
						nextNode.setSign(-1);
						nextNode.setOffset(offsetTmp);
						CFGBuilder.nodeNumber++;
						nextNode.setNodeNumber(CFGBuilder.nodeNumber);
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(nextNode);
							nextNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
						
						CFGBuilder.currentNode = new CFGNode();
						CFGBuilder.currentNode.addParent(nextNode);
						nextNode.addChild(CFGBuilder.currentNode);
						
						CFGBuilder.terminalNodes.add(CFGBuilder.currentNode);
						whileNode = CFGBuilder.currentNode;
					}else{
						CFGBuilder.currentNode = new CFGNode();
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(CFGBuilder.currentNode);
							CFGBuilder.currentNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
						
						CFGBuilder.terminalNodes.add(CFGBuilder.currentNode);
						whileNode = CFGBuilder.currentNode;
					}
					
					WhileStatementVisitor whileVisitor = new WhileStatementVisitor();
					whileVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(whileVisitor);
					//处理break节点
					if(CFGBuilder.breakNodes.size()>0){
						for(CFGNode breakNode : CFGBuilder.breakNodes){
							CFGBuilder.terminalNodes.add(breakNode);
						}
					}
					CFGBuilder.breakNodes = new ArrayList<CFGNode>();
					//处理continue节点
					if(CFGBuilder.continueNodes.size()>0){
						for(CFGNode continueNode : CFGBuilder.continueNodes){
							whileNode.addParent(continueNode);
							continueNode.addChild(whileNode);
						}
					}
					CFGBuilder.continueNodes = new ArrayList<CFGNode>();
					firstSequenceStatementIndex = i+1;
				}
			}
			else if(iacsChildren[i] instanceof IASTDoStatement){
				/****************************
				* 			Do-while语句	    *
				****************************/
                countI++;
                endStatementIndex = i;
				if(countI == 1){
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					if(i==0){
						this.startNode = CFGBuilder.currentNode;
					}
					else{
						this.startNode.addChild(CFGBuilder.currentNode);
					    CFGBuilder.currentNode.addParent(this.startNode);
					}							
					
					DoStatementVisitor doVisitor = new DoStatementVisitor();
					doVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(doVisitor);
					//处理break节点
					if(CFGBuilder.breakNodes.size()>0){
						for(CFGNode breakNode : CFGBuilder.breakNodes){
							CFGBuilder.terminalNodes.add(breakNode);
						}
					}
					CFGBuilder.breakNodes = new ArrayList<CFGNode>();
					firstSequenceStatementIndex = i+1;
				}else{
					lastStatementOffset = iacsChildren[i].getFileLocation().getNodeOffset();
					
					if(firstSequenceStatementIndex != i){
						CFGNode nextNode = new CFGNode();
						int offsetTmp = iacsChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
						nextNode.setSign(-1);
						nextNode.setOffset(offsetTmp);
						CFGBuilder.nodeNumber++;
						nextNode.setNodeNumber(CFGBuilder.nodeNumber);
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(nextNode);
							nextNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
						
						CFGBuilder.currentNode = new CFGNode();
						CFGBuilder.currentNode.addParent(nextNode);
						nextNode.addChild(CFGBuilder.currentNode);
					}else{
						CFGBuilder.currentNode = new CFGNode();
						int terminalNodesNum = CFGBuilder.terminalNodes.size();
						for(int k=0; k<terminalNodesNum; k++){
							CFGBuilder.terminalNodes.get(k).addChild(CFGBuilder.currentNode);
							CFGBuilder.currentNode.addParent(CFGBuilder.terminalNodes.get(k));
						}
						CFGBuilder.terminalNodes = new ArrayList<CFGNode>();
					}
					
					DoStatementVisitor doVisitor = new DoStatementVisitor();
					doVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(doVisitor);
					//处理break节点
					if(CFGBuilder.breakNodes.size()>0){
						for(CFGNode breakNode : CFGBuilder.breakNodes){
							CFGBuilder.terminalNodes.add(breakNode);
						}
					}
					CFGBuilder.breakNodes = new ArrayList<CFGNode>();
					firstSequenceStatementIndex = i+1;
				}
			}
			else if(iacsChildren[i] instanceof IASTReturnStatement){
				/****************************************************
				* 		Return语句,默认return为当前模块的最后一句			*
				****************************************************/
				countI++;
				endStatementIndex = i;
				if(i!=n-1){
					JOptionPane.showMessageDialog(null, "return语句位置存在错误！");
				}
				if(countI==1){
					if(i==0){
					    this.startNode = CFGBuilder.currentNode;
				    }else{
				        CFGBuilder.currentNode.addParent(this.startNode);
						this.startNode.addChild(CFGBuilder.currentNode);
					}
					
					ReturnStatementVisitor returnStatementVisitor = new ReturnStatementVisitor();
					returnStatementVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(returnStatementVisitor);
				}else{
				    if(firstSequenceStatementIndex == (n-1)){
					    CFGBuilder.currentNode = new CFGNode();

					    //为当前顺序节点添加父节点
					    int terminalNodesNum = CFGBuilder.terminalNodes.size();
					    for(int k=(terminalNodesNum-1); k>-1; k--){
						    if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastStatementOffset){
							    CFGBuilder.terminalNodes.get(k).addChild(CFGBuilder.currentNode);
							    CFGBuilder.currentNode.addParent(CFGBuilder.terminalNodes.get(k));
							    CFGBuilder.terminalNodes.remove(k);
						    }
					    }
				    }else{
					    CFGNode nextNode = new CFGNode();
					    int offsetTmp = iacsChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
					    nextNode.setOffset(offsetTmp);
					    nextNode.setSign(-1);
					    CFGBuilder.nodeNumber++;
					    nextNode.setNodeNumber(CFGBuilder.nodeNumber);
					    //为当前顺序节点添加父节点
					    int terminalNodesNum = CFGBuilder.terminalNodes.size();
					    for(int k=(terminalNodesNum-1); k>-1; k--){
						    if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastStatementOffset){
						    	CFGBuilder.terminalNodes.get(k).addChild(nextNode);
						    	nextNode.addParent(CFGBuilder.terminalNodes.get(k));
							    CFGBuilder.terminalNodes.remove(k);
						    }
					    }
					
					    CFGBuilder.currentNode = new CFGNode();
				    	nextNode.addChild(CFGBuilder.currentNode);
				    	CFGBuilder.currentNode.addParent(nextNode);
				    }
				    ReturnStatementVisitor returnStatementVisitor = new ReturnStatementVisitor();
					returnStatementVisitor.shouldVisitStatements = true;
					iacsChildren[i].accept(returnStatementVisitor);
				}
			}
		}
		if(endStatementIndex > -1 && endStatementIndex < n-1){
			CFGNode nextNode = new CFGNode();
			int offsetTmp = iacsChildren[endStatementIndex+1].getFileLocation().getNodeOffset();
			nextNode.setOffset(offsetTmp);
			nextNode.setSign(-1);
			CFGBuilder.nodeNumber++;
			nextNode.setNodeNumber(CFGBuilder.nodeNumber);
			
			//为当前顺序节点添加父节点
			int terminalNodesNum = CFGBuilder.terminalNodes.size();
			for(int k=(terminalNodesNum-1); k>-1; k--){
				if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastStatementOffset){
					CFGBuilder.terminalNodes.get(k).addChild(nextNode);
					nextNode.addParent(CFGBuilder.terminalNodes.get(k));
					CFGBuilder.terminalNodes.remove(k);
				}
			}
		}
	}
}
