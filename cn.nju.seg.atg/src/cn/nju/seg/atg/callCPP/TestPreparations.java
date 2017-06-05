package cn.nju.seg.atg.callCPP;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class TestPreparations {

  public static void prepareTest(final String testFunctionName, final CallCPP callProxy) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(testFunctionName));
    Preconditions.checkNotNull(callProxy);

    switch (testFunctionName) {
    case CallCPP.STAT_TEST_FUNCTION_NAME: // for Stat tests
      callProxy.prepareStat();
      break;
    default:
      // do nothing
    }
  }

  @Deprecated
  private TestPreparations() {}
}
