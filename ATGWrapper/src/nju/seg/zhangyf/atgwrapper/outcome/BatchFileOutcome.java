package nju.seg.zhangyf.atgwrapper.outcome;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.ATG;
import nju.seg.zhangyf.atgwrapper.batch.BatchFileRunnerBase.TaskOutcome;
import nju.seg.zhangyf.util.Util;

/**
 * Stores results of processing a batch file.
 * 
 * @see nju.seg.zhangyf.atgwrapper.batch.BatchPathCoverageFileRunner
 * @author Zhang Yifan
 */
public final class BatchFileOutcome<TTestOutcome extends TestOutcome> {

  final List<TaskOutcome<TTestOutcome>> succeedFunctions;
  final List<TaskOutcome<TTestOutcome>> failedFunctions;

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

    // append statistical information
    Util.appendNewLine(output);
    Util.appendAllWithNewLine(output, "Statistical information for each run:");

    final List<TTestOutcome> testOutcomes = this.succeedFunctions.stream().map(f -> f.optioanlTestOutcome.get()).collect(Collectors.toList());

    final int[] coverdNumSumForEachRun =
        IntStream.range(0, TestBuilder.repetitionNum)
                 .map(i -> testOutcomes.stream().mapToInt(outcome -> outcome.coverage[i].coverdNum).sum())
                 .toArray();
    final int[] totalNumSumForEachRun =
        IntStream.range(0, TestBuilder.repetitionNum)
                 .map(i -> testOutcomes.stream().mapToInt(outcome -> outcome.coverage[i].totalNum).sum())
                 .toArray();
    final double averageCoverageForRuns = (double) IntStream.of(coverdNumSumForEachRun).sum() / IntStream.of(totalNumSumForEachRun).sum();
    Util.appendAllWithNewLine(output,
                              "Average coverage for each run: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageCoverageForRuns));

    // final List<CoverageResult> coverageSumForEachRun =
    // IntStream.range(0, TestBuilder.repetitionNum)
    // .mapToObj(i -> new CoverageResult(coverdNumSumForEachRun[i], totalNumSumForEachRun[i]))
    // .collect(Collectors.toList());
    output.append("Detail coverage for each run: ");
    Joiner.on(", ").appendTo(output,
                             IntStream.range(0, TestBuilder.repetitionNum)
                                      .mapToObj(i -> coverdNumSumForEachRun[i] + " / " + totalNumSumForEachRun[i])
                                      .collect(Collectors.toList()));
    Util.appendNewLine(output);

    final double[] totalTimeForEachRun =
        IntStream.range(0, TestBuilder.repetitionNum)
                 .mapToDouble(i -> testOutcomes.stream().mapToDouble(outcome -> outcome.totalTime[i]).sum())
                 .toArray();

    final double averageTotalTimeForRuns = DoubleStream.of(totalTimeForEachRun).average().getAsDouble();
    Util.appendAllWithNewLine(output, "Average total time for each run: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTimeForRuns), " sec");

    output.append("Detail total time for each run: ");
    Joiner.on(", ").appendTo(output, DoubleStream.of(totalTimeForEachRun).boxed().toArray());
    Util.appendNewLine(output);

    final double[] algorithmTimeForEachRun =
        IntStream.range(0, TestBuilder.repetitionNum)
                 .mapToDouble(i -> testOutcomes.stream().mapToDouble(outcome -> outcome.algorithmTime[i]).sum())
                 .toArray();

    final double averageAlgorithmTimeForRuns = DoubleStream.of(algorithmTimeForEachRun).average().getAsDouble();
    Util.appendAllWithNewLine(output, "Average algorithm time for each run: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageAlgorithmTimeForRuns), " sec");

    output.append("Detail algorithm time for each run: ");
    Joiner.on(", ").appendTo(output, DoubleStream.of(algorithmTimeForEachRun).boxed().toArray());
    Util.appendNewLine(output);

    final double[] totalUncoverdPathsTimeForEachRun =
        IntStream.range(0, TestBuilder.repetitionNum)
                 .mapToDouble(i -> testOutcomes.stream().mapToLong(outcome -> outcome.totalUncoverdPathsTime[i]).sum() / 1000.0)
                 .toArray();

    final double averageTotalUncoverdPathsTimeForRuns = DoubleStream.of(totalUncoverdPathsTimeForEachRun).average().getAsDouble();
    Util.appendAllWithNewLine(output,
                              "Average time for uncovered paths for each run: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalUncoverdPathsTimeForRuns), " sec");
    Util.appendAllWithNewLine(output,
                              "Average time for covered paths for each run: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTimeForRuns - averageTotalUncoverdPathsTimeForRuns), " sec");

    output.append("Detail time for uncovered paths for each run: ");
    Joiner.on(", ").appendTo(output,
                             DoubleStream.of(totalUncoverdPathsTimeForEachRun)
                                         .mapToObj(t -> TestOutcome.DEFAULT_DECIMAL_FORMAT.format(t))
                                         .collect(Collectors.toList()));
    Util.appendNewLine(output);

    output.append("Detail time for covered paths for each run: ");
    Joiner.on(", ").appendTo(output,
                             IntStream.range(0, TestBuilder.repetitionNum)
                                      .mapToObj(i -> TestOutcome.DEFAULT_DECIMAL_FORMAT.format(totalTimeForEachRun[i] - totalUncoverdPathsTimeForEachRun[i]))
                                      .collect(Collectors.toList()));
    Util.appendNewLine(output);

    Util.appendNewLine(output);
    Util.appendAllWithNewLine(output, "Statistical information for each function:");
    final double averageCoverage =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().coverage)
                             .mapToDouble(covergaeArray -> Stream.of(covergaeArray).mapToDouble(CoverageResult::coverageRatio).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average coverage for each function: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageCoverage));

    final double averageTotalTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().totalTime)
                             .mapToDouble(totalTimeArray -> DoubleStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average total time for each function: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTime), " sec");

    final double averageAlgorithmTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().algorithmTime)
                             .mapToDouble(totalTimeArray -> DoubleStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average algorithm time for each function: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageAlgorithmTime), " sec");

    final double averageTotalUncoveredPathsTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().totalUncoverdPathsTime)
                             .mapToDouble(totalTimeArray -> LongStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();

    // Note: `averageTotalUncoveredPathsTime`'s time unit is ms.
    Util.appendAllWithNewLine(output, "Average total time for uncovered paths for each function: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalUncoveredPathsTime / 1000.0),
                              " sec");

    Util.appendAllWithNewLine(output, "Average total time for covered paths for each function: ",
                              TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTime - averageTotalUncoveredPathsTime / 1000.0),
                              " sec");
  }

  private static void appendTestFunctionSignuature(final Appendable output, final TaskOutcome<?> outcome) throws IOException {
    assert output != null;
    assert outcome != null;

    Util.appendAllWithNewLine(output, "Function: ", outcome.testFunctionSignuature);
  }
}