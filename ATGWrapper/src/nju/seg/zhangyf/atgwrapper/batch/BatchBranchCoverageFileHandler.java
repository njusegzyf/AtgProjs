package nju.seg.zhangyf.atgwrapper.batch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.core.resources.IFile;

import com.google.common.base.Strings;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.CFGPath;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.batch.BatchBranchCoverageConfig.BatchBranchCoverageItemConfig;
import nju.seg.zhangyf.atgwrapper.batch.BatchBranchCoverageConfig.TargetNodeConfig;
import nju.seg.zhangyf.atgwrapper.cfg.CfgPathUtil;
import nju.seg.zhangyf.atgwrapper.coverage.BranchCoverage;
import nju.seg.zhangyf.atgwrapper.outcome.BranchCoverageTestOutcome;
import nju.seg.zhangyf.util.CdtUtil;
import nju.seg.zhangyf.util.ResourceAndUiUtil;

/**
 * @author Zhang Yifan
 */
public final class BatchBranchCoverageFileHandler extends BatchFileHandlerBase<BatchBranchCoverageItemConfig, BatchBranchCoverageConfig, BranchCoverageTestOutcome> {

  @Override
  protected BatchBranchCoverageConfig parseConfig(final IFile configFile) throws Exception {
    assert configFile != null;

    return BatchBranchCoverageConfig.parseBatchConfig(ResourceAndUiUtil.eclipseFileToPath(configFile));
  }

  @Override
  protected Predicate<IFunctionDeclaration> getFunctionFilter(final BatchBranchCoverageItemConfig batchItem) {
    assert batchItem != null && !Strings.isNullOrEmpty(batchItem.batchFunction);

    return fun -> batchItem.batchFunction.equals(fun.getElementName());
  }

  @Override
  protected BranchCoverageTestOutcome runTest(final IFunctionDeclaration function,
                                              final BatchBranchCoverageConfig batchConfig,
                                              final BatchBranchCoverageItemConfig batchItem) {
    assert function != null && batchConfig != null && batchItem != null;

    final String functionSignature = CdtUtil.getFunctionSinguatureOrName(function);
    AtgWrapperPluginSettings.doIfDebug(() -> {
      System.out.println("\nProcess function: " + functionSignature + ", use Branch Coverage.\n");
    });

    // if config item defines target nodes, create a `targetNodesProvider` that returns this target nodes,
    // else use the default one (`BranchCoverage::getTargetNodeIdsInBranchCoverage`)
    final Function<IFunctionDeclaration, List<String>> targetNodesProvider = batchItem.targetNodes.map(targetNodes -> {
      final Function<IFunctionDeclaration, List<String>> provider = ignored -> {
        return targetNodes.stream().map(c -> c.name)
                          .collect(Collectors.toList());
      };
      return provider;
    }).orElse(BranchCoverage::getTargetNodeIdsInBranchCoverage);

    final BiFunction<IFunctionDeclaration, String, List<CFGPath>> targetPathsProvider = batchItem.targetNodes.map(targetNodes -> {
      final BiFunction<IFunctionDeclaration, String, List<CFGPath>> provider = (ignoredFunction, nodeName) -> {
        // get the config related to the node
        final TargetNodeConfig nodeConfig = targetNodes.stream().filter(c -> nodeName.equals(c.name))
                                                       .findFirst()
                                                       .get();

        return nodeConfig.targetPaths.map((final List<List<String>> pathsOfNodeNames) -> { // if the targetPaths of the node is defined, use it
          // use `CfgPathUtil.getRelatedCfgPath` to map list of node names to a `CFGPath`, and then collect the `CFGPath`s
          return pathsOfNodeNames.stream().map(pathOfnodeNams -> CfgPathUtil.getRelatedCfgPath(pathOfnodeNams, TestBuilder.allPaths).get())
                                 .collect(Collectors.toList());
        }).orElse(BranchCoverage.getAllTargetPaths(ignoredFunction, nodeName)); // else get all target paths
      };
      return provider;
    }).orElse(BranchCoverage::getAllTargetPaths);

    final BranchCoverage branchCoverage = new BranchCoverage();
    branchCoverage.buildCfgAndPaths(function);

    try {
      // The `run` method and underlying methods are fixed to make the work cancelable.
      // They will check whether the thread is interrupted which means the task is cancelled, and throw `CancellationException` if interrupted.
      final double[] branchCoverages = branchCoverage.run(function, targetNodesProvider, Optional.empty(), targetPathsProvider, Optional.empty());

      // build a outcome for the single test, which is a snapshot of current `TestBuilder`.
      return new BranchCoverageTestOutcome(functionSignature, branchCoverages);
    } catch (final CancellationException ce) {
      // FIXME instead of rethrow, we may gather some info from the canceled work.
      throw ce;
    }
  }
}
