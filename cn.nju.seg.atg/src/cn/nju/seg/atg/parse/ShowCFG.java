package cn.nju.seg.atg.parse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.Filter;
import org.eclipse.zest.layouts.LayoutItem;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import cn.nju.seg.atg.model.constraint.BinaryExpression;
import cn.nju.seg.atg.model.constraint.Operator;
import cn.nju.seg.atg.util.ConstantValue;
import cn.nju.seg.atg.visitor.CFGNode;

/**
 * 以图形化方式显示当前函数的AST树
 * @author ChengXin
 */
public class ShowCFG extends AbstractAST{
	private static List<Integer> nodeShowed = new ArrayList<Integer>();
	private static Display d = Display.getDefault();
	private static Shell shell = new Shell(d);
	private static Graph g = new Graph(shell, SWT.NONE);
	private static GraphConnection gc = null;
	private static GraphNode[] graphNodes = null;
	private final int NODE_NUMBER = 200;
	private static CFGNode startNode = null;
	private final static int WIDTH = 1000;
	private final static int HEIGHT = 1000;
	
	public ShowCFG(){
	}
	/**
	 * 利用zest插件，显示CFG的树形结构（仅当前函数）
	 * @param ifd
	 */
	public void showSimpleCFG(IFunctionDeclaration ifd){
		//借助CDT插件，遍历函数节点ifd，生成CFG树	
		this.buildCFG(ifd, false);
		//从CFG树的条件节点中取出逻辑符号,并记录到该条件节点中                           
		List<Integer> nodePrinted = new ArrayList<Integer>();
		addSymbol(CFGBuilder.function.getStartNode(),nodePrinted);
		setStartNode(CFGBuilder.function.getStartNode());
		showCFG();
	}
	
	/**
	 * 利用zest插件，显示CFG的树形结构（包括函数调用）
	 * @param ifd
	 */
	public void showAllCFG(IFunctionDeclaration ifd){
		this.staticParse(ifd);
		//借助CDT插件，遍历函数节点ifd，生成CFG树	
		this.buildCFG(ifd, true);
		//从CFG树的条件节点中取出逻辑符号,并记录到该条件节点中                           
		List<Integer> nodePrinted = new ArrayList<Integer>();
		addSymbol(CFGBuilder.function.getStartNode(),nodePrinted);
		setStartNode(CFGBuilder.function.getStartNode());
//		setStartNode(CFGBuilder.function.getEntry());
		showCFG();
	}
	
