package cn.nju.seg.atg.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;

import com.google.common.base.Objects;

import cn.nju.seg.atg.model.Condition;
import cn.nju.seg.atg.model.constraint.BinaryExpression;
import cn.nju.seg.atg.model.constraint.BinaryExpressionUtil;
import cn.nju.seg.atg.model.constraint.Operator;
import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.parse.ConditionCoverage;
import cn.nju.seg.atg.util.ConstantValue;

/**
 * represent a node in CFG
 * 取消复合节点内部的控制流连接
 * 
 * @since 0.1 Add {@link #hashCode()}.
 * @author zy
 * @author Zhang Yifan
 */
public class CFGNode {
  private List<CFGNode> parents = null;
  private List<CFGNode> children = null;
  private CFGNode ifChild = null;
  private CFGNode elseChild = null;
  private BinaryExpression be = null;
  private int offset = -1;
  private int sign = -1; // 0:if分支节点; 1：else分支节点; -1:顺序执行节点; 2：for节点;
  // 3:(if-else)条件节点; 4:return节点; 5:while节点; 6:do-while节点;
  // 7:break节点; 8:continue节点; -2:函数开始节点; －3：函数结束节点;
  // 9:顺序语句中的call节点; 10:复合表达式中的call节点;
  private String nodeId;
  private int nodeNumber = -1;
  private String operator = "";
  private String funcName = null;
  private boolean isBranchNode = false;
  private boolean isSplitNode = false;
  // 在总的CFG树中的编号
  private int nodeIndex = -1;
  // 复合约束分离为简单约束的数量(下标)
  private int splitIndex = 0;
  // 若为函数调用节点，则记录其对应的函数调用
  private Call call = null;
  private Map<Integer, BinaryExpression> splitCompoundExprs;
  private List<SplitBranchNode> partitions = null;

  /**
   * 寻找目标简单子约束所在的分离节点
   * 
   * @param offset
   * @return
   */
  public CFGNode findSplitNode(int offset) {
    for (int i = 0; i < this.splitIndex; i++) {
      if (offset < this.partitions.get(i).getNode().getOffset())
        return this.partitions.get(i - 1).getNode();
    }
    return this.partitions.get(this.splitIndex - 1).getNode();
  }

  public CFGNode getFirstSplitNode() {
    return this.partitions.get(0).getNode();
  }

  private int getSplitNodeIndex(int offset) {
    for (int i = 0; i < this.splitIndex; i++) {
      if (offset < this.partitions.get(i).getNode().getOffset())
        return i - 1;
    }
    return this.splitIndex - 1;
  }

  /**
   * 分离复合约束的每个简单子约束
   */
  public void splitBranchNode(List<Call> calls) {
    this.partitions = new ArrayList<SplitBranchNode>();
    splitBranchNode(this.be, null, new ArrayList<Operator>());
    if (calls == null || !CFGBuilder.splitCompositeNode)
      return;
    for (Call call : calls) {
      // 分离复合约束的条件:1.复合约束中存在一个不位于第一个简单子约束中的函数调用;2.满足1的函数调用中存在控制流变化
      if (getSplitNodeIndex(call.getOffset()) != 0 && call.isExistControlFlowJump()) {
        insertSplitNode();
        break;
      }
    }
  }

