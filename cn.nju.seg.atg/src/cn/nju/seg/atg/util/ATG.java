package cn.nju.seg.atg.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

// import com.google.common.io.Files;

import cn.nju.seg.atg.callCPP.CallCPP;
import cn.nju.seg.atg.model.Interval;
import cn.nju.seg.atg.model.SearchTask;
import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.relevant.TableData;

/**
 * Since 0.1, change some static final fields to be non-final, which allows to set them on the fly.
 * 
 * @version 0.1
 * @author zy
 * @author Zhang Yifan
 */
public abstract class ATG {

  /**
   * 目标函数的参数个数
   */
  public static int NUM_OF_PARAM;

  /**
   * 生成随机初始值的取值点
   */
  public static double START_POINT = 0.0;
  // public static double[] LEFT_BOUNDARY = {-Double.MAX_VALUE, 14, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
  // public static double[] RIGHT_BOUNDARY = {Double.MAX_VALUE, 16, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};

  /**
   * 最大步长
   * <p>
   * 从自变参数生成池中衍生新的值时，向两边扩展的最大步长
   * <p>
   * 向外扩展的步长为(0,MAX_STEP)内的随机值
   */
  public static double MAX_STEP = 5.0;

  /**
   * 用户定制的输入参数值
   * 
   * @since 0.1 Change to non-final to allow modification at run time.
   */
  private static double[] CUSTOMIZED_PARAMS = {};

  /**
   * @since 0.1
   */
  public static void setCustomizedParams(final double[] v) {
    Preconditions.checkNotNull(v);

    ATG.CUSTOMIZED_PARAMS = v;
  }

  // for Opti helicalValley, solutions are (0, -1, 0) and (0, 1, 0)
  // private static final double[] CUSTOMIZED_PARAMS = { 0.0, 1.0, 0.0 };

  // for Opti wood, solutions are (1.0, 1.0, 1.0, 1.0)
  // private static final double[] CUSTOMIZED_PARAMS = { 1.0, 1.1, 1.1, 1.1 };

  // for Tsafe TurnLogic, x0 == x1 && y0 == y1 (node3) can be solved by provide params
  // private static final double[] CUSTOMIZED_PARAMS = { 0.0, 0.0, 0.0, 0.1, 0.0, 0.0, 0.0, 0.0};

  // public static String resultFolder_FSE = "CLFF-ATG(FSE)/T(100)/tsafe/";
  // public static final int MAX_NUM_OF_PREDICT_PARAM = 100;
  // public static final int MAX_NUM_OF_GENERATE_CYCLE = 2;
  // public static final double PREDICT_BOUNDARY = 5.0;

  // public static String resultFolder_FSE = "CLFF-ATG(FSE)/T(400)/tsafe/";
  // public static final int MAX_NUM_OF_PREDICT_PARAM = 400;
  // public static final int MAX_NUM_OF_GENERATE_CYCLE = 2;
  // public static final double PREDICT_BOUNDARY = 5.0;

  // public static String resultFolder_FSE = "CLFF-ATG(FSE)/N(1)/tsafe/";
  // public static final int MAX_NUM_OF_PREDICT_PARAM = 200;
  // public static final int MAX_NUM_OF_GENERATE_CYCLE = 1;
  // public static final double PREDICT_BOUNDARY = 5.0;

  // public static String resultFolder_FSE = "CLFF-ATG(FSE)/N(4)/tsafe/";
  // public static final int MAX_NUM_OF_PREDICT_PARAM = 200;
  // public static final int MAX_NUM_OF_GENERATE_CYCLE = 4;
  // public static final double PREDICT_BOUNDARY = 5.0;

  // public static String resultFolder_FSE = "CLFF-ATG(FSE)/I(2)/tsafe/";
  // public static final int MAX_NUM_OF_PREDICT_PARAM = 200;
  // public static final int MAX_NUM_OF_GENERATE_CYCLE = 2;
  // public static final double PREDICT_BOUNDARY = 2.0;

