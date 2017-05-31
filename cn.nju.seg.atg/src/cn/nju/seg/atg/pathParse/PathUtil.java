package cn.nju.seg.atg.pathParse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cn.nju.seg.atg.model.Constraint;
import cn.nju.seg.atg.model.Coodinate;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.ConstantValue;
import cn.nju.seg.atg.visitor.CFGNode;

/**
 * 涉及到路径的一些操作
 * 1. 从文件读取路径
 * 2. 从CFG生成所有路径
 * 
 * @author zy
 */
public class PathUtil {
  /**
   * 从文件读取执行路径
   * 
   * @param parameter
   * @param filePath
   * @return 一条路径
   */
  public static List<CFGPath> readPath(double parameter, String filePath) {
    List<XPath> xPaths = new ArrayList<XPath>();
    PathUtil pu = new PathUtil();
    XPath xPath = pu.new XPath();
    xPaths.add(xPath);

    try {
      String str;
      BufferedReader in = new BufferedReader(new FileReader(filePath));

      // 记录分支节点的个数
      int numOfBranchNode = 0;
      while ((str = in.readLine()) != null) {
        if (!str.equals("")) {
          String[] array = str.split(" ");

          if (array.length > 1) {
            numOfBranchNode++;
          }

          if (isInNextNodes(xPaths, array[0])) {
            // 未发现新路径
            int xPathsSize = xPaths.size();
            for (int i = 0; i < xPathsSize; i++) {
              xPaths.get(i).updateAll(array, parameter);
            }
          } else {
            // 发现一条新路径
            int xPathsSize = xPaths.size();

            List<XPath> newXPaths = getNewXPaths(xPaths, array, parameter);
            int newXPathsSize = newXPaths.size();
            for (int i = 0; i < newXPathsSize; i++) {
              xPaths.add(newXPaths.get(i));
            }

            // 更新原有路径的nextNodeIndex[]
            for (int i = 0; i < xPathsSize; i++) {
              xPaths.get(i).updateNextNodeIndex(array[0], xPaths.get(i).getPathSize());
            }
          }
        }
      }
      in.close();

      // 读取末尾的所有非分支节点
      List<String> endNodes = new ArrayList<String>();
      String endBranchNode = null;
      in = new BufferedReader(new FileReader(filePath));
      while ((str = in.readLine()) != null) {
        if (!str.equals("")) {

          if (numOfBranchNode == 0) {
            endNodes.add(str);
          } else {
            String[] array = str.split(" ");
            if (array.length > 1) {
              if (numOfBranchNode == 1) {
                endBranchNode = array[0];
              }
              numOfBranchNode--;
            }
          }
        }
      }
      in.close();

      // 为残缺路径补全末尾节点
      int xPathsSize = xPaths.size();
      int numOfEndNodes = endNodes.size();
      for (int i = 0; i < xPathsSize; i++) {
        if (xPaths.get(i).getPath().getEndNodeName().equals(endBranchNode)) {
          for (int j = 0; j < numOfEndNodes; j++) {
            SimpleCFGNode tempNode = new SimpleCFGNode();
            tempNode.setName(endNodes.get(j));
            tempNode.setType(ConstantValue.NORMAL_NODE);
            xPaths.get(i).getPath().addNode(tempNode);
          }
        }
      }
    } catch (Exception e) {
      System.out.print(e.toString());
    }

    List<CFGPath> executedPaths = new ArrayList<CFGPath>();
    int xPathsSize = xPaths.size();
    for (int i = 0; i < xPathsSize; i++) {
      executedPaths.add(xPaths.get(i).getPath());
    }

    return executedPaths;
  }