  private void insertSplitNode() {
    // 插入第一个节点，第一个节点的类型与原节点保持一致
    SplitBranchNode firstSnode = this.partitions.get(0);
    CFGNode firstNode = firstSnode.getNode();
    if (this.parents != null) {
      for (CFGNode parent : this.parents) {
        if (!parent.replaceIfChild(this.offset, firstNode)) {
          if (!parent.replaceElseChild(this.offset, firstNode)) {
            parent.replaceChild(this.offset, firstNode);
          }
        }
        firstNode.addParent(parent);
      }
      // this.deleteParent();
    }
    int Tindex = firstSnode.getTrueNextNodeIndex();
    int Findex = firstSnode.getFalseNextNodeIndex();
    if (this.sign == ConstantValue.BRANCH_IF) {
      if (Tindex == -1) {
        firstNode.setIfChild(this.ifChild);
        this.ifChild.replaceParent(this.offset, firstNode);
      } else {
        firstNode.setIfChild(this.partitions.get(Tindex - 1).getNode());
        this.partitions.get(Tindex - 1).getNode().addParent(firstNode);
      }
      if (Findex == -1) {
        if (this.elseChild == null) {
          if (this.children != null) {
            firstNode.addChild(this.children.get(0));
            this.children.get(0).replaceParent(this.offset, firstNode);
          }
        } else {
          firstNode.setElseChild(this.elseChild);
          this.elseChild.replaceParent(this.offset, firstNode);
        }
      } else {
        firstNode.setElseChild(this.partitions.get(Findex - 1).getNode());
        this.partitions.get(Findex - 1).getNode().addParent(firstNode);
      }
    } else {
      if (Tindex == -1) {
        firstNode.addChild(this.children.get(0));
        this.children.get(0).replaceParent(this.offset, firstNode);
      } else {
        firstNode.addChild(this.partitions.get(Tindex - 1).getNode());
        this.partitions.get(Tindex - 1).getNode().addParent(firstNode);
      }
      if (Findex == -1) {
        if (this.children.size() > 1) {
          firstNode.addChild(this.children.get(1));
          this.children.get(1).replaceParent(this.offset, firstNode);
        }
      } else {
        firstNode.addChild(this.partitions.get(Findex - 1).getNode());
        this.partitions.get(Findex - 1).getNode().addParent(firstNode);
      }
    }
    // 依次加入各个分离出来的简单约束，作为if-else节点加入
    for (int i = 1; i < this.partitions.size(); i++) {
      insertSplitNode(this.partitions.get(i));
    }
  }

  private void insertSplitNode(SplitBranchNode splitBranchNode) {
    int Tindex = splitBranchNode.getTrueNextNodeIndex();
    int Findex = splitBranchNode.getFalseNextNodeIndex();
    CFGNode node = splitBranchNode.getNode();
    if (Tindex == -1) {
      if (this.sign == ConstantValue.BRANCH_IF) {
        node.setIfChild(this.ifChild);
        this.ifChild.addParent(node);
      } else {
        node.setIfChild(this.children.get(0));
        this.children.get(0).addParent(node);
      }
    } else {
      node.setIfChild(this.partitions.get(Tindex - 1).getNode());
      this.partitions.get(Tindex - 1).getNode().addParent(node);
    }
    if (Findex == -1) {
      if (this.sign == ConstantValue.BRANCH_IF) {
        if (this.elseChild == null) {
          if (this.children != null) {
            node.addChild(this.children.get(0));
            this.children.get(0).addParent(node);
          }
        } else {
          node.setElseChild(this.elseChild);
          this.elseChild.addParent(node);
        }
      } else {
        if (this.children.size() > 1) {
          node.setElseChild(this.children.get(1));
          this.children.get(1).addParent(node);
        }
      }
    } else {
      node.setElseChild(this.partitions.get(Findex - 1).getNode());
      this.partitions.get(Findex - 1).getNode().addParent(node);
    }
  }

  /**
   * 递归函数，分割复合约束
   * 
   * @param expression
   * @param innerOp
   *          子约束相邻的逻辑连接符
   * @param outerOp
   *          子约束的母约束相邻的逻辑连接符
   */
  private void splitBranchNode(BinaryExpression expression, Operator innerOp, List<Operator> outerOps) {
    if (BinaryExpressionUtil.isAtomicConstraint(expression)) {
      this.splitIndex++;
      CFGNode node = new CFGNode();
      node.setBinaryExpression(expression);
      node.setOffset(expression.getOffset());
      int bSign = this.splitIndex == 1 ? this.sign : 3;
      node.setSign(bSign);
      node.setNodeNumber(this.nodeNumber);
      node.setNodeIndex(CFGBuilder.nodeIndex++);
      expression.setId("expression@" + node.getNodeIndex());
      // ConditionCoverage.allNodes.put(node.getNodeIndex(), node);
      node.setFuncName(this.funcName);
      node.setBranchNode(true);
      node.setNodeId("node" + this.nodeNumber + "@" + this.funcName);
      node.setSplitIndex(this.splitIndex);
      node.setSplitNode(true);
      SplitBranchNode sNode = new SplitBranchNode(node, innerOp, outerOps);
      this.partitions.add(sNode);
      String constraint = expression.toString();
      String info = "[constraint: " + constraint +
          ", id: " + node.getNodeId() + ", function: " + node.getFuncName() + "]";
      ConditionCoverage.conditions.add(new Condition(info, this.getNodeId(), this.getNodeIndex(), node.getNodeIndex(), expression));
    } else {
      Operator tempOperator = expression.getOp();
      List<Operator> copy = copyList(outerOps);
      copy.add(innerOp);
      splitBranchNode((BinaryExpression) expression.getOperand1(), tempOperator, copy);
      splitBranchNode((BinaryExpression) expression.getOperand2(), innerOp, copyList(outerOps));
    }
  }

