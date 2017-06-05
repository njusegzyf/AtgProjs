package nju.seg.zhangyf.atgwrapper.batch;

import java.util.concurrent.CancellationException;
import java.util.function.Predicate;
import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.typesafe.config.Config;

import cn.nju.seg.atg.gui.AtgConsole;
import cn.nju.seg.atg.util.ATG;
import nju.seg.zhangyf.atgwrapper.config.batch.CollectBranchCoverageBatchConfig.CollectBranchCoverageBatchItemConfig;
import nju.seg.zhangyf.atgwrapper.coverage.Coverages;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.config.batch.CollectBranchCoverageBatchConfig;
import nju.seg.zhangyf.atgwrapper.outcome.CollectCoverageOutcome;
import nju.seg.zhangyf.util.CdtUtil;

/**
 * 
 * @author Zhang Yifan
 */
public final class CollectBranchCoverageBatchFileRunner
    extends BatchFileRunnerBase<CollectBranchCoverageBatchItemConfig, CollectBranchCoverageBatchConfig, CollectCoverageOutcome> {

  @Override
  protected CollectBranchCoverageBatchConfig parseConfig(final Config rawConfig) {
    assert rawConfig != null;

    return CollectBranchCoverageBatchConfig.parseBatchConfig(rawConfig);
  }

  @Override
  protected Predicate<IFunctionDeclaration> getFunctionFilter(CollectBranchCoverageBatchItemConfig batchItem) {
    assert batchItem != null && !Strings.isNullOrEmpty(batchItem.batchFunction);

    return fun -> batchItem.batchFunction.equals(fun.getElementName());
  }

  @Override
  protected CollectCoverageOutcome runTest(final IFunctionDeclaration function,
                                           final CollectBranchCoverageBatchConfig batchConfig,
                                           final CollectBranchCoverageBatchItemConfig batchItem) {
    assert function != null && batchConfig != null && batchItem != null;
    // require that config item defines target nodes
    Preconditions.checkArgument(batchItem.targetNodes.isPresent());

    final String functionSignature = CdtUtil.getFunctionSinguatureOrName(function);
    AtgWrapperPluginSettings.doIfDebug(() -> {
      System.out.println("\nProcess function: " + functionSignature + ", use Branch Coverage.\n");
    });

    try {
      final CollectCoverageOutcome outcome = Coverages.collectCoverageFromInputs(function, batchItem.inputs, batchItem.targetNodes.get(), ATG.callCPP);
      AtgConsole.consoleStream.println("Collect coverage for fucntion: " + functionSignature);
      return outcome;
    } catch (final CancellationException ce) {
      // FIXME instead of rethrow, we may gather some info from the canceled work.
      AtgConsole.consoleStream.println("Failed to collect coverage for fucntion: " + functionSignature);
      throw ce;
    }
  }
}
