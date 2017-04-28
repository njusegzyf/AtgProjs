package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.nju.seg.atg.parse.PathCoverage;
import nju.seg.zhangyf.atgwrapper.coverage.BranchCoverage;

/**
 * @author Zhang Yifan
 */
public final class BatchFileRunners {

  public static BatchFileRunnerBase<?, ?, ?> createBatchFileRunner(final String action) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(action));

    final String lowercaseAction = action.trim().toLowerCase();
    if (BranchCoverage.BRANCH_COVERAGE_ACTION_NAME.equals(lowercaseAction)) {
      return new BatchBranchCoverageFileRunner();
    } else if (PathCoverage.PATH_COVERAGE_ACTION_NAME.equals(lowercaseAction) || PathCoverage.TARGET_NODE_COVERAGE_ACTION_NAME.equals(lowercaseAction)) {
      return new BatchPathCoverageFileRunner();
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
