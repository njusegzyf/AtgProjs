package nju.seg.zhangyf.atgwrapper.outcome;

/**
 * @author Zhang Yifan
 */
public final class CoverageResult {
  public final int coverdNum;
  public final int totalNum;

  public CoverageResult(final int coverdNum, final int totalNum) {
    assert coverdNum >= 0 && coverdNum <= totalNum;
    // assert totalNum >= 0;

    this.coverdNum = coverdNum;
    this.totalNum = totalNum;
  }

  public String toCoverageString() {
    return this.coverdNum + "/" + this.totalNum;
  }

  public double coverageRatio() {
    return (double) this.coverdNum / this.totalNum;
  }
}
