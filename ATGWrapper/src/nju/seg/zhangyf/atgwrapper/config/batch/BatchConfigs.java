package nju.seg.zhangyf.atgwrapper.config.batch;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.core.resources.IFile;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import nju.seg.zhangyf.util.ResourceAndUiUtil;
import nju.seg.zhangyf.util.SwtUtil;

/**
 * @author Zhang Yifan
 */
public class BatchConfigs {

  public static <T> Optional<T> tryParseAndShowErrorIfFailed(final File configFile, final Function<File, T> parseFunc) {
    Preconditions.checkNotNull(configFile);
    Preconditions.checkNotNull(parseFunc);

    try {
      return Optional.of(parseFunc.apply(configFile));
    } catch (final Throwable e) {
      // handle failed to parse config
      SwtUtil.createErrorMessageBoxWithActiveShell(
                                                   "Failed to parse the config file: \n"
                                                       + configFile.getAbsolutePath().toString()
                                                       + "with exception: \n"
                                                       + e.toString())
             .open();
      return Optional.empty();
    }
  }

  public static Optional<Config> tryParseAndShowErrorIfFailed(final File configFile) {
    Preconditions.checkNotNull(configFile);

    return BatchConfigs.tryParseAndShowErrorIfFailed(configFile, ConfigFactory::parseFile);
  }

  public static <T> Optional<T> tryParseAndShowErrorIfFailed(final IFile configFile, final Function<Config, T> parseFunc) {
    Preconditions.checkNotNull(configFile);
    Preconditions.checkNotNull(parseFunc);

    try {
      final Config rawConfig = ConfigFactory.parseFile(ResourceAndUiUtil.eclipseFileToJavaFile(configFile));
      final Config rawConfigResolved =  rawConfig.resolve();
      return Optional.of(parseFunc.apply(rawConfigResolved));
    } catch (final Throwable e) {
      // handle failed to parse config
      SwtUtil.createErrorMessageBoxWithActiveShell(
                                                   "Failed to parse the config file: \n"
                                                       + configFile.getLocation().toString()
                                                       + "with exception: \n"
                                                       + e.toString())
             .open();
      return Optional.empty();
    }
  }

  public static Optional<Config> tryParseAndShowErrorIfFailed(final IFile configFile) {
    Preconditions.checkNotNull(configFile);

    return BatchConfigs.tryParseAndShowErrorIfFailed(configFile, Function.identity());
  }

  @Deprecated
  private BatchConfigs() {}
}
