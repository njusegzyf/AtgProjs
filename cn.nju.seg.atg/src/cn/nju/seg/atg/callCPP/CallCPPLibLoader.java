package cn.nju.seg.atg.callCPP;

/**
 * @author Zhang Yifan
 */
public class CallCPPLibLoader {

  public static final String LIB_PREFIX = "CallCPP";

  public static final String[] libSuffixes = {
      "Coral",
      "Stat",
      "DartAndEtc",
      "BlindHashOpti"
  };

  public static void loadLibs() {
    for (final String libSuffix : CallCPPLibLoader.libSuffixes) {
      try {
        // 加载本地方法所在的链接库名
        System.loadLibrary(CallCPPLibLoader.LIB_PREFIX + libSuffix);
      } catch (UnsatisfiedLinkError e) {
        System.err.println("Cannot load callCPP library:\n" + e.toString());
      }
    }
  }

  @Deprecated
  private CallCPPLibLoader() {}
}
