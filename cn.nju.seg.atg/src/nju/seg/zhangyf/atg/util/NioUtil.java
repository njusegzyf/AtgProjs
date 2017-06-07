package nju.seg.zhangyf.atg.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Preconditions;

/**
 * @version 0.1
 * @author Zhang Yifan
 */
public final class NioUtil {

  public static void createNonExistParentDirectories(final String filePathString) throws IOException {
    Preconditions.checkNotNull(filePathString);
    
    NioUtil.createNonExistParentDirectories(Paths.get(filePathString));
  }

  public static void createNonExistParentDirectories(final Path filePath) throws IOException {
    Preconditions.checkNotNull(filePath);
    
    final Path fileParentPath = filePath.getParent();
    Files.createDirectories(fileParentPath);
    // if (!Files.exists(filePath) && !Files.exists(fileParentPath)) {
    // Files.createDirectories(fileParentPath);
    // }
  }

  public static Path createFileAndNonExistParentDirectories(final Path filePath) throws IOException {
    Preconditions.checkNotNull(filePath);

    Files.createDirectories(filePath.getParent());
    if (!Files.exists(filePath)) {
      Files.createFile(filePath);
    }
    return filePath;
  }

  @Deprecated
  private NioUtil() {}
}
