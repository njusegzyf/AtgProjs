package nju.seg.zhangyf.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import com.google.common.base.Preconditions;

/**
 * @author Zhang Yifan
 */
public final class Util {

  public static <T> T as(final Object value, final Class<T> cla) {
    if (value != null && cla.isInstance(value)) {
      return cla.cast(value);
    } else {
      return null;
    }
  }

  public static <T> Optional<T> asOptional(final Object value, final Class<T> cla) {
    if (value != null && cla.isInstance(value)) {
      return Optional.of(cla.cast(value));
    } else {
      return Optional.empty();
    }
  }

  public static void createNonExistParentDirectories(final String filePathString) {
    Preconditions.checkNotNull(filePathString);

    Util.createNonExistParentDirectories(Paths.get(filePathString));
  }

  public static void createNonExistParentDirectories(final Path filePath) {
    Preconditions.checkNotNull(filePath);

    try {
      final Path fileParentPath = filePath.getParent();
      if (!Files.exists(filePath) && !Files.exists(fileParentPath)) {
        Files.createDirectories(fileParentPath);
      }
    } catch (final IOException e) {}
  }

  public static <T> T[] sameLengthCopyOfArray(final T[] original) {
    Preconditions.checkNotNull(original);

    return Arrays.copyOf(original, original.length);
  }

  public static double[] sameLengthCopyOfArray(final double[] original) {
    Preconditions.checkNotNull(original);

    return Arrays.copyOf(original, original.length);
  }

  public static int[] sameLengthCopyOfArray(final int[] original) {
    Preconditions.checkNotNull(original);

    return Arrays.copyOf(original, original.length);
  }
  
  public static long[] sameLengthCopyOfArray(final long[] original) {
    Preconditions.checkNotNull(original);

    return Arrays.copyOf(original, original.length);
  }

  @Deprecated
  private Util() {}
}
