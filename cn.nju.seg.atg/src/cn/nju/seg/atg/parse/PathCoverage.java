package cn.nju.seg.atg.parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.nju.seg.atg.model.Condition;
import cn.nju.seg.atg.model.Constraint;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.pathParse.PathUtil;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.MathFunc;
import cn.nju.seg.atg.util.PCATG;
import nju.seg.zhangyf.atg.AtgPluginSettings;

/**
 * path coverage
 * <p>
 * 生成所有待测试路径,对每一条路径做测试数据自动生成
 * 
 * @version 0.1 Move code that sets `atg-tsc` target node from {@link cn.nju.seg.atg.parse.AbstractAST#parse(IFunctionDeclaration)} to
 *          {@link #run(IFunctionDeclaration)}.
 *          Fix output constraint in `atg-tsc`.
 * @author zy
 * @author Zhang Yifan
 */
public class PathCoverage extends CoverageCriteria {

  public PathCoverage(final String actionName) {
    super(actionName);

    // @since 0.1, throw exception for actions that can not be handled
    Preconditions.checkArgument(actionName != null && (actionName.equals("atg-tsc") || actionName.equals("atg-pc")));
  }

  /**
   * 程序结束后，被覆盖到的路径数
   */
  public static int countOfCoveredPath;
  /**
   * 被覆盖到的路径编号集合
   */
  public static String strCoveredPath;

  /**
   * 根据静态分析生成的程序CFG，获取程序的路径集合
   * 
   * @since 0.1 Change from a instance method to a static method, and change to public for debug.
   */
  public static void buildPaths() {
    // 初始化已被覆盖的路径数为0
    countOfCoveredPath = 0;
    // 初始化已被覆盖的路径集合为空
    strCoveredPath = "";
    // 初始化未执行过ATG的路径集合
    TestBuilder.uncheckedPaths = PathUtil.getAllPaths(CFGBuilder.function.getStartNode());

    // @since 0.1 reuse these list by simply clear them

    // 初始化未找到可用输入向量的集合为空
    TestBuilder.uncoveredPaths.clear(); // = new ArrayList<CFGPath>();
    // 初始化找到可用输入向量的集合为空
    TestBuilder.coveredPaths.clear(); // = new ArrayList<CFGPath>();
    TestBuilder.allPaths.clear(); // = new ArrayList<CFGPath>();

    final int pathsSize = TestBuilder.uncheckedPaths.size();
    TestBuilder.pathsSize = pathsSize;

    // @since 0.1
    TestBuilder.uncheckedPaths.stream().map(path -> path.clonePath())
                              .forEach(path -> TestBuilder.allPaths.add(path));
    // for (int i = 0; i < pathsSize; i++) {
    // TestBuilder.allPaths.add(TestBuilder.uncheckedPaths.get(i).clonePath());
    // }

    System.out.println("number of paths in this function's CFCG: " + pathsSize);
  }

