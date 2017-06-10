package cn.nju.seg.atg.visitor;

import java.util.*;

import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;

import cn.nju.seg.atg.model.Condition;
import cn.nju.seg.atg.model.Constraint;
import cn.nju.seg.atg.model.constraint.BinaryExpressionUtil;
import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.parse.ConditionCoverage;
import cn.nju.seg.atg.util.ConstantValue;

/**
 * represent a function
 * 
 * @author zy
 */
public class Function {
  private int offset;
  private String funcName;
  private String funcType;
  private CFGNode startNode = null;
  private List<CFGNode> endNodes;
  private String functionId;
  private int fid;
  private IASTStandardFunctionDeclarator declarator;
  private List<Call> functionCalls = null;
  private boolean existBranchNode = false;
  private boolean existFunctionCall = false;
  private boolean isBuilt = false;
  private CFGNode entry = null, exit = null;

  private int lastNodeNum = -1;
  // 按照节点编号从小到大顺序，记录每个节点的offset
  private int[] nodeOffset;
  private CFGNode[] nodeArray;
  private Map<Integer, List<Call>> branchWithCall = null;

  /**
   * 带参构造函数
   * 
   * @param name
   *          函数名
   * @param type
   *          函数类型（返回值类型＋参数类型）
   * @param offset
   *          在原文件中的偏移量
   * @param declarator
   *          函数声明
   */
  public Function(String name, String type, int offset, IASTStandardFunctionDeclarator declarator) {
    this.funcName = name;
    this.funcType = type;
    this.offset = offset;
    this.declarator = declarator;
    this.endNodes = new ArrayList<CFGNode>();
    this.functionId = name;
  }

  /**
   * 构建该函数的CFG,不考虑函数调用
   */
  public void simpleBuild() {
    buildCFG(false);
  }

  /**
   * 构建该函数的CFG,为连接调用函数的CFG做好准备
   */
  public void complexBuild() {
    buildCFG(true);
  }

  private void build() {
    this.getBranchWithCall();
    List<Integer> nodeTraversed = new ArrayList<Integer>();
    this.traversal(this.startNode, nodeTraversed);
    nodeTraversed.clear();
    this.traversal1(this.startNode, nodeTraversed);
    this.isBuilt = true;
  }

  private void buildCFG(boolean visitFunCalls) {
    CFGBuilder.initial();
    FunctionVisitor expressionVisitor = new FunctionVisitor(this.funcName);
    expressionVisitor.build(this.declarator);
    this.startNode = expressionVisitor.getStartNode();

    this.lastNodeNum = CFGBuilder.nodeNumber;
    this.nodeOffset = new int[this.lastNodeNum];
    this.nodeArray = new CFGNode[this.lastNodeNum];

    List<Integer> nodeTraversed = new ArrayList<Integer>();
    this.traversal0(this.startNode, nodeTraversed, visitFunCalls);
    if (!visitFunCalls) {
      nodeTraversed.clear();
      this.traversal1(this.startNode, nodeTraversed);
      this.complementExitNode();
    }
  }

  /**
   * 构建CCFG(combined control flow graph)
   * <p>
   * 删除每个函数调用出口节点的后继节点
   * <p>
   * 要么进入函数，要么跳过函数
   * <p>
   * 若函数末尾节点类型为return,则删除exit节点,为程序插桩做准备
   * 
   * @param function
   */
  public void buildCCFG() {
    buildCCFG(this);
    // 删除函数出口节点的后继
    for (Function f : CFGBuilder.allFunctions.values()) {
      if (f.getExit() != null) {
        f.getExit().deleteChild();
        boolean existReturnNode = true;
        for (CFGNode node : f.getExit().getParents()) {
          if (node.getSign() != ConstantValue.STATEMENT_RETURN) {
            existReturnNode = false;
            break;
          }
        }
        if (existReturnNode) {
          for (CFGNode node : f.getExit().getParents()) {
            node.deleteChild();
          }
          f.setExit(null);
        }
      }
    }
  }

  private void buildCCFG(Function function) {
    if (function.getFunctionCalls() != null) {
      for (Call call : function.getFunctionCalls()) {
        if (!call.isExistControlFlowJump())
          continue;
        Function f = CFGBuilder.allFunctions.get(call.getFunctionId());
        call.skipCall(f);

        buildCCFG(f);
      }
    }
  }

