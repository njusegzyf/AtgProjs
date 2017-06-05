package nju.seg.zhangyf.atgwrapper.outcome;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.math.Stats;

import cn.nju.seg.atg.util.ATG;
import nju.seg.zhangyf.atgwrapper.batch.BatchFileRunnerBase.TaskOutcome;
import nju.seg.zhangyf.util.Util;

/**
 * Stores results of processing a batch file.
 * 
 * @see nju.seg.zhangyf.atgwrapper.batch.PathCoverageBatchFileRunner
 * @author Zhang Yifan
 */
public final class BatchFileOutcome<TTestOutcome extends TestOutcome> implements Serializable {

  private final List<TaskOutcome<TTestOutcome>> succeedFunctions;
  private final List<TaskOutcome<TTestOutcome>> failedFunctions;

  public BatchFileOutcome(final List<TaskOutcome<TTestOutcome>> succeedFunctions,
                          final List<TaskOutcome<TTestOutcome>> failedFunctions) {
    assert succeedFunctions != null;
    assert failedFunctions != null;

    this.succeedFunctions = succeedFunctions;
    this.failedFunctions = failedFunctions;
  }

  public void appendOverview(final Appendable output) throws IOException {
    Preconditions.checkNotNull(output);

    Util.appendAllWithNewLine(output, "Overall execution information:");
    if (this.failedFunctions.isEmpty()) { // if all test are succeed
      Util.appendAllWithNewLine(output, "Batch all ", String.valueOf(this.succeedFunctions.size()), " functions sucessfully:");
      for (final TaskOutcome<TTestOutcome> outcome : this.succeedFunctions) {
        BatchFileOutcome.appendTestFunctionSignuature(output, outcome);
      }
    } else { // if some test are failed
      Util.appendAllWithNewLine(output, "Failed to batch ", String.valueOf(this.failedFunctions.size()), " functions for exceptions or timeouts:");
      for (final TaskOutcome<TTestOutcome> outcome : this.failedFunctions) {
        BatchFileOutcome.appendTestFunctionSignuature(output, outcome);
      }
      Util.appendAllWithNewLine(output, "Succeeded to batch ", String.valueOf(this.succeedFunctions.size()), " functions:");
      for (final TaskOutcome<TTestOutcome> outcome : this.succeedFunctions) {
        BatchFileOutcome.appendTestFunctionSignuature(output, outcome);
      }
    }
  }