  private static List<Operator> copyList(List<Operator> list) {
    if (list.size() == 0)
      return list;
    List<Operator> copy = new ArrayList<Operator>();
    for (int i = 0; i < list.size(); i++) {
      copy.add(list.get(i));
    }
    return copy;
  }

  public boolean isCompositeBranchNode() {
    return !BinaryExpressionUtil.isAtomicConstraint(this.be);
  }

  /**
   * 在分支节点前插入函数调用
   * 
   * @param callNode,
   *          enterNode, exitNode
   */
  public void insertPriorNode(CFGNode call, CFGNode entry, CFGNode exit) {
    if (this.parents != null) {
      for (CFGNode parent : this.parents) {
        if (!parent.replaceIfChild(this.offset, call)) {
          if (!parent.replaceElseChild(this.offset, call)) {
            parent.replaceChild(this.offset, call);
          }
        }
        call.addParent(parent);
        if (parent.getSign() == ConstantValue.EXIT_NODE) {
          Function f = CFGBuilder.allFunctions.get(parent.getFuncName());
          CFGNode preEntry = f.getEntry();
          CFGNode preCallNode = preEntry.getParents().get(preEntry.getParents().size() - 1);
          preCallNode.getCall().setNextNode(call);
        }
      }
      this.deleteParent();
    }
    call.addChild(entry);
    entry.addParent(call);
    this.addParent(exit);
    exit.addChild(this);
  }

  /**
   * 在分支节点后插入函数调用
   * <p>
   * (此时该节点为顺序节点)
   * 
   * @param callNode,
   *          enterNode, exitNode, nextNode
   */
  public void insertSuccedNode(CFGNode call, CFGNode entry, CFGNode exit, CFGNode nextNode) {
    this.replaceChild(nextNode.getOffset(), call);
    call.addParent(this);
    call.addChild(entry);
    entry.addParent(call);
    nextNode.replaceParent(this.offset, exit);
    exit.addChild(nextNode);
  }

  /**
   * 获取约束为真时的下一节点
   * 
   * @return true branch
   */
  public CFGNode getTrueChild() {
    if (this.sign == ConstantValue.BRANCH_IF)
      return this.ifChild;
    else
      return this.children.get(0);
  }

  /**
   * 获取约束为假时的下一节点
   * 
   * @return false branch
   */
  public CFGNode getFalseChild() {
    if (this.sign == ConstantValue.BRANCH_IF)
      return (this.elseChild != null) ? this.elseChild : this.children.get(0);
    else
      return this.children.get(1);
  }

  @Override
  public boolean equals(Object obj) {
    CFGNode node = (CFGNode) obj;
    return (this.nodeId == node.nodeId);
  }

  public boolean isStartNode() {
    return this.nodeNumber == 1 ? true : false;
  }

  public int getSplitIndex() {
    return this.splitIndex;
  }

  public void setSplitIndex(int index) {
    this.splitIndex = index;
  }

  public int getNodeIndex() {
    return this.nodeIndex;
  }

  public void setNodeIndex(int nodeIndex) {
    this.nodeIndex = nodeIndex;
  }

  public boolean isBranchNode() {
    return this.isBranchNode;
  }

  public void setBranchNode(boolean isBranchNode) {
    this.isBranchNode = isBranchNode;
  }

  public boolean isSplitNode() {
    return this.isSplitNode;
  }

  public void setSplitNode(boolean isSplitNode) {
    this.isSplitNode = isSplitNode;
  }

  public String getFuncName() {
    return this.funcName;
  }

  public void setFuncName(String funcName) {
    this.funcName = funcName;
  }

  public void addParent(CFGNode parent) {
    if (this.parents == null) {
      this.parents = new ArrayList<CFGNode>();
      this.parents.add(parent);
    } else {
      this.parents.add(parent);
    }
  }

  public void deleteParent(int offset) {
    if (this.parents != null) {
      for (int i = 0; i < this.parents.size(); i++) {
        if (this.parents.get(i).getOffset() == offset) {
          this.parents.remove(i);
        }
      }
    }
  }