  /************************************************
   * | 从CFG树生成所有待测试路径 |
   * | @param cfgStartNode |
   * | @return 所有路径 |
   ***********************************************/
  public static List<CFGPath> getAllPaths(CFGNode cfgStartNode) {
    List<CFGPath> allPaths = new ArrayList<CFGPath>();
    CFGPath path = new CFGPath();
    List<Integer> nodeTraversed = new ArrayList<Integer>();

    searchPaths(allPaths, path, nodeTraversed, cfgStartNode, false);
    // searchFullPaths(allPaths, path, nodeTraversed, cfgStartNode, false);

    List<CFGPath> newAllPaths = new ArrayList<CFGPath>();
    int size = allPaths.size();
    for (int i = 0; i < size; i++) {
      if (!isInAllPaths(newAllPaths, allPaths.get(i))) {
        newAllPaths.add(allPaths.get(i));
      }
    }

    return newAllPaths;
  }

  /**
   * 路径结构体
   * 用于从文件读取路径
   */
  private class XPath {
    private CFGPath path;
    private int[] nextNodeIndex = { 0, -1 };

    public XPath() {
      this.path = new CFGPath();
    }

    /**
     * 返回路径长度
     * 
     * @return
     */
    public int getPathSize() {
      return this.path.getPath().size();
    }

    /**
     * 获取路径
     * 
     * @return
     */
    public CFGPath getPath() {
      return this.path;
    }

    /**
     * 添加两个坐标到nextNodeIndex[]
     * 
     * @param index1
     * @param index2
     */
    private void setNextNodeIndex(int index1, int index2) {
      this.nextNodeIndex[0] = index1;
      this.nextNodeIndex[1] = index2;
    }

    /**
     * 获取下一节点集合中较小的一个
     * 
     * @return
     */
    private int getMinIndex() {
      if (this.getSizeOfNNI() == 1) {
        if (this.nextNodeIndex[0] != -1)
          return this.nextNodeIndex[0];
        else
          return this.nextNodeIndex[1];
      } else if (this.getSizeOfNNI() == 2) {
        if (this.nextNodeIndex[0] > this.nextNodeIndex[1])
          return this.nextNodeIndex[1];
        else
          return this.nextNodeIndex[0];
      } else
        return -1;
    }

    /**
     * 计算下一节点集合中的元素个数
     * 
     * @return
     */
    private int getSizeOfNNI() {
      if (this.nextNodeIndex[0] == -1 && this.nextNodeIndex[1] == -1)
        return 0;
      else if (this.nextNodeIndex[0] != -1 && this.nextNodeIndex[1] != -1)
        return 2;
      else
        return 1;
    }

    /**
     * 下一节点集合是否为空
     * 
     * @return
     */
    public boolean hasNextNodeIndex() {
      if (this.nextNodeIndex[0] == -1 && this.nextNodeIndex[1] == -1)
        return false;
      else
        return true;
    }

    /**
     * 名字为nodeName的节点是否在下一节点集合中
     * 
     * @param nodeName
     * @return
     */
    public boolean isNextNode(String nodeName) {
      int pathSize = this.path.getPath().size();
      if (pathSize > this.nextNodeIndex[0] && pathSize > this.nextNodeIndex[1]) {
        if (this.nextNodeIndex[0] != -1) {
          if (this.path.getPath().get(this.nextNodeIndex[0]).getName().equals(nodeName)) {
            return true;
          }
        }
        if (this.nextNodeIndex[1] != -1) {
          if (this.path.getPath().get(this.nextNodeIndex[1]).getName().equals(nodeName)) {
            return true;
          }
        }
      } else if (pathSize == this.nextNodeIndex[0] || pathSize == this.nextNodeIndex[1]) {
        return true;
      }

      return false;
    }

    /**
     * 更新下一节点集合
     * 
     * @param nodeName
     */
    public void updateNextNodeIndex(String nodeName, int maxIndex) {
      // 清空nextNodeIndex[]
      this.setNextNodeIndex(-1, -1);
      int count = 0;

      for (int i = 0; i < maxIndex; i++) {
        if (this.path.getPath().get(i).getName().equals(nodeName)) {
          this.nextNodeIndex[count] = i + 1;
          count++;
        }
      }
    }

