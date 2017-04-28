package nju.seg.zhangyf.atgwrapper.config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

import cn.nju.seg.atg.util.CFGPath;

/**
 * @author Zhang Yifan
 */
public final class PathFragmentListConfig {

  public final List<PathFragmentConfig> pathFragments;

  private PathFragmentListConfig(final List<PathFragmentConfig> pathFragments) {
    assert pathFragments != null && !pathFragments.isEmpty();

    this.pathFragments = pathFragments;
  }

  public boolean isMatchPath(final CFGPath cfgPath) {
    Preconditions.checkNotNull(cfgPath);

    return this.pathFragments.stream().anyMatch(pathFragment -> pathFragment.isMatchPath(cfgPath));
  }

  public static PathFragmentListConfig parse(final Config rawConfig) {
    Preconditions.checkNotNull(rawConfig);

    final List<PathFragmentConfig> pathFragments = rawConfig.getConfigList(ConfigTags.TARGET_PATH_FRAGMENTS_TAG).stream()
                                                            .map(PathFragmentConfig::parse)
                                                            .collect(Collectors.toList());
    Preconditions.checkArgument(!pathFragments.isEmpty());
    return new PathFragmentListConfig(pathFragments);
  }
  
  public static Optional<PathFragmentListConfig> tryParse(final Config rawConfig) {
    Preconditions.checkNotNull(rawConfig);

    if (rawConfig.hasPath(ConfigTags.TARGET_PATH_FRAGMENTS_TAG)) {
      return Optional.of(PathFragmentListConfig.parse(rawConfig));
    }else {
      return Optional.empty();
    }
  }
}
