package nju.seg.zhangyf.atgwrapper.outcome;

import com.google.common.base.Preconditions;

/**
 * @author Zhang Yifan
 */
public final class BranchCoverageTestOutcome extends TestOutcome {

  public BranchCoverageTestOutcome(final String testFunctionSignuature, final CoverageResult[] coverage) {
    super(Preconditions.checkNotNull(testFunctionSignuature),
          Preconditions.checkNotNull(coverage));
  }
}