  /**
   * 连接自身CFG与其所涉及的函数调用的CFG
   */
  public void connectCFG() {
    if (this.equals(CFGBuilder.function))
      CFGBuilder.nodeIndex++;
    this.build();

    this.complementExitNode();
    connectCFG(this);
  }

  /**
   * 被测主函数最后节点不是return时,添加exit节点作为统一出口节点
   */
  private void complementExitNode() {
    for (CFGNode node : this.endNodes)
      if (node.getSign() == ConstantValue.STATEMENT_RETURN)
        return;

    CFGNode exitNode = new CFGNode();
    exitNode.setSign(-3);
    exitNode.setNodeIndex(CFGBuilder.nodeIndex++);
    exitNode.setNodeNumber(this.lastNodeNum + 1);
    exitNode.setFuncName(this.getFunctionId());
    exitNode.setOffset(this.nodeArray[this.lastNodeNum - 1].getOffset() + 1);
    exitNode.setNodeId("exit@" + this.funcName);

    for (CFGNode endNode : this.endNodes) {
      endNode.addChild(exitNode);
      exitNode.addParent(endNode);
    }
  }

  /**
   * 添加入口与出口节点
   */
  private void addEntryAndExitNode() {
    CFGNode entryNode = new CFGNode();
    entryNode.setSign(-2);
    if (this.equals(CFGBuilder.function))
      entryNode.setNodeIndex(0);
    else
      entryNode.setNodeIndex(CFGBuilder.nodeIndex++);
    entryNode.setFuncName(this.getFunctionId());
    entryNode.setOffset(this.offset + 1);
    entryNode.setNodeId("entry@" + this.funcName);

    CFGNode exitNode = new CFGNode();
    exitNode.setSign(-3);
    exitNode.setNodeIndex(CFGBuilder.nodeIndex++);
    exitNode.setFuncName(this.getFunctionId());
    exitNode.setOffset(this.nodeArray[this.lastNodeNum - 1].getOffset() + 1);
    exitNode.setNodeId("exit@" + this.funcName);

    entryNode.addChild(this.startNode);
    this.startNode.addParent(entryNode);
    for (CFGNode endNode : this.endNodes) {
      endNode.addChild(exitNode);
      exitNode.addParent(endNode);
    }
    this.setEntry(entryNode);
    this.setExit(exitNode);
  }

  /**
   * 递归遍历需要访问的函数调用，分别构建CFG，将它们连入同一个CFG中
   * 
   * @param function
   */
  @SuppressWarnings("null")
  private void connectCFG(Function function) {
    if (function != null && function.getFunctionCalls() != null) {
      for (Call call : function.getFunctionCalls()) {
        if (!call.isExistControlFlowJump())
          continue;
        Function f = CFGBuilder.allFunctions.get(call.getFunctionId());
        // 构建该函数的CFG
        if (!f.isBuilt()) {
          f.build();
          // 添加函数的入口与出口节点
          f.addEntryAndExitNode();
        }
        CFGNode callNode = new CFGNode();
        callNode.setSign(ConstantValue.STATEMENT_CALL);
        callNode.setNodeIndex(CFGBuilder.nodeIndex++);
        ConditionCoverage.allNodes.put(callNode.getNodeIndex(), callNode);
        callNode.setFuncName(function.getFunctionId());
        callNode.setOffset(call.getOffset() + 1);
        callNode.setNodeId("call@" + function.getFuncName());
        callNode.setCall(call);
        call.setCallNode(callNode);
        // 获取该函数被调用时所在的节点
        CFGNode fcNode = function.findFunctionCallIndex(call);
        // 若函数调用存在于分支节点中，要根据复合约束的具体情况，分情况处理
        // 将函数调用存在的简单约束提取处出来，封装为一个if-else节点
        if (fcNode.isBranchNode()) {
          // 简单约束
          if (BinaryExpressionUtil.isAtomicConstraint(fcNode.getBinaryExpression())) {
            fcNode.insertPriorNode(callNode, f.getEntry(), f.getExit());
            call.setNextNode(fcNode);
            if (fcNode.isStartNode()) {
              // 将函数调用的入口节点设为当前函数的开始节点
              function.setStartNode(callNode);
            }
          }// 复合约束
          else {
            CFGNode splitNode = fcNode.findSplitNode(call.getOffset());
            if (CFGBuilder.splitCompositeNode) {
              splitNode.insertPriorNode(callNode, f.getEntry(), f.getExit());
              call.setNextNode(splitNode);
            } else {
              // 生成关于目标点的相关路径时，不分离复合约束内部控制流
              if (splitNode.equals(fcNode.getFirstSplitNode())) {
                fcNode.insertPriorNode(callNode, f.getEntry(), f.getExit());
                call.setNextNode(fcNode);
              } else {
                fcNode.addChild(callNode);
                callNode.addParent(fcNode);
                f.getEntry().addParent(callNode);
                callNode.addChild(f.getEntry());
                f.getExit().addChild(fcNode);
                fcNode.addParent(f.getExit());
                call.setNextNode(fcNode);

                callNode.setSign(ConstantValue.STATEMENT_CALL_CE);
                fcNode.putCompoundExpr(callNode.getNodeIndex(), BinaryExpressionUtil.splitCompoundExpr(splitNode.getBinaryExpression()));
              }
            }
          }
        }// 函数调用存在于普通节点中
         // eg: node1(a function call "f")->node2
         // node1->enter_f->(path in f)->exit_f->node2
         // 1.需要考虑多个函数调用同时存在于一个节点的情况
         // 2.顺序节点只可能存在一个后继节点，但顺序节点的子节点不一定只存在一个父节点
        else {
          if (fcNode.getChildren() == null) {
            fcNode.addChild(callNode);
            callNode.addParent(fcNode);
          } else {
            CFGNode nextNode = fcNode.getChildren().get(0);
            if (nextNode.getType() == ConstantValue.STATEMENT_CALL) {
              CFGNode preCallNode = null;
              while (nextNode.getType() == ConstantValue.STATEMENT_CALL) {
                preCallNode = nextNode;
                nextNode = nextNode.getCall().getNextNode();
              }
              nextNode.insertPriorNode(callNode, f.getEntry(), f.getExit());
              preCallNode.getCall().setNextNode(callNode);
              call.setNextNode(nextNode);
            } else {
              fcNode.insertSuccedNode(callNode, f.getEntry(), f.getExit(), nextNode);
              call.setNextNode(nextNode);
            }
          }
        }
        // 递归执行
        connectCFG(f);
      }
    }
  }

