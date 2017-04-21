package nju.seg.zhangyf.atgwrapper.batch;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * Stores results of processing a batch file.
 * 
 * @see nju.seg.zhangyf.atgwrapper.batch.BatchFileHandler
 * @author Zhang Yifan
 */
final class BatchFileOutcome {

  final List<String> succeedFunctionNames;
  final List<String> failedFunctionNames;

  BatchFileOutcome(final List<String> succeedFunctionNames,
                   final List<String> failedFunctionNames) {
    assert succeedFunctionNames != null;
    assert failedFunctionNames != null;

    this.succeedFunctionNames = succeedFunctionNames;
    this.failedFunctionNames = failedFunctionNames;
  }

  void printOutcome(final PrintStream output) {
    assert output != null;

    if (this.failedFunctionNames.isEmpty()) { // if all test are succeed
      output.println("Batch all functions sucessfully:");
      for (String functionName : this.succeedFunctionNames) {
        output.println("Function : " + functionName);
      }
    } else { // if some test are failed
      System.out.println("Failed to batch some functions for exceptions or timeouts:");
      for (String functionName : this.failedFunctionNames) {
        output.println("Function : " + functionName);
      }
      System.out.println("Succeeded to batch some functions:");
      for (String functionName : this.succeedFunctionNames) {
        output.println("Function : " + functionName);
      }
    }
  }

  public void printOutcomeAndCloseStream(final Supplier<PrintStream> outputSupplier) {
    assert outputSupplier != null;

    try (final PrintStream output = outputSupplier.get()) {
      this.printOutcome(output);
    }
  }
}
