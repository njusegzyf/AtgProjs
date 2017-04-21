package nju.seg.zhangyf.atg;

import nju.seg.zhangyf.atg.util.ThrowableAction;

/**
 * @since 0.1
 * @author Zhang Yifan
 */
public final class AtgPluginSettings {

  public static final boolean IS_DEBUG = true;

  public static final void doIfDebug(final ThrowableAction act) {
    if (AtgPluginSettings.IS_DEBUG) {
      try {
        act.apply();
      } catch (Exception ignored) {}
    }
  }
  
  @Deprecated
  private AtgPluginSettings() {}
}
