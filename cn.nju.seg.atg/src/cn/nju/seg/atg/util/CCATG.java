package cn.nju.seg.atg.util;

import java.util.ArrayList;
import java.util.List;

import cn.nju.seg.atg.model.Interval;
import cn.nju.seg.atg.model.SearchTask;
import cn.nju.seg.atg.parse.ConditionCoverage;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.pathParse.PathFragmentUtil;

/**
 * condition-oriented test data generation
 * 
 * @version 0.1
 * @author zy
 * @author Zhang Yifan
 */
public class CCATG extends ATG {

  @Override
  protected int generateTestDataForParam(int pathIndex, int paramIndex, String pathFile, double[] seed, int round, int nextRoundSeedIndex, int max_num_of_predict_param) {
    currentSearchParamIndex = paramIndex;
    // 初始化覆盖到的最长路径片段为空
    CFGPath maxPath = new CFGPath();
    // 初始化自变参数产生池为空
    TestBuilder.parameterList = new ArrayList<Double>();
    TestBuilder.autoIncreasedParameterList = new ArrayList<Double>();
    // 初始化参数值
    initialParameter(seed);
    // 添加当前自变参数到"自变参数产生池"
    TestBuilder.parameterList.add(parameters[paramIndex]);
    // 标记为非线性拟合函数预测的
    TestBuilder.autoIncreasedParameterList.add(parameters[paramIndex]);
    // 判断是否已覆盖目标路径
    boolean isCoveredTargetPath = getPathCoveredCondition(maxPath, paramIndex, pathFile);

    if (!isCoveredTargetPath) {
      // 改变当前自变参数为另一随机值，生成第二个随机输入向量
      setRandom(paramIndex, parameters[paramIndex], ConstantValue.INTERVAL_LEFT_OPEN);
      // 添加当前自变参数到"自变参数产生池"
      int index = parameters[paramIndex] > TestBuilder.parameterList.get(0) ? 1 : 0;
      TestBuilder.parameterList.add(index, parameters[paramIndex]);
      // 标记为非线性拟合函数预测的
      TestBuilder.autoIncreasedParameterList.add(parameters[paramIndex]);
      // 判断是否已覆盖目标路径
      isCoveredTargetPath = getPathCoveredCondition(maxPath, paramIndex, pathFile);

      if (!isCoveredTargetPath) {
        int parameterNum = 0;
        while (parameterNum < max_num_of_predict_param && !isCoveredTargetPath) {
          // 确定当前搜索区间的左右边界值
          Interval maxInterval = maxInterval();
          TestBuilder.leftSearchBoundary = maxInterval.getLeftBoundary();
          TestBuilder.rightSearchBoundary = maxInterval.getRightBoundary();
          // 从自变参数衍生池中衍生新的自变参数
          List<Double> newParameterList = maxPath.getNewParameterListForCondition(paramIndex, ConditionCoverage.targetCondition, !ConditionCoverage.targetCondition.isTbranchCovered());
          // 更新自变参数衍生池
          List<Double> newParameters = updateParameterList(newParameterList);
          parameterNum = TestBuilder.parameterList.size();
          if (newParameters.size() < 1)
            break;
          // 判断衍生出的新输入向量中是否存在覆盖目标路径的
          int newParameterNum = newParameters.size();
          for (int i = 0; i < newParameterNum; i++) {
            // 读取一个衍生出的新输入向量
            parameters[paramIndex] = newParameters.get(i);
            isCoveredTargetPath = getPathCoveredCondition(maxPath, paramIndex, pathFile);
          }
        }
      }
    }
    // 返回结果
    if (isCoveredTargetPath) {
      return (pathIndex + 1);
    } else {
      // 获取路径覆盖度值最高的输入数据
      double[] optimalInput = new double[NUM_OF_PARAM];
      System.arraycopy(parameters, 0, optimalInput, 0, NUM_OF_PARAM);
      double priority = maxPath.pathCoverageEvaluation();
      optimalInput[paramIndex] = maxPath.getDivergenceNode().getOptimal();

      if (round == -1) {
        seedArray = optimalInput;
      }
      // 第一轮，且是以随机值开始搜索
      else if (seed == null) {
        seeds[nextRoundSeedIndex] = new SearchTask(optimalInput, priority, paramIndex, round);
        NumOfCoveredNodes[paramIndex] = maxPath.getPath().size();
      }
      // 第一轮，且是以指定值开始搜索
      else if (round == 0) {
        if (maxPath.getPath().size() > NumOfCoveredNodes[paramIndex]) {
          seeds[nextRoundSeedIndex] = new SearchTask(optimalInput, priority, paramIndex, round);
        }
        NumOfCoveredNodes[paramIndex] = maxPath.getPath().size();
      }
      // 非第一轮
      else {
        nextSeeds[nextRoundSeedIndex] = new SearchTask(optimalInput, priority, paramIndex, round);
      }

      return -1;
    }
  }

  /**
   * 获取路径覆盖情况
   * 
   * @param maxPath
   * @param paramIndex
   * @param pathFile
   * @return isTargetPath
   */
  private static boolean getPathCoveredCondition(CFGPath maxPath, int paramIndex, String pathFile) {
    // 执行程序
    CallFunction cf = new CallFunction(parameters, pathFile);
    cf.callFunction();
    TestBuilder.function_time += cf.executeTime();
    TestBuilder.function_frequency++;

    // @since 0.1 record execution time in {@link cn.nju.seg.atg.parse.TestBuilder#totalIoTime}.
    final long startTime = System.currentTimeMillis();
    // 读取路径
    CFGPath excutedPath = PathFragmentUtil.readPathFragment(paramIndex, parameters, pathFile);
    TestBuilder.ioTime += System.currentTimeMillis() - startTime;

    // 计算目标路径被当前输入向量覆盖到的部分
    CFGPath coveredPath = excutedPath.getCoveredPathFragment(CoverageCriteria.targetPath);
    // System.out.println(maxPath.getPath().size()+", "+coveredPath.getPath().size());
    // 更新目标路径能被覆盖到的最长部分路径
    maxPath.update(coveredPath, parameters);
    // 判断是否已覆盖目标条件
    return ConditionCoverage.targetCondition.isCovered();
  }
}
