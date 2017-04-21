package nju.seg.zhangyf.atgwrapper.outcome;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.nju.seg.atg.parse.TestBuilder;
import nju.seg.zhangyf.util.Util;

/**
 * The outcome of a single test.
 * 
 * @version 0.1
 * @see cn.nju.seg.atg.parse.TestBuilder
 * @author Zhang Yifan
 */
@SuppressWarnings("unused")
public class SingleTestOutcome {

  public final String testFunctionSignuature;

  /**
   * ATG实验过程重复执行次数
   */
  public final int countOfRepeation;

  /**
   * 目标程序执行的总时间, Unit : ms.
   */
  public final long functionTime;

  /**
   * 目标程序执行的总次数
   */
  public final int functionFrequency;

  /**
   * 单次运行总时间, Unit : second.
   */
  public final double[] totalTime;

  /**
   * 单次运行总次数
   */
  public final int[] totalFrequency;

  /**
   * 单次运行中函数执行时间, Unit : second.
   */
  public final double[] algorithmTime;

  /**
   * 单次覆盖率（覆盖路径的条数）
   */
  public final int[] coveredRatio;

  public final String[] findResult;

  private SingleTestOutcome(final String testFunctionSignuature,
                            final int countOfRepeation,
                            final long functionTime,
                            final int functionFrequency,
                            final double[] totalTime,
                            final int[] totalFrequency,
                            final double[] algorithmTime,
                            final int[] coveredRatio, 
                            final String[] findResult) {

    this.testFunctionSignuature = testFunctionSignuature;
    this.countOfRepeation = countOfRepeation;
    this.functionTime = functionTime;
    this.functionFrequency = functionFrequency;
    this.totalTime = totalTime;
    this.totalFrequency = totalFrequency;
    this.algorithmTime = algorithmTime;
    this.coveredRatio = coveredRatio;
    this.findResult = findResult;
  }

  public static SingleTestOutcome buildFromTestBuilder(final String testFunctionSignuature) {
    // Preconditions.checkArgument(!Strings.isNullOrEmpty(testFunctionSignuature));
    assert !Strings.isNullOrEmpty(testFunctionSignuature);
    
    return new SingleTestOutcome(testFunctionSignuature,
                                 TestBuilder.repetitionNum,
                                 TestBuilder.function_time,
                                 TestBuilder.function_frequency,
                                 Util.sameLengthCopyOfArray(TestBuilder.totalTime),
                                 Util.sameLengthCopyOfArray(TestBuilder.totalFrequency),
                                 Util.sameLengthCopyOfArray(TestBuilder.algorithmTime),
                                 Util.sameLengthCopyOfArray(TestBuilder.coveredRatio),
                                 Util.sameLengthCopyOfArray(TestBuilder.findResult));
  }
}