  /* (non-Javadoc)
   * @see cn.nju.seg.atg.parse.CoverageCriteria#run(org.eclipse.cdt.core.model.IFunctionDeclaration)
   */
  @Override
  public void run(IFunctionDeclaration ifd) {
    // 构建被测程序的CFG
    this.buildCFG(ifd);

    final StringBuilder resultStr = new StringBuilder();

    for (int indexOfRun = 1; indexOfRun <= TestBuilder.repetitionNum; indexOfRun++) {
      // 获取当前微秒时间，为计算插件运行时间做准备
      long start_time = System.currentTimeMillis();

      // @since 0.1, reset statistics data
      TestBuilder.resetForNewTestRepeation();

      // 获取程序的路径集合
      PathCoverage.buildPaths();

      // @since 0.1, only print in debug mode
      AtgPluginSettings.doIfDebug(() -> {
        for (final Condition condition : ConditionCoverage.conditions) {
          System.out.println(condition.getConstraint().getId() + "," + condition.getInfo());
        }
      });

      // 判断当前路径是否被覆盖：-1:未覆盖；0~+:被覆盖；
      int isCovered = -1;
      if (this.actionName.equals("atg-tsc")) {
        // @since 0.1 Set atg-tsc target node if not present
        if (Strings.isNullOrEmpty(TestBuilder.targetNode)) {
          TestBuilder.targetNode = "node2@" + ATG.callFunctionName;
        }

        // 获取目标语句所在路径编号
        boolean find = false;
        for (CFGPath path : TestBuilder.allPaths) {
          for (SimpleCFGNode node : path.getPath()) {
            if (node.getName().equals(TestBuilder.targetNode)) {
              find = true;
              break;
            }
          }
          if (find) {
            targetPath = path;
            break;
          }
        }

        // @since 0.1 throw if not find a target path
        if (!find) {
          throw new IllegalStateException("Can not find a target path that covers node: " + TestBuilder.targetNode);
        }

        // 获取目标路径编号
        int pathIndex = getPathNum(targetPath);
        // 执行ATG过程
        isCovered = new PCATG().generateTestData(pathIndex - 1);

        final long execute_time = System.currentTimeMillis() - start_time - TestBuilder.function_time;
        TestBuilder.totalTime[indexOfRun - 1] = (execute_time + TestBuilder.function_time) / 1000.0;
        TestBuilder.algorithmTime[indexOfRun - 1] = execute_time / 1000.0;
        TestBuilder.totalUncoverdPathsTime[indexOfRun - 1] = TestBuilder.uncoverdPathsTime;
        TestBuilder.totalFrequency[indexOfRun - 1] = TestBuilder.function_frequency;
        TestBuilder.findResult[indexOfRun - 1] = isCovered > -1 ? "Y" : "N";

        // @since 0.1, Remove `result`, use `resultStr` directly
        // 输出结果
        // final StringBuilder result = new StringBuilder();
        resultStr.append("----------------------------run" + indexOfRun + "----------------------------\n");
        resultStr.append("target node: " + TestBuilder.targetNode + "\n");

        // @since 0.1 The following code get the target node, and then prints the constraint of previous node.
        // This only works for some code like coral examples, where the target node 2 is the true branch, and the previous node 1 is the if expression.
        // result.append("target constraint: " + targetPath.getPath().get(targetPath.getNodeIndex(TestBuilder.targetNode) -
        // 1).getConstraint().getExpression().toString() + "\n");
        //
        // To fix it, only print the target constraint if find
        final Constraint targetConstraint = targetPath.getPath().get(targetPath.getNodeIndex(TestBuilder.targetNode) - 1).getConstraint();
        if (targetConstraint != null /* && targetConstraint.getExpression() != null */) {
          resultStr.append("target constraint: ");
          resultStr.append(targetConstraint.getExpression().toString());
          resultStr.append("\n");
        }

        resultStr.append("target path: " + printPath_simply(targetPath) + "\n");
        if (isCovered > -1) {
          resultStr.append("coverage result:\nfind covered input: ");
          int length = targetPath.getOptimalParams().length;
          resultStr.append("(");
          for (int k = 0; k < length - 1; k++) {
            resultStr.append(targetPath.getOptimalParams()[k] + ", ");
          }
          resultStr.append(targetPath.getOptimalParams()[length - 1] + ")");
          resultStr.append("\n");
        } else {
          resultStr.append("coverage result:\ncannot find covered input! the best input is:(" + targetPath.getNumOfCoveredNodes() + "," + targetPath.getEndCoveredNodeName() + ")");
          int length = targetPath.getOptimalParams().length;
          resultStr.append("(");
          for (int k = 0; k < length - 1; k++) {
            resultStr.append(targetPath.getOptimalParams()[k] + ", ");
          }
          resultStr.append(targetPath.getOptimalParams()[length - 1] + ")");
          resultStr.append("\n");
        }
        resultStr.append("\ntotal time: " + (execute_time + TestBuilder.function_time) / 1000.0 + " sec\n");
        resultStr.append("function time: " + TestBuilder.function_time / 1000.0 + " sec\n");
        resultStr.append("function execution frequency: " + TestBuilder.function_frequency + " times\n");
        resultStr.append("algorithm time: " + execute_time / 1000.0 + " sec\n");

        // @since 0.1
        resultStr.append("io time:\t" + TestBuilder.ioTime / 1000.0 + " sec\n");

        resultStr.append("\n");
      } else if (this.actionName.equals("atg-pc")) {
        int pathsSize = TestBuilder.uncheckedPaths.size();

        while (!TestBuilder.uncheckedPaths.isEmpty()) {
          // 获取目标路径编号
          int pathIndex = getPathNum(TestBuilder.uncheckedPaths.get(0));
          // 初始化目标路径
          targetPath = TestBuilder.uncheckedPaths.get(0);
          // printPath(targetPath);
          // 执行ATG过程
          isCovered = new PCATG().generateTestData(pathIndex - 1);

          final CFGPath pathCopy = TestBuilder.allPaths.get(getPathNum(targetPath) - 1).clonePath();
          if (isCovered > -1) {
            countOfCoveredPath++;
            strCoveredPath += isCovered + ",";
            TestBuilder.coveredPaths.add(pathCopy);
          } else {
            TestBuilder.uncoveredPaths.add(pathCopy);
          }
          // 移除已执行过ATG的路径
          TestBuilder.uncheckedPaths.remove(0);

          int countOfCheckedPath = pathsSize - TestBuilder.uncheckedPaths.size();
          // 打印路径覆盖进度
          System.out.println("已执行路径进度：");
          System.out.println(((countOfCheckedPath * 100) / pathsSize) + "%,");
        }

        final long execute_time = System.currentTimeMillis() - start_time - TestBuilder.function_time;
        TestBuilder.totalTime[indexOfRun - 1] = (execute_time + TestBuilder.function_time) / 1000;
        TestBuilder.algorithmTime[indexOfRun - 1] = execute_time / 1000;
        TestBuilder.totalUncoverdPathsTime[indexOfRun - 1] = TestBuilder.uncoverdPathsTime;
        TestBuilder.coveredRatio[indexOfRun - 1] = countOfCoveredPath;
        TestBuilder.totalFrequency[indexOfRun - 1] = TestBuilder.function_frequency;

        final List<Integer> pathIndex = new ArrayList<Integer>();
        for (int i = 0; i < TestBuilder.coveredPaths.size(); i++) {
          pathIndex.add(getPathNum(TestBuilder.coveredPaths.get(i)));
        }
        Collections.sort(pathIndex);
        TestBuilder.everyCoveredPaths[indexOfRun - 1] = pathIndex.toString();

        System.out.println("\n覆盖路径：");
        System.out.println(pathIndex.toString() + "\n");

        // 输出最终结果
        PathCoverage.printResult(indexOfRun, execute_time);
      }
    }

    // @since 0.1, move these output out of the inner loop, which should be printed once at the end
    final DecimalFormat df = new DecimalFormat("0.000");
    // @since 0.1, use joiner to join strings
    Joiner joinerOnTab = Joiner.on('\t');
    for (int i = 0; i < TestBuilder.repetitionNum; i++) {
      joinerOnTab.appendTo(resultStr,
                           TestBuilder.allPaths.size(),
                           TestBuilder.coveredRatio[i],
                           TestBuilder.algorithmTime[i],
                           df.format(TestBuilder.totalTime[i] - TestBuilder.algorithmTime[i]),
                           TestBuilder.totalTime[i],
                           TestBuilder.everyCoveredPaths[i]);
      resultStr.append('\n');
      // resultStr.append(TestBuilder.allPaths.size() + "\t" + TestBuilder.coveredRatio[i] + "\t"
      // + TestBuilder.algorithmTime[i] + "\t" + df.format(TestBuilder.totalTime[i] - TestBuilder.algorithmTime[i]) + "\t"
      // + TestBuilder.totalTime[i] + "\t" + TestBuilder.everyCoveredPaths[i] + "\n");
    }
    resultStr.append("best coverage:\t" + MathFunc.getMax(TestBuilder.coveredRatio) + " / " + TestBuilder.allPaths.size() + "\n");
    resultStr.append("average coverage:\t" + MathFunc.getAverage(TestBuilder.coveredRatio) + " / " + TestBuilder.allPaths.size() + "\n");

    // @since 0.1
    resultStr.append("Detail coverage:\n");
    // Note: `Arrays.asList(TestBuilder.coveredRatio)` will returns a list that contains one element which is the int array,
    // as it accepts `Object...` but not `int...`.
    joinerOnTab.appendTo(resultStr, IntStream.of(TestBuilder.coveredRatio)
                                             .boxed()
                                             .collect(Collectors.toList()));
    resultStr.append('\n');

    this.printTotalResult(resultStr);
  }

