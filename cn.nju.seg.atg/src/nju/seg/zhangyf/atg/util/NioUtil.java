package nju.seg.zhangyf.atg.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @version 0.1
 * @author Zhang Yifan
 */
public final class NioUtil {

  public static void createNonExistParentDirectories(final String filePathString) {
    NioUtil.createNonExistParentDirectories(Paths.get(filePathString));
  }

  public static void createNonExistParentDirectories(final Path filePath) {
    try {
      final Path fileParentPath = filePath.getParent();
      if (!Files.exists(filePath) && !Files.exists(fileParentPath)) {
        Files.createDirectories(fileParentPath);
      }
    } catch (final IOException e) {}
  }

  @Deprecated
  private NioUtil() {}
}