  public void appendSucceedTaskOutcomes(final Appendable output) throws IOException {
    Preconditions.checkNotNull(output);

    // FIXME we may need to record and print the settings used by the batch, not current settings in class `ATG`
    Util.appendAllWithNewLine(output,
                              "Parameter setting: ",
                              "MAX_NUM_OF_PREDICT_PARAM=", String.valueOf(ATG.MAX_NUM_OF_PREDICT_PARAM),
                              ", MAX_NUM_OF_GENERATE_CYCLE=", String.valueOf(ATG.MAX_NUM_OF_GENERATE_CYCLE),
                              ", PREDICT_BOUNDARY=" + String.valueOf(ATG.PREDICT_BOUNDARY));
    Util.appendAllWithNewLine(output, "Search strategy: ", (ATG.SEARCH_STRATEGY == 0 ? "SEARCH_STRATEGY_ALL" : "SEARCH_STRATEGY_ONE_BY_ONE"));

    if (this.succeedFunctions.isEmpty()) {
      Util.appendAllWithNewLine(output, "No succeed functions.");
      return;
    }

    // append detail result of each single test
    // Util.appendNewLine(output);
    // Util.appendAllWithNewLine(output, "Detail execution information:");
    // for (final TaskOutcome<TTestOutcome> outcome : this.succeedFunctions) {
    // outcome.optioanlTestOutcome.get().appendOutcome(output);
    // Util.appendNewLine(output);
    // }

    // Statistical information for each run
    // Note: Each function runs N times, and the K'th run is defined as the collection of all the function's K'th run

    Util.appendNewLine(output);
    Util.appendAllWithNewLine(output, "Statistical information for each run:");

    final List<TTestOutcome> testOutcomes = this.succeedFunctions.stream().map(f -> f.optioanlTestOutcome.get()).collect(Collectors.toList());
    // FIXME we assume that all test have the same count of repeation
    int repeatCount = testOutcomes.get(0).countOfRepeation;
        
    final int[] coverdNumSumForEachRun =
        IntStream.range(0, repeatCount)
                 .map(i -> testOutcomes.stream().mapToInt(outcome -> outcome.coverage[i].coverdNum).sum())
                 .toArray();
    final int[] totalNumSumForEachRun =
        IntStream.range(0, repeatCount)
                 .map(i -> testOutcomes.stream().mapToInt(outcome -> outcome.coverage[i].totalNum).sum())
                 .toArray();
    final double[] coveragePercentagForEachRun =
        IntStream.range(0, repeatCount)
                 .mapToDouble(i -> (double) (100 * coverdNumSumForEachRun[i]) / totalNumSumForEachRun[i])
                 .toArray();
    final double averageCoverageForOneRun = DoubleStream.of(coveragePercentagForEachRun).average().getAsDouble();
    // final double averageCoverageForOneRun = (double) (100 * IntStream.of(coverdNumSumForEachRun).sum()) / IntStream.of(totalNumSumForEachRun).sum();
    Util.appendAllWithNewLine(output,
                              "Average coverage for one run: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageCoverageForOneRun), "%");

    Util.appendAllWithNewLine(output,
                              "Population variance of coverage (in percentage): ", String.valueOf(Stats.of(coveragePercentagForEachRun).populationVariance()));

    output.append("Detail coverage for each run: ");
    Joiner.on(", ").appendTo(output,
                             IntStream.range(0, repeatCount)
                                      .mapToObj(i -> coverdNumSumForEachRun[i] + " / " + totalNumSumForEachRun[i])
                                      .collect(Collectors.toList()));
    Util.appendNewLine(output);

    final double[] totalTimeForEachRun =
        IntStream.range(0, repeatCount)
                 .mapToDouble(i -> testOutcomes.stream().mapToDouble(outcome -> outcome.totalTime[i]).sum())
                 .toArray();

    final double averageTotalTimeForOneRun = DoubleStream.of(totalTimeForEachRun).average().getAsDouble();
    Util.appendAllWithNewLine(output,
                              "Average total time for one run: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTimeForOneRun), " sec");

    output.append("Detail total time for each run: ");
    Joiner.on(", ").appendTo(output, DoubleStream.of(totalTimeForEachRun).boxed().toArray());
    Util.appendNewLine(output);

    final double[] algorithmTimeForEachRun =
        IntStream.range(0, repeatCount)
                 .mapToDouble(i -> testOutcomes.stream().mapToDouble(outcome -> outcome.algorithmTime[i]).sum())
                 .toArray();

    final double averageAlgorithmTimeOneRun = DoubleStream.of(algorithmTimeForEachRun).average().getAsDouble();
    Util.appendAllWithNewLine(output,
                              "Average algorithm time for one run: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageAlgorithmTimeOneRun), " sec");

    output.append("Detail algorithm time for each run: ");
    Joiner.on(", ").appendTo(output, DoubleStream.of(algorithmTimeForEachRun).boxed().toArray());
    Util.appendNewLine(output);

    final double[] totalUncoverdPathsTimeForEachRun =
        IntStream.range(0, repeatCount)
                 .mapToDouble(i -> testOutcomes.stream().mapToLong(outcome -> outcome.totalUncoverdPathsTime[i]).sum() / 1000.0)
                 .toArray();

    final double averageTotalUncoverdPathsTimeForOneRun = DoubleStream.of(totalUncoverdPathsTimeForEachRun).average().getAsDouble();
    Util.appendAllWithNewLine(output,
                              "Average time for uncovered paths for one run: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalUncoverdPathsTimeForOneRun), " sec");
    Util.appendAllWithNewLine(output,
                              "Average time for covered paths for one run: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTimeForOneRun - averageTotalUncoverdPathsTimeForOneRun), " sec");

    output.append("Detail time for uncovered paths for each run: ");
    Joiner.on(", ").appendTo(output,
                             DoubleStream.of(totalUncoverdPathsTimeForEachRun)
                                         .mapToObj(t -> TestOutcome.DEFAULT_DECIMAL_FORMAT.format(t))
                                         .collect(Collectors.toList()));
    Util.appendNewLine(output);

    output.append("Detail time for covered paths for each run: ");
    Joiner.on(", ").appendTo(output,
                             IntStream.range(0, repeatCount)
                                      .mapToObj(i -> TestOutcome.DEFAULT_DECIMAL_FORMAT.format(totalTimeForEachRun[i] - totalUncoverdPathsTimeForEachRun[i]))
                                      .collect(Collectors.toList()));
    Util.appendNewLine(output);

    // Statistical information for each function

    Util.appendNewLine(output);
    Util.appendAllWithNewLine(output, "Statistical information for each function:");

    final double averageCoverage =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().coverage)
                             .mapToDouble(covergaeArray -> Stream.of(covergaeArray).mapToDouble(CoverageOutcome::coverageRatio).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average coverage for one function: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageCoverage));

    final double averageTotalTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().totalTime)
                             .mapToDouble(totalTimeArray -> DoubleStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average total time for one function: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTime), " sec");

    final double averageAlgorithmTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().algorithmTime)
                             .mapToDouble(totalTimeArray -> DoubleStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average algorithm time for one function: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageAlgorithmTime), " sec");

    final double averageTotalUncoveredPathsTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().totalUncoverdPathsTime)
                             .mapToDouble(totalTimeArray -> LongStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();

    // Note: `averageTotalUncoveredPathsTime`'s time unit is ms.
    Util.appendAllWithNewLine(output, "Average total time for uncovered paths for one function: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalUncoveredPathsTime / 1000.0),
                              " sec");

    Util.appendAllWithNewLine(output, "Average total time for covered paths for one function: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTime - averageTotalUncoveredPathsTime / 1000.0),
                              " sec");
  }

  private static void appendTestFunctionSignuature(final Appendable output, final TaskOutcome<?> outcome) throws IOException {
    assert output != null;
    assert outcome != null;

    Util.appendAllWithNewLine(output, "Function: ", outcome.testFunctionSignuature);
  }
  
  private static final long serialVersionUID = 1L;
}
