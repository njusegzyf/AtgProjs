package nju.seg.zhangyf.atgwrapper.config;

import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.util.CFGPath;

/**
 * @author Zhang Yifan
 */
public final class PathFragmentConfig {

  public List<String> nodeNames;

  private PathFragmentConfig(final List<String> nodeNames) {
    assert nodeNames != null && !nodeNames.isEmpty();

    this.nodeNames = nodeNames;
  }

  public boolean isMatchPath(final CFGPath cfgPath) {
    Preconditions.checkNotNull(cfgPath);
    
    final int pathFragmentLength = this.nodeNames.size();
    final List<SimpleCFGNode> path = cfgPath.getPath();
    final int pathLength = path.size();
    
    if (pathLength < pathFragmentLength) {
      return false;
    }
    
    return IntStream.range(0, pathLength - pathFragmentLength + 1)
    .mapToObj(startIndex -> path.subList(startIndex, startIndex + pathFragmentLength)) // generate all path fragments
    .anyMatch(pathFragment -> {
      // check if the node names match
      return IntStream.range(0, pathFragmentLength)
      .allMatch(index -> this.nodeNames.get(index).equals(pathFragment.get(index).getName()));
    });
  }

  public static PathFragmentConfig parse(final Config rawConfig) {
    Preconditions.checkNotNull(rawConfig);

    final List<String> nodeNames = rawConfig.getStringList(ConfigTags.PATH_FRAGMENT_TAG);
    if (nodeNames.isEmpty()) {
      throw new IllegalArgumentException();
    }
    return new PathFragmentConfig(nodeNames);
  }
}