  private void getBranchWithCall() {
    this.branchWithCall = new HashMap<Integer, List<Call>>();
    if (this.getFunctionCalls() != null)
      for (Call call : this.getFunctionCalls()) {
        CFGNode fcNode = this.findFunctionCallIndex(call);
        int nodeNumber = fcNode.getNodeNumber();
        if (!this.branchWithCall.containsKey(nodeNumber)) {
          List<Call> calls = new ArrayList<Call>();
          calls.add(call);
          this.branchWithCall.put(nodeNumber, calls);
        } else {
          this.branchWithCall.get(nodeNumber).add(call);
        }
      }
  }

  /**
   * 根据函数入口节点，找到出口节点
   * 
   * @param enterNode
   * @return exitNode
   */
  public static CFGNode findExitNode(CFGNode enterNode) {
    CFGNode node = enterNode.getChildren().get(0);
    while (node != null) {
      if (node.getChildren() != null) {
        boolean find = false;
        for (CFGNode child : node.getChildren()) {
          if (child.getType() == ConstantValue.EXIT_NODE) {
            if (child.getNodeNumber() == enterNode.getNodeNumber())
              return child;
            else
              continue;
          }
          if (child.getNodeIndex() > node.getNodeIndex()) {
            node = child;
            find = true;
            break;
          }
        }
        if (find)
          continue;
      }
      if (node.getIfChild() != null) {
        if (node.getIfChild().getNodeIndex() > node.getNodeIndex()) {
          node = node.getIfChild();
          continue;
        }
      }
      if (node.getElseChild() != null) {
        if (node.getElseChild().getNodeIndex() > node.getNodeIndex()) {
          node = node.getElseChild();
          continue;
        }
      }
    }
    return null;
  }

  /**
   * 找到函数调用所在的CFG节点
   * <p>
   * 函数调用处于顺序节点或分支节点皆可
   * 
   * @return CFG node
   */
  private CFGNode findFunctionCallIndex(Call call) {
    int offset = call.getOffset();
    if (offset <= this.nodeOffset[0])
      return this.nodeArray[0];
    for (int i = 1; i < this.lastNodeNum; i++) {
      if (offset < this.nodeOffset[i])
        return this.nodeArray[i - 1];
    }
    return this.nodeArray[this.lastNodeNum - 1];
  }

