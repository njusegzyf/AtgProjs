package nju.seg.zhangyf.atgwrapper.outcome;

import java.io.IOException;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

import com.google.common.base.Preconditions;

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

    if (this.succeedFunctions.isEmpty()) {
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
    Util.appendAllWithNewLine(output, "Statistical information:");

    final double averageCoverage =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().coverage)
                             .mapToDouble(totalTimeArray -> DoubleStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average coverage: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageCoverage));

    final double averageTotalTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().totalTime)
                             .mapToDouble(totalTimeArray -> DoubleStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average total time: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTime), " seconds");

    final double averageAlgorithmTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().algorithmTime)
                             .mapToDouble(totalTimeArray -> DoubleStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    Util.appendAllWithNewLine(output, "Average algorithm time: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageAlgorithmTime), " seconds");

    final double averageTotalUncoveredPathsTime =
        this.succeedFunctions.stream().map(outcome -> outcome.optioanlTestOutcome.get().totalUncoverdPathsTime)
                             .mapToDouble(totalTimeArray -> LongStream.of(totalTimeArray).average().getAsDouble())
                             .average()
                             .getAsDouble();
    // Note: `averageTotalUncoveredPathsTime`'s time unit is ms.
    Util.appendAllWithNewLine(output, "Average total time for uncovered paths: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalUncoveredPathsTime / 1000.0), " seconds");

    Util.appendAllWithNewLine(output, "Average total time for covered paths: ", TestOutcome.DEFAULT_DECIMAL_FORMAT.format(averageTotalTime - averageTotalUncoveredPathsTime / 1000.0), " seconds");

    
    // FIXME we may need to record and print the settings used by the batch, not current settings in class `ATG`
    Util.appendAllWithNewLine(output,
                              "Parameter setting: ",
                              "MAX_NUM_OF_PREDICT_PARAM=", String.valueOf(ATG.MAX_NUM_OF_PREDICT_PARAM),
                              ", MAX_NUM_OF_GENERATE_CYCLE=", String.valueOf(ATG.MAX_NUM_OF_GENERATE_CYCLE),
                              ", PREDICT_BOUNDARY=" + String.valueOf(ATG.PREDICT_BOUNDARY));
    Util.appendAllWithNewLine(output, "Search strategy: ", (ATG.SEARCH_STRATEGY == 0 ? "SEARCH_STRATEGY_ALL" : "SEARCH_STRATEGY_ONE_BY_ONE"));
  }

  private static void appendTestFunctionSignuature(final Appendable output, final TaskOutcome<?> outcome) throws IOException {
    assert output != null;
    assert outcome != null;

    Util.appendAllWithNewLine(output, "Function: ", outcome.testFunctionSignuature);
  }
}