  // public static String resultFolder_FSE = "CLFF-ATG(FSE)/I(10)/tsafe/";
  // public static final int MAX_NUM_OF_PREDICT_PARAM = 200;
  // public static final int MAX_NUM_OF_GENERATE_CYCLE = 2;
  // public static final double PREDICT_BOUNDARY = 10.0;

  // @since 0.1 Change default result folder
  public static String resultFolder = "lffResult/tempResult";

  public static String tempFolder = "lffResult/temp";

  /**
   * 结束一次查找前，预测出的参数个数的上限(100,200,400) --- T
   */
  public static int MAX_NUM_OF_PREDICT_PARAM = 400; // 4000;
  /**
   * 预测的轮数上限(1,2,4) --- N
   */
  public static int MAX_NUM_OF_GENERATE_CYCLE = 50; // 12;
  /**
   * 生成预测值的小区间大小(2,5,10)) --- I
   */
  public static double PREDICT_BOUNDARY = 5.0;
  /**
   * 变量搜索方式
   * <p>
   * SEARCH_STRATEGY_ALL = 0,
   * <p>
   * SEARCH_STRATEGY_ONE_BY_ONE = 1
   */
  public static int SEARCH_STRATEGY = 0;
  /**
   * 是否需要精细搜索
   */
  public static boolean NEED_REFINED_SEARCH = true;
  /**
   * 最大预测区间
   */
  public static final double MAX_PREDICT_BOUNDARY = 20.0;
  /**
   * 最小预测区间
   */
  public static final double MIN_PREDICT_BOUNDARY = 0.1;
  /**
   * 取值调整粒度
   */
  public static final double ADJUST_RATIO_DOUBLE_ZERO = 1e-40;
  public static final double ADUJST_RATIO_DOUBLE = 1e-15;
  public static final int ADJUST_GRANULARITY_INTEGER = 1;

  /**
   * 当前搜素变量的下标
   */
  public static int currentSearchParamIndex;
  /**
   * 类c
   * <p>
   * 为callFunction中的函数调用做准备
   */
  @SuppressWarnings("rawtypes")
  public static Class c = null;

  /**
   * 为callFunction中的函数调用做准备
   * 
   * @since 0.1 init `ATG.CallCPP` statically
   */
  public static final CallCPP callCPP = ATG.createCallCpp();

