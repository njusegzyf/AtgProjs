package nju.seg.zhangyf.atgwrapper.batch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import nju.seg.zhangyf.util.ConfigUtil2;

/**
 * @author Zhang Yifan
 */
public class BatchBranchCoverageConfig extends BatchConfigBase<BatchBranchCoverageConfig.BatchBranchCoverageItemConfig> {

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
    final String name;

    /** Paths that are used to cover the node. */
    final Optional<List<List<String>>> targetPaths;

    public TargetNodeConfig(String name, Optional<List<List<String>>> targetPaths) {
      assert name != null && targetPaths != null;

      this.name = name;
      this.targetPaths = targetPaths;
    }

    public TargetNodeConfig(final String nodeId) {
      this(nodeId, Optional.empty());
    }

    public TargetNodeConfig(final String nodeId, final List<List<String>> targetPaths) {
      this(nodeId, Optional.of(targetPaths));
    }
  }

  public static final class BatchBranchCoverageItemConfig extends BatchItemBase {
    final public String batchFunction;
    final public Optional<List<TargetNodeConfig>> targetNodes;

    public BatchBranchCoverageItemConfig(final Optional<String> project,
                                         final String batchFile,
                                         final String batchFunction,
                                         final Optional<List<TargetNodeConfig>> targetNodes) {
      super(project, batchFile);
      assert !Strings.isNullOrEmpty(batchFunction);
      assert targetNodes != null;
      
      this.batchFunction = batchFunction;
      this.targetNodes = targetNodes;
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
    Preconditions.checkArgument(config.hasPath(BatchTags.LIBRARIES_TAG), "Illegal config file.");
    final List<String> libraries = config.getStringList(BatchTags.LIBRARIES_TAG);

    final Optional<AtgConfig> atgConfig = ConfigUtil2.getOptionalConfig(config, AtgConfig.ATG_CONFIG_TAG)
                                                     .map(AtgConfig::parse);

    final Optional<ExecutorConfig> executorConfig = ConfigUtil2.getOptionalConfig(config, ExecutorConfig.EXECUTOR_CONFIG_TAG)
                                                               .map(ExecutorConfig::parse);

    final List<? extends Config> rawBatchItemConfigs = config.getConfigList(BatchTags.BATCH_ITEMS_TAG);

    final BatchBranchCoverageItemConfigBuilder builder = new BatchBranchCoverageItemConfigBuilder();
    final List<BatchBranchCoverageItemConfig> batchItems = rawBatchItemConfigs.stream().map((final Config rawBatchItem) -> {
      Preconditions.checkState(rawBatchItem.hasPath(BatchTags.BATCH_FILE_TAG));
      Preconditions.checkState(rawBatchItem.hasPath(BatchTags.BATCH_FUNCTION_TAG));

      builder.project = ConfigUtil2.getOptionalString(rawBatchItem, BatchTags.PROJECT_TAG);
      builder.batchFile = rawBatchItem.getString(BatchTags.BATCH_FILE_TAG);
      builder.batchFunction = rawBatchItem.getString(BatchTags.BATCH_FUNCTION_TAG);

      if (rawBatchItem.hasPath(BatchTags.TARGET_NODES_TAG)) {
        final List<? extends Config> rawTargetNodeConfigs = rawBatchItem.getConfigList(BatchTags.TARGET_NODES_TAG);

        final List<TargetNodeConfig> targetNodes = rawTargetNodeConfigs.stream().map(rawTargetNodeConfig -> {
          Preconditions.checkState(rawTargetNodeConfig.hasPath(BatchTags.NAME_TAG));

          final String targetNodeName = rawTargetNodeConfig.getString(BatchTags.NAME_TAG);
          if (rawTargetNodeConfig.hasPath(BatchTags.TARGET_PATHS_TAG)) {
            final List<List<String>> targetPaths = rawTargetNodeConfig.getConfigList(BatchTags.TARGET_PATHS_TAG).stream()
                                                                      .map(c -> c.getStringList(BatchTags.PATH_TAG))
                                                                      .collect(Collectors.toList());
            return new TargetNodeConfig(targetNodeName, targetPaths);
          } else {
            return new TargetNodeConfig(targetNodeName);
          }
        }).collect(Collectors.toList());
        builder.targetNodes = Optional.of(targetNodes);
      }

      return builder.build();
    }).collect(Collectors.toList());

    return new BatchBranchCoverageConfig(libraries, atgConfig, executorConfig, batchItems);
  }
}
