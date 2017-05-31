package cn.nju.seg.atg.pathParse;

import java.io.BufferedReader;
import java.io.FileReader;

import cn.nju.seg.atg.model.Coodinate;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.ConstantValue;

/**
 * 涉及到Z路径覆盖准则的一些操作
 * 1. 从文件读取路径
 * 
 * @author zy
 */
public class ZpathUtil {
  /**
   * 从文件读取路径，采用Z路径覆盖准则，循环结构只考虑最多循环一次
   * 
   * @param parameter
   * @param filePath
   * @return 一条路径
   */
  public static CFGPath readPath_Z(double parameter, String filePath) {
    ZpathUtil pu = new ZpathUtil();
    XPath xPath = pu.new XPath();

    try {
      String str;
      BufferedReader in = new BufferedReader(new FileReader(filePath));

      while ((str = in.readLine()) != null) {
        if (!str.equals("")) {
          String[] array = str.split(" ");
          // 更新路径
          xPath.updateAll(array, parameter);
        }
      }
      in.close();
    } catch (Exception e) {
      System.out.print(e.toString());
    }

    return xPath.getPath();
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
      // System.out.println(node[0]+":"+nextNodeIndex[0]+","+nextNodeIndex[1]);
      int pathSize = this.path.getPath().size();
      int sizeOfNNI = getSizeOfNNI();

      if (sizeOfNNI == 1) {
        if (this.nextNodeIndex[0] != -1) {
          if (this.nextNodeIndex[0] == pathSize) {
            this.addNode_Z(node, parameter);
            this.updateNextNodeIndex(node[0], this.path.getPath().size());
          } else {
            // 循环次数超过一次时，不关注分支函数值，只更新下一节点集合
            this.updateNextNodeIndex(node[0], pathSize);
          }
        } else {
          if (this.nextNodeIndex[1] == pathSize) {
            this.addNode_Z(node, parameter);
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
            this.addNode_Z(node, parameter);
            this.updateNextNodeIndex(node[0], this.path.getPath().size());
          }
        } else if (this.nextNodeIndex[1] == pathSize) {
          if (this.path.getPath().get(this.nextNodeIndex[0]).getName().equals(node[0])) {
            this.updateNextNodeIndex(node[0], this.nextNodeIndex[0] + 1);
          } else {
            this.addNode_Z(node, parameter);
            this.updateNextNodeIndex(node[0], this.path.getPath().size());
          }
        } else {
          if (this.path.getPath().get(this.nextNodeIndex[0]).getName().equals(node[0])) {
            this.updateNextNodeIndex(node[0], this.nextNodeIndex[0] + 1);
          } else {
            this.updateNextNodeIndex(node[0], this.nextNodeIndex[1] + 1);
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
    public void addNode_Z(String[] node, double parameter) {
      // 添加普通节点
      if (node.length == 1) {
        SimpleCFGNode tempNode = new SimpleCFGNode();
        tempNode.setName(node[0]);
        tempNode.setType(ConstantValue.NORMAL_NODE);
        this.path.addNode(tempNode);
      } else if (node.length == 3) {
        assert (node[2].startsWith("expression"));
        // 当分支函数插桩结果为异常值时，转换为Java对应形式
        if (node[1].contains("nan")) {
          node[1] = node[1].replace("nan", "NaN");
        } else if (node[1].contains("inf")) {
          node[1] = node[1].replace("inf", "Infinity");
        }

        int pathSize = this.getPathSize();
        // 复合约束,在同一分支节点上按照条件表达式添加相应的分支函数值
        if (pathSize != 0 && this.path.getPath().get(pathSize - 1).getName().equals(node[0])) {
          Coodinate c = new Coodinate(parameter, Double.parseDouble(node[1]));
          this.path.getPath().get(pathSize - 1).addValue(node[2], c);
        }
        // 添加新的分支节点
        // 循环节点第二次加入时可以考虑当做普通节点加入
        else {
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
}
