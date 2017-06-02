package cn.nju.seg.atg.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import cn.nju.seg.atg.visitor.CFGNode;

/**
 * 为目标程序生成插桩程序
 * 
 * @author zy
 */
public class Instrument extends AbstractAST {

  /**
   * 用于存放已打印节点的链表
   */
  private List<Integer> nodePrinted;

  /**
   * 由函数转成的CFG树的开始节点
   */
  public CFGNode cfgStartNode;

  public Instrument() {

  }

  /**
   * 插桩程序
   * <p>
   * 在同目录下生成插桩后源码，例如a.cpp插桩后为a+.cpp
   * 
   * @param ifd
   */
  public void instrument(IFunctionDeclaration ifd) {
    // 收集被测程序中的函数调用信息
    this.staticParse(ifd);
    // 借助CDT插件，遍历函数节点ifd，生成CFG树
    this.buildCFG(ifd, CFGBuilder.visitFunCalls);

    // String fileRead = ifd.getLocationURI().toString().substring("file:".length()).replace("%20", " ");
    // String fileWritten = ifd.getLocationURI().toString().substring("file:".length()).replace("%20", " ").replace(".", "+.");
    // try {
    // RandomAccessFile raf = new RandomAccessFile(fileRead, "r");
    // FileWriter writer = new FileWriter(fileWritten);
    // raf.seek(0);
    // byte[] bytes = new byte[(int)raf.length()];
    // raf.read(bytes);
    // writer.write(new String(bytes));
    // raf.close();
    // writer.close();
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    this.cfgStartNode = CFGBuilder.function.getStartNode();
    this.instrumantation("");
  }

  /**
   * 插桩程序
   * 
   * @param filePath
   */
  private void instrumantation(String filePath) {
    this.nodePrinted = new ArrayList<Integer>();
    List<CFGNode> nodeList = new ArrayList<CFGNode>();
    this.getAllCFGNode(this.cfgStartNode, nodeList);
    // this.sortCFGNodes(nodeList);
    // int offsetIncreased = 0;
    int nodeListSize = nodeList.size();
    for (int i = 0; i < nodeListSize; i++) {
      System.out.println("node id:" + nodeList.get(i).getNodeId() + ",nodeNumber:" + nodeList.get(i).getNodeNumber() +
          ",offset:" + nodeList.get(i).getOffset());
      // if (nodeList.get(i).getBinaryExpression() != null)
      // {
      // IASTBinaryExpression iabe = nodeList.get(i).getBinaryExpression();
      // IASTNode[] iabeChildren = iabe.getChildren();
      // if (iabeChildren.length == 2)
      // {
      // IASTExpression iaeLeft = (IASTExpression) iabeChildren[0];
      // IASTExpression iaeRight = (IASTExpression) iabeChildren[1];
      // String instrument = "printf(\"node%d %d %s if\\r\\n\",";
      // if (nodeList.get(i).getSign() == 2)
      // {
      // instrument = "printf(\"node%d %d %s for\\r\\n\",";
      // }
      // instrument += nodeList.get(i).getNodeNumber()
      // + ","
      // + "((" + iaeLeft.getRawSignature() + ")"
      // + "-"
      // + "(" + iaeRight.getRawSignature() + "))"
      // + ","
      // + "\"" + iabe.getRawSignature() + "\""
      // + "),";
      // this.insertBeforeOffset(instrument, nodeList.get(i).getOffset()+offsetIncreased, filePath);
      // offsetIncreased = offsetIncreased + instrument.length();
      // }
      // }
      // else
      // {
      // String instrument = "printf(\"node%d\\r\\n\"," + nodeList.get(i).getNodeNumber() + ");\r\n\t";
      // this.insertBeforeOffset(instrument, nodeList.get(i).getOffset()+offsetIncreased, filePath);
      // offsetIncreased = offsetIncreased + instrument.length();
      // }
    }
  }

  /**
   * 获取所有的CFG节点（无序）
   * 为程序插桩做准备
   * 
   * @param node
   * @param nodeList
   */
  private void getAllCFGNode(CFGNode node, List<CFGNode> nodeList) {
    if (node != null) {
      if (node.getOffset() != -1 && !this.nodePrinted.contains(node.getOffset())) {
        CFGNode nodeTemp = new CFGNode();
        nodeTemp.setBinaryExpression(node.getBinaryExpression());
        nodeTemp.setNodeNumber(node.getNodeNumber());
        nodeTemp.setOffset(node.getOffset());
        nodeTemp.setSign(node.getSign());
        nodeTemp.setNodeId(node.getNodeId());
        nodeList.add(nodeTemp);
      }
      this.nodePrinted.add(node.getOffset());

      // 迭代
      List<CFGNode> children = node.getChildren();
      if (children != null) {
        for (int i = 0; i < children.size(); i++) {
          if (!this.nodePrinted.contains(children.get(i).getOffset())) {
            this.getAllCFGNode(children.get(i), nodeList);
          }
        }
      }
      this.getAllCFGNode(node.getIfChild(), nodeList);
      this.getAllCFGNode(node.getElseChild(), nodeList);
    }
  }
}
