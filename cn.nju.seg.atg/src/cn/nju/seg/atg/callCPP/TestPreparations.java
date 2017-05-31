package cn.nju.seg.atg.callCPP;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class TestPreparations {

  public static void prepareTest(final String testFunctionName, final CallCPP proxy) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(testFunctionName));
    Preconditions.checkNotNull(proxy);

    switch (testFunctionName) {
    case CallCPP.TCAS_TEST_FUNCTION_NAME:
      proxy.prepareStat();
      break;
    default:
      // do nothing
    }
  }

  @Deprecated
  private TestPreparations() {}
}