  /**
   * 找到函数调用所在的CFG节点的下一个节点
   * 
   * @return CFG node
   */
  public CFGNode findFunctionCallNext(Function f) {
    int offset = f.getOffset();
    for (int i = 0; i < this.lastNodeNum; i++) {
      if (offset < this.nodeOffset[i])
        return this.nodeArray[i];
    }
    return null;
  }

  /**
   * 递归函数1，遍历函数的每个节点
   * 1.收集节点信息
   * <p>
   * 2.标记每个节点是否为分支节点
   * 
   * @param node
   */
  private void traversal0(CFGNode node, List<Integer> nodeTraversed, boolean visitFunCalls) {
    if (nodeTraversed.contains(node.getNodeNumber())) {
      return;
    } else {
      int index = node.getNodeNumber() - 1;
      if (index < 0) {
        System.out.println("xx");
      }
      this.nodeOffset[index] = node.getOffset();
      this.nodeArray[index] = node;
      node.setFuncName(this.functionId);
      if (!visitFunCalls) {
        node.setNodeIndex(CFGBuilder.nodeIndex++);
        ConditionCoverage.allNodes.put(node.getNodeIndex(), node);
      }
      nodeTraversed.add(node.getNodeNumber());
      if (node.getType() == ConstantValue.BRANCH_DO || node.getType() == ConstantValue.BRANCH_FOR
          || node.getType() == ConstantValue.BRANCH_IF || node.getType() == ConstantValue.BRANCH_WHILE) {
        node.setBranchNode(true);
        this.existBranchNode = true;
      }
    }
    if (node.getChildren() == null && node.getIfChild() == null && node.getElseChild() == null) {
      return;
    } else {
      if (node.getChildren() != null) {
        for (CFGNode child : node.getChildren()) {
          if (child.getNodeNumber() > node.getNodeNumber()) {
            traversal0(child, nodeTraversed, visitFunCalls);
          }
        }
      }
      if (node.getIfChild() != null) {
        if (node.getIfChild().getNodeNumber() > node.getNodeNumber()) {
          traversal0(node.getIfChild(), nodeTraversed, visitFunCalls);
        }
      }
      if (node.getElseChild() != null) {
        if (node.getElseChild().getNodeNumber() > node.getNodeNumber()) {
          traversal0(node.getElseChild(), nodeTraversed, visitFunCalls);
        }
      }
    }
  }

  /**
   * 递归函数2，遍历函数的每个节点
   * <p>
   * 收集程序结束节点序列,注意到如果因条件语句中存在函数调用而对复合语句进行分离,此时结束节点可能会有所变动
   * 
   * @param node
   */
  private void traversal1(CFGNode node, List<Integer> nodeTraversed) {
    if (nodeTraversed.contains(node.getNodeIndex())) {
      return;
    } else {
      nodeTraversed.add(node.getNodeIndex());
    }
    if (node.getChildren() == null && node.getIfChild() == null && node.getElseChild() == null) {
      this.endNodes.add(node);
    } else {
      if (node.isBranchNode() && node.getElseChild() == null) {
        if (node.getChildren() == null || (node.getChildren().size() == 1 && node.getIfChild() == null))
          this.endNodes.add(node);
      }
      if (node.getChildren() != null) {
        for (CFGNode child : node.getChildren()) {
          if (child.getNodeIndex() > node.getNodeIndex()) {
            traversal1(child, nodeTraversed);
          }
        }
      }
      if (node.getIfChild() != null) {
        if (node.getIfChild().getNodeIndex() > node.getNodeIndex()) {
          traversal1(node.getIfChild(), nodeTraversed);
        }
      }
      if (node.getElseChild() != null) {
        if (node.getElseChild().getNodeIndex() > node.getNodeIndex()) {
          traversal1(node.getElseChild(), nodeTraversed);
        }
      }
    }
  }