  public void deleteParent() {
    this.parents = null;
  }

  public boolean replaceParent(int offset, CFGNode replace) {
    if (this.parents != null) {
      for (int i = 0; i < this.parents.size(); i++) {
        if (this.parents.get(i).getOffset() == offset) {
          this.parents.set(i, replace);
          return true;
        }
      }
    }
    return false;
  }

  public void addChild(CFGNode child) {
    if (this.children == null) {
      this.children = new ArrayList<CFGNode>();
      this.children.add(child);
    } else {
      this.children.add(child);
    }
  }

  public void deleteChild(int offset) {
    if (this.children != null) {
      for (int i = 0; i < this.children.size(); i++) {
        if (this.children.get(i).getOffset() == offset) {
          this.children.remove(i);
        }
      }
    }
  }

  public void deleteChild() {
    this.children = null;
  }

  public boolean replaceChild(int offset, CFGNode replace) {
    if (this.children != null) {
      for (int i = 0; i < this.children.size(); i++) {
        if (this.children.get(i).getOffset() == offset) {
          this.children.set(i, replace);
          return true;
        }
      }
    }
    return false;
  }

  public boolean replaceIfChild(int offset, CFGNode replace) {
    if (this.ifChild != null && this.ifChild.getOffset() == offset) {
      this.ifChild = replace;
      return true;
    }
    return false;
  }

  public boolean replaceElseChild(int offset, CFGNode replace) {
    if (this.elseChild != null && this.elseChild.getOffset() == offset) {
      this.elseChild = replace;
      return true;
    }
    return false;
  }

  public void setIfChild(CFGNode ifChild) {
    this.ifChild = ifChild;
  }

  public void setElseChild(CFGNode elseChild) {
    this.elseChild = elseChild;
  }

  public void setBinaryExpression(IASTBinaryExpression iabe) {
    this.be = BinaryExpressionUtil.translateExpr(iabe);
  }

  public void setBinaryExpression(BinaryExpression be) {
    this.be = be;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public void setSign(int sign) {
    this.sign = sign;
  }

  public void setNodeNumber(int nodeNumber) {
    this.nodeNumber = nodeNumber;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public ArrayList<CFGNode> getParents() {
    ArrayList<CFGNode> parents = (ArrayList<CFGNode>) this.parents;
    return parents;
  }

  public ArrayList<CFGNode> getChildren() {
    ArrayList<CFGNode> children = (ArrayList<CFGNode>) this.children;
    return children;
  }

  public CFGNode getIfChild() {
    CFGNode ifChild = this.ifChild;
    return ifChild;
  }

  public CFGNode getElseChild() {
    CFGNode elseChild = this.elseChild;
    return elseChild;
  }

  public BinaryExpression getBinaryExpression() {
    BinaryExpression be = this.be;
    return be;
  }

  public int getOffset() {
    int offset = this.offset;
    return offset;
  }

  public int getSign() {
    int sign = this.sign;
    return sign;
  }

  public int getNodeNumber() {
    int nodeNumber = this.nodeNumber;
    return nodeNumber;
  }

  public int getType() {
    return this.sign;
  }

  public String getOperator() {
    String operator = this.operator;
    return operator;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public String getNodeId() {
    return this.nodeId;
  }

  public Call getCall() {
    return this.call;
  }

  public void setCall(Call call) {
    this.call = call;
  }

  /**
   * 以被调用节点的nodeIndex为key,存储分离后生成的新复合表达式
   * 
   * @param nodeIndex
   * @param expr
   */
  public void putCompoundExpr(int nodeIndex, BinaryExpression expr) {
    if (this.splitCompoundExprs == null) {
      this.splitCompoundExprs = new HashMap<Integer, BinaryExpression>();
    }
    this.splitCompoundExprs.put(nodeIndex, expr);
  }

  public BinaryExpression getCompoundExpr(int nodeIndex) {
    return this.splitCompoundExprs.get(nodeIndex);
  }

  public Map<Integer, BinaryExpression> getCompoundExprs() {
    return this.splitCompoundExprs;
  }

  public boolean equals(final CFGNode node) {
    return this.nodeId == node.getNodeId();
  }

  /** @since 0.1 */
  @Override
  public int hashCode() {
    // use `Objects.hashCode` to avoid null pointer in `this.nodeId`
    return Objects.hashCode(this.nodeId);
    // return this.nodeId.hashCode();
  }
}
