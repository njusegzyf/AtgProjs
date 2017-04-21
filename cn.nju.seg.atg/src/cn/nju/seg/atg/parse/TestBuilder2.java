package cn.nju.seg.atg.parse;

import java.util.ArrayList;
import java.util.List;

import cn.nju.seg.atg.util.CFGPath;

/**
 * A refactor of {@code TestBuilder}, which stores all data in an instance instead of static fields.
 * 
 * @see cn.nju.seg.atg.parse.TestBuilder
 * @author Zhang Yifan
 */
@Deprecated
public class TestBuilder2 {

  /**
   * 用于存储测试结果的文件夹
   */
  public String targetNode;
  /**
   * ATG实验过程重复执行次数
   */
  public int countOfRepeation = 7;
  /**
   * 目标程序执行的总时间
   */
  public long function_time;
  /**
   * 目标程序执行的总次数
   */
  public int function_frequency;
  /**
   * 单次运行总时间
   */
  public double totalTime[] = new double[this.countOfRepeation];
  /**
   * 单次运行总次数
   */
  public int totalFrequency[] = new int[this.countOfRepeation];
  /**
   * 单次运行中函数执行时间
   */
  public double algorithmTime[] = new double[this.countOfRepeation];
  /**
   * 单次覆盖率（覆盖路径的条数）
   */
  public int coveredRatio[] = new int[this.countOfRepeation];
  public String findResult[] = new String[this.countOfRepeation];
  /**
   * 单次覆盖的路径列表
   */
  String everyCoveredPaths[] = new String[this.countOfRepeation];

  //////////////////////////// path coverage ///////////////////////////
  /**
   * 尚未做ATG的路径集合
   */
  public List<CFGPath> uncheckedPaths;
  /**
   * 执行过ATG后仍未找到可用输入向量的路径集合
   */
  public List<CFGPath> uncoveredPaths;
  /**
   * 执行过ATG后已找到可用输入向量的路径集合
   */
  public List<CFGPath> coveredPaths;
  /**
   * 带测程序的所有路径
   */
  public List<CFGPath> allPaths;

  /////////////////////////// value space ///////////////////////////
  /**
   * 自变参数产生池
   */
  public ArrayList<Double> parameterList;
  /**
   * 当前搜索区域的左边界
   */
  public double leftSearchBoundary;
  /**
   * 当前搜索区域的右边界
   */
  public double rightSearchBoundary;
  /**
   * 拟合线段延长线与坐标轴的交点集合（2014-7-9)
   */
  public List<Double> paramsInExtensionCord;
  /**
   * 向两边扩展后得到的新自变参数
   */
  public List<Double> autoIncreasedParameterList;
}
