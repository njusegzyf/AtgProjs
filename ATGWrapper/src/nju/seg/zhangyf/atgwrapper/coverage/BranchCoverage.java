package nju.seg.zhangyf.atgwrapper.coverage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.CFGPath;
import nju.seg.zhangyf.atgwrapper.cfg.CfgPathUtil;
import nju.seg.zhangyf.atgwrapper.coverage.NodeCoverages.NodeCoverageOutcome;

/**
 * Use {@link nju.seg.zhangyf.atgwrapper.coverage.NodeCoverages#runNodeCoverageInAtg(int, java.util.Optional)} to perform branch coverage.
 * 
 * @see nju.seg.zhangyf.atgwrapper.coverage.NodeCoverages
 * @see cn.nju.seg.atg.parse.CoverageCriteria
 * @see cn.nju.seg.atg.parse.PathCoverage
 * @author Zhang Yifan
 */
public final class BranchCoverage extends CoverageCriteria {

  public BranchCoverage() {
    this(BranchCoverage.BRANCH_COVERAGE_ACTION_NAME);
  }

  private BranchCoverage(final String actionName) {
    super(actionName);
  }

  Optional<IFunctionDeclaration> lastBuiltFunction = Optional.empty();

  public void buildCfgAndPaths(final IFunctionDeclaration function) {
    Preconditions.checkNotNull(function);

    if (this.lastBuiltFunction.isPresent() && this.lastBuiltFunction.get().equals(function)) {
      // we have built CFG and paths for the function, do not rebuild
      return;
    }

    // 构建被测程序的CFG
    this.buildCFG(function);

    // 获取程序的路径集合
    // Note: Since we do not change paths, we do not need to build paths for every run
    PathCoverage.buildPaths();

    this.lastBuiltFunction = Optional.of(function);
  }

  /**
   * Note: The framework of this function refers to {@link cn.nju.seg.atg.parse.PathCoverage#run(IFunctionDeclaration)}
   */
  public void run(final IFunctionDeclaration function,
                  final Function<IFunctionDeclaration, List<String>> targetNodesProvider,
                  final Optional<Function<List<String>, List<String>>> targetNodeSorter,
                  final BiFunction<IFunctionDeclaration, String, List<CFGPath>> targetPathsProvider,
                  final Optional<Function<List<CFGPath>, List<CFGPath>>> pathSortFunc) {
    Preconditions.checkNotNull(function);
    Preconditions.checkNotNull(targetNodeSorter);

    this.buildCfgAndPaths(function);

    final List<String> targetNodeNames = targetNodesProvider.apply(function);
    final HashSet<String> targetNodeNamesSet = Sets.newHashSet(targetNodeNames);
    final List<String> sortedTargetNodeNames = targetNodeSorter.map(func -> func.apply(targetNodeNames))
                                                               .orElse(targetNodeNames);

    final String functionName = function.getElementName();

    // create the folder to store result files
    // if failed to create, `folderPath` will be null and results will not be written to files
    Path folderPath = null;
    try {
      folderPath = Files.createDirectories(Paths.get(ATG.resultFolder).resolve(functionName).toAbsolutePath());
    } catch (final IOException ignored) {}

    // record all run's branch coverage
    final double[] branchCoverages = new double[TestBuilder.repetitionNum];

    // reused result string builder
    final StringBuilder result = new StringBuilder();
    // reused string joiner
    final Joiner joiner = Joiner.on(", ");

    // Note: `indexOfRun` starts from 1
    for (int indexOfRun = 1; indexOfRun <= TestBuilder.repetitionNum; indexOfRun++) {
      // record a map of target node -> covered paths
      final ArrayListMultimap<String, CFGPath> coveredTargetNodesMap = ArrayListMultimap.create();
      // record all paths that have run
      final HashSet<CFGPath> completedPaths = Sets.newHashSetWithExpectedSize(TestBuilder.allPaths.size());

      // 获取当前微秒时间，为计算插件运行时间做准备
      TestBuilder.resetForNewTestRepeation();
      final long start_time = System.currentTimeMillis();

      for (final String targetNodeName : sortedTargetNodeNames) {
        assert !Strings.isNullOrEmpty(targetNodeName);

        if (coveredTargetNodesMap.containsKey(targetNodeName)) {
          // if we have covered the target node in previous runs
          continue;
        }

        // run node coverage
        final NodeCoverageOutcome targetNodeCoverage =
            NodeCoverages.runNodeCoverageInAtg(targetNodeName,
                                               nodeName -> targetPathsProvider.apply(function, nodeName),
                                               pathSortFunc,
                                               completedPaths,
                                               cfgPath -> { // when a new path is covered, mark all its nodes as covered
                                                 for (final SimpleCFGNode coveredNode : cfgPath.getPath()) {
                                                   final String coveredNodeName = coveredNode.getName();
                                                   if (targetNodeNamesSet.contains(coveredNodeName)) {
                                                     coveredTargetNodesMap.put(coveredNodeName, cfgPath);
                                                   }
                                                 }
                                               });

        // since the target node may be a virtual node that does not exist in code, we must add it by hand
        if (targetNodeCoverage.isTargetNodeCovered()) {
          final CFGPath cfgPath = targetNodeCoverage.optionalCoveredPath.get();
          coveredTargetNodesMap.put(targetNodeName, cfgPath);
        }
      }

      // handle one run result
      // Note: the output refers to `PathCoverage`
      final double execute_time = System.currentTimeMillis() - start_time - TestBuilder.function_time;
      TestBuilder.totalTime[indexOfRun - 1] = (execute_time + TestBuilder.function_time) / 1000.0;
      TestBuilder.algorithmTime[indexOfRun - 1] = execute_time / 1000.0;
      TestBuilder.totalFrequency[indexOfRun - 1] = TestBuilder.function_frequency;

      // 输出结果
      if (folderPath != null) {
        // clear result buffer
        result.delete(0, result.length());

        result.append("----------------------------run" + indexOfRun + "----------------------------\n");

        result.append("target branch nodes: ");
        joiner.appendTo(result, targetNodeNames);
        result.append('\n');

        result.append("covered target branch nodes: ");
        // Joiner.on(',').appendTo(result, coveredTargetNodesMap.keySet());
        // do follow to make the output nodes in order
        final Set<String> coveredNodeNamesSet = coveredTargetNodesMap.keySet();
        final List<String> coverdNodeNamesList = targetNodeNames.stream().filter(nodeName -> coveredNodeNamesSet.contains(nodeName))
                                                                .collect(Collectors.toList());
        joiner.appendTo(result, coverdNodeNamesList);
        result.append('\n');

        result.append("branch node coverage: ");
        final double branchCoverage = (double) coverdNodeNamesList.size() / targetNodeNames.size();
        branchCoverages[indexOfRun - 1] = branchCoverage;
        result.append(branchCoverage);
        result.append('\n');

        for (final String coveredNodeName : coverdNodeNamesList) {
          result.append("\ncovered target branch node: ");
          result.append(coveredNodeName);
          result.append(" with paths:\n");

          for (final CFGPath cfgPath : coveredTargetNodesMap.get(coveredNodeName)) {
            joiner.appendTo(result, CfgPathUtil.cfgPathNodeNames(cfgPath).collect(Collectors.toList()));
            result.append('\n');
          }
        }

        result.append("\ntotal time:" + (execute_time + TestBuilder.function_time) / 1000.0 + " sec\n");
        result.append("function time:" + TestBuilder.function_time / 1000.0 + " sec (" + TestBuilder.function_frequency + " times) \n");
        result.append("algorithm time:" + execute_time / 1000.0 + " sec\n");

        // @since 0.1
        result.append("io time: " + TestBuilder.totalIoTime / 1000.0 + " sec\n");

        final Path resultFilePath = folderPath.resolve(functionName + ".result(" + indexOfRun + ").txt");
        try {
          com.google.common.io.Files.asCharSink(resultFilePath.toFile(), Charsets.US_ASCII)
                                    .write(result);
        } catch (IOException ignored) {}
      }
    }

    // handle all runs' result

    // clear result buffer
    result.delete(0, result.length());

    final DecimalFormat df = new DecimalFormat("0.000");
    // prints each run's info
    for (int i = 0; i < TestBuilder.repetitionNum; i++) {
      Joiner.on('\t').appendTo(result,
                               "Run " + (i + 1) + ":",
                               branchCoverages[i],
                               TestBuilder.algorithmTime[i],
                               df.format(TestBuilder.totalTime[i] - TestBuilder.algorithmTime[i]),
                               TestBuilder.totalTime[i]);
      result.append('\n');
    }

    result.append("best coverage:\t" + Arrays.stream(branchCoverages).max().getAsDouble() + "\n");
    result.append("average coverage:\t" + Arrays.stream(branchCoverages).average().getAsDouble() + "\n");

    this.printTotalResult(result);
  }