  /**
   * 输出最终结果
   */
  private static void printResult(int indexOfRun, double execute_time) {
    StringBuilder result = new StringBuilder();

    int numOfPath = TestBuilder.allPaths.size();
    for (int i = 0; i < numOfPath; i++) {
      result.append("/----------path " + (i + 1) + "-----------/\n");

      // @since 0.1, extra local var `cfgPath` and `cfgPathArray`
      final CFGPath cfgPath = TestBuilder.allPaths.get(i);
      ArrayList<SimpleCFGNode> cfgPathArray = cfgPath.getPath();

      result.append(cfgPathArray.get(0).getName());
      for (int j = 1; j < cfgPathArray.size(); j++) {
        result.append("->" + cfgPathArray.get(j).getName());
      }
      result.append("\n");

      // @since 0.1, extra local var `optimalParams`
      final double[] optimalParams = cfgPath.getOptimalParams();

      if (cfgPath.isCovered()) {
        result.append("find covered input：\n(");
        int length = optimalParams.length;
        for (int k = 0; k < length - 1; k++) {
          result.append(optimalParams[k] + ", ");
        }
        result.append(optimalParams[length - 1]);
        result.append(")\n\n");
      } else {
        // FIXME in some cases, the cfgPath's `endCoveredNodeName` and `optimalParams` may be null
        // For example, function turnLogic with path: node1@turnLogic, node2@turnLogic, expression@3, expression@4, node3@turnLogic
        // @since 0.1, skip print optimal input if not present.
        result.append("cannot find covered input");
        if (optimalParams != null) {
          result.append(", the optimal one：（" + cfgPath.getNumOfCoveredNodes() + "," + cfgPath.getEndCoveredNodeName() + "）\n(");
          int length = optimalParams.length;
          for (int k = 0; k < length - 1; k++) {
            result.append(optimalParams[k] + ",");
          }
          result.append(optimalParams[length - 1]);
          result.append(")");
        }
        result.append("\n\n");
        // result.append("cannot find covered input,the optimal one：（" + cfgPath.getNumOfCoveredNodes() + "," + cfgPath.getEndCoveredNodeName() + "）\n(");
        // int length = cfgPath.getOptimalParams().length;
        // for (int k = 0; k < length - 1; k++) {
        // result.append(cfgPath.getOptimalParams()[k] + ",");
        // }
        // result.append(cfgPath.getOptimalParams()[length - 1]);
        // result.append(")\n\n");
      }
    }

    result.append("\ncoverage ratio:" + TestBuilder.coveredPaths.size() + " / " + TestBuilder.allPaths.size() + "\n");

    // @since 0.1 Use stream to create `pathIndex`
    final List<Integer> pathIndex = TestBuilder.coveredPaths.stream()
                                                            .map(cfgPath -> PathCoverage.getPathNum(cfgPath))
                                                            .sorted()
                                                            .collect(Collectors.toList());
    // final List<Integer> pathIndex = new ArrayList<Integer>(TestBuilder.coveredPaths.size());
    // for (int i = 0; i < TestBuilder.coveredPaths.size(); i++) {
    // pathIndex.add(getPathNum(TestBuilder.coveredPaths.get(i)));
    // }
    // Collections.sort(pathIndex);
    result.append("\ncovered paths:");
    result.append(pathIndex.toString());
    result.append("\n");

    // @since 0.1, collect and print node coverage
    // first collect all nodes in every covered path as a set, and then sort the set
    final List<String> coveredNodes = TestBuilder.coveredPaths.stream()
                                                              .flatMap(cfgPath -> {
                                                                return cfgPath.getPath().stream()
                                                                              .map(node -> node.getName());
                                                              })
                                                              .collect(Collectors.toSet())
                                                              .stream()
                                                              .sorted()
                                                              .collect(Collectors.toList());
    result.append("covered nodes:\t");
    Joiner.on(',').appendTo(result, coveredNodes);
    result.append("\n");

    result.append("total time:" + (execute_time + TestBuilder.function_time) / 1000.0 + " sec\n");
    result.append("function time:" + TestBuilder.function_time / 1000.0 + " sec（" + TestBuilder.function_frequency + " times）\n");
    result.append("algorithm time:" + execute_time / 1000.0 + " sec\n");

    // @since 0.1
    result.append("io time: " + TestBuilder.ioTime / 1000.0 + " sec\n");

    // @since 0.1
    // String folderPath = "/home/zy/Desktop/" + ATG.resultFolder + "/result/" + ATG.callFunctionName;
    // is change to:
    final String folderPath = Paths.get(ATG.resultFolder).resolve(ATG.callFunctionName).toAbsolutePath().toString();

    final File folder = new File(folderPath);
    if (!folder.exists()) {
      folder.mkdirs();
    }
    final String resultPath = folderPath + "/" + ATG.callFunctionName + ".result(" + indexOfRun + ")";

    // @since 0.1, use try-with-resource
    try (final FileOutputStream out = new FileOutputStream(resultPath);
        final BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(out))) {
      bufferedwriter.write(result.toString());
    } catch (final IOException ignored) {}
    // try {
    // FileOutputStream out = new FileOutputStream(resultPath);
    // BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(out));
    // bufferedwriter.write(result.toString());
    // bufferedwriter.flush();
    // bufferedwriter.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
  }

  /**
   * 获取路径编号（从1开始）
   * 
   * @param paths
   * @param path
   * @return
   */
  public static int getPathNum(CFGPath path) {
    int pathsSize = TestBuilder.allPaths.size();
    for (int i = 0; i < pathsSize; i++) {
      if (TestBuilder.allPaths.get(i).isEqual(path.getPath())) {
        return (i + 1);
      }
    }

    return -1;
  }

  /**
   * @since 0.1
   */
  public static final String TARGET_NODE_COVERAGE_ACTION_NAME = "atg-tsc";

  /**
   * @since 0.1
   */
  public static final String PATH_COVERAGE_ACTION_NAME = "atg-pc";
}
