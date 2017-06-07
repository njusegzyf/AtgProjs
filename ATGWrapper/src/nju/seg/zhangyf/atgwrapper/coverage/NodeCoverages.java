package nju.seg.zhangyf.atgwrapper.coverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cn.nju.seg.atg.gui.AtgConsole;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.PCATG;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;

/**
 * Uses {@link PCATG} to run node coverage.
 * 
 * @see cn.nju.seg.atg.parse.PathCoverage
 * @see cn.nju.seg.atg.util.PCATG
 * @author Zhang Yifan
 */
public final class NodeCoverages {

  /**
   * Represents the result of node coverage.
   * 
   * @author Zhang Yifan
   */
  final static class NodeCoverageOutcome {
    final String function;
    final String targetNodeName;
    final Optional<CFGPath> optionalCoveredPath;
    final List<CFGPath> failedPaths;

    NodeCoverageOutcome(final String function,
                        final String targetNodeName,
                        final Optional<CFGPath> optionalCoveredPath,
                        final List<CFGPath> failedPaths) {
      assert function != null;
      assert !Strings.isNullOrEmpty(targetNodeName);
      assert optionalCoveredPath != null;
      assert failedPaths != null;

      this.function = function;
      this.targetNodeName = targetNodeName;
      this.optionalCoveredPath = optionalCoveredPath;
      this.failedPaths = failedPaths;
    }

    public boolean isTargetNodeCovered() {
      return this.optionalCoveredPath.isPresent();
    }
  }

  /**
   * Runs node coverage with settings from {@link cn.nju.seg.atg.util.ATG}.
   * <p>
   * Note: This method does not build CFG and paths like {@link cn.nju.seg.atg.parse.PathCoverage#run(IFunctionDeclaration)} method,
   * so the CFG and paths should be built before run this method.
   * 
   * @param targetNodeName
   *          The name of the node to be covered.
   * @param completedPaths
   *          The paths that have been run node coverage.
   * @param coveredPathHandler
   *          The handler that will be called when a new path that covers the target is find.
   */
  static NodeCoverageOutcome runNodeCoverageInAtg(final String targetNodeName,
                                                  final Function<String, List<CFGPath>> targetPathsProvider,
                                                  final Optional<Function<List<CFGPath>, List<CFGPath>>> pathSorter,
                                                  final HashSet<CFGPath> completedPaths,
                                                  final Consumer<CFGPath> newCoveredPathHandler,
                                                  final Consumer<CFGPath> executedPathHandler) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(targetNodeName));
    Preconditions.checkNotNull(targetPathsProvider);
    Preconditions.checkNotNull(pathSorter);
    Preconditions.checkNotNull(completedPaths);
    Preconditions.checkNotNull(newCoveredPathHandler);
    Preconditions.checkNotNull(executedPathHandler);

    // Note: We do not use `TestBuilder.targetNode` like `atg-tsc` in `PathCoverage`.
    // TestBuilder.targetNode = targetNodeName;

    // filter paths that contains the target node
    final List<CFGPath> filteredPaths = targetPathsProvider.apply(targetNodeName);

    // get the path to be searched in order (apply the `pathSortFunc` if present)
    final List<CFGPath> orderedTargetPaths = pathSorter.map(func -> func.apply(filteredPaths))
                                                       .orElse(filteredPaths);

    final ArrayList<CFGPath> failedPaths = Lists.newArrayListWithCapacity(orderedTargetPaths.size());

    for (final CFGPath targetPath : orderedTargetPaths) {
      assert targetPath != null;

      // 获取目标路径编号
      final int pathIndex = PathCoverage.getPathNum(targetPath);
      // Note: `PCATG` gets the target path from static field `CoverageCriteria`, so we must set the target path here.
      CoverageCriteria.targetPath = targetPath;

      final boolean isCovered;
      if (!completedPaths.contains(targetPath)) { // if we have not run the path, run the paths
        // 执行ATG过程
        isCovered = new PCATG().generateTestData(pathIndex - 1, executedPathHandler) > -1;
        // mark the path as has been run
        completedPaths.add(targetPath);
        if (isCovered) { // if we find a new path that covers the target node
          newCoveredPathHandler.accept(targetPath);
        }
      } else { // if the path has been run, just get the coverage result from the path
        isCovered = targetPath.isCovered();
      }

      if (isCovered) { // the target path (which covers the target node) is covered
        // print debug info
        AtgWrapperPluginSettings.doIfDebug(() -> {
          AtgConsole.consoleStream.println("Cover node " + targetNodeName + " with path:");
          AtgConsole.consoleStream.println(targetPath.getJoinedPathNodeNamesAsString());
        });

        // report that we can cover the target node with the target path
        return new NodeCoverageOutcome(ATG.callFunctionName, targetNodeName, Optional.of(targetPath), failedPaths);
      } else { // the target path is not covered
        // print debug info
        AtgWrapperPluginSettings.doIfDebug(() -> {
          AtgConsole.consoleStream.println("Can not cover node " + targetNodeName + " with path:");
          AtgConsole.consoleStream.println(targetPath.getJoinedPathNodeNamesAsString());
        });

        failedPaths.add(targetPath);
      }
    }

    // after run all paths, we still can not find a path that can cover the target node
    return new NodeCoverageOutcome(ATG.callFunctionName, targetNodeName, Optional.empty(), failedPaths);
  }

  public static final List<CFGPath> getAllCoveredPaths(final String targetNodeName) {
    // filter paths that contains the target node
    final List<CFGPath> filteredPaths = TestBuilder.allPaths.stream()
                                                            .filter(cfgPath -> cfgPath.getPath().stream().anyMatch(node -> targetNodeName.equals(node.getName())))
                                                            .collect(Collectors.toList());

    return filteredPaths;
  }

  @Deprecated
  private NodeCoverages() {}
}
