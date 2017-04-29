package nju.seg.zhangyf.atgwrapper.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.core.resources.IFile;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.CFGPath;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.cfg.CfgPathUtil;
import nju.seg.zhangyf.atgwrapper.config.PathFragmentConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchBranchCoverageConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchBranchCoverageConfig.BatchBranchCoverageItemConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchBranchCoverageConfig.TargetNodeConfig;
import nju.seg.zhangyf.atgwrapper.coverage.BranchCoverage;
import nju.seg.zhangyf.atgwrapper.outcome.BranchCoverageTestOutcome;
import nju.seg.zhangyf.atgwrapper.outcome.CoverageResult;
import nju.seg.zhangyf.util.CdtUtil;
import nju.seg.zhangyf.util.ResourceAndUiUtil;

/**
 * @author Zhang Yifan
 */
public final class BatchBranchCoverageFileRunner extends BatchFileRunnerBase<BatchBranchCoverageItemConfig, BatchBranchCoverageConfig, BranchCoverageTestOutcome> {

  @Override
  protected BatchBranchCoverageConfig parseConfig(final IFile configFile) { // throws Exception {
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

        // Note: we do not allow use `targetPaths` and `targetPathFragements` together
        if (nodeConfig.targetPaths.isPresent()) {
          // if `targetPaths` is defined, use `CfgPathUtil.getRelatedCfgPath` to map list of node names to a `CFGPath`, and then collect the `CFGPath`s
          return nodeConfig.targetPaths.get().stream().map(pathOfnodeNams -> CfgPathUtil.getRelatedCfgPath(pathOfnodeNams, TestBuilder.allPaths).get())
                                       .collect(Collectors.toList());
        } else if (nodeConfig.targetPathFragments.isPresent()) {
          // if `targetPathFragements` is defined, get all paths that matches any path fragment in the `PathFragmentListConfig`
          return TestBuilder.allPaths.stream().filter(nodeConfig.targetPathFragments.get()::isMatchPath)
                                     .collect(Collectors.toList());
        } else {
          // if no information is provided, get all paths that covered the node
          return BranchCoverage.getAllCoveredPaths(ignoredFunction, nodeName);
        }
      };
      return provider;
    }).orElse(BranchCoverage::getAllCoveredPaths);

    final BranchCoverage branchCoverage = new BranchCoverage();
    branchCoverage.buildCfgAndPaths(function);

    try {
      // The `run` method and underlying methods are fixed to make the work cancelable.
      // They will check whether the thread is interrupted which means the task is cancelled, and throw `CancellationException` if interrupted.
      final CoverageResult[] branchCoverages = branchCoverage.run(function, targetNodesProvider, Optional.empty(), targetPathsProvider, Optional.empty());

      // build a outcome for the single test, which is a snapshot of current `TestBuilder`.
      return new BranchCoverageTestOutcome(functionSignature, branchCoverages);
    } catch (final CancellationException ce) {
      // FIXME instead of rethrow, we may gather some info from the canceled work.
      throw ce;
    }
  }

  @Override
  protected List<String> checkTestConfig(final IFunctionDeclaration function,
                                         final BatchBranchCoverageConfig batchConfig,
                                         final BatchBranchCoverageItemConfig batchItem) {
    assert function != null && batchConfig != null && batchItem != null;

    final ArrayList<String> errorList = Lists.newArrayList(super.checkTestConfig(function, batchConfig, batchItem));

    final BranchCoverage branchCoverage = new BranchCoverage();
    branchCoverage.buildCfgAndPaths(function);

    if (batchItem.targetNodes.isPresent()) {
      for (final BatchBranchCoverageConfig.TargetNodeConfig targetNode : batchItem.targetNodes.get()) {
        if (targetNode.targetPaths.isPresent()) {
          // if target paths are given, check these paths exist
          for (final List<String> targetPath : targetNode.targetPaths.get()) {
            if (!CfgPathUtil.getRelatedCfgPath(targetPath, TestBuilder.allPaths).isPresent()) {
              // if we can not find the related path
              errorList.add("Can not find related path for: " + Joiner.on(", ").join(targetPath));
            }
          }
        } else if (targetNode.targetPathFragments.isPresent()) {
          // if target path fragments are given, check that some each fragment is contained in at least one path
          for (final PathFragmentConfig pathFragment : targetNode.targetPathFragments.get().pathFragments) {
            final boolean isContainedInAnyPath = TestBuilder.allPaths.stream()
                                                                     .anyMatch(pathFragment::isMatchPath);
            if (!isContainedInAnyPath) {
              // if the path fragment is not contained in any path
              errorList.add("Can not find any path that contains path fragment: " + Joiner.on(", ").join(pathFragment.nodeNames));
            }
          }
        }
      }
    }

    return errorList;
  }
}
