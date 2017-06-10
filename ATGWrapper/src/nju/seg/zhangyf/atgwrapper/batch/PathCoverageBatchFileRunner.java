package nju.seg.zhangyf.atgwrapper.batch;

import java.nio.channels.IllegalSelectorException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.typesafe.config.Config;

import cn.nju.seg.atg.parse.ConditionCoverage;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.config.batch.PathCoverageBatchConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.PathCoverageBatchConfig.PathCoverageBatchItemConfig;
import nju.seg.zhangyf.atgwrapper.outcome.TestOutcome;
import nju.seg.zhangyf.util.CdtUtil;

/**
 * @author Zhang Yifan
 */
public final class PathCoverageBatchFileRunner extends BatchFileRunnerBase<PathCoverageBatchItemConfig, PathCoverageBatchConfig, TestOutcome> {

  @Override
  protected PathCoverageBatchConfig parseConfig(final Config rawConfig) { // throws Exception {
    assert rawConfig != null;

    return PathCoverageBatchConfig.parseBatchConfig(rawConfig);
  }

  @Override
  protected Predicate<IFunctionDeclaration> getFunctionFilter(final PathCoverageBatchItemConfig batchItem) {
    assert batchItem != null;

    switch (batchItem.mode) {
    case ALL:
      return func -> true;

    case SPECIFY_INCLUDED:
      // if the config specify functions, the predicate checks the function's name
      return func -> batchItem.includedBatchFunctions.contains(func.getElementName());

    case SPECIFY_EXCLUDED:
      return func -> !batchItem.excludedBatchFunctions.contains(func.getElementName());

    default:
      throw new IllegalSelectorException();
    }
  }

  @Override
  protected TestOutcome runTest(final IFunctionDeclaration function, final PathCoverageBatchConfig batchConfig, final PathCoverageBatchItemConfig batchItem) {
    assert function != null && batchConfig != null && batchItem != null;

    final String action = batchConfig.atgConfig.flatMap(v -> v.action)
                                               .orElse("atg-pc");
    final String functionSignature = CdtUtil.getFunctionSinguatureOrName(function);

    // set the coverage criteria
    final CoverageCriteria cc;
    if (PathCoverage.TARGET_NODE_COVERAGE_ACTION_NAME.equals(action)
        || PathCoverage.PATH_COVERAGE_ACTION_NAME.equals(action)) { // action.equals("atg-tsc") || action.equals("atg-pc")
      cc = new PathCoverage(action);
      AtgWrapperPluginSettings.doIfDebug(() -> {
        System.out.println("\nProcess function: " + functionSignature + " with action: " + action + ", use PathCoverage.\n");
      });
    } else {
      cc = new ConditionCoverage(action);
      AtgWrapperPluginSettings.doIfDebug(() -> {
        System.out.println("\nProcess function: " + functionSignature + " with action: " + action + ", use ConditionCoverage.\n");
      });
    }

    try {
      // The `run` method and underlying methods are fixed to make the work cancelable.
      // They will check whether the thread is interrupted which means the task is cancelled, and throw `CancellationException` if interrupted.
      cc.run(function);

      // build a outcome for the single test, which is a snapshot of current `TestBuilder`.
      if (PathCoverage.PATH_COVERAGE_ACTION_NAME.equals(action)) {
        return TestOutcome.createForPathCoverage(functionSignature);
      } else if (PathCoverage.TARGET_NODE_COVERAGE_ACTION_NAME.equals(action)) {
        return TestOutcome.createForTargetNodeCoverage(functionSignature);
      } else {
        return TestOutcome.createForOtherCoverage(functionSignature);
      }
    } catch (final CancellationException ce) {
      // FIXME instead of rethrow, we may gather some info from the canceled work.
      throw ce;
    }
  }

  @Override
  protected List<String> checkTestConfig(final IFunctionDeclaration function,
                                         final PathCoverageBatchConfig batchConfig,
                                         final PathCoverageBatchItemConfig batchItem) {
    assert function != null && batchConfig != null && batchItem != null;

    // TODO add check for path coverage config
    return super.checkTestConfig(function, batchConfig, batchItem);
  }

  // public
}