  /**
   * @since 0.1
   */
  private static CallCPP createCallCpp() {
    try {
      c = Class.forName("cn.nju.seg.atg.callCPP.CallCPP");
      return (CallCPP) c.newInstance();
    } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      // @since 0.1 return if error occurs
      // FIXME
      return null;
    }
  }

  /**
   * CallCPP中需要执行的函数名
   */
  public static String callFunctionName;
  /**
   * 插装文件前缀
   */
  public static String pathFilePrefix;

  /**
   * 路径约束的变量相关性表
   */
  protected static TableData table;
  /**
   * 存储每一轮的种子搜索点(对每一个变量的搜索都可得到下一轮的种子搜索点）
   */
  protected static SearchTask[] seeds, nextSeeds;
  /**
   * 上一变量方向上搜索的最优解
   */
  protected static double[] seedArray;
  /**
   * 存储第一轮中以随机值开始搜索的每一个变量方向上最优点的覆盖节点数
   */
  protected static int[] NumOfCoveredNodes;
  /**
   * 当前输入向量
   */
  public static double[] parameters;

  /**
   * 优先级阻塞队列
   */
  // Queue<SearchTask> S = new PriorityBlockingQueue<SearchTask>();

  /**
   * ATG核心过程
   * 
   * @param pathIndex
   * @return isCovered
   */
  public int generateTestData(int pathIndex) {
    // @since 0.1 record time used for uncovered paths
    final long startTime = System.currentTimeMillis();
    // final long startFunctionTime = TestBuilder.function_time;

    // @since 0.1 move init `ATG.CallCPP` code into static init expression
    // try {
    // c = Class.forName("cn.nju.seg.atg.callCPP.CallCPP");
    // callCPP = (CallCPP) c.newInstance();
    // } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
    // // @since 0.1 return if error occurs
    // // FIXME
    // return -1;
    // }

    // @since 0.1
    // pathFilePrefix = "/home/zy/atg_data/" + CFGBuilder.funcName.substring(0, CFGBuilder.funcName.indexOf("("));
    // String pathFile = pathFilePrefix + ".dat";
    // is changed to
    final Path tempPathFolder = Paths.get(ATG.tempFolder);
    try {
      Files.createDirectories(tempPathFolder);
    } catch (final Exception e) {
      return -1;
    }
    final Path tempPathFile = tempPathFolder.resolve(CFGBuilder.funcName.substring(0, CFGBuilder.funcName.indexOf("(")) + ".dat");
    final String pathFile = tempPathFile.toAbsolutePath().toString();

    int isCovered = -1;

    parameters = new double[NUM_OF_PARAM];
    NumOfCoveredNodes = new int[NUM_OF_PARAM];
    // 添加路径约束的变量相关性信息
    // table = new TableData();
    // table.data_for_caldat();;

    if (NUM_OF_PARAM > 6)
      SEARCH_STRATEGY = ConstantValue.SEARCH_STRATEGY_ONE_BY_ONE;
    else
      SEARCH_STRATEGY = ConstantValue.SEARCH_STRATEGY_ALL;

    if (SEARCH_STRATEGY == ConstantValue.SEARCH_STRATEGY_ALL) {
      int round = 0;
      while (round < MAX_NUM_OF_GENERATE_CYCLE && isCovered <= -1) {
        int paramIndex;
        // 第一轮，以随机值与指定值开始搜索
        if (round == 0) {
          int max_num_of_predict_param = MAX_NUM_OF_PREDICT_PARAM;
          if (CUSTOMIZED_PARAMS.length != 0) {
            max_num_of_predict_param /= 2;
          }
          seeds = new SearchTask[NUM_OF_PARAM];
          paramIndex = 0;
          // 对所有参数执行ATG
          while (paramIndex < NUM_OF_PARAM && isCovered <= -1) {
            isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, null, round, paramIndex, max_num_of_predict_param);
            if (isCovered == -1 && CUSTOMIZED_PARAMS.length != 0) {
              double[] initParams = new double[NUM_OF_PARAM];
              for (int i = 0; i < CUSTOMIZED_PARAMS.length; i++) {
                for (int j = 0; j < NUM_OF_PARAM; j++) {
                  initParams[j] = CUSTOMIZED_PARAMS[i];
                }
                isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, initParams, round, paramIndex, max_num_of_predict_param);
                if (isCovered > -1) {
                  break;
                }
              }
            }
            paramIndex++;
            // break;
          }
        }
        // 其余轮，以上一轮生成的最优点开始搜索
        else {
          // 计算本轮生成的种子数
          int seedsNum = (int) (NUM_OF_PARAM * Math.pow((NUM_OF_PARAM - 1), round - 1));
          int nextRoundSeedsNum = seedsNum * (NUM_OF_PARAM - 1);
          nextSeeds = new SearchTask[nextRoundSeedsNum];

          int seedIndex = 0;
          int nextRoundSeedIndex = 0;
          while (seedIndex < seedsNum && isCovered <= -1) {
            paramIndex = 0;
            while (paramIndex < NUM_OF_PARAM && isCovered <= -1) {
              // 判断目标路径是否与搜索变量相关
              // if(isRelevant(Builder.targetPath,paramIndex) && paramIndex != seedIndex){
              if (paramIndex != seeds[seedIndex].getSearchIndex()) {
                // 以给定值为初始参数进行ATG
                isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, seeds[seedIndex].getInitInputs(), round, nextRoundSeedIndex, MAX_NUM_OF_PREDICT_PARAM);
                nextRoundSeedIndex++;
              }
              // }
              paramIndex++;
            }
            seedIndex++;
          }
          // 将新生成的最优点作为下一轮的初始种子
          seeds = nextSeeds;
        }
        round++;
      }
    } else {
      int round = 0;
      while (round < MAX_NUM_OF_GENERATE_CYCLE && isCovered <= -1) {
        int paramIndex = 0;
        // 对所有参数执行ATG
        double[] seed;
        while (paramIndex < NUM_OF_PARAM && isCovered <= -1) {
          if (round == 0 && paramIndex == 0) {
            seed = null;
          } else {
            seed = seedArray;
          }
          isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, seed, -1, paramIndex, MAX_NUM_OF_PREDICT_PARAM);
          paramIndex++;
        }
        round++;
      }
    }

    if (isCovered < 0) {
      // final long endFunctionTime = TestBuilder.function_time;
      TestBuilder.uncoverdPathsTime += System.currentTimeMillis() - startTime /* - (endFunctionTime - startFunctionTime) */ ;
    }

    return isCovered;
  }

  /**
   * Adds a handler for processing executed paths.
   * 
   * @since 0.1
   * @see #generateTestData(int)
   */
  public int generateTestData(int pathIndex,
                              Consumer<CFGPath> executedPathHandler) {
    // @since 0.1 record time used for uncovered paths
    final long startTime = System.currentTimeMillis();
    // final long startFunctionTime = TestBuilder.function_time;

    // @since 0.1 move init `ATG.CallCPP` code into static init expression
    // try {
    // c = Class.forName("cn.nju.seg.atg.callCPP.CallCPP");
    // callCPP = (CallCPP) c.newInstance();
    // } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
    // // @since 0.1 return if error occurs
    // // FIXME
    // return -1;
    // }

    // @since 0.1
    // pathFilePrefix = "/home/zy/atg_data/" + CFGBuilder.funcName.substring(0, CFGBuilder.funcName.indexOf("("));
    // String pathFile = pathFilePrefix + ".dat";
    // is changed to
    final Path tempPathFolder = Paths.get(ATG.tempFolder);
    try {
      Files.createDirectories(tempPathFolder);
    } catch (final Exception e) {
      return -1;
    }
    final Path tempPathFile = tempPathFolder.resolve(CFGBuilder.funcName.substring(0, CFGBuilder.funcName.indexOf("(")) + ".dat");
    final String pathFile = tempPathFile.toAbsolutePath().toString();

    int isCovered = -1;

    parameters = new double[NUM_OF_PARAM];
    NumOfCoveredNodes = new int[NUM_OF_PARAM];
    // 添加路径约束的变量相关性信息
    // table = new TableData();
    // table.data_for_caldat();;

    if (NUM_OF_PARAM > 6)
      SEARCH_STRATEGY = ConstantValue.SEARCH_STRATEGY_ONE_BY_ONE;
    else
      SEARCH_STRATEGY = ConstantValue.SEARCH_STRATEGY_ALL;

    if (SEARCH_STRATEGY == ConstantValue.SEARCH_STRATEGY_ALL) {
      int round = 0;
      while (round < MAX_NUM_OF_GENERATE_CYCLE && isCovered <= -1) {
        int paramIndex;
        // 第一轮，以随机值与指定值开始搜索
        if (round == 0) {
          int max_num_of_predict_param = MAX_NUM_OF_PREDICT_PARAM;
          if (CUSTOMIZED_PARAMS.length != 0) {
            max_num_of_predict_param /= 2;
          }
          seeds = new SearchTask[NUM_OF_PARAM];
          paramIndex = 0;
          // 对所有参数执行ATG
          while (paramIndex < NUM_OF_PARAM && isCovered <= -1) {
            isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, null, round, paramIndex, max_num_of_predict_param, executedPathHandler);
            if (isCovered == -1 && CUSTOMIZED_PARAMS.length != 0) {
              double[] initParams = new double[NUM_OF_PARAM];
              for (int i = 0; i < CUSTOMIZED_PARAMS.length; i++) {
                for (int j = 0; j < NUM_OF_PARAM; j++) {
                  initParams[j] = CUSTOMIZED_PARAMS[i];
                }
                isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, initParams, round, paramIndex, max_num_of_predict_param, executedPathHandler);
                if (isCovered > -1) {
                  break;
                }
              }
            }
            paramIndex++;
            // break;
          }
        }
        // 其余轮，以上一轮生成的最优点开始搜索
        else {
          // 计算本轮生成的种子数
          int seedsNum = (int) (NUM_OF_PARAM * Math.pow((NUM_OF_PARAM - 1), round - 1));
          int nextRoundSeedsNum = seedsNum * (NUM_OF_PARAM - 1);
          nextSeeds = new SearchTask[nextRoundSeedsNum];

          int seedIndex = 0;
          int nextRoundSeedIndex = 0;
          while (seedIndex < seedsNum && isCovered <= -1) {
            paramIndex = 0;
            while (paramIndex < NUM_OF_PARAM && isCovered <= -1) {
              // 判断目标路径是否与搜索变量相关
              // if(isRelevant(Builder.targetPath,paramIndex) && paramIndex != seedIndex){
              if (paramIndex != seeds[seedIndex].getSearchIndex()) {
                // 以给定值为初始参数进行ATG
                isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, seeds[seedIndex].getInitInputs(), round, nextRoundSeedIndex, MAX_NUM_OF_PREDICT_PARAM, executedPathHandler);
                nextRoundSeedIndex++;
              }
              // }
              paramIndex++;
            }
            seedIndex++;
          }
          // 将新生成的最优点作为下一轮的初始种子
          seeds = nextSeeds;
        }
        round++;
      }
    } else {
      int round = 0;
      while (round < MAX_NUM_OF_GENERATE_CYCLE && isCovered <= -1) {
        int paramIndex = 0;
        // 对所有参数执行ATG
        double[] seed;
        while (paramIndex < NUM_OF_PARAM && isCovered <= -1) {
          if (round == 0 && paramIndex == 0) {
            seed = null;
          } else {
            seed = seedArray;
          }
          isCovered = generateTestDataForParam(pathIndex, paramIndex, pathFile, seed, -1, paramIndex, MAX_NUM_OF_PREDICT_PARAM, executedPathHandler);
          paramIndex++;
        }
        round++;
      }
    }

    if (isCovered < 0) {
      // final long endFunctionTime = TestBuilder.function_time;
      TestBuilder.uncoverdPathsTime += System.currentTimeMillis() - startTime /* - (endFunctionTime - startFunctionTime) */ ;
    }

    return isCovered;
  }

  /**
   * 以第paramIndex个参数为自变参数，预测可用测试数据
   * 
   * @param pathIndex
   * @param paramIndex
   * @param pathFile
   * @param seed
   * @return isCovered
   */
  protected abstract int generateTestDataForParam(int pathIndex, int paramIndex, String pathFile,
                                                  double[] seed, int round, int nextRoundSeedIndex, int max_num_of_predict_param);

  /**
   * @since 0.1
   */
  protected int generateTestDataForParam(int pathIndex, int paramIndex, String pathFile,
                                         double[] seed, int round, int nextRoundSeedIndex, int max_num_of_predict_param,
                                         Consumer<CFGPath> executedPathHandler) {
    throw new UnsupportedOperationException();
  }

  /**
   * 初始化参数值
   */
  protected static void initialParameter(double[] initParams) {
    if (initParams != null) {
      // 手动设置初始值
      for (int i = 0; i < NUM_OF_PARAM; i++) {
        parameters[i] = initParams[i];
      }
    } else {
      // 根据取值区间随机生成初始值
      for (int i = 0; i < NUM_OF_PARAM; i++) {
        // parameters[i] = 0;
        setRandom(i, START_POINT, ConstantValue.INTERVAL_LEFT_OPEN);
      }
    }
  }

  /**
   * 打印当前参数(输入向量)
   */
  protected static void printParams() {
    String paramsStr = "";
    int paramsNum = parameters.length;
    for (int i = 0; i < paramsNum; i++) {
      paramsStr += parameters[i] + ", ";
    }
    System.out.println(paramsStr);
  }

  /**
   * 将第paramIndex个参数设为随机值
   * 
   * @param paramIndex
   * @param leftBoundary
   * @param rightBoundary
   * @param mode
   * @INTERVAL_BOTH_OPEN：在(leftBoundary, rightBoundary)内产生随机值
   * @INTERVAL_RIGHT_OPEN：在[leftBoundary, rightBoundary)内产生随机值
   * @INTERVAL_LEFT_OPEN：在(leftBoundary, rightBoundary]内产生随机值
   * @INTERVAL_BOTH_CLOSED：在[leftBoundary, rightBoundary]内产生随机值
   */
  protected static void setRandom(int paramIndex, double value, int mode) {
    double leftBoundary = value - PREDICT_BOUNDARY;
    double rightBoundary = value + PREDICT_BOUNDARY;

    if (mode == ConstantValue.INTERVAL_BOTH_OPEN) {
      double random = Math.random();
      while (random == 0.0) {
        random = Math.random();
      }
      parameters[paramIndex] = random * (rightBoundary - leftBoundary) + leftBoundary;
    } else if (mode == ConstantValue.INTERVAL_LEFT_OPEN) {
      parameters[paramIndex] = rightBoundary - Math.random() * (rightBoundary - leftBoundary);
    } else if (mode == ConstantValue.INTERVAL_RIGHT_OPEN) {
      parameters[paramIndex] = Math.random() * (rightBoundary - leftBoundary) + leftBoundary;
    } else if (mode == ConstantValue.INTERVAL_BOTH_CLOSED) {
      if (Math.random() < 0.5) {
        parameters[paramIndex] = Math.random() * (rightBoundary - leftBoundary) + leftBoundary;
      } else {
        parameters[paramIndex] = rightBoundary - Math.random() * (rightBoundary - leftBoundary);
      }
    }

    if (CFGBuilder.parameterTypes[paramIndex].equals("int")) {
      parameters[paramIndex] = Math.round(parameters[paramIndex]);
    }
  }

  /**
   * 更新输入数据集合
   * 将list有序插入到当前输入数据集合中
   * 更新后的输入数据集合仍为从小到大排序
   * 
   * @param list
   */
  public static List<Double> updateParameterList(List<Double> list) {
    List<Double> newParameters = new ArrayList<Double>();
    int index1 = 0;
    int index2 = 0;
    int listSize = list.size();

    while (index2 < listSize) {
      if (index1 < TestBuilder.parameterList.size()) {
        if (Math.abs(TestBuilder.parameterList.get(index1) - list.get(index2)) <= Double.MIN_VALUE) {
          index2++;
          index1 = 0;
        } else if (TestBuilder.parameterList.get(index1) > list.get(index2)) {
          TestBuilder.parameterList.add(index1, list.get(index2));
          newParameters.add(list.get(index2));
          index2++;
          index1 = 0;
        } else {
          index1++;
        }
      } else {
        TestBuilder.parameterList.add(list.get(index2));
        newParameters.add(list.get(index2));
        index2++;
      }
    }

    return newParameters;
  }

  /**
   * 获取当前最大可用区间
   * 
   * @return 一个区间
   */
  public static Interval maxInterval() {
    Interval interval = new Interval();
    double minParameter = TestBuilder.parameterList.get(0);
    double maxParameter = TestBuilder.parameterList.get(0);
    int parameterNum = TestBuilder.parameterList.size();
    double tempParameter;
    for (int i = 1; i < parameterNum; i++) {
      tempParameter = TestBuilder.parameterList.get(i);
      if (tempParameter > maxParameter) {
        maxParameter = tempParameter;
      }
      if (tempParameter < minParameter) {
        minParameter = tempParameter;
      }
    }
    interval.setLeftBoundary(minParameter);
    interval.setRightBoundary(maxParameter);

    return interval;
  }

  /**
   * 获取目标路径与某个变量的相关性
   * 
   * @param targetPath
   * @param paramIndex
   * @return
   */
  protected static boolean isRelevant(CFGPath targetPath, int paramIndex) {
    int pathSize = targetPath.getPath().size();

    for (int i = 0; i < pathSize; i++) {
      if (targetPath.getPath().get(i).isBranchNode()) {
        if (table.getRelevantInfo(targetPath.getPath().get(i).getName(), paramIndex)) {
          return true;
        }
      }
    }
    return false;
  }
}
