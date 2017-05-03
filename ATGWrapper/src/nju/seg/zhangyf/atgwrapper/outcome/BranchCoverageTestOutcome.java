package nju.seg.zhangyf.atgwrapper.outcome;

import java.io.Serializable;

import com.google.common.base.Preconditions;

/**
 * @author Zhang Yifan
 */
public final class BranchCoverageTestOutcome extends TestOutcome implements Serializable {

  public BranchCoverageTestOutcome(final String testFunctionSignuature, final CoverageResult[] coverage) {
    super(Preconditions.checkNotNull(testFunctionSignuature),
          Preconditions.checkNotNull(coverage));
  }
  
  private static final long serialVersionUID = 1L;
}
