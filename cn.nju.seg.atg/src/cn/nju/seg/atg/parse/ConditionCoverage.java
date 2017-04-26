package cn.nju.seg.atg.parse;

import java.util.*;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import cn.nju.seg.atg.model.Condition;
import cn.nju.seg.atg.pathParse.PathFragmentUtil;
import cn.nju.seg.atg.util.CCATG;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.visitor.CFGNode;

/**
 * condition coverage
 * <p>
 * 生成所有待测试约束条件，按顺序对条件生成测试数据
 * 
 * @version 0.1
 * 
 * @author zy
 * @author Zhang Yifan
 */
public class ConditionCoverage extends CoverageCriteria {

  private static int RELEVANT_PATH_NUM = 3;

  public ConditionCoverage(String actionName) {
    super(actionName);
  }

  /**
   * 程序中存在的条件节点集合
   */
  public static List<Condition> conditions;
  /**
   * 未被全部覆盖的条件集合
   */
  public static Map<Integer, Condition> uncoveredConditions;
  /**
   * 记录程序中的所有节点
   */
  public static Map<Integer, CFGNode> allNodes;
  /**
   * 目标条件
   */
  public static Condition targetCondition;

  @Override
  public void run(IFunctionDeclaration ifd) {
    // 构建被测程序的CCFG
    this.buildCFG(ifd);
    System.out.println("number of conditions in all of the functions: " + conditions.size());
    uncoveredConditions = new HashMap<Integer, Condition>();
    // for(Entry<Integer, CFGNode> entry : allNodes.entrySet())
    // System.out.println(entry.getKey()+", "+entry.getValue().getNodeId()+", "+entry.getValue().getBinaryExpression());
    for (Condition condition : conditions) {
      uncoveredConditions.put(condition.getId(), condition);
      System.out.println(condition.getConstraint().getId() + "," + condition.getInfo());
    }
    StringBuilder resultStr = new StringBuilder();
    for (int indexOfRun = 1; indexOfRun <= TestBuilder.repetitionNum; indexOfRun++) {
      // 获取当前微秒时间，为计算插件运行时间做准备
      long start_time = System.currentTimeMillis();

      // @since 0.1
      TestBuilder.resetForNewTestRepeation();

      List<CFGPath> relevantPaths = null;
      Random random = new Random();
      int rand, covered;
      for (Condition cond : conditions) {
        if (!uncoveredConditions.containsKey(cond.getId()))
          continue;
        targetCondition = cond;
        System.out.println(cond.getInfo());
        // 获取目标条件所在节点的相关路径集合
        relevantPaths = PathFragmentUtil.getAllRelevantPaths(allNodes.get(cond.getNodeIndex()));
        List<CFGPath> executePaths = new ArrayList<CFGPath>();
        // 按照路径长度对相关路径集合进行排序
        Collections.sort(relevantPaths, new Comparator<CFGPath>() {
          @Override
          public int compare(CFGPath path1, CFGPath path2) {
            return Integer.compare(path1.getPath().size(), path2.getPath().size());
          }
        });

        executePaths.add(relevantPaths.remove(0));
        for (int i = 0; i < RELEVANT_PATH_NUM - 1 && i < relevantPaths.size(); i++) {
          rand = random.nextInt(relevantPaths.size());
          executePaths.add(relevantPaths.remove(rand));
        }
        for (CFGPath path : executePaths) {
          targetPath = path;
          // 执行ATG过程
          covered = new CCATG().generateTestData(-1);
          if (covered > 1)
            break;
        }
      }

      long execute_time = System.currentTimeMillis() - start_time - TestBuilder.function_time;
      TestBuilder.totalTime[indexOfRun - 1] = (execute_time + TestBuilder.function_time) / 1000;
      TestBuilder.algorithmTime[indexOfRun - 1] = execute_time / 1000;
      TestBuilder.totalFrequency[indexOfRun - 1] = TestBuilder.function_frequency;
      // 输出结果
      StringBuilder result = new StringBuilder();
      result.append("----------------------------run" + indexOfRun + "----------------------------\n");
      result.append("constraints number: " + conditions.size() + "\n");
      int coveredCount = 0;
      for (int cindex = 0; cindex < conditions.size(); cindex++) {
        Condition condition = conditions.get(cindex);
        result.append("\n/----------Condition" + cindex + "-----------\\\n");
        result.append("target condition info: " + condition.getInfo() + "\n");
        if (condition.isTbranchCovered()) {
          coveredCount++;
          result.append("T branch coverage result: find covered input: ");
          int length = condition.getTcoveredInput().length;
          result.append("(");
          for (int k = 0; k < length - 1; k++) {
            result.append(condition.getTcoveredInput()[k] + ", ");
          }
          result.append(condition.getTcoveredInput()[length - 1] + ")");
          result.append("\n");
        } else {
          result.append("T branch coverage result: cannot find covered input!");
          result.append("\n");
        }
        if (condition.isFbranchCovered()) {
          coveredCount++;
          result.append("F branch coverage result: find covered input: ");
          int length = condition.getFcoveredInput().length;
          result.append("(");
          for (int k = 0; k < length - 1; k++) {
            result.append(condition.getFcoveredInput()[k] + ", ");
          }
          result.append(condition.getFcoveredInput()[length - 1] + ")");
          result.append("\n");
        } else {
          result.append("F branch coverage result: cannot find covered input!");
          result.append("\n");
        }
      }
      result.append("\ncoverage result:\t" + coveredCount + " / " + 2 * conditions.size() + "\n");
      result.append("coverage ratio:\t" + ((double) coveredCount / (2 * conditions.size())) * 100 + "%\n");
      TestBuilder.findResult[indexOfRun - 1] = coveredCount + "/" + 2 * conditions.size();
      result.append("total time:\t" + (execute_time + TestBuilder.function_time) / 1000.0 + " sec\n");
      result.append("function time:\t" + TestBuilder.function_time / 1000.0 + " sec\n");

      // @since 0.1
      result.append("io time:\t" + TestBuilder.ioTime / 1000.0 + " sec\n");

      result.append("function execution frequency:\t" + TestBuilder.function_frequency + " times\n");
      result.append("algorithm tiem:\t" + execute_time / 1000.0 + " sec\n");
      resultStr.append(result + "\n");
    }
    printTotalResult(resultStr);
  }
}
