package cn.nju.seg.atg.pathParse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cn.nju.seg.atg.model.Constraint;
import cn.nju.seg.atg.model.Coodinate;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.parse.ConditionCoverage;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.model.Condition;
import cn.nju.seg.atg.model.constraint.Operator;
import cn.nju.seg.atg.util.ConstantValue;
import cn.nju.seg.atg.visitor.CFGNode;

/**
 * 对含有扩模块函数调用程序,涉及到与目标约束相关的路径片段的一些操作
 * 1. 从文件读取路径
 * 2. 从CFG生成所有含有目标约束的路径片段
 * <p>
 * 暂不支持循环结构:for/while/do-while
 * 
 * @author zy
 */
public class PathFragmentUtil {
  /**
   * 从文件读取路径片段
   * 
   * @param parameter
   * @param filePath
   * @param targetPath
   * @return 一条相关路径片段
   */
  public static CFGPath readPathFragment(int paramIndex, double[] parameters, String filePath) {
    CFGPath execPath = new CFGPath();
    try {
      String str;
      BufferedReader in = new BufferedReader(new FileReader(filePath));

      while ((str = in.readLine()) != null) {
        if (!str.equals("")) {
          String[] array = str.split(" ");
          if (array.length == 3) {
            checkConditionCoverage(Double.parseDouble(array[1]), Integer.parseInt(array[2].substring(11)), parameters);
          }
          addNode(array, parameters[paramIndex], execPath);
        }
      }
      in.close();
    } catch (Exception e) {
      System.out.print(e.toString());
    }

    return execPath;
  }

  private static void checkConditionCoverage(double readValue, int expressionId, double[] parameters) {
    if (ConditionCoverage.uncoveredConditions.containsKey(expressionId)) {
      Condition condition = ConditionCoverage.uncoveredConditions.get(expressionId);
      Operator op = condition.getConstraint().getOp();
      if (conditionValue(readValue, op) && !condition.isTbranchCovered())
        condition.setTcoveredInput(parameters);
      else if (!conditionValue(readValue, op) && !condition.isFbranchCovered())
        condition.setFcoveredInput(parameters);
    }
  }

  /**
   * 根据条件约束对应分支函数的插桩值和关系符判断其被满足情况
   * 
   * @param readValue
   * @param op
   * @return
   */
  private static boolean conditionValue(double readValue, Operator op) {
    switch (op) {
    case GT:
      return readValue > 0 ? true : false;
    case GE:
      return readValue >= 0 ? true : false;
    case LE:
      return readValue <= 0 ? true : false;
    case LT:
      return readValue < 0 ? true : false;
    case EQ:
      return readValue == 0 ? true : false;
    default:
      return readValue != 0 ? true : false;
    }
  }

  /**
   * 在path末尾添加一个新节点
   * 
   * @param node
   * @param paramIndex
   */
  private static void addNode(String[] node, double parameter, CFGPath path) {
    // 添加普通节点
    if (node.length == 1) {
      SimpleCFGNode tempNode = new SimpleCFGNode();
      tempNode.setName(node[0]);
      tempNode.setType(ConstantValue.NORMAL_NODE);
      path.addNode(tempNode);
    } else if (node.length == 3) {
      assert (node[2].startsWith("expression"));
      // 当分支函数插桩结果为异常值时，转换为Java对应形式
      if (node[1].contains("nan")) {
        node[1] = node[1].replace("nan", "NaN");
      } else if (node[1].contains("inf")) {
        node[1] = node[1].replace("inf", "Infinity");
      }

      int pathSize = path.getPath().size();
      // 复合约束,在同一分支节点上按照条件表达式添加相应的分支函数值
      if (pathSize != 0 && path.getPath().get(pathSize - 1).getName().equals(node[0])) {
        Coodinate c = new Coodinate(parameter, Double.parseDouble(node[1]));
        path.getPath().get(pathSize - 1).addValue(node[2], c);
      }
      // 作为新的分支节点加入路径
      else {
        Coodinate c = new Coodinate(parameter, Double.parseDouble(node[1]));
        SimpleCFGNode tempNode = new SimpleCFGNode();
        tempNode.setName(node[0]);
        tempNode.setType(ConstantValue.BRANCH_NODE);
        tempNode.addValue(node[2], c);
        path.addNode(tempNode);
      }
    }
  }

