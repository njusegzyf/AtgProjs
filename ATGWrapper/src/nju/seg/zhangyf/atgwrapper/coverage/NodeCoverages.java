package nju.seg.zhangyf.atgwrapper.coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.PCATG;

/**
 * @see cn.nju.seg.atg.parse.PathCoverage
 * @see cn.nju.seg.atg.util.PCATG
 * @author Zhang Yifan
 */
public final class NodeCoverages {

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
   * @param nodeId
   *          The id of the node to be covered.
   */
  static NodeCoverageOutcome runNodeCoverageInAtg(final String targetNodeName,
                                                  final Function<String, List<CFGPath>> targetPathsProvider,
                                                  final Optional<Function<List<CFGPath>, List<CFGPath>>> pathSortFunc) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(targetNodeName));
    Preconditions.checkNotNull(targetPathsProvider);
    Preconditions.checkNotNull(pathSortFunc);

    // Note: We do not use `TestBuilder.targetNode` like `atg-tsc` in `PathCoverage`. 
    // TestBuilder.targetNode = targetNodeName;
    
    // filter paths that contains the target node
    final List<CFGPath> filteredPaths = targetPathsProvider.apply(targetNodeName);

    // get the path to be searched in order (apply the `pathSortFunc` if present)
    final List<CFGPath> orderedTargetPaths = pathSortFunc.map(func -> func.apply(filteredPaths))
                                                         .orElse(filteredPaths);

    final ArrayList<CFGPath> failedPaths = Lists.newArrayListWithCapacity(orderedTargetPaths.size());

    for (final CFGPath targetPath : orderedTargetPaths) {
      assert targetPath != null;

      
      // 获取目标路径编号
      int pathIndex = PathCoverage.getPathNum(targetPath);
      // Note: `PCATG` gets the target path from static field `CoverageCriteria`,
      // so we must set the target path here.
      CoverageCriteria.targetPath = targetPath;
      // 执行ATG过程
      final int isCovered = new PCATG().generateTestData(pathIndex - 1);

      if (isCovered > -1) {
        // the path covered target node
        return new NodeCoverageOutcome(ATG.callFunctionName, targetNodeName, Optional.of(targetPath), failedPaths);
      } else {
        failedPaths.add(targetPath);
      }
    }

    // not find a path that can cover the target node
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
