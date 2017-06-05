package nju.seg.zhangyf.atgwrapper.coverage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import cn.nju.seg.atg.callCPP.CallCPP;
import cn.nju.seg.atg.pathParse.ZpathUtil;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.CFGPath;
import cn.nju.seg.atg.util.CallFunction;
import nju.seg.zhangyf.atg.AtgPluginSettings;
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
                                                                 final CallCPP callProxy) {
    Preconditions.checkNotNull(function);
    Preconditions.checkArgument(inputs != null && !inputs.isEmpty());
    Preconditions.checkArgument(targetNodes != null && !targetNodes.isEmpty());
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
        if (!coveredTargetNodes.contains(targetNode) && targetNode.isMatchPath(excutedPath)) {
          coveredTargetNodes.add(targetNode);
        }
      }
    }

    final long functionTime = System.currentTimeMillis() - functionTimeStart;

    final String[] coveredTargetNodeNames = coveredTargetNodes.stream().map(node -> node.name)
                                                              .<String> toArray(num -> new String[num]);

    return new CollectCoverageOutcome(functionSignature,
                                      functionTime,
                                      inputs.size(),
                                      coveredTargetNodeNames,
                                      new CoverageOutcome(coveredTargetNodes.size(), targetNodes.size()));
  }

  private Coverages() {}
}