    /**
     * 更新path和nextNodeIndex
     * 
     * @param nodeName
     */
    public void updateAll(String[] node, double parameter) {
      int pathSize = this.path.getPath().size();
      int sizeOfNNI = getSizeOfNNI();

      if (this.isNextNode(node[0])) {
        if (sizeOfNNI == 1) {
          if (this.nextNodeIndex[0] != -1) {
            if (this.nextNodeIndex[0] == pathSize) {
              this.addNode(node, parameter);
              this.updateNextNodeIndex(node[0], this.path.getPath().size());
            } else {
              this.updateNextNodeIndex(node[0], this.nextNodeIndex[0] + 1);
            }
          } else {
            if (this.nextNodeIndex[1] == pathSize) {
              this.addNode(node, parameter);
              this.updateNextNodeIndex(node[0], this.path.getPath().size());
            } else {
              this.updateNextNodeIndex(node[0], this.nextNodeIndex[1] + 1);
            }
          }
        } else if (sizeOfNNI == 2) {
          if (this.nextNodeIndex[0] == pathSize) {
            if (this.path.getPath().get(this.nextNodeIndex[1]).getName().equals(node[0])) {
              this.updateNextNodeIndex(node[0], this.nextNodeIndex[1] + 1);
            } else {
              this.addNode(node, parameter);
              this.updateNextNodeIndex(node[0], this.path.getPath().size());
            }
          } else if (this.nextNodeIndex[1] == pathSize) {// 这里可以进行更新（加入新的值）
            if (this.path.getPath().get(this.nextNodeIndex[0]).getName().equals(node[0])) {
              this.updateNextNodeIndex(node[0], this.nextNodeIndex[0] + 1);
            } else {
              this.addNode(node, parameter);
              this.updateNextNodeIndex(node[0], this.path.getPath().size());
            }
          } else {
            if (this.path.getPath().get(this.nextNodeIndex[0]).getName().equals(node[0])) {
              this.updateNextNodeIndex(node[0], this.nextNodeIndex[0] + 1);
            } else {
              this.updateNextNodeIndex(node[0], this.nextNodeIndex[1] + 1);
            }
          }
        }
      } else {
        if (hasNextNodeIndex()) {
          this.setNextNodeIndex(-1, -1);
        } else {
          this.updateNextNodeIndex(node[0], this.path.getPath().size());
        }
      }
    }

    /**
     * 在path末尾添加一个新节点
     * 
     * @param node
     * @param paramIndex
     */
    public void addNode(String[] node, double parameter) {
      if (node.length == 1) {
        SimpleCFGNode tempNode = new SimpleCFGNode();
        tempNode.setName(node[0]);
        tempNode.setType(ConstantValue.NORMAL_NODE);
        this.path.addNode(tempNode);
      } else if (node.length == 3) {
        // 当分支函数插桩结果为异常值时，直接略去
        if (!node[1].contains("nan") && !node[1].contains("inf")) {
          int pathSize = this.getPathSize();
          if (pathSize != 0 && this.path.getPath().get(pathSize - 1).getName().equals(node[0])) {
            Coodinate c = new Coodinate(parameter, Double.parseDouble(node[1]));
            this.path.addBranchNode(node[0], ConstantValue.BRANCH_NODE, node[2], c);
          } else {
            Coodinate c = new Coodinate(parameter, Double.parseDouble(node[1]));
            SimpleCFGNode tempNode = new SimpleCFGNode();
            tempNode.setName(node[0]);
            tempNode.setType(ConstantValue.BRANCH_NODE);
            tempNode.addValue(node[2], c);
            this.path.addNode(tempNode);
          }
        }
      }
    }

    /**
     * 添加一个节点
     * 
     * @param snode
     */
    private void addNode(SimpleCFGNode snode) {
      this.path.getPath().add(snode);
    }