  /**
   * 递归函数，遍历函数的每个节点
   * <p>
   * 复合约束存在函数调用时,需要进行分离
   * 
   * @param node
   */
  private void traversal(CFGNode node, List<Integer> nodeTraversed) {
    if (nodeTraversed.contains(node.getNodeNumber())) {
      return;
    } else {
      node.setNodeIndex(CFGBuilder.nodeIndex++);
      ConditionCoverage.allNodes.put(node.getNodeIndex(), node);
      node.setNodeId("node" + node.getNodeNumber() + "@" + this.funcName);
      if (node.getType() == ConstantValue.BRANCH_DO || node.getType() == ConstantValue.BRANCH_FOR
          || node.getType() == ConstantValue.BRANCH_IF || node.getType() == ConstantValue.BRANCH_WHILE) {
        // 分离复合约束
        // attention here! 如果分支内存在的函数调用内不存在(约束和其他函数调用),则不进行分割!!!
        if (node.isCompositeBranchNode()) {
          node.splitBranchNode(this.branchWithCall.get(node.getNodeNumber()));
          // if(node.equals(this.startNode))
          // this.setStartNode(node.getFirstSplitNode());
        } else {
          String constraint = Constraint.removeGap(node.getBinaryExpression().toString());
          String info = "[constraint: " + constraint +
              ", id: " + node.getNodeId() + ", function: " + node.getFuncName() + "]";
          node.getBinaryExpression().setId("expression@" + node.getNodeIndex());
          ConditionCoverage.conditions.add(new Condition(info, node.getNodeId(), node.getNodeIndex(), node.getNodeIndex(), node.getBinaryExpression()));
        }
      }
      nodeTraversed.add(node.getNodeNumber());
    }
    if (node.getChildren() == null && node.getIfChild() == null && node.getElseChild() == null) {
      return;
    } else {
      if (node.getChildren() != null) {
        for (CFGNode child : node.getChildren()) {
          if (child.getNodeNumber() > node.getNodeNumber()) {
            traversal(child, nodeTraversed);
          }
        }
      }
      if (node.getIfChild() != null) {
        if (node.getIfChild().getNodeNumber() > node.getNodeNumber()) {
          traversal(node.getIfChild(), nodeTraversed);
        }
      }
      if (node.getElseChild() != null) {
        if (node.getElseChild().getNodeNumber() > node.getNodeNumber()) {
          traversal(node.getElseChild(), nodeTraversed);
        }
      }
    }
  }

  /**
   * 打印函数调用链
   */
  public void printCallChain() {
    if (this.functionCalls == null)
      return;
    System.out.println("function call chains...");
    String chain = "";
    printCallChain(this, chain);
  }

  private void printCallChain(Function f, String chain) {
    if (f.getFunctionCalls() == null) {
      if (chain.equals(""))
        System.out.println(f.getFuncName());
      else
        System.out.println(chain + "->" + f.getFuncName());
    } else {
      for (Call call : f.getFunctionCalls()) {
        Function fc = CFGBuilder.allFunctions.get(call.getFunctionId());
        if (chain.equals(""))
          printCallChain(fc, f.getFuncName());
        else
          printCallChain(fc, chain + "->" + f.getFuncName());
      }
    }
  }

  public boolean equals(Function f) {
    return this.functionId.equals(f.getFunctionId());
  }

  /**
   * 添加函数中存在的函数调用
   * 
   * @param function
   */
  public void addFunctionCall(String fId, int offset) {
    Call call = new Call(fId, offset);
    if (this.functionCalls == null) {
      this.functionCalls = new ArrayList<Call>();
      this.functionCalls.add(call);
      this.existFunctionCall = true;
    } else {
      this.functionCalls.add(call);
    }
  }

  public List<Call> getFunctionCalls() {
    return this.functionCalls;
  }

  public int getOffset() {
    return this.offset;
  }

  public String getFuncName() {
    return this.funcName;
  }

  public String getFuncType() {
    return this.funcType;
  }

  public CFGNode getStartNode() {
    return this.startNode;
  }

  public IASTStandardFunctionDeclarator getDeclarator() {
    return this.declarator;
  }

  public void setStartNode(CFGNode startNode) {
    this.startNode = startNode;
  }

  public List<CFGNode> getEndNodes() {
    return this.endNodes;
  }

  public String getFunctionId() {
    return this.functionId;
  }

  public boolean isExistBranchNode() {
    return this.existBranchNode;
  }

  public boolean isExistFunctionCall() {
    return this.existFunctionCall;
  }

  public boolean isBuilt() {
    return this.isBuilt;
  }

  public CFGNode getEntry() {
    return this.entry;
  }

  public void setEntry(CFGNode entry) {
    this.entry = entry;
  }

  public CFGNode getExit() {
    return this.exit;
  }

  public void setExit(CFGNode exit) {
    this.exit = exit;
  }

  public int getFid() {
    return this.fid;
  }

  public void setFid(int fid) {
    this.fid = fid;
  }
}
