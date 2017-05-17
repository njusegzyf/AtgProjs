package nju.seg.zhangyf.util;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

/**
 * @author Zhang Yifan
 */
public final class ConfigUtil2 {

  public static <T> Optional<T> getOptional(final Config config, final String path, final BiFunction<Config, String, T> getFunc) {
    Preconditions.checkNotNull(config);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(getFunc);

    if (config.hasPath(path)) {
      return Optional.of(getFunc.apply(config, path));
    } else {
      return Optional.empty();
    }
  }

  public static Optional<Config> getOptionalConfig(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getConfig);
    
    // Preconditions.checkNotNull(config);
    // Preconditions.checkNotNull(path);
    //
    // if (config.hasPath(path)) {
    // return Optional.of(config.getConfig(path));
    // } else {
    // return Optional.empty();
    // }
  }

  public static Optional<String> getOptionalString(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getString);

    // Preconditions.checkNotNull(config);
    // Preconditions.checkNotNull(path);
    //
    // if (config.hasPath(path)) {
    // return Optional.of(config.getString(path));
    // } else {
    // return Optional.empty();
    // }
  }

  public static Optional<List<String>> getOptionalStringList(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getStringList);

    // Preconditions.checkNotNull(config);
    // Preconditions.checkNotNull(path);
    //
    // if (config.hasPath(path)) {
    // return Optional.of(config.getStringList(path));
    // } else {
    // return Optional.empty();
    // }
  }

  public static Optional<Integer> getOptionalInteger(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getInt);

    // Preconditions.checkNotNull(config);
    // Preconditions.checkNotNull(path);
    //
    // if (config.hasPath(path)) {
    // return Optional.of(config.getInt(path));
    // } else {
    // return Optional.empty();
    // }
  }
  
  public static Optional<Double> getOptionalDouble(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getDouble);
  }
  
  public static Optional<List<Double>> getOptionalDoubleList(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getDoubleList);
  }
  
  public static Optional<Duration> getOptionalDuration(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getDuration);
  }
  
  public static Optional<Boolean> getOptionalBoolean(final Config config, final String path) {
    return ConfigUtil2.getOptional(config, path, Config::getBoolean);
  }

  @Deprecated
  private ConfigUtil2() {}
}