    /**
     * 复制路径片段
     * 为添加新路径做准备
     * 
     * @return
     */
    public XPath getNewXPath() {
      XPath xPath = new XPath();

      int minIndex = this.getMinIndex();

      if (minIndex != -1) {
        for (int i = 0; i < minIndex; i++) {
          SimpleCFGNode snode = this.path.getPath().get(i);
          xPath.addNode(snode);
        }
      } else {
        xPath = null;
      }

      return xPath;
    }

    /**
     * 判断两条路径是否相同
     * 
     * @param xPath
     * @return
     */
    public boolean isSamePath(XPath xPath) {
      if (this.path.isEqual(xPath.getPath().getPath())) {
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * 判断节点名为nodeName的节点是否在所有路径的下一节点集合中
   * 
   * @param xPaths
   * @param nodeName
   * @return
   */
  private static boolean isInNextNodes(List<XPath> xPaths, String nodeName) {
    int size = xPaths.size();
    for (int i = 0; i < size; i++) {
      if (xPaths.get(i).isNextNode(nodeName)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 添加新的路径
   * 
   * @param xPaths
   * @param node
   * @return
   */
  private static List<XPath> getNewXPaths(List<XPath> xPaths, String[] node, double parameter) {
    List<XPath> newXPaths = new ArrayList<XPath>();

    int size = xPaths.size();
    for (int i = 0; i < size; i++) {
      if (xPaths.get(i).hasNextNodeIndex()) {
        XPath pathTemp = xPaths.get(i).getNewXPath();
        pathTemp.addNode(node, parameter);
        pathTemp.updateNextNodeIndex(node[0], pathTemp.getPathSize());
        if (!isInXPathList(newXPaths, pathTemp)) {
          newXPaths.add(pathTemp);
        }
      }
    }

    return newXPaths;
  }

  /**
   * 判断路径xPath是否在xPathList中
   * 
   * @param xPathList
   * @param xPath
   * @return
   */
  private static boolean isInXPathList(List<XPath> xPathList, XPath xPath) {
    int size = xPathList.size();
    for (int i = 0; i < size; i++) {
      if (xPathList.get(i).isSamePath(xPath)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断path是否已经在allPaths中
   * 
   * @param allPaths
   * @param path
   * @return
   */
  private static boolean isInAllPaths(List<CFGPath> allPaths, CFGPath path) {
    int size = allPaths.size();
    for (int i = 0; i < size; i++) {
      if (path.isEqual(allPaths.get(i).getPath())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Modified version of {@link #searchPaths(List, CFGPath, List, CFGNode, boolean)},
   * which expands a function call and then search its successor of the function call.
   * It also do not add `call@method` nodes to paths.
   * 
   * @param allPaths
   *          路径集合
   * @param path
   *          当前路径
   * @param nodeTraversed
   *          已搜索节点
   * @param node
   *          当前节点
   * @param endFlag
   *          记录是否是循环结束节点
   *          
   *          @see #searchPaths(List, CFGPath, List, CFGNode, boolean)
   *          @since 0.1
   */
  private static void searchFullPaths(List<CFGPath> allPaths, CFGPath path, List<Integer> nodeTraversed, CFGNode node, boolean endFlag) {
    CFGPath pathTemp = path.clonePath();
    List<Integer> nodeTraversedTemp = copyNodeTraversed(nodeTraversed);

    if (node.getChildren() == null && node.getIfChild() == null && node.getElseChild() == null) {
      pathTemp.addNormalNode(node.getNodeId(), ConstantValue.NORMAL_NODE);
      if (!nodeTraversedTemp.contains(node.getNodeIndex())) {
        nodeTraversedTemp.add(node.getNodeIndex());
      }
      allPaths.add(pathTemp);
    } else {
      if (node.getBinaryExpression() != null)	// 条件节点(if-else/for/while/do-while)
      {
        if (endFlag) {
          SimpleCFGNode tempNode = new SimpleCFGNode();
          tempNode.setName(node.getNodeId());
          tempNode.setType(ConstantValue.NORMAL_NODE);
          pathTemp.addNode(tempNode);
          endFlag = false;
        } else {
          Constraint constraintTemp = new Constraint(node.getBinaryExpression());
          SimpleCFGNode snode = new SimpleCFGNode(node.getNodeId(), node.getType(), constraintTemp, true);
          pathTemp.addNode(snode);
        }
        if (!nodeTraversedTemp.contains(node.getNodeIndex())) {
          nodeTraversedTemp.add(node.getNodeIndex());
        }
        // 处理if-else
        if (node.getIfChild() != null) {
          // 处理if分支
          if (nodeTraversedTemp.contains(node.getIfChild().getNodeIndex())) {
            if (node.getNodeIndex() > node.getIfChild().getNodeIndex()) {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getIfChild(), endFlag);
            } else {
              if (node.getElseChild() == null && node.getChildren() == null) {
                allPaths.add(pathTemp);
              } else {
                pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
                if (node.getElseChild() != null) {
                  if (nodeTraversedTemp.contains(node.getElseChild().getNodeIndex())) {
                    if (node.getNodeIndex() > node.getElseChild().getNodeIndex()) {
                      searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
                    } else {
                      allPaths.add(pathTemp);
                    }
                  } else {
                    searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
                  }
                } else {
                  if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
                    if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
                      searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
                    } else {
                      allPaths.add(pathTemp);
                    }
                  } else {
                    searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
                  }
                }
              }
            }
          } else {
            searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getIfChild(), endFlag);
          }

          // 处理else分支
          if (node.getElseChild() != null) {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);

            if (nodeTraversedTemp.contains(node.getElseChild().getNodeIndex())) {
              if (node.getNodeIndex() > node.getElseChild().getNodeIndex()) {
                searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
              } else {
                allPaths.add(pathTemp);
              }
            } else {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
            }

          } else if (node.getChildren() != null)	// 处理if-else中只有if的情况
          {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);

            if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
              if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
                searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), true);
              } else {
                allPaths.add(pathTemp);
              }
            } else {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
            }
          }
        }
        // 处理for/while
        else if (node.getType() == ConstantValue.BRANCH_FOR || node.getType() == ConstantValue.BRANCH_WHILE) {
          // 条件成立
          if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
            // 嵌套循环
            if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
              pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), true);
            } else {
              if (node.getChildren().size() == 1) {
                allPaths.add(pathTemp);
              } else if (node.getChildren().size() > 1) {
                pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
                if (nodeTraversedTemp.contains(node.getChildren().get(1).getNodeIndex())) {
                  if (node.getNodeIndex() > node.getChildren().get(1).getNodeIndex()) {
                    searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
                  } else {
                    allPaths.add(pathTemp);
                  }
                } else {
                  searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
                }
              }
            }
          } else {   // 进入循环体
            searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
          }

          // 条件不成立
          if (node.getChildren().size() > 1) {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);

            if (nodeTraversedTemp.contains(node.getChildren().get(1).getNodeIndex())) {
              if (node.getNodeIndex() > node.getChildren().get(1).getNodeIndex()) {
                searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
              } else {
                allPaths.add(pathTemp);
              }
            } else {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
            }
          } else {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
            allPaths.add(pathTemp);
          }
        }
        // 处理do-while
        else if (node.getType() == ConstantValue.BRANCH_DO) {

        }
      } else {
        // @since 0.1 Change search for function call
        if (node.getSign() == ConstantValue.STATEMENT_CALL && CFGBuilder.shouldBuildCCFG) {
          // use `addNormalNodeForce` to allow add multiple `call@method` nodes to the path
          // pathTemp.addNormalNodeForce(node.getNodeId(), ConstantValue.NORMAL_NODE);
          // if (!nodeTraversedTemp.contains(node.getNodeIndex())) {
          // nodeTraversedTemp.add(node.getNodeIndex());
          // }

          // node.getChildren().get(0) returns the entry node of the calling function
          // here we get all paths of the function
          final List<CFGPath> callFunctionPaths = getAllPaths(node.getChildren().get(0));
          for (CFGPath callFunctionPath : callFunctionPaths) {
            // for each path of the calling function, we first merge the current path with the function path
            final CFGPath pathTempMergerd = CFGPath.mergePaths(pathTemp, callFunctionPath);
            final List<Integer> nodeTraversedTempMergerd = copyNodeTraversed(nodeTraversed);

            // then we continue to search the successor of the function call
            CFGNode functionCallSuccessor = node.getChildren().get(1);
            if (nodeTraversedTempMergerd.contains(functionCallSuccessor.getNodeIndex())) {
              if (node.getNodeIndex() > functionCallSuccessor.getNodeIndex()) {
                searchFullPaths(allPaths, pathTempMergerd, nodeTraversedTempMergerd, functionCallSuccessor, true);
              } else {
                allPaths.add(pathTemp);
              }
            } else {
              searchFullPaths(allPaths, pathTempMergerd, nodeTraversedTempMergerd, functionCallSuccessor, endFlag);
            }
          }
        } else {
          pathTemp.addNormalNode(node.getNodeId(), ConstantValue.NORMAL_NODE);

          if (!nodeTraversedTemp.contains(node.getNodeIndex())) {
            nodeTraversedTemp.add(node.getNodeIndex());
          }
          if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
            if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), true);
            } else {
              allPaths.add(pathTemp);
            }
          } else {
            searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
          }
        }
      }
    }
  }

  /**
   * 递归函数
   * 找出当前CFG树的所有路径
   * <p>
   * （注：循环节点第二次加入路径时，标记为普通节点，不参与线性拟合运算）
   * 
   * @param allPaths
   *          路径集合
   * @param path
   *          当前路径
   * @param nodeTraversed
   *          已搜索节点
   * @param node
   *          当前节点
   * @param endFlag
   *          记录是否是循环结束节点
   */
  private static void searchPaths(List<CFGPath> allPaths, CFGPath path, List<Integer> nodeTraversed, CFGNode node, boolean endFlag) {
    CFGPath pathTemp = path.clonePath();
    List<Integer> nodeTraversedTemp = copyNodeTraversed(nodeTraversed);

    if (node.getChildren() == null && node.getIfChild() == null && node.getElseChild() == null) {
      pathTemp.addNormalNode(node.getNodeId(), ConstantValue.NORMAL_NODE);
      if (!nodeTraversedTemp.contains(node.getNodeIndex())) {
        nodeTraversedTemp.add(node.getNodeIndex());
      }
      allPaths.add(pathTemp);
    } else {
      if (node.getBinaryExpression() != null) // 条件节点(if-else/for/while/do-while)
      {
        if (endFlag) {
          SimpleCFGNode tempNode = new SimpleCFGNode();
          tempNode.setName(node.getNodeId());
          tempNode.setType(ConstantValue.NORMAL_NODE);
          pathTemp.addNode(tempNode);
          endFlag = false;
        } else {
          Constraint constraintTemp = new Constraint(node.getBinaryExpression());
          SimpleCFGNode snode = new SimpleCFGNode(node.getNodeId(), node.getType(), constraintTemp, true);
          pathTemp.addNode(snode);
        }
        if (!nodeTraversedTemp.contains(node.getNodeIndex())) {
          nodeTraversedTemp.add(node.getNodeIndex());
        }
        // 处理if-else
        if (node.getIfChild() != null) {
          // 处理if分支
          if (nodeTraversedTemp.contains(node.getIfChild().getNodeIndex())) {
            if (node.getNodeIndex() > node.getIfChild().getNodeIndex()) {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getIfChild(), endFlag);
            } else {
              if (node.getElseChild() == null && node.getChildren() == null) {
                allPaths.add(pathTemp);
              } else {
                pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
                if (node.getElseChild() != null) {
                  if (nodeTraversedTemp.contains(node.getElseChild().getNodeIndex())) {
                    if (node.getNodeIndex() > node.getElseChild().getNodeIndex()) {
                      searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
                    } else {
                      allPaths.add(pathTemp);
                    }
                  } else {
                    searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
                  }
                } else {
                  if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
                    if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
                      searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
                    } else {
                      allPaths.add(pathTemp);
                    }
                  } else {
                    searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
                  }
                }
              }
            }
          } else {
            searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getIfChild(), endFlag);
          }

          // 处理else分支
          if (node.getElseChild() != null) {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);

            if (nodeTraversedTemp.contains(node.getElseChild().getNodeIndex())) {
              if (node.getNodeIndex() > node.getElseChild().getNodeIndex()) {
                searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
              } else {
                allPaths.add(pathTemp);
              }
            } else {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getElseChild(), endFlag);
            }

          } else if (node.getChildren() != null)  // 处理if-else中只有if的情况
          {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);

            if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
              if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
                searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), true);
              } else {
                allPaths.add(pathTemp);
              }
            } else {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
            }
          }
        }
        // 处理for/while
        else if (node.getType() == ConstantValue.BRANCH_FOR || node.getType() == ConstantValue.BRANCH_WHILE) {
          // 条件成立
          if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
            // 嵌套循环
            if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
              pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), true);
            } else {
              if (node.getChildren().size() == 1) {
                allPaths.add(pathTemp);
              } else if (node.getChildren().size() > 1) {
                pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
                if (nodeTraversedTemp.contains(node.getChildren().get(1).getNodeIndex())) {
                  if (node.getNodeIndex() > node.getChildren().get(1).getNodeIndex()) {
                    searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
                  } else {
                    allPaths.add(pathTemp);
                  }
                } else {
                  searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
                }
              }
            }
          } else {   // 进入循环体
            searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
          }

          // 条件不成立
          if (node.getChildren().size() > 1) {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);

            if (nodeTraversedTemp.contains(node.getChildren().get(1).getNodeIndex())) {
              if (node.getNodeIndex() > node.getChildren().get(1).getNodeIndex()) {
                searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
              } else {
                allPaths.add(pathTemp);
              }
            } else {
              searchFullPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
            }
          } else {
            pathTemp.getPath().get(pathTemp.getPath().size() - 1).setTrue(false);
            allPaths.add(pathTemp);
          }
        }
        // 处理do-while
        else if (node.getType() == ConstantValue.BRANCH_DO) {

        }
      } else {
        pathTemp.addNormalNode(node.getNodeId(), ConstantValue.NORMAL_NODE);

        if (!nodeTraversedTemp.contains(node.getNodeIndex())) {
          nodeTraversedTemp.add(node.getNodeIndex());
        }
        if (nodeTraversedTemp.contains(node.getChildren().get(0).getNodeIndex())) {
          if (node.getNodeIndex() > node.getChildren().get(0).getNodeIndex()) {
            searchPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), true);
          } else {
            allPaths.add(pathTemp);
          }
        } else {
          searchPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(0), endFlag);
        }
        if (node.getSign() == ConstantValue.STATEMENT_CALL && CFGBuilder.shouldBuildCCFG) {
          searchPaths(allPaths, pathTemp, nodeTraversedTemp, node.getChildren().get(1), endFlag);
        }
      }
    }
  }

  /**
   * 拷贝出一个新的nodeTraversed
   * 
   * @param nodeTraversed
   * @return
   */
  private static List<Integer> copyNodeTraversed(List<Integer> nodeTraversed) {
    List<Integer> nodeTraversedTemp = new ArrayList<Integer>();
    int size = nodeTraversed.size();
    for (int i = 0; i < size; i++) {
      nodeTraversedTemp.add(nodeTraversed.get(i));
    }
    return nodeTraversedTemp;
  }
}
