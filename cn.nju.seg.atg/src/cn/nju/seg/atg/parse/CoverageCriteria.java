package cn.nju.seg.atg.parse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Charsets;

import cn.nju.seg.atg.gui.AtgConsole;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.MathFunc;

/**
 * @version 0.1
 * 
 * @author zy
 * @author Zhang Yifan
 */
public abstract class CoverageCriteria extends AbstractAST {

  // FIXME This variable is used to fix PCATG problem.
  /**
   * @since 0.1
   */
  public static String lastAction = "";

  /**
   * 需要调用的action的名字
   */
  protected String actionName;

  /**
   * 目标路径
   */
  public static CFGPath targetPath;

  /**
   * 带参构造函数
   * 
   * @param actionName
   */
  public CoverageCriteria(String actionName) {
    this.actionName = actionName;

    CoverageCriteria.lastAction = this.actionName;
  }

  /**
   * 借助CDT提供的AST,构建被测程序的CFG
   * 
   * @since 0.1 Change to public in order to just build cfg for debug (print paths).
   * @param ifd
   */
  public void buildCFG(IFunctionDeclaration ifd) {
    // 收集被测程序中的函数调用信息
    this.staticParse(ifd);
    // 借助CDT插件，遍历函数节点ifd，生成CFG树
    this.buildCFG(ifd, CFGBuilder.visitFunCalls);
    setParameters();
  }

  /**
   * 输出统计结果
   * 
   * @param resultStr
   */
  protected void printTotalResult(StringBuilder resultStr) {
    resultStr.append("----------------------------statistical result----------------------------\n");

    resultStr.append("average time:\t" + MathFunc.getAverage(TestBuilder.totalTime) + "\n");
    resultStr.append("best time:\t" + MathFunc.getMin(TestBuilder.totalTime) + "\n");
    resultStr.append("average frequency:\t" + MathFunc.getAverage(TestBuilder.totalFrequency) + "\n");
    resultStr.append("coverage result:\t" + Arrays.toString(TestBuilder.findResult) + "\n");
    resultStr.append("parameter setting: " + "MAX_NUM_OF_PREDICT_PARAM=" + ATG.MAX_NUM_OF_PREDICT_PARAM
        + ", MAX_NUM_OF_GENERATE_CYCLE=" + ATG.MAX_NUM_OF_GENERATE_CYCLE + ", PREDICT_BOUNDARY=" + ATG.PREDICT_BOUNDARY + "\n");
    resultStr.append("search strategy: " + (ATG.SEARCH_STRATEGY == 0 ? "SEARCH_STRATEGY_ALL" : "SEARCH_STRATEGY_ONE_BY_ONE") + "\n");

    // @since 0.1 change output path, use java nio and Guava `Files`
    // output path is changed from
    // String resultPath = "/home/zy/Desktop/" + ATG.resultFolder + ATG.callFunctionName + ".result";
    // to
    final Path resultFolderPath = Paths.get(ATG.resultFolder);
    try {
      Files.createDirectories(resultFolderPath);
    } catch (IOException ignored) {
      return;
    }

    final Path resultPath = resultFolderPath.resolve(ATG.callFunctionName + ".result.txt").toAbsolutePath();

    try {
      com.google.common.io.Files.asCharSink(resultPath.toFile(), Charsets.US_ASCII)
                                .write(resultStr);
    } catch (IOException ignored) {}

    // final String resultPath = resultFolderPath.resolve(ATG.callFunctionName + ".result").toAbsolutePath().toString();
    // try {
    // FileOutputStream out = new FileOutputStream(resultPath);
    // BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(out));
    // bufferedwriter.write(resultStr.toString());
    // bufferedwriter.flush();
    // bufferedwriter.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    System.out.println(CFGBuilder.funcName + "finished");
    AtgConsole.consoleStream.println(CFGBuilder.funcName + "finished");
  }

  /**
   * 获取输入变量的类型（仅区分int与double)
   */
  private static void setParameters() {
    for (int i = 0; i < ATG.NUM_OF_PARAM; i++) {
      if (CFGBuilder.parameterTypes[i].equals("int")) {
        CFGBuilder.parameterTypes[i] = "int";
      } else if (CFGBuilder.parameterTypes[i].equals("const int")) {
        CFGBuilder.parameterTypes[i] = "int";
      } else if (CFGBuilder.parameterTypes[i].equals("int&")) {
        CFGBuilder.parameterTypes[i] = "int";
      } else if (CFGBuilder.parameterTypes[i].equals("long")) {
        CFGBuilder.parameterTypes[i] = "int";
      } else if (CFGBuilder.parameterTypes[i].equals("double")) {
        CFGBuilder.parameterTypes[i] = "double";
      } else if (CFGBuilder.parameterTypes[i].equals("float")) {
        CFGBuilder.parameterTypes[i] = "double";
      } else if (CFGBuilder.parameterTypes[i].equals("short")) {
        CFGBuilder.parameterTypes[i] = "int";
      } else if (CFGBuilder.parameterTypes[i].equals("unsigned short")) {
        CFGBuilder.parameterTypes[i] = "int";
      } else if (CFGBuilder.parameterTypes[i].equals("unsigned long")) {
        CFGBuilder.parameterTypes[i] = "int";
      } else {
        CFGBuilder.parameterTypes[i] = "double";
      }
    }
  }

  /**
   * 输出一条路经
   * 
   * @param path
   */
  public static void printPath(CFGPath path) {
    if (path.getPath().get(0).isBranchNode()) {
      SimpleCFGNode node = path.getPath().get(0);
      System.out.print(node.getName());
      char isTrue = node.isTrue() ? 'T' : 'F';
      System.out.print("(isTrue:" + isTrue + ", ");
      System.out.print("constraint:" + node.getConstraint().getExpression() + ")");
    } else {
      System.out.print(path.getPath().get(0).getName());
    }
    for (int j = 1; j < path.getPath().size(); j++) {
      SimpleCFGNode node = path.getPath().get(j);
      System.out.print("->" + node.getName());
      if (node.isBranchNode()) {
        char isTrue = node.isTrue() ? 'T' : 'F';
        System.out.print("(isTrue:" + isTrue + ", ");
        System.out.print("constraint:" + node.getConstraint().getExpression() + ")");
      }
    }

    System.out.println();
  }

  public static String printPath_simply(CFGPath path) {
    String pathStr = path.getPath().get(0).getName();
    for (int j = 1; j < path.getPath().size(); j++) {
      pathStr += "->" + path.getPath().get(j).getName();
    }
    return pathStr;
  }

  public abstract void run(IFunctionDeclaration ifd);

}