	/**
	 * 从CFG树的条件节点中取出逻辑符号，并记录到该条件节点中
	 * @param currentNode
	 */
	private void addSymbol(CFGNode currentNode, List<Integer> nodePrinted){
		if (currentNode != null) {
			if (currentNode.getOffset() != -1 && !nodePrinted.contains(currentNode.getOffset())) {
				if (currentNode.getBinaryExpression() != null) {
					BinaryExpression be = currentNode.getBinaryExpression();
					Operator operator = be.getOp();
					if(operator == Operator.GT){
						if(currentNode.getIfChild() != null){
							currentNode.getIfChild().setOperator(">");
						}
						if(currentNode.getElseChild() != null){
							currentNode.getElseChild().setOperator("<=");
						}
					}else if(operator == Operator.GE){
						if(currentNode.getIfChild() != null){
							currentNode.getIfChild().setOperator(">=");
						}
						if(currentNode.getElseChild() != null){
							currentNode.getElseChild().setOperator("<");
						}
					}else if(operator == Operator.LT){
						if(currentNode.getIfChild() != null){
							currentNode.getIfChild().setOperator("<");
						}
						if(currentNode.getElseChild() != null){
							currentNode.getElseChild().setOperator(">=");
						}
					}else if(operator == Operator.LE){
						if(currentNode.getIfChild() != null){
							currentNode.getIfChild().setOperator("<=");
						}
						if(currentNode.getElseChild() != null){
							currentNode.getElseChild().setOperator(">");
						}
					}else if(operator == Operator.EQ){
						if(currentNode.getIfChild() != null){
							currentNode.getIfChild().setOperator("==");
						}
						if(currentNode.getElseChild() != null){
							currentNode.getElseChild().setOperator("!=");
						}
					}else if(operator == Operator.NE){
						if(currentNode.getIfChild() != null){
							currentNode.getIfChild().setOperator("!=");
						}
						if(currentNode.getElseChild() != null){
							currentNode.getElseChild().setOperator("==");
						}
					}
				}
			}
			nodePrinted.add(currentNode.getOffset());
			List<CFGNode> children = currentNode.getChildren();
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					if (!nodePrinted.contains(children.get(i).getOffset())) {
						addSymbol(children.get(i),nodePrinted);
					}
				}
			}
			addSymbol(currentNode.getIfChild(),nodePrinted);
			addSymbol(currentNode.getElseChild(),nodePrinted);
		}
	}
	
	private CFGNode getStartNode() {
		return startNode;
	}

	private void setStartNode(CFGNode startNode) {
		ShowCFG.startNode = startNode;
	}
	
	private void showCFG(){
		nodeShowed = new ArrayList<Integer>();
		shell = new Shell(d);
		g = new Graph(shell, SWT.NONE);
		graphNodes = new GraphNode[NODE_NUMBER];
		
		shell.setText("函数"+CFGBuilder.funcName+"的CFG树形结构");
		shell.setLayout(new FillLayout());
		shell.setSize(WIDTH, HEIGHT);
		
//		new GraphNode(g, SWT.NONE, "保存为PNG");
		int index = startNode.getNodeIndex();

		graphNodes[index] = new GraphNode(g, SWT.NONE, getNodeSymbol(startNode));
		if(startNode.getType()==ConstantValue.ENTRY_NODE){
			graphNodes[index].setBackgroundColor(ColorConstants.green);
		}else{
			if(startNode.isBranchNode()){
		        graphNodes[index].setBackgroundColor(ColorConstants.yellow);
			}
		}
		drawCFG(this.getStartNode(), false);
		
		TreeLayoutAlgorithm treeLayoutAlgorithm = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		Filter filter = new Filter() {
			public boolean isObjectFiltered(LayoutItem item) {
				// Get the "Connection" from the Layout Item
				// and use this connection to get the "Graph Data"
				Object object = item.getGraphData();
				if  (object instanceof GraphConnection ) {
					GraphConnection connection = (GraphConnection) object;
					if ( connection.getData() != null && connection.getData() instanceof Boolean ) {
						// If the data is false, don't filter, otherwise, filter.
						return ((Boolean)connection.getData()).booleanValue();
					}
					return false;
				}
				return false;
			}
		};
		treeLayoutAlgorithm.setFilter(filter);
		g.setLayoutAlgorithm(treeLayoutAlgorithm, true);
		g.addDisposeListener(
			new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent arg0) {
//					saveCFG(cfgPicPath);
				}
			}
		);