  /**
   * 根据提供的目标节点,返回目标节点相关的所有路径片段
   * 
   * @param cfgTargetNode
   * @return path list
   */
  public static List<CFGPath> getAllRelevantPaths(CFGNode cfgTargetNode) {
    List<CFGPath> allPaths = new ArrayList<CFGPath>();
    CFGPath path = new CFGPath();
    searchFragmentPaths(allPaths, path, cfgTargetNode);

    for (CFGPath p : allPaths) {
      addTruthInfo(p);
      // PathCoverage.printPath(p);
    }

    return allPaths;
  }

  /**
   * 为逆序生成的路径添加分支节点的真值信息
   * 
   * @param path
   */
  private static void addTruthInfo(CFGPath path) {
    List<SimpleCFGNode> list = path.getPath();

    for (int i = 0; i < list.size() - 1; i++) {
      if (list.get(i).getType() == ConstantValue.BRANCH_IF) {
        CFGNode node = ConditionCoverage.allNodes.get(list.get(i).getId());
        if (node.getElseChild() != null) {
          list.get(i).setTrue(list.get(i + 1).getName() != node.getElseChild().getNodeId());
        } else {
          for (CFGNode child : node.getChildren()) {
            if (child.getType() != ConstantValue.STATEMENT_CALL_CE) {
              list.get(i).setTrue(list.get(i + 1).getName() != child.getNodeId());
              break;
            }
          }
        }
      }
    }
  }

  /**
   * 递归遍历CCFG树，找到关于程序目标点的相关路径集合
   * 
   * @param allPaths
   * @param path
   * @param node
   */
  private static void searchFragmentPaths(List<CFGPath> allPaths, CFGPath path, CFGNode node) {
    CFGPath pathTemp = path.clonePath();
    assert (node.getType() != ConstantValue.BRANCH_DO && node.getType() != ConstantValue.BRANCH_FOR
        && node.getType() != ConstantValue.BRANCH_WHILE);
    // if (node == null) System.out.println();
    if (node.getSign() == ConstantValue.STATEMENT_CALL_CE) {
      pathTemp.addNodeFromBegin(new SimpleCFGNode(node.getNodeId(), ConstantValue.NORMAL_NODE, null, true, node.getNodeIndex()));
      assert (node.getParents().size() > 1);
      CFGNode parentNode = node.getParents().get(0);
      Constraint constraintTemp = new Constraint(parentNode.getCompoundExpr(node.getNodeIndex()));
      pathTemp.addNodeFromBegin(new SimpleCFGNode(parentNode.getNodeId(), parentNode.getType(), constraintTemp, true, parentNode.getNodeIndex()));
      node = parentNode;
    } else if (node.getType() == ConstantValue.BRANCH_IF) {
      pathTemp.addNodeFromBegin(new SimpleCFGNode(node.getNodeId(), node.getType(), new Constraint(node.getBinaryExpression()), true, node.getNodeIndex()));
    } else {
      pathTemp.addNodeFromBegin(new SimpleCFGNode(node.getNodeId(), ConstantValue.NORMAL_NODE, null, true, node.getNodeIndex()));
    }

    if (node.equals(CFGBuilder.function.getStartNode())) {
      allPaths.add(pathTemp);
    } else {
      for (CFGNode pNode : node.getParents()) {
        if (node.getSign() == ConstantValue.ENTRY_NODE || pNode.getSign() != ConstantValue.STATEMENT_CALL_CE)
          searchFragmentPaths(allPaths, pathTemp, pNode);
      }
    }
  }
}
