package nju.seg.zhangyf.atgwrapper.config.batch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import nju.seg.zhangyf.atgwrapper.config.AtgConfig;
import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
import nju.seg.zhangyf.atgwrapper.config.ExecutorConfig;
import nju.seg.zhangyf.atgwrapper.config.PathFragmentListConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchBranchCoverageConfig.BatchBranchCoverageItemConfig;
import nju.seg.zhangyf.util.ConfigUtil2;

/**
 * @author Zhang Yifan
 */
public class BatchBranchCoverageConfig extends BatchConfigBase<BatchBranchCoverageItemConfig> {

  public final List<BatchBranchCoverageItemConfig> batchItems;

  @Override
  public List<BatchBranchCoverageItemConfig> getBatchItems() {
    return this.batchItems;
  }

  BatchBranchCoverageConfig(final List<String> libraries,
                            final Optional<AtgConfig> atgConfig,
                            final Optional<ExecutorConfig> executorConfig,
                            final List<BatchBranchCoverageItemConfig> batchItems) {
    super(libraries, atgConfig, executorConfig);

    this.batchItems = batchItems;
  }

  public static final class TargetNodeConfig {
    public final String name;

    /** Paths that are used to cover the node. */
    public final Optional<List<List<String>>> targetPaths;

    /** Path fragments that are used to cover the node. */
    public final Optional<PathFragmentListConfig> targetPathFragments;

    public TargetNodeConfig(final String name,
                            final Optional<List<List<String>>> targetPaths,
                            final Optional<PathFragmentListConfig> targetPathFragements) {
      assert name != null && targetPaths != null && targetPathFragements != null;
      assert !(targetPaths.isPresent() && targetPathFragements.isPresent());

      this.name = name;
      this.targetPaths = targetPaths;
      this.targetPathFragments = targetPathFragements;
    }

    @Deprecated
    public TargetNodeConfig(final String nodeId) {
      this(nodeId, Optional.empty(), Optional.empty());
    }

    @Deprecated
    public TargetNodeConfig(final String nodeId, final List<List<String>> targetPaths) {
      this(nodeId, Optional.of(targetPaths), Optional.empty());
    }

    public static TargetNodeConfig parse(final Config rawConfig) {
      Preconditions.checkNotNull(rawConfig);
      Preconditions.checkState(rawConfig.hasPath(ConfigTags.NAME_TAG));

      final String targetNodeName = rawConfig.getString(ConfigTags.NAME_TAG);

      final Optional<List<List<String>>> targetPaths;
      if (rawConfig.hasPath(ConfigTags.TARGET_PATHS_TAG)) {
        targetPaths = Optional.of(rawConfig.getConfigList(ConfigTags.TARGET_PATHS_TAG).stream()
                                           .map(c -> c.getStringList(ConfigTags.PATH_TAG))
                                           .collect(Collectors.toList()));

      } else {
        targetPaths = Optional.empty();
      }

      final Optional<PathFragmentListConfig> targetPathFragements = PathFragmentListConfig.tryParse(rawConfig);

      // FIXME For simplification, do not allow use `targetPaths` and `targetPathFragements` together.
      Preconditions.checkArgument(!(targetPaths.isPresent() && targetPathFragements.isPresent()));

      return new TargetNodeConfig(targetNodeName, targetPaths, targetPathFragements);
    }
  }

  public static final class BatchBranchCoverageItemConfig extends BatchItemConfigBase {
    public final String batchFunction;
    public final Optional<List<TargetNodeConfig>> targetNodes;

    BatchBranchCoverageItemConfig(final Optional<String> project,
                                  final String batchFile,
                                  final String batchFunction,
                                  final Optional<List<TargetNodeConfig>> targetNodes) {
      super(project, batchFile);
      assert !Strings.isNullOrEmpty(batchFunction);
      assert targetNodes != null;

      this.batchFunction = batchFunction;
      this.targetNodes = targetNodes;
    }

    @Override
    public String toString() {
      return "BatchBranchCoverageItemConfig [batchFunction=" + this.batchFunction + ", batchFile=" + this.batchFile + "]";
    }
  }

  public static final class BatchBranchCoverageItemConfigBuilder {
    public Optional<String> project;
    public String batchFile;
    public String batchFunction;
    public Optional<List<TargetNodeConfig>> targetNodes;

    public BatchBranchCoverageItemConfigBuilder() {
      this.reset();
    }

    public BatchBranchCoverageItemConfig build() {
      this.checkVaild();

      final BatchBranchCoverageItemConfig result = new BatchBranchCoverageItemConfig(this.project,
                                                                                     this.batchFile,
                                                                                     this.batchFunction,
                                                                                     this.targetNodes);
      this.reset();
      return result;
    }

    public void checkVaild() {
      Preconditions.checkState(this.project != null);
      Preconditions.checkState(!Strings.isNullOrEmpty(this.batchFile));
      Preconditions.checkState(!Strings.isNullOrEmpty(this.batchFunction));
      Preconditions.checkState(this.targetNodes != null);
    }

    public void reset() {
      this.project = Optional.empty();
      this.batchFile = null;
      this.batchFunction = null;
    }
  }

  public static BatchBranchCoverageConfig parseBatchConfig(final Path configPath) {
    Preconditions.checkNotNull(configPath);
    Preconditions.checkArgument(Files.isRegularFile(configPath));

    final Config config = ConfigFactory.parseFile(configPath.toFile());

    // check essential config items

    // check config of libraries
    Preconditions.checkArgument(config.hasPath(ConfigTags.LIBRARIES_TAG), "Illegal config file.");
    final List<String> libraries = config.getStringList(ConfigTags.LIBRARIES_TAG);

    final Optional<AtgConfig> atgConfig = ConfigUtil2.getOptionalConfig(config, ConfigTags.ATG_CONFIG_TAG)
                                                     .map(AtgConfig::parse);

    final Optional<ExecutorConfig> executorConfig = ConfigUtil2.getOptionalConfig(config, ConfigTags.EXECUTOR_TAG)
                                                               .map(ExecutorConfig::parse);

    final List<? extends Config> rawBatchItemConfigs = config.getConfigList(ConfigTags.BATCH_ITEMS_TAG);

    final BatchBranchCoverageItemConfigBuilder builder = new BatchBranchCoverageItemConfigBuilder();
    final List<BatchBranchCoverageItemConfig> batchItems = rawBatchItemConfigs.stream().map((final Config rawBatchItem) -> {
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.BATCH_FILE_TAG));
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.BATCH_FUNCTION_TAG));

      builder.project = ConfigUtil2.getOptionalString(rawBatchItem, ConfigTags.PROJECT_TAG);
      builder.batchFile = rawBatchItem.getString(ConfigTags.BATCH_FILE_TAG);
      builder.batchFunction = rawBatchItem.getString(ConfigTags.BATCH_FUNCTION_TAG);

      if (rawBatchItem.hasPath(ConfigTags.TARGET_NODES_TAG)) {
        final List<? extends Config> rawTargetNodeConfigs = rawBatchItem.getConfigList(ConfigTags.TARGET_NODES_TAG);

        final List<TargetNodeConfig> targetNodes =
            rawTargetNodeConfigs.stream().map(TargetNodeConfig::parse)
                                .collect(Collectors.toList());
        builder.targetNodes = Optional.of(targetNodes);
      }

      return builder.build();
    }).collect(Collectors.toList());

    return new BatchBranchCoverageConfig(libraries, atgConfig, executorConfig, batchItems);
  }
}
