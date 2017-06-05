package nju.seg.zhangyf.atg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

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

  // @since 0.1 Able to change the temp and result folder

  /**
   * @since 0.1
   */
  public static String resultFolderPathString = "lffResult/tempResult";

  /**
   * @since 0.1
   */
  public static String tempFolderPathString = "lffResult/temp";

  /**
   * Gets the temp path for a function.
   * 
   */
  public static Path getTempPath(final String functionName) throws IOException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(functionName));
    
    final Path tempPathFolder = Paths.get(AtgPluginSettings.tempFolderPathString);
    Files.createDirectories(tempPathFolder);
    return tempPathFolder.resolve(functionName + ".dat");
  }

  @Deprecated
  private AtgPluginSettings() {}
}
