package nju.seg.zhangyf.atgwrapper.outcome;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.typesafe.config.ConfigException.Null;

import cn.nju.seg.atg.parse.TestBuilder;
import nju.seg.zhangyf.atgwrapper.batch.BatchFileHandlerBase.TaskOutcome;
import nju.seg.zhangyf.util.Util;

/**
 * The outcome of a single test.
 * 
 * @version 0.1
 * @see cn.nju.seg.atg.parse.TestBuilder
 * @author Zhang Yifan
 */
@SuppressWarnings("unused")
public class TestOutcome {

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
   * Coverage for each tests as a percentage.
   * Note: It differs from {@link TestBuilder#coveredRatio} which records the covered path number. 
   */
  public final double[] coverage;

  public final String[] findResult;
  
  public final long[] totalUncoverdPathsTime;

  private TestOutcome(final String testFunctionSignuature,
                      final int countOfRepeation,
                      final long functionTime,
                      final int functionFrequency,
                      final double[] totalTime,
                      final int[] totalFrequency,
                      final double[] algorithmTime,
                      final String[] findResult,
                      final long[] totalUncoverdPathsTime,
                      final double[] coverage) {
    assert !Strings.isNullOrEmpty(testFunctionSignuature);
    assert countOfRepeation > 0;
    assert functionTime > 0L;
    assert functionFrequency > 0;
    assert totalTime != null && totalTime.length >= countOfRepeation;
    assert totalFrequency != null && totalFrequency.length >= countOfRepeation;
    assert algorithmTime != null && algorithmTime.length >= countOfRepeation;
    assert findResult != null && findResult.length >= countOfRepeation;
    assert totalUncoverdPathsTime != null && totalUncoverdPathsTime.length >= countOfRepeation;
    assert coverage != null && coverage.length >= countOfRepeation;

    this.testFunctionSignuature = testFunctionSignuature;
    this.countOfRepeation = countOfRepeation;
    this.functionTime = functionTime;
    this.functionFrequency = functionFrequency;
    this.totalTime = totalTime;
    this.totalFrequency = totalFrequency;
    this.algorithmTime = algorithmTime;
    this.findResult = findResult;
    this.totalUncoverdPathsTime = totalUncoverdPathsTime;
    this.coverage = coverage;
  }

  /**
   * Constructs a new `TestOutcome` with the coverage calculated by `(double) coverdNum / TestBuilder.pathsSize`.
   */
  public TestOutcome(final String testFunctionSignuature) {
    this(testFunctionSignuature,
         IntStream.of(TestBuilder.coveredRatio).mapToDouble(coverdNum -> (double) coverdNum / TestBuilder.pathsSize)
         .toArray());
  }
  
  /**
   * Note: Since {@link TestBuilder} reuse arrays, we must create copies here like {@code Util.sameLengthCopyOfArray(TestBuilder.totalTime)}.
   */
  protected TestOutcome(final String testFunctionSignuature, final double[] coverage) {
    this(testFunctionSignuature,
         TestBuilder.repetitionNum,
         TestBuilder.function_time,
         TestBuilder.function_frequency,
         Util.sameLengthCopyOfArray(TestBuilder.totalTime),
         Util.sameLengthCopyOfArray(TestBuilder.totalFrequency),
         Util.sameLengthCopyOfArray(TestBuilder.algorithmTime),
         Util.sameLengthCopyOfArray(TestBuilder.findResult),
         Util.sameLengthCopyOfArray(TestBuilder.totalUncoverdPathsTime),
         coverage);
  }

  public void appendOutcome(final Appendable output) throws IOException {
    assert output != null;

    Util.appendAllWithNewLine(output, "Function : ", this.testFunctionSignuature);
  }
  
  public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("0.000");
}
