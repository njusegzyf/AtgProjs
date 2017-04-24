package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;

public abstract class StorageConfig {

  /**
   * @see cn.nju.seg.atg.util.ATG#resultFolder
   */
  public final Optional<String> resultFolder;

  public final boolean isCopyConfigToResultFolder;

  public StorageConfig(final Optional<String> resultFolder, final boolean isCopyConfigToResultFolder) {
    assert resultFolder != null;

    this.resultFolder = resultFolder;
    this.isCopyConfigToResultFolder = isCopyConfigToResultFolder;
  }

  public static abstract class StrorageConfigBuilder {

    Optional<String> resultFolder;
    boolean isCopyConfigToResultFolder;

    protected StrorageConfigBuilder() {
      this.clear();
    }

    // @OverridingMethodsMustInvokeSuper
    protected void clear() {
      this.resultFolder = Optional.empty();
      this.isCopyConfigToResultFolder = false;
    }

    // @OverridingMethodsMustInvokeSuper
    protected void checkVaild() {
      assert this.resultFolder != null;
    }
  }
}
