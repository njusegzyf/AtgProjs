package cn.nju.seg.atg.util;

import java.util.ArrayList;
import java.util.List;

import cn.nju.seg.atg.model.Interval;
import cn.nju.seg.atg.model.SearchTask;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.pathParse.ZpathUtil;

/**
 * path-oriented test data generation
 * 
 * @version 0.1
 * @author zy
 * @author Zhang Yifan
 */
public class PCATG extends ATG {

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
          // 通过变量相关性表，获得最大路径最远分支与当前搜索变量的相关性信息，
          // 若无关，则停止对当前变量的搜索
          // String divergenceNodeName = maxPath.getDivergenceNode().getName();
          // if(!table.getRelevantInfo(divergenceNodeName, paramIndex)){
          // break;
          // }
          // 确定当前搜索区间的左右边界值
          Interval maxInterval = maxInterval();
          TestBuilder.leftSearchBoundary = maxInterval.getLeftBoundary();
          TestBuilder.rightSearchBoundary = maxInterval.getRightBoundary();
          /************************************************************************/
          // 从自变参数衍生池中衍生新的自变参数
          List<Double> newParameterList = maxPath.getNewParameterList(paramIndex);
          // 更新自变参数衍生池
          List<Double> newParameters = updateParameterList(newParameterList);
          /*************************************************************************/
          parameterNum = TestBuilder.parameterList.size();
          if (newParameters.size() < 1)
            break;
          // 判断衍生出的新输入向量中是否存在覆盖目标路径的
          int newParameterNum = newParameters.size();
          /********************************
           * 所有新衍生出的输入变量均可以并行执行 *
           ********************************/
          // CompletionService<Boolean> service = new ExecutorCompletionService<Boolean>(Builder.exec);
          // int taskNum = 0;
          // List<newParameterRunnable> taskList = new ArrayList<newParameterRunnable>();
          // List<Double> times = new ArrayList<Double>();
          for (int i = 0; i < newParameterNum; i++) {
            // 读取一个衍生出的新输入向量
            parameters[paramIndex] = newParameters.get(i);
            isCoveredTargetPath = getPathCoveredCondition(maxPath, paramIndex, pathFile);
            // //记录目标程序被执行一次
            // Builder.function_frequency++;
            // //并行执行下，每次执行改变一下输出路径
            // taskNum++;
            // //读取一个衍生出的新输入向量
            // Builder.parameters[paramIndex] = newParameterList.get(i);
            // double parameter = newParameterList.get(i);
            // double[] params = new double[NUM_OF_PARAM];
            // System.arraycopy(Builder.parameters, 0, params, 0, NUM_OF_PARAM);
            // //执行函数
            // newParameterRunnable task = new newParameterRunnable(maxPath, parameter, taskNum, params);
            // taskList.add(task);
            // service.submit(task);
          }
          // for (int i = 0; i < taskNum; i++){
          // try {
          // if(service.take().get()){
          // isCoveredTargetPath = true;
          //// break;
          // }
          // } catch (InterruptedException e) {
          // e.printStackTrace();
          // } catch (ExecutionException e) {
          // e.printStackTrace();
          // }
          // }
          // for(newParameterRunnable task : taskList){
          // times.add(task.executeTime());
          // }
          // Builder.function_time += Math.round(StaticMethod.getAverage(times));
        }
      }
    }
    
    // @since 0.1 extra local var
    final CFGPath testPath = TestBuilder.allPaths.get(pathIndex);
    
    // 返回结果
    if (isCoveredTargetPath) {
      testPath.setCovered(true);
      testPath.setOptimalParams(maxPath.getOptimalParams());
      testPath.setNumOfCoveredNodes(maxPath.coveredPathLength());
      testPath.setEndCoveredNodeName(maxPath.getEndNodeName());

      /***************************************
       * 显示当前路径上所有分支节点上的线性拟合函数 *
       ***************************************/
      // maxPath.showCLF(pathIndex, Builder.parameters[paramIndex]);

      return (pathIndex + 1);
    } else {
      // 获取路径覆盖度值最高的输入数据
      double[] optimalInput = new double[NUM_OF_PARAM];
      System.arraycopy(parameters, 0, optimalInput, 0, NUM_OF_PARAM);
      double priority = maxPath.pathCoverageEvaluation();

      // FIXME @since 0.1, for stat and turnLogic, in some cases, `maxPath` may be empty and thus the following statement throws `NullPointerException`
      // optimalInput[paramIndex] = maxPath.getDivergenceNode().getOptimal();
      // simply skip this if the `maxPath` is empty
      if (maxPath.getDivergenceNode() != null) {
        optimalInput[paramIndex] = maxPath.getDivergenceNode().getOptimal();
      }

      testPath.setCovered(false);
      if (testPath.getNumOfCoveredNodes() < maxPath.getPath().size()) {
        testPath.setOptimalParams(optimalInput);
        testPath.setNumOfCoveredNodes(maxPath.coveredPathLength());
        testPath.setEndCoveredNodeName(maxPath.getEndNodeName());
      }
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

      /***************************************
       * 显示当前路径上所有分支节点上的线性拟合函数 *
       ***************************************/
      // int pathShown[] = {1,2};
      // for (int i=0; i<pathShown.length; i++) {
      // if (pathIndex+1 == pathShown[i]) {
      // maxPath.showCLF(pathIndex, Builder.parameters[paramIndex], "apl1>wd3");
      // }
      // }

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
    CFGPath excutedPath = ZpathUtil.readPath_Z(parameters[paramIndex], pathFile);
    TestBuilder.ioTime += System.currentTimeMillis() - startTime;

    // 计算目标路径被当前输入向量覆盖到的部分
    CFGPath coveredPath = excutedPath.getCoveredPath(CoverageCriteria.targetPath, parameters);
    // 更新目标路径能被覆盖到的最长部分路径
    maxPath.update(coveredPath, parameters);
    // 判断是否已覆盖目标路径
    boolean isCoveredTargetPath = maxPath.isTargetPath(CoverageCriteria.targetPath);

    // FIXME @since 0.1 In 'atg-pc', it seems that the `PathCoverage` is responsible for removing paths in `TestBuilder`,
    // so a simple fix is skip the removing paths processing if the last action is `atg-pc`.
    if (!CoverageCriteria.lastAction.equals("atg-pc")) {
      if (!isCoveredTargetPath) {
        // 移除其他未执行过ATG的路径中被当前输入向量覆盖的那条
        removeUncheckPath(excutedPath, parameters);
        // 移除已执行过ATG后仍未被覆盖的路径集合中被当前输入向量覆盖的那条
        removeUncoveredPath(excutedPath, parameters);
      }
    }

    return isCoveredTargetPath;
  }

  /**
   * 计算目标路径被覆盖到的最长部分路径
   * 
   * @param excutedPaths
   * @param targetPath
   * @return coveredPath
   */
  public static CFGPath getCoveredPath(List<CFGPath> excutedPaths, CFGPath targetPath, double[] parameters) {
    CFGPath coveredPath = new CFGPath();

    int size = excutedPaths.size();
    for (int i = 0; i < size; i++) {
      CFGPath tempPath = excutedPaths.get(i).getCoveredPath(targetPath, parameters);
      if (tempPath.getPath().size() > coveredPath.getPath().size()) {
        coveredPath = tempPath;
      }
    }

    return coveredPath;
  }

  /**
   * 移除尚未执行ATG的路径集合中已被覆盖到的一条路径
   * 如果当前输入向量覆盖到uncheckedPaths中的某一条
   * 则将该条路径移除，并标记为覆盖
   * 
   * @param path
   * @return
   */
  public synchronized static boolean removeUncheckPath(CFGPath path, double[] parameters) {
    int uncheckedPathsSize = TestBuilder.uncheckedPaths.size();
    int i;
    for (i = 0; i < uncheckedPathsSize; i++) {
      if (path.isEqual(TestBuilder.uncheckedPaths.get(i).getPath())) {
        break;
      }
    }

    if (i < uncheckedPathsSize) {
      PathCoverage.countOfCoveredPath++;
      int pathIndex = PathCoverage.getPathNum(TestBuilder.uncheckedPaths.get(i));
      PathCoverage.strCoveredPath += pathIndex + ",";

      final CFGPath cfgPath = TestBuilder.allPaths.get(pathIndex - 1);
      cfgPath.setCovered(true);
      cfgPath.setOptimalParams(parameters);
      cfgPath.setEndCoveredNodeName(cfgPath.getEndNodeName());
      cfgPath.setNumOfCoveredNodes(cfgPath.coveredPathLength());

      // 添加已覆盖路径到coveredPaths
      TestBuilder.coveredPaths.add(TestBuilder.uncheckedPaths.get(i).clonePath());

      // 移除
      TestBuilder.uncheckedPaths.remove(i);
      return true;
    } else
      return false;
  }

  /**
   * 移除执行过ATG后仍未被覆盖到的路径集合中的一条路径
   * 如果当前输入向量覆盖到uncoveredPaths中的某一条
   * 则将该条路径移除，并标记为覆盖
   * 
   * @param paths
   * @return
   */
  public synchronized static boolean removeUncoveredPath(CFGPath path, double[] parameters) {
    int uncoveredPathsSize = TestBuilder.uncoveredPaths.size();
    int i;
    for (i = 0; i < uncoveredPathsSize; i++) {
      if (path.isEqual(TestBuilder.uncoveredPaths.get(i).getPath())) {
        break;
      }
    }

    if (i < uncoveredPathsSize) {
      PathCoverage.countOfCoveredPath++;
      int pathIndex = PathCoverage.getPathNum(TestBuilder.uncoveredPaths.get(i));
      PathCoverage.strCoveredPath += pathIndex + ",";

      final CFGPath cfgPath = TestBuilder.allPaths.get(pathIndex - 1);
      cfgPath.setCovered(true);
      cfgPath.setOptimalParams(parameters);
      cfgPath.setEndCoveredNodeName(cfgPath.getEndNodeName());
      cfgPath.setNumOfCoveredNodes(cfgPath.coveredPathLength());

      // 添加已覆盖路径到coveredPaths
      TestBuilder.coveredPaths.add(TestBuilder.uncoveredPaths.get(i).clonePath());

      // 移除
      TestBuilder.uncoveredPaths.remove(i);
      return true;
    } else
      return false;
  }
}
