package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.nju.seg.atg.parse.PathCoverage;
import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
import nju.seg.zhangyf.atgwrapper.coverage.BranchCoverage;

/**
 * @author Zhang Yifan
 */
public final class BatchFileRunners {

  public static BatchFileRunnerBase<?, ?, ?> createBatchFileRunner(final String action) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(action));

    final String trimedAction = action.trim();
    if (BranchCoverage.BRANCH_COVERAGE_ACTION_NAME.equalsIgnoreCase(trimedAction)) {
      return new BranchCoverageBatchFileRunner();
    } else if (PathCoverage.PATH_COVERAGE_ACTION_NAME.equalsIgnoreCase(trimedAction) || PathCoverage.TARGET_NODE_COVERAGE_ACTION_NAME.equalsIgnoreCase(trimedAction)) {
      return new PathCoverageBatchFileRunner();
    } else if (ConfigTags.COLLECT_BRANCH_COVERAGE_TAG.equalsIgnoreCase(trimedAction)) {
      return new CollectBranchCoverageBatchFileRunner();
    } else {
      // TODO handle other coverage
      throw new IllegalArgumentException("Unknown action: " + action);
    }
  }

  public static Optional<BatchFileRunnerBase<?, ?, ?>> tryCreateBatchFileRunner(final String action) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(action));

    try {
      return Optional.of(BatchFileRunners.createBatchFileRunner(action));
    } catch (final Throwable ignored) {
      return Optional.empty();
    }
  }

  private BatchFileRunners() {}
}