//		g.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				if (((Graph) e.widget).getSelection().toString().compareTo("[GraphModelNode: 保存为PNG]") == 0) {
//					saveCFG();
//				}
//			}
//		});

		shell.open();
	}
	
	private static void drawCFG(CFGNode currentNode, boolean inFunctionCall){
		if(currentNode != null){
			if(currentNode.getOffset() != -1 && (!nodeShowed.contains(currentNode.getNodeIndex())||inFunctionCall)){
				nodeShowed.add(currentNode.getNodeIndex());
				if(currentNode.getChildren() != null){
					List<CFGNode> children = currentNode.getChildren();
					if(children.size() > 0){
						for(int i=0; i<children.size(); i++){
							if(graphNodes[children.get(i).getNodeIndex()] == null){
								graphNodes[children.get(i).getNodeIndex()] = new GraphNode(g, SWT.NONE, getNodeSymbol(children.get(i)));
								if(children.get(i).getType()==ConstantValue.ENTRY_NODE||children.get(i).getType()==ConstantValue.EXIT_NODE){
									graphNodes[children.get(i).getNodeIndex()].setBackgroundColor(ColorConstants.green);
								}else{
								    if(children.get(i).isBranchNode()){
								    	graphNodes[children.get(i).getNodeIndex()].setBackgroundColor(ColorConstants.yellow);
								    }
								}
							}
							gc = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, graphNodes[currentNode.getNodeIndex()], graphNodes[children.get(i).getNodeIndex()]);
							if(currentNode.getOffset() <= children.get(i).getOffset()){
								//function call
								if(children.get(i).getType()==ConstantValue.ENTRY_NODE || currentNode.getType()==ConstantValue.EXIT_NODE){
									gc.setLineColor(ColorConstants.blue);
									gc.setLineWidth(3);
								}
								else{
									if(currentNode.isBranchNode() && currentNode.getSign()!=ConstantValue.BRANCH_IF){
									    if(i==0) gc.setText("     T");
									    else if(i==1) gc.setText("     F");
									}
									if(currentNode.getSign()==ConstantValue.BRANCH_IF)
									    if(i==0) gc.setText("     F");
								    gc.setLineColor(ColorConstants.black);
								    gc.setLineWidth(2);
								}
							}else{
								if(((currentNode.getType()==ConstantValue.STATEMENT_CALL||currentNode.getType()==ConstantValue.STATEMENT_CALL_CE)&&
										children.get(i).getType()!=ConstantValue.ENTRY_NODE) 
										|| children.get(i).getType()==ConstantValue.EXIT_NODE || currentNode.getType()==ConstantValue.ENTRY_NODE){
									gc.setLineColor(ColorConstants.black);
									gc.setLineWidth(2);
								}
								else if(children.get(i).getType()==ConstantValue.ENTRY_NODE || currentNode.getType()==ConstantValue.EXIT_NODE){
									gc.setLineColor(ColorConstants.blue);
									gc.setLineWidth(3);
								}
								else{
								    gc.setLineColor(ColorConstants.red);
								    gc.setLineWidth(2);
								    gc.setData(Boolean.TRUE);
								}
							}
						}
					}
				}
				if(currentNode.getIfChild() != null){
					if(graphNodes[currentNode.getIfChild().getNodeIndex()] == null){
						graphNodes[currentNode.getIfChild().getNodeIndex()] = new GraphNode(g, SWT.NONE, getNodeSymbol(currentNode.getIfChild()));
						if(currentNode.getIfChild().getType()==ConstantValue.ENTRY_NODE||currentNode.getIfChild().getType()==ConstantValue.EXIT_NODE){
							graphNodes[currentNode.getIfChild().getNodeIndex()].setBackgroundColor(ColorConstants.green);
						}else{
						    if(currentNode.getIfChild().isBranchNode()){
					    	    graphNodes[currentNode.getIfChild().getNodeIndex()].setBackgroundColor(ColorConstants.yellow);
					        }
						}
					}
					if(currentNode.getOffset() <= currentNode.getIfChild().getOffset()){
						gc = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, graphNodes[currentNode.getNodeIndex()], graphNodes[currentNode.getIfChild().getNodeIndex()]);
						gc.setLineColor(ColorConstants.black);
						gc.setLineWidth(2);
						gc.setText("     T");
					}else{
						gc = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, graphNodes[currentNode.getNodeIndex()], graphNodes[currentNode.getIfChild().getNodeIndex()]);
						gc.setLineColor(ColorConstants.red);
						gc.setLineWidth(2);
						gc.setData(Boolean.TRUE);
					}
				}
				if(currentNode.getElseChild() != null){
					if(graphNodes[currentNode.getElseChild().getNodeIndex()] == null){
						graphNodes[currentNode.getElseChild().getNodeIndex()] = new GraphNode(g, SWT.NONE, getNodeSymbol(currentNode.getElseChild()));
						if(currentNode.getElseChild().getType()==ConstantValue.ENTRY_NODE||currentNode.getElseChild().getType()==ConstantValue.EXIT_NODE){
							graphNodes[currentNode.getElseChild().getNodeIndex()].setBackgroundColor(ColorConstants.green);
						}else{
						    if(currentNode.getElseChild().isBranchNode()){
					    	    graphNodes[currentNode.getElseChild().getNodeIndex()].setBackgroundColor(ColorConstants.yellow);
					        }
						}
					}
					if(currentNode.getOffset() <= currentNode.getElseChild().getOffset()){
						gc = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, graphNodes[currentNode.getNodeIndex()], graphNodes[currentNode.getElseChild().getNodeIndex()]);
						gc.setLineColor(ColorConstants.black);
						gc.setLineWidth(2);
						gc.setText("     F");
					}else{
						gc = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, graphNodes[currentNode.getNodeIndex()], graphNodes[currentNode.getElseChild().getNodeIndex()]);
						gc.setLineColor(ColorConstants.red);
						gc.setLineWidth(2);
						gc.setData(Boolean.TRUE);
					}
				}
			}
			
			if(currentNode.getChildren() != null){
				List<CFGNode> children = currentNode.getChildren();
				if(children.size() > 0){
					for(int i=0; i<children.size(); i++){
						if(children.get(i).getType()==ConstantValue.ENTRY_NODE && !CFGBuilder.shouldBuildCCFG)
							inFunctionCall = true;
						else if(children.get(i).getType()==ConstantValue.EXIT_NODE){
							inFunctionCall = false;
						}
						if(!nodeShowed.contains(children.get(i).getNodeIndex()) || inFunctionCall){
							drawCFG(children.get(i), inFunctionCall);
						}
					}
				}
			}
			if(currentNode.getIfChild() != null && (!nodeShowed.contains(currentNode.getIfChild().getNodeIndex())||inFunctionCall)){
				drawCFG(currentNode.getIfChild(), inFunctionCall);
			}
			if(currentNode.getElseChild() != null && (!nodeShowed.contains(currentNode.getElseChild().getNodeIndex())||inFunctionCall)){
				drawCFG(currentNode.getElseChild(), inFunctionCall);
			}
		}
	}
	
