package nju.seg.zhangyf.atgwrapper.outcome;

/**
 * @author Zhang Yifan
 */
public final class CollectCoverageOutcome extends TestOutcome {

  public CollectCoverageOutcome(final String testFunctionSignuature,
                                final long functionTime,
                                final int functionFrequency,
                                final String[] findResult,
                                final CoverageOutcome coverage) {
    super(testFunctionSignuature, 1, functionTime, functionFrequency,
          new double[] { functionTime }, new int[] { functionFrequency }, new double[] { functionTime / 1000.0 }, // filled content
          findResult, new long[] { 0L }, new CoverageOutcome[] { coverage });
  }

  private static final long serialVersionUID = 1L;
}
