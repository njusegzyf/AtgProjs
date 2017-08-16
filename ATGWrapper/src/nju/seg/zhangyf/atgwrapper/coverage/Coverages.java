package nju.seg.zhangyf.atgwrapper.coverage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.io.FileWriteMode;

import cn.nju.seg.atg.callCPP.CallCPP;
import cn.nju.seg.atg.pathParse.ZpathUtil;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.CallFunction;
import nju.seg.zhangyf.atg.AtgPluginSettings;
import nju.seg.zhangyf.atg.util.NioUtil;
import nju.seg.zhangyf.atgwrapper.config.batch.BranchCoverageBatchConfig.TargetNodeConfig;
import nju.seg.zhangyf.atgwrapper.outcome.CollectCoverageOutcome;
import nju.seg.zhangyf.atgwrapper.outcome.CoverageOutcome;

/**
 * @author Zhang Yifan
 */
public final class Coverages {

  /**
   * @param callProxy
   *          Currently, {@link cn.nju.seg.atg.util.ATG#callCPP} is used and this {@code callProxy} is not used.
   */
  public static CollectCoverageOutcome collectCoverageFromInputs(final IFunctionDeclaration function,
                                                                 final List<double[]> inputs,
                                                                 final List<TargetNodeConfig> targetNodes,
                                                                 final Path resultFile,
                                                                 final CallCPP callProxy) {
    Preconditions.checkNotNull(function);
    Preconditions.checkArgument(inputs != null && !inputs.isEmpty());
    Preconditions.checkArgument(targetNodes != null && !targetNodes.isEmpty());
    // check that the result file is writable
    Preconditions.checkNotNull(resultFile);
    try {
      Preconditions.checkArgument(Files.isWritable(NioUtil.createFileAndNonExistParentDirectories(resultFile)));
    } catch (IOException ignored) {
      throw new IllegalArgumentException("Can not write to resultFile: " + resultFile.toAbsolutePath().toString(), ignored);
    }
    Preconditions.checkNotNull(callProxy);

    final String functionSignature;
    try {
      functionSignature = function.getSignature();
    } catch (final CModelException e) {
      throw new IllegalArgumentException(e);
    }

    final Path tempPathFile;
    try {
      tempPathFile = AtgPluginSettings.getTempPath(function.getElementName());
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
    final String tempPathFileString = tempPathFile.toAbsolutePath().toString();

    // the `StringBuilder` used to store result that will be written to `resultFile`
    final StringBuilder result = new StringBuilder();

    final CallFunction callFunction = new CallFunction(null, tempPathFileString);
    // since `CallFunction` reads the function name from `ATG.callFunctionName`, we must set it here
    ATG.callFunctionName = function.getElementName();

    HashSet<TargetNodeConfig> coveredTargetNodes = Sets.newHashSet();

    final long functionTimeStart = System.currentTimeMillis();

    for (final double[] input : inputs) {
      // set input and then execute
      callFunction.setParameters(input);
      callFunction.callFunction();

      // read path according to `PCATG#getPathCoveredCondition`

      final CFGPath excutedPath = ZpathUtil.readPath_Z(0, tempPathFileString);
      for (final TargetNodeConfig targetNode : targetNodes) {
        // `!coveredTargetNodes.contains(targetNode)` is used to skip checking a target node is already covered by some input
        if ( /* !coveredTargetNodes.contains(targetNode) && */ targetNode.isMatchPath(excutedPath)) {
          // `targetNode` can be covered with `input`
          coveredTargetNodes.add(targetNode);
          result.append("Cover node \"" + targetNode.name + "\" with input: " + Arrays.toString(input));
          result.append('\n');
        }
      }
    }

    final long functionTime = System.currentTimeMillis() - functionTimeStart;

    final String[] coveredTargetNodeNames = coveredTargetNodes.stream()
                                                              .map(node -> node.name)
                                                              .<String> toArray(num -> new String[num]);

    // write result to `resultFile`
    try {
      com.google.common.io.Files.asCharSink(resultFile.toFile(), Charsets.US_ASCII)
                                .write("Covered nodes: " + Arrays.toString(coveredTargetNodeNames) + "\n\n");

      final String[] uncoveredTargetNodeNames = targetNodes.stream()
                                                           .filter(node -> !coveredTargetNodes.contains(node))
                                                           .map(node -> node.name)
                                                           .<String> toArray(num -> new String[num]);

      com.google.common.io.Files.asCharSink(resultFile.toFile(), Charsets.US_ASCII, FileWriteMode.APPEND)
                                .write("Uncovered nodes: " + Arrays.toString(uncoveredTargetNodeNames) + "\n\n");

      com.google.common.io.Files.asCharSink(resultFile.toFile(), Charsets.US_ASCII, FileWriteMode.APPEND)
                                .write(result);
    } catch (IOException ignored) {}

    return new CollectCoverageOutcome(functionSignature,
                                      functionTime,
                                      inputs.size(),
                                      coveredTargetNodeNames,
                                      new CoverageOutcome(coveredTargetNodes.size(), targetNodes.size()));
  }

  private Coverages() {}
}
