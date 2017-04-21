package nju.seg.zhangyf.atgwrapper;

import nju.seg.zhangyf.atg.util.ThrowableAction;

/**
 * @since 0.1
 * @author Zhang Yifan
 */
public final class AtgWrapperPluginSettings {

  public static final boolean IS_DEBUG = true;

  public static final void doIfDebug(final ThrowableAction act) {
    if (AtgWrapperPluginSettings.IS_DEBUG) {
      try {
        act.apply();
      } catch (Exception e) {}
    }
  }

  @Deprecated
  private AtgWrapperPluginSettings() {}
}