  /**
   * @deprecated Use {@link #run(IFunctionDeclaration, Function, Optional, BiFunction, Optional)} instead.
   */
  @Deprecated
  @Override
  public void run(final IFunctionDeclaration ifd) {
    Preconditions.checkNotNull(ifd);

    this.run(ifd,
             BranchCoverage::getTargetNodeIdsInBranchCoverage, Optional.empty(),
             BranchCoverage::getAllTargetPaths, Optional.empty());
  }

  /**
   * Gets all target nodes for branch coverage.
   * <p>
   * This method is used when user do not specify target nodes in config file.
   */
  public static List<String> getTargetNodeIdsInBranchCoverage(final IFunctionDeclaration function) {
    Preconditions.checkNotNull(function);
    // FIXME instead of static specific target nodes for a specific function, we may get the from its AST

    switch (function.getElementName()) {
    case "conflict":
      return Lists.newArrayList("node3@conflict", "node4@conflict", "node7@conflict", "node8@conflict", "node10@conflict", "node11@conflict");

    default:
      throw new IllegalArgumentException("Can not get target nodes for baranch coverage in function: " + function.getElementName());
    }
  }

  /**
   * Gets all target paths for a given function and a given node.
   * <p>
   * This method is used when user do not specify target paths in config file.
   */
  public static List<CFGPath> getAllTargetPaths(final IFunctionDeclaration ifd, final String targetNodeName) {
    assert ifd != null;
    assert targetNodeName != null;
    // FIXME Since `NodeCoverages.getAllCoveredPaths` assume that the target function's CFG is parsed and is stored in `TestBuilder`,
    // so we should do some check here

    return NodeCoverages.getAllCoveredPaths(targetNodeName);
  }

  public static final String BRANCH_COVERAGE_ACTION_NAME = "atg-bc";
}
