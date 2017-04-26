package nju.seg.zhangyf.atgwrapper.batch;

import java.nio.channels.IllegalSelectorException;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;
import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.core.resources.IFile;

import cn.nju.seg.atg.parse.ConditionCoverage;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.batch.BatchConfig.BatchItem;
import nju.seg.zhangyf.atgwrapper.outcome.TestOutcome;
import nju.seg.zhangyf.util.CdtUtil;
import nju.seg.zhangyf.util.ResourceAndUiUtil;

/**
 * @author Zhang Yifan
 */
public final class BatchFileHandler extends BatchFileHandlerBase<BatchItem, BatchConfig, TestOutcome> {

  @Override
  protected BatchConfig parseConfig(IFile configFile) throws Exception {
    assert configFile != null;

    return BatchConfig.parseBatchConfig(ResourceAndUiUtil.eclipseFileToPath(configFile));
  }

  @Override
  protected Predicate<IFunctionDeclaration> getFunctionFilter(final BatchItem batchItem) {
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
  protected TestOutcome runTest(final IFunctionDeclaration function, final BatchConfig batchConfig, final BatchItem batchItem) {
    assert function != null && batchConfig != null && batchItem != null;

    final String action = batchConfig.atgConfig.flatMap(v -> v.action)
                                               .orElse("atg-pc");
    final String functionSignature = CdtUtil.getFunctionSinguatureOrName(function);

    // set the coverage criteria
    final CoverageCriteria cc;
    if (action.equals("atg-tsc") || action.equals("atg-pc")) {
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
    // The `run` method and underlying methods are fixed to make the work cancelable.
    // They will check whether the thread is interrupted which means the task is cancelled, and throw `CancellationException` if interrupted.
    try {
      cc.run(function);
      // build a outcome for the single test, which is a snapshot of current `TestBuilder`.
      return new TestOutcome(functionSignature);
    } catch (final CancellationException ce) {
      // FIXME instead of rethrow, we may gather some info from the canceled work.
      throw ce;
    }
  }

  // @Override
  // protected void processBatchResult(final BatchConfig batchConfig, final BatchFileOutcome<TestOutcome> batchFileOutcome) {}
}
