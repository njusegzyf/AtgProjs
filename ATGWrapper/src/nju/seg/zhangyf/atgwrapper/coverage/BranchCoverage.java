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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.nju.seg.atg.gui.AtgConsole;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.CFGPath;
import nju.seg.zhangyf.atg.AtgPluginSettings;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.coverage.NodeCoverages.NodeCoverageOutcome;
import nju.seg.zhangyf.atgwrapper.outcome.CoverageOutcome;
import nju.seg.zhangyf.util.Util;

/**
 * Uses {@link nju.seg.zhangyf.atgwrapper.coverage.NodeCoverages#runNodeCoverageInAtg(int, java.util.Optional)} to perform branch coverage.
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

  // FIXME A`lastBuiltFunction` just record the last built function use `buildCfgAndPaths` method,
  // but other sub classes of `CoverageCriteria` also can build CFG, it will be wrong if we run BC and other coverage together.
  Optional<IFunctionDeclaration> lastBuiltFunction = Optional.empty();

  public void buildCfgAndPaths(final IFunctionDeclaration function) {
    Preconditions.checkNotNull(function);

    if (this.lastBuiltFunction.isPresent() && this.lastBuiltFunction.get().equals(function)) {
      // we have built CFG and paths for the function, do not rebuild
      return;
    }

    // 构建被测程序的CFG
    super.buildCFG(function);

    // 获取程序的路径集合
    // Note: Since we do not change paths, there is no need to rebuild paths for every run
    PathCoverage.buildPaths();

    this.lastBuiltFunction = Optional.of(function);
  }

  /**
   * @deprecated use {@link #buildCfgAndPaths(IFunctionDeclaration)}
   */
  @Deprecated
  @Override
  public void buildCFG(final IFunctionDeclaration ifd) {
    Preconditions.checkNotNull(ifd);

    this.buildCfgAndPaths(ifd);
    // super.buildCFG(ifd);
  }

  /**
   * Note: The framework of this function refers to {@link cn.nju.seg.atg.parse.PathCoverage#run(IFunctionDeclaration)}
   * 
   * @return
   */
  public CoverageOutcome[] run(final IFunctionDeclaration function,
                              final Function<IFunctionDeclaration, List<String>> targetNodesProvider,
                              final Optional<Function<List<String>, List<String>>> targetNodeSorter,
                              final BiFunction<IFunctionDeclaration, String, List<CFGPath>> targetPathsProvider,
                              final Optional<Function<List<CFGPath>, List<CFGPath>>> pathSorter) {
    Preconditions.checkNotNull(function);
    Preconditions.checkNotNull(targetNodesProvider);
    Preconditions.checkNotNull(targetNodeSorter);
    Preconditions.checkNotNull(targetPathsProvider);
    Preconditions.checkNotNull(pathSorter);

    this.buildCfgAndPaths(function);

    final List<String> targetNodeNames = targetNodesProvider.apply(function);
    final HashSet<String> targetNodeNamesSet = Sets.newHashSet(targetNodeNames);
    final List<String> sortedTargetNodeNames = targetNodeSorter.map(func -> func.apply(targetNodeNames))
                                                               .orElse(targetNodeNames);

    final String functionName = function.getElementName();

    // create the folder to store result files
    // if failed to create, `folderPath` will be null and results will not be written to files
    /* final */ Path folderPath = null;
    try {
      folderPath = Files.createDirectories(Paths.get(AtgPluginSettings.resultFolderPathString).resolve(functionName).toAbsolutePath());
    } catch (final IOException ignored) {
      // throw new IllegalStateException();
    }

    // record all run's branch coverage
    final CoverageOutcome[] branchCoverages = new CoverageOutcome[TestBuilder.repetitionNum];
    final int totalBranchNum = targetNodeNames.size();
    final int totalPathNum = TestBuilder.allPaths.size();

    // reused result string builder
    final StringBuilder result = new StringBuilder();
    // reused string joiner
    final Joiner joinerOnComma = Joiner.on(", ");

    // Note: `indexOfRun` starts from 1
    for (int indexOfRun = 1; indexOfRun <= TestBuilder.repetitionNum; indexOfRun++) {
      // record a map of target node -> covered paths
      // Note: `HashMultimap` behaves like a `Map<String, HashMap<CFGPath>>`
      final HashMultimap<String, CFGPath> coveredTargetNodesMap = HashMultimap.create();
      // record all paths that have run
      final HashSet<CFGPath> completedPaths = Sets.newHashSetWithExpectedSize(TestBuilder.allPaths.size());

      // 获取当前微秒时间，为计算插件运行时间做准备
      TestBuilder.resetForNewTestRepeation();
      final long start_time = System.currentTimeMillis();

      // use `AtomicInteger` to record tested node number, so that it is `final` and can be used in lambdas
      final AtomicInteger testedNodeNum = new AtomicInteger(0);
      for (final String targetNodeName : sortedTargetNodeNames) {
        // run node/branch coverage for each node/branch
        assert !Strings.isNullOrEmpty(targetNodeName);

        if (coveredTargetNodesMap.containsKey(targetNodeName)) {
          // if we have covered the target node in previous runs
          testedNodeNum.incrementAndGet();

          // print progress for debug
          AtgWrapperPluginSettings.doIfDebug(() -> {
            AtgConsole.consoleStream.println(targetNodeName + " is coverd in previous run.");
            AtgConsole.consoleStream.println("Complete " + testedNodeNum.get() + "/" + totalBranchNum + " branches.");
          });

          continue;
        }
        
        final Consumer<CFGPath> executedPathHandler = cfgPath -> {
          // when we get an executed path, mark all its nodes as covered
          for (final SimpleCFGNode coveredNode : cfgPath.getPath()) {
            final String coveredNodeName = coveredNode.getName();
            // for an executed path, only add to the `coveredTargetNodesMap` it is not covered before,
            // otherwise we may add too much paths to the `coveredTargetNodesMap`
            if (targetNodeNamesSet.contains(coveredNodeName) 
                && (!coveredTargetNodesMap.containsKey(coveredNodeName))) {
              coveredTargetNodesMap.put(coveredNodeName, cfgPath);
            }
          }
        };

        final Consumer<CFGPath> newCoveredPathHandler = cfgPath -> {
          // when a new path is covered, always mark all its nodes as covered
          for (final SimpleCFGNode coveredNode : cfgPath.getPath()) {
            final String coveredNodeName = coveredNode.getName();
            if (targetNodeNamesSet.contains(coveredNodeName)) {
              coveredTargetNodesMap.put(coveredNodeName, cfgPath);
            }
          }
        };

        // run node coverage
        final NodeCoverageOutcome targetNodeCoverage =
            NodeCoverages.runNodeCoverageInAtg(targetNodeName,
                                               nodeName -> targetPathsProvider.apply(function, nodeName),
                                               pathSorter,
                                               completedPaths,
                                               newCoveredPathHandler,
                                               executedPathHandler);

        testedNodeNum.incrementAndGet();

        // print progress for debug
        AtgWrapperPluginSettings.doIfDebug(() -> {
          AtgConsole.consoleStream.println("Complete " + testedNodeNum.get() + "/" + totalBranchNum + " branches.");
          AtgConsole.consoleStream.println("Complete " + completedPaths.size() + "/" + totalPathNum + " paths.");
        });

        // since the target node may be a virtual node that does not exist in code, we must add it by hand
        if (targetNodeCoverage.isTargetNodeCovered()) {
          final CFGPath cfgPath = targetNodeCoverage.optionalCoveredPath.get();
          coveredTargetNodesMap.put(targetNodeName, cfgPath);
        }
      }

      // handle one run result
      // Note: the output refers to `PathCoverage`
      final long execute_time = System.currentTimeMillis() - start_time - TestBuilder.function_time;
      final long totalTimeInMs = execute_time + TestBuilder.function_time;
      double totalTime = totalTimeInMs / 1000.0;
      TestBuilder.totalTime[indexOfRun - 1] = totalTime;
      TestBuilder.algorithmTime[indexOfRun - 1] = execute_time / 1000.0;
      TestBuilder.totalUncoverdPathsTime[indexOfRun - 1] = TestBuilder.uncoverdPathsTime;
      TestBuilder.totalFrequency[indexOfRun - 1] = TestBuilder.function_frequency;

      // 输出结果
      if (folderPath != null) {
        // clear result buffer
        result.delete(0, result.length());

        result.append("----------------------------Run" + indexOfRun + "----------------------------\n");

        result.append("Target branch nodes: ");
        joinerOnComma.appendTo(result, targetNodeNames);
        result.append('\n');

        result.append("Covered target branch nodes: ");
        // Joiner.on(',').appendTo(result, coveredTargetNodesMap.keySet());
        // do follow to make the output nodes in order
        final Set<String> coveredNodeNamesSet = coveredTargetNodesMap.keySet();
        final List<String> coverdNodeNamesList = targetNodeNames.stream().filter(nodeName -> coveredNodeNamesSet.contains(nodeName))
                                                                .collect(Collectors.toList());
        joinerOnComma.appendTo(result, coverdNodeNamesList);
        result.append('\n');

        result.append("Branch node coverage: ");
        branchCoverages[indexOfRun - 1] = new CoverageOutcome(coverdNodeNamesList.size(), totalBranchNum);
        result.append(branchCoverages[indexOfRun - 1].toCoverageString());
        result.append('\n');

        result.append("Total time:" + totalTime + " sec\n");
        result.append("Function time:" + TestBuilder.function_time / 1000.0 + " sec (" + TestBuilder.function_frequency + " times) \n");
        result.append("Algorithm time:" + execute_time / 1000.0 + " sec\n");

        final long uncoverdPathsTimeInMs = TestBuilder.uncoverdPathsTime;
        Util.appendAllWithNewLine(result, "Total time for uncovered paths:", String.valueOf(uncoverdPathsTimeInMs / 1000.0), " sec");
        Util.appendAllWithNewLine(result, "Total time for covered paths:", String.valueOf((totalTimeInMs - uncoverdPathsTimeInMs) / 1000.0), " sec");
        Util.appendAllWithNewLine(result, "Io time: ", String.valueOf(TestBuilder.ioTime / 1000.0), " sec");

        // print each node's coverage
        for (final String coveredNodeName : coverdNodeNamesList) {
          Util.appendNewLine(result);
          Util.appendAllWithNewLine(result, "Covered target branch node: ", coveredNodeName, " with paths:");

          for (final CFGPath cfgPath : coveredTargetNodesMap.get(coveredNodeName)) {
            joinerOnComma.appendTo(result, cfgPath.getPathNodeNames().collect(Collectors.toList()));
            result.append('\n');
          }
        }

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
    Joiner joinerOnTab = Joiner.on('\t');
    // prints each run's info
    for (int i = 0; i < TestBuilder.repetitionNum; i++) {
      joinerOnTab.appendTo(result,
                           "Run " + (i + 1) + ":",
                           branchCoverages[i].coverdNum + " / " + branchCoverages[i].totalNum,
                           TestBuilder.algorithmTime[i],
                           df.format(TestBuilder.totalTime[i] - TestBuilder.algorithmTime[i]),
                           TestBuilder.totalTime[i]);
      result.append('\n');
    }

    final double[] branchCoveragesRatio = Arrays.stream(branchCoverages).mapToDouble(CoverageOutcome::coverageRatio).toArray();
    Util.appendAllWithNewLine(result, "Best branch coverage ratio: ", String.valueOf(DoubleStream.of(branchCoveragesRatio).max().getAsDouble()));
    Util.appendAllWithNewLine(result, "Average branch coverage ratio: ", String.valueOf(DoubleStream.of(branchCoveragesRatio).average().getAsDouble()));

    result.append("Detail coverage ratio:\n");
    joinerOnTab.appendTo(result, DoubleStream.of(branchCoveragesRatio)
                                             .boxed()
                                             .collect(Collectors.toList()));
    Util.appendNewLine(result);

    this.printTotalResult(result);

    return branchCoverages;
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
             BranchCoverage::getAllCoveredPaths, Optional.empty());
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
   * Gets all paths that covers a given node in a given function.
   * <p>
   * This method is used when user do not specify target paths in config file.
   */
  public static List<CFGPath> getAllCoveredPaths(final IFunctionDeclaration ifd, final String targetNodeName) {
    assert ifd != null;
    assert targetNodeName != null;
    // FIXME Since `NodeCoverages.getAllCoveredPaths` assume that the target function's CFG is parsed and is stored in `TestBuilder`,
    // so we should do some check here

    return NodeCoverages.getAllCoveredPaths(targetNodeName);
  }

  public static final String BRANCH_COVERAGE_ACTION_NAME = "atg-bc";
}
