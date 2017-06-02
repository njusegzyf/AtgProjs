package cn.nju.seg.atg.visitor;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;

import cn.nju.seg.atg.parse.CFGBuilder;

/**
 * a visitor for while statement in C or C++ programs.
 * @author zy
 *
 */
public class WhileStatementVisitor extends ASTVisitor {
    public WhileStatementVisitor(){  }
    
    @Override
    public int visit(IASTStatement node) {
    	if (node instanceof IASTWhileStatement) {
			IASTWhileStatement iafs = (IASTWhileStatement)node;
			IASTNode[] iafsChildren = iafs.getChildren();		
			if(iafsChildren[0] instanceof IASTBinaryExpression){
				IASTBinaryExpression iabe = (IASTBinaryExpression)iafsChildren[0];
				int offsetTmp = iabe.getFileLocation().getNodeOffset();
				CFGBuilder.currentNode.setSign(5);
				CFGBuilder.currentNode.setBinaryExpression(iabe);
				CFGBuilder.currentNode.setOffset(offsetTmp);
				CFGBuilder.nodeNumber++;
				CFGBuilder.currentNode.setNodeNumber(CFGBuilder.nodeNumber);
				
				addNode(CFGBuilder.currentNode, iafs);
			}else{
				JOptionPane.showMessageDialog(null, "当前while循环的格式无法识别！");
			}
			
			return PROCESS_ABORT;
		}

		return PROCESS_CONTINUE;
    }
    
    public void addNode(CFGNode parent, IASTWhileStatement iaws) {
		CFGNode whileNode = new CFGNode();
		int lastWhileStatementOffset = -1;
		int endStatementIndex = -1;
		
		if(iaws.getChildren()[1] instanceof IASTCompoundStatement){
			/*************************
			 * while循环主体是复合语句	 *
			 ************************/
			IASTCompoundStatement whileCompound = (IASTCompoundStatement)iaws.getChildren()[1];
			
			IASTNode[] whileCompoundChildren = whileCompound.getChildren();
			int whileCompoundChildrenN = whileCompoundChildren.length;
			if(whileCompoundChildrenN>0){
				if(whileCompoundChildren[0] instanceof IASTWhileStatement){
					//空语句
				}else if(whileCompoundChildren[0] instanceof IASTForStatement){
					//空语句
				}else if(whileCompoundChildren[0] instanceof IASTIfStatement){
					//空语句
				}else{
					int offsetTmp = whileCompound.getFileLocation().getNodeOffset();
					whileNode.setSign(-1);
					whileNode.setOffset(offsetTmp);
					CFGBuilder.nodeNumber++;
					whileNode.setNodeNumber(CFGBuilder.nodeNumber);
					parent.addChild(whileNode);
					whileNode.addParent(parent);
				}
			}
			int countI=0;
			int firstSequenceStatementIndex = -1;
			for(int i=0; i<whileCompoundChildrenN; i++){
				//嵌套While循环
				if(whileCompoundChildren[i] instanceof IASTWhileStatement){
					countI++;					
					endStatementIndex = i;
					
					if(countI == 1){
						lastWhileStatementOffset = whileCompoundChildren[i].getFileLocation().getNodeOffset();
						
						IASTBinaryExpression iabe = (IASTBinaryExpression)whileCompoundChildren[i].getChildren()[0];
						int offsetTmp = iabe.getFileLocation().getNodeOffset();
						if(i==0){
							whileNode.setBinaryExpression(iabe);
							whileNode.setOffset(offsetTmp);
							whileNode.setSign(5);
							CFGBuilder.nodeNumber++;
							whileNode.setNodeNumber(CFGBuilder.nodeNumber);
							parent.addChild(whileNode);
							whileNode.addParent(parent);
							
							CFGBuilder.terminalNodes.add(whileNode);							
							addNode(whileNode, (IASTWhileStatement)whileCompoundChildren[i]);
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
						}else{
							CFGNode nextNode = new CFGNode();
							nextNode.setBinaryExpression(iabe);
							nextNode.setOffset(offsetTmp);
							nextNode.setSign(5);
							CFGBuilder.nodeNumber++;
							nextNode.setNodeNumber(CFGBuilder.nodeNumber);
							whileNode.addChild(nextNode);
							nextNode.addParent(whileNode);
							
							CFGBuilder.terminalNodes.add(nextNode);						
							addNode(nextNode, (IASTWhileStatement)whileCompoundChildren[i]);
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
									nextNode.addParent(continueNode);
									continueNode.addChild(nextNode);
								}
							}
							CFGBuilder.continueNodes = new ArrayList<CFGNode>();
						}
						firstSequenceStatementIndex = i+1;
					}else{// 当前while分支内有2个或2个以上的控制语句
						if(firstSequenceStatementIndex != i){
							//控制语句之间存在顺序语句
							CFGNode nextNode = new CFGNode();
							int offsetTmp = whileCompoundChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
							nextNode.setSign(-1);
							nextNode.setOffset(offsetTmp);
							CFGBuilder.nodeNumber++;
							nextNode.setNodeNumber(CFGBuilder.nodeNumber);
							int terminalNodesNum = CFGBuilder.terminalNodes.size();
							for(int k=(terminalNodesNum-1); k>-1; k--){
								if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastWhileStatementOffset){
									CFGBuilder.terminalNodes.get(k).addChild(nextNode);
									nextNode.addParent(CFGBuilder.terminalNodes.get(k));
									CFGBuilder.terminalNodes.remove(k);
								}
							}
							
							CFGNode nextWhileNode = new CFGNode();
							IASTBinaryExpression iabe = (IASTBinaryExpression)whileCompoundChildren[i].getChildren()[0];
							offsetTmp = iabe.getFileLocation().getNodeOffset();
							nextWhileNode.setBinaryExpression(iabe);
							nextWhileNode.setOffset(offsetTmp);
							nextWhileNode.setSign(5);
							CFGBuilder.nodeNumber++;
							nextWhileNode.setNodeNumber(CFGBuilder.nodeNumber);
							nextNode.addChild(nextWhileNode);
							nextWhileNode.addParent(nextNode);
							
							CFGBuilder.terminalNodes.add(nextWhileNode);							
							addNode(nextWhileNode, (IASTWhileStatement)whileCompoundChildren[i]);
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
									nextWhileNode.addParent(continueNode);
									continueNode.addChild(nextWhileNode);
								}
							}
							CFGBuilder.continueNodes = new ArrayList<CFGNode>();
						}else{
							CFGNode nextWhileNode = new CFGNode();
							IASTBinaryExpression iabe = (IASTBinaryExpression)whileCompoundChildren[i].getChildren()[1];
							int offsetTmp = iabe.getFileLocation().getNodeOffset();
							nextWhileNode.setBinaryExpression(iabe);
							nextWhileNode.setOffset(offsetTmp);
							nextWhileNode.setSign(5);
							CFGBuilder.nodeNumber++;
							nextWhileNode.setNodeNumber(CFGBuilder.nodeNumber);
							int terminalNodesNum = CFGBuilder.terminalNodes.size();
							for(int k=(terminalNodesNum-1); k>-1; k--){
								if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastWhileStatementOffset){
									CFGBuilder.terminalNodes.get(k).addChild(nextWhileNode);
									nextWhileNode.addParent(CFGBuilder.terminalNodes.get(k));
									CFGBuilder.terminalNodes.remove(k);
								}
							}
							