//	private static void saveCFG(String cfgPicPath) {
//		int width = g.getBounds().width;
//		int height = g.getBounds().height;
//		IFigure figure = g.getContents();		
//		Image img = new Image(null, width, height);
//		GC gc = new GC(img);
//		Graphics g = new SWTGraphics(gc);
//		g.translate(figure.getBounds().getLocation());
//		figure.paint(g);
//		g.dispose();
//		gc.dispose();
//		ImageLoader imgLoader = new ImageLoader();
//		imgLoader.data = new ImageData[] { img.getImageData() };
//		imgLoader.save(cfgPicPath, SWT.IMAGE_PNG);
//	}
	
	private static String getNodeSymbol(CFGNode node){
//		return node.getNodeId();
		String nodeSymbol = "";
		int sign = node.getSign();
		switch(sign){
			case 0: nodeSymbol = node.getNodeNumber()+": "+node.getOperator(); break;
			case 1:	nodeSymbol = node.getNodeNumber()+": "+node.getOperator(); break;
			case 2: nodeSymbol = node.getNodeNumber()+": for:"+node.getBinaryExpression().toString(); break;
			case 3: nodeSymbol = node.getNodeNumber()+": if:"+node.getBinaryExpression().toString(); break;
			case 5: nodeSymbol = node.getNodeNumber()+": while:"+node.getBinaryExpression().toString(); break;
			case 6: nodeSymbol = node.getNodeNumber()+": do-while:"+node.getBinaryExpression().toString(); break;
//			case 2: nodeSymbol = node.getNodeNumber()+": for"; break;
//			case 3: nodeSymbol = node.getNodeNumber()+": if"; break;
//			case 5: nodeSymbol = node.getNodeNumber()+": while"; break;
//			case 6: nodeSymbol = node.getNodeNumber()+": do-while"; break;
			case 4: nodeSymbol = node.getNodeNumber()+": return"; break;
			case 7: nodeSymbol = node.getNodeNumber()+": break"; break;
			case 8: nodeSymbol = node.getNodeNumber()+": continue"; break;
			case 9: nodeSymbol = node.getNodeId(); break;
			case 10: nodeSymbol = node.getNodeId(); break;
			case -1: nodeSymbol = node.getNodeNumber()+""; break;
			case -2: nodeSymbol = "entry("+node.getFuncName()+")"; break;
			case -3: nodeSymbol = "exit("+node.getFuncName()+")"; break;
			default: nodeSymbol="";
		}
		
		if(!node.getFuncName().equals(CFGBuilder.function.getFunctionId()) && 
				node.getType()!=ConstantValue.ENTRY_NODE && node.getType()!=ConstantValue.EXIT_NODE)
		    return "("+node.getFuncName()+")"+nodeSymbol;
		else
		    return nodeSymbol;
	}
}
