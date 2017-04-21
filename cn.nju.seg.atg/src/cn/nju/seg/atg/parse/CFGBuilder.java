package cn.nju.seg.atg.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.visitor.CFGNode;
import cn.nju.seg.atg.visitor.Function;

/**
 * @version 0.1 Move `consoleStream` to {@link cn.nju.seg.atg.gui.AtgConsole}.
 * @author zy
 * @author Zhang Yifan
 */
public class CFGBuilder {
  /**
   * 是否需要访问三元条件运算
   */
  public final static boolean shouldVisitConditionalOperator = true;
  /**
   * 是否需要访问函数调用
   */
  public final static boolean visitFunCalls = true;
  /**
   * 是否需要构建CCFG
   */
  public final static boolean shouldBuildCCFG = true;

  /**
   * 显示CFG时需要分离复合约束，生成路径时则不需要
   */
  public static boolean splitCompositeNode = false;

  /**
   * 需要访问的所有函数列表
   */
  public static Map<String, Function> allFunctions;
  /**
   * 执行单元测试的函数
   */
  public static Function function;
  /**
   * 函数输入参数的类型
   */
  public static String[] parameterTypes = null;
  /**
   * 执行单元测试的函数名
   */
  public static String funcName = null;

  /**
   * CFG树中的当前节点，是全局变量，在构建、遍历CFG树的时候会用到
   */
  public static CFGNode currentNode;
  /**
   * 单个函数CFG树中节点的编号，是全局变量，构建CFG树时会用到
   */
  public static int nodeNumber;
  /**
   * 节点在总的CFG树（包括当前函数及其包含的函数调用）中的编号
   */
  public static int nodeIndex;

  /**
   * 构建CFG树过程中用于存放终止节点的链表，是全局变量
   */
  public static List<CFGNode> terminalNodes;
  /**
   * 构建CFG树过程中用于存放break节点的链表
   */
  public static List<CFGNode> breakNodes;
  /**
   * 构建CFG树过程中用于存放continue节点的链表
   */
  public static List<CFGNode> continueNodes;

  /**
   * 变量初始化
   */
  public static void initial() {
    currentNode = new CFGNode();
    nodeNumber = 0;
    terminalNodes = new ArrayList<CFGNode>();
    breakNodes = new ArrayList<CFGNode>();
    continueNodes = new ArrayList<CFGNode>();
  }

  /**
   * 输出一条路经
   * 
   * @param path
   */
  public static void printPath(CFGPath path) {
    if (path.getPath().get(0).isBranchNode()) {
      System.out.print(path.getPath().get(0).getName());
      char isTrue = path.getPath().get(0).isTrue() ? 'T' : 'F';
      System.out.print("(" + isTrue + ")");
    } else {
      System.out.print(path.getPath().get(0).getName());
    }
    for (int j = 1; j < path.getPath().size(); j++) {
      System.out.print("->" + path.getPath().get(j).getName());
      if (path.getPath().get(j).isBranchNode()) {
        char isTrue = path.getPath().get(j).isTrue() ? 'T' : 'F';
        System.out.print("(" + isTrue + ")");
      }
    }

    System.out.println();
  }
}