							CFGBuilder.terminalNodes.add(nextWhileNode);						
							addNode(nextWhileNode, (IASTWhileStatement)whileCompoundChildren[i]);
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
									nextWhileNode.addParent(continueNode);
									continueNode.addChild(nextWhileNode);
								}
							}
							CFGBuilder.continueNodes = new ArrayList<CFGNode>();
						}
						firstSequenceStatementIndex = i+1;
					}
				}else if(whileCompoundChildren[i] instanceof IASTIfStatement){
					countI++;					
					endStatementIndex = i;
					
					if(countI == 1){
						lastWhileStatementOffset = whileCompoundChildren[i].getFileLocation().getNodeOffset();

						if(i==0){
							CFGBuilder.currentNode = new CFGNode();
							parent.addChild(CFGBuilder.currentNode);
							CFGBuilder.currentNode.addParent(parent);
							
							IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
							ifStatementVisitor.shouldVisitStatements = true;
							whileCompoundChildren[i].accept(ifStatementVisitor);
						}else{
							CFGBuilder.currentNode = new CFGNode();
							whileNode.addChild(CFGBuilder.currentNode);
							CFGBuilder.currentNode.addParent(whileNode);
							
							IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
							ifStatementVisitor.shouldVisitStatements = true;
							whileCompoundChildren[i].accept(ifStatementVisitor);
						}
						firstSequenceStatementIndex = i+1;
					}else{	// 当前while分支内有2个或2个以上的控制语句
						if(firstSequenceStatementIndex != i){
							CFGNode nextNode = new CFGNode();
							int offsetTmp = whileCompoundChildren[firstSequenceStatementIndex].getFileLocation().getNodeOffset();
							nextNode.setSign(-1);
							nextNode.setOffset(offsetTmp);
							CFGBuilder.nodeNumber++;
							nextNode.setNodeNumber(CFGBuilder.nodeNumber);
							int terminalNodesNum = CFGBuilder.terminalNodes.size();
							for(int k=(terminalNodesNum-1); k>-1; k--){
								if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastWhileStatementOffset){
									CFGBuilder.terminalNodes.get(k).addChild(nextNode);
									nextNode.addParent(CFGBuilder.terminalNodes.get(k));
									CFGBuilder.terminalNodes.remove(k);
								}
							}
							
							CFGBuilder.currentNode = new CFGNode();
							nextNode.addChild(CFGBuilder.currentNode);
							CFGBuilder.currentNode.addParent(nextNode);
							
							IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
							ifStatementVisitor.shouldVisitStatements = true;
							whileCompoundChildren[i].accept(ifStatementVisitor);
						}else{
							CFGBuilder.currentNode = new CFGNode();
							int terminalNodesNum = CFGBuilder.terminalNodes.size();
							for(int k=(terminalNodesNum-1); k>-1; k--){
								if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastWhileStatementOffset){
									CFGBuilder.terminalNodes.get(k).addChild(CFGBuilder.currentNode);
									CFGBuilder.currentNode.addParent(CFGBuilder.terminalNodes.get(k));
									CFGBuilder.terminalNodes.remove(k);
								}
							}
							
							IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
							ifStatementVisitor.shouldVisitStatements = true;
							whileCompoundChildren[i].accept(ifStatementVisitor);
						}
						firstSequenceStatementIndex = i+1;
					}
				}
			}
			if(countI == 0){
				whileNode.addChild(parent);
				parent.addParent(whileNode);
			}
			//当前块中最后的语句是顺序语句
			if(endStatementIndex > -1 && endStatementIndex < whileCompoundChildrenN-1){
				CFGNode nextNode = new CFGNode();
				int offsetTmp = whileCompoundChildren[endStatementIndex+1].getFileLocation().getNodeOffset();
				nextNode.setOffset(offsetTmp);
				nextNode.setSign(-1);
				CFGBuilder.nodeNumber++;
				nextNode.setNodeNumber(CFGBuilder.nodeNumber);
				
				//为当前顺序节点添加父节点
				int terminalNodesNum = CFGBuilder.terminalNodes.size();
				for(int k=(terminalNodesNum-1); k>-1; k--){
					if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastWhileStatementOffset){
						CFGBuilder.terminalNodes.get(k).addChild(nextNode);
						nextNode.addParent(CFGBuilder.terminalNodes.get(k));
						CFGBuilder.terminalNodes.remove(k);
					}
				}
				
				nextNode.addChild(parent);
				parent.addParent(nextNode);
			}
			if(whileCompoundChildren[whileCompoundChildrenN-1] instanceof IASTWhileStatement || whileCompoundChildren[whileCompoundChildrenN-1] instanceof IASTIfStatement){
				int terminalNodesNum = CFGBuilder.terminalNodes.size();
				for(int k=(terminalNodesNum-1); k>-1; k--){
					if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastWhileStatementOffset){
						CFGBuilder.terminalNodes.get(k).addChild(parent);
						parent.addParent(CFGBuilder.terminalNodes.get(k));
						CFGBuilder.terminalNodes.remove(k);
					}
				}
			}
			endStatementIndex = -1;
			firstSequenceStatementIndex = -1;
		}else{
			/*************************
			 * while循环主体是单条语句	 *
			 ************************/
			if(iaws.getChildren()[1] instanceof IASTWhileStatement){
				IASTBinaryExpression iabe = (IASTBinaryExpression)iaws.getChildren()[1].getChildren()[0];
				int offsetTmp = iabe.getFileLocation().getNodeOffset();
				whileNode.setBinaryExpression(iabe);
				whileNode.setOffset(offsetTmp);
				whileNode.setSign(5);
				CFGBuilder.nodeNumber++;
				whileNode.setNodeNumber(CFGBuilder.nodeNumber);
				parent.addChild(whileNode);
				whileNode.addParent(parent);
				parent.addParent(whileNode);
				whileNode.addChild(parent);
				
				addNode(whileNode, (IASTWhileStatement)iaws.getChildren()[1]);
				//处理break节点
				if(CFGBuilder.breakNodes.size()>0){
					for(CFGNode breakNode : CFGBuilder.breakNodes){
						parent.addParent(breakNode);
						breakNode.addChild(parent);
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
			}else if(iaws.getChildren()[1] instanceof IASTIfStatement){
				lastWhileStatementOffset = iaws.getChildren()[1].getFileLocation().getNodeOffset();
				CFGBuilder.currentNode = new CFGNode();
				parent.addChild(CFGBuilder.currentNode);
				CFGBuilder.currentNode.addParent(parent);
				
				IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
				ifStatementVisitor.shouldVisitStatements = true;
				iaws.getChildren()[1].accept(ifStatementVisitor);
				
				int terminalNodesNum = CFGBuilder.terminalNodes.size();
				for(int k=(terminalNodesNum-1); k>-1; k--){
					if(CFGBuilder.terminalNodes.get(k).getOffset() >= lastWhileStatementOffset){
						CFGBuilder.terminalNodes.get(k).addChild(parent);
						parent.addParent(CFGBuilder.terminalNodes.get(k));
						CFGBuilder.terminalNodes.remove(k);
					}
				}
			}else{
				int offsetTmp = iaws.getChildren()[1].getFileLocation().getNodeOffset();
				whileNode.setOffset(offsetTmp);
				whileNode.setSign(-1);
				CFGBuilder.nodeNumber++;
				whileNode.setNodeNumber(CFGBuilder.nodeNumber);
				parent.addChild(whileNode);
				whileNode.addParent(parent);
				parent.addParent(whileNode);
				whileNode.addChild(parent);
			}
		}
	}
}
