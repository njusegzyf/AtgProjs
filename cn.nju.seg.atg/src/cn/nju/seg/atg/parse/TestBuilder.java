package cn.nju.seg.atg.parse;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import cn.nju.seg.atg.util.CFGPath;

/**
 * @version 0.1 Change fields that are used to record time from double to long.
 *          Add reset methods.
 * @author zy
 * @author Zhang Yifan
 */
public final class TestBuilder {

  // FIXME set `repetitionNum` and init data in method `resetForNewTest`

  /** @since 0.1 */
  public static final int DEFAULT_TEST_REPETITION_NUM = 7;

  /**
   * ATG实验过程重复执行次数
   * 
   * @since 0.1 Rename to `repetitionNum`
   */
  public static int repetitionNum = TestBuilder.DEFAULT_TEST_REPETITION_NUM;

  // Data that are used to record execution time and frequency.

  /**
   * 目标程序执行的总时间
   */
  public static long function_time;

  /**
   * 目标程序执行的总次数
   */
  public static int function_frequency;

  /**
   * The total time in milliseconds that are used to parse path from files.
   * 
   * @see cn.nju.seg.atg.pathParse
   * @since 0.1
   */
  public static long ioTime;

  /**
   * The total time in milliseconds that are used to process that are failed to cover.
   * Note: This is only used for {@link cn.nju.seg.atg.util.PCATG}.
   *
   * @see {@link cn.nju.seg.atg.util.PCATG#generateTestData(int)}
   * @since 0.1
   */
  public static long uncoverdPathsTime;

  /**
   * 单次覆盖率（覆盖路径的条数）
   */
  public static int coveredRatio[] = new int[TestBuilder.repetitionNum];

  /**
   * The name of target node.
   * 
   * @since 0.1
   */
  public static String targetNode = null;

  // Data that are computed.

  /**
   * 单次运行总时间, Unit : second.
   */
  public static double totalTime[] = new double[repetitionNum];
  /**
   * 单次运行总次数
   */
  public static int totalFrequency[] = new int[repetitionNum];
  /**
   * 单次运行中函数执行时间, Unit : second.
   */
  public static double algorithmTime[] = new double[repetitionNum];

  /**
   * @since 0.1
   */
  public static long[] totalUncoverdPathsTime = new long[TestBuilder.repetitionNum];

  public static String findResult[] = new String[TestBuilder.repetitionNum];
  /**
   * 单次覆盖的路径列表
   */
  static String everyCoveredPaths[] = new String[repetitionNum];

  //////////////////////////// path coverage ///////////////////////////

  // @since 0.1 use `Lists.newArrayList()` to create the lists and reuse them

  /**
   * 尚未做ATG的路径集合
   */
  public static List<CFGPath> uncheckedPaths = Lists.newArrayList();
  /**
   * 执行过ATG后仍未找到可用输入向量的路径集合
   */
  public static List<CFGPath> uncoveredPaths = Lists.newArrayList();
  /**
   * 执行过ATG后已找到可用输入向量的路径集合
   */
  public static List<CFGPath> coveredPaths = Lists.newArrayList();
  /**
   * 带测程序的所有路径
   */
  public static List<CFGPath> allPaths = Lists.newArrayList();

  public static int pathsSize = 0;

  /////////////////////////// value space ///////////////////////////
  /**
   * 自变参数产生池
   */
  public static ArrayList<Double> parameterList;
  /**
   * 当前搜索区域的左边界
   */
  public static double leftSearchBoundary;
  /**
   * 当前搜索区域的右边界
   */
  public static double rightSearchBoundary;
  /**
   * 拟合线段延长线与坐标轴的交点集合（2014-7-9)
   */
  public static List<Double> paramsInExtensionCord;
  /**
   * 向两边扩展后得到的新自变参数
   */
  public static List<Double> autoIncreasedParameterList;

  // @since 0.1 Use the static method to init data.
  static {
    TestBuilder.resetForNewTest(TestBuilder.DEFAULT_TEST_REPETITION_NUM);
  }

  /**
   * Reset all statistics data for a new test.
   * 
   * @since 0.1
   */
  public static void resetForNewTest(final int newCountOfRepeation) {
    if (newCountOfRepeation != TestBuilder.repetitionNum) {
      // TODO if we change `newCountOfRepeation`, we need to resize all arrays and collections
      throw new IllegalStateException();
    } else {
      // TODO we may need to clean some data.
    }

    TestBuilder.resetForNewTestRepeation();
  }

  /**
   * Reset all statistics data for a new test..
   * 
   * @since 0.1
   */
  public static void resetForNewTest() {
    Preconditions.checkState(TestBuilder.repetitionNum > 0);

    TestBuilder.resetForNewTest(TestBuilder.repetitionNum);
  }

  /**
   * Reset statistics data for a new test repeation.
   * 
   * @since 0.1
   */
  public static void resetForNewTestRepeation() {
    // 初始化目标程序执行时间为0
    TestBuilder.function_time = 0L;
    // 初始化目标程序执行次数为0
    TestBuilder.function_frequency = 0;

    TestBuilder.ioTime = 0L;
    TestBuilder.uncoverdPathsTime = 0L;

    TestBuilder.targetNode = null;

    // TODO clear other data
  }
}
