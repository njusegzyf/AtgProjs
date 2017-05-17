package nju.seg.zhangyf.atgwrapper.config.batch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.typesafe.config.Config;

import nju.seg.zhangyf.atgwrapper.config.AtgConfig;
import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
import nju.seg.zhangyf.atgwrapper.config.ExecutorConfig;
import nju.seg.zhangyf.atgwrapper.config.PathFragmentListConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchItemConfigBase.BatchItemConfigBuilderBase;
import nju.seg.zhangyf.atgwrapper.config.batch.BranchCoverageBatchConfig.BranchCoverageBatchItemConfig;
import nju.seg.zhangyf.util.ConfigUtil2;

/**
 * @author Zhang Yifan
 */
public class BranchCoverageBatchConfig extends BatchConfigBase<BranchCoverageBatchItemConfig> {

  public final List<BranchCoverageBatchItemConfig> batchItems;

  @Override
  public List<BranchCoverageBatchItemConfig> getBatchItems() {
    return this.batchItems;
  }

  BranchCoverageBatchConfig(final List<String> libraries,
                            final Optional<AtgConfig> atgConfig,
                            final Optional<ExecutorConfig> executorConfig,
                            final List<BranchCoverageBatchItemConfig> batchItems) {
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

  public static final class BranchCoverageBatchItemConfig extends BatchItemConfigBase {
    public final String batchFunction;
    public final Optional<List<TargetNodeConfig>> targetNodes;

    BranchCoverageBatchItemConfig(final Optional<String> project,
                                  final String batchFile,
                                  final Optional<AtgConfig> atgConfig,
                                  final String batchFunction,
                                  final Optional<List<TargetNodeConfig>> targetNodes) {
      super(project, batchFile, atgConfig);
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

  public static final class BranchCoverageBatchItemConfigBuilder extends BatchItemConfigBuilderBase<BranchCoverageBatchItemConfig> {
    public String batchFunction;
    public Optional<List<TargetNodeConfig>> targetNodes;

    public BranchCoverageBatchItemConfigBuilder() {
      this.reset();
    }

    @Override
    public BranchCoverageBatchItemConfig build() {
      this.checkVaild();

      final BranchCoverageBatchItemConfig result = new BranchCoverageBatchItemConfig(super.project,
                                                                                     super.batchFile,
                                                                                     super.atgConfig,
                                                                                     this.batchFunction,
                                                                                     this.targetNodes);

      this.reset();
      return result;
    }

    @Override
    public void checkVaild() {
      super.checkVaild();

      Preconditions.checkState(this.project != null);
      Preconditions.checkState(!Strings.isNullOrEmpty(this.batchFile));
      Preconditions.checkState(!Strings.isNullOrEmpty(this.batchFunction));
      Preconditions.checkState(this.targetNodes != null);
    }

    @Override
    public void reset() {
      super.reset();

      this.batchFunction = null;
    }
  }

  public static BranchCoverageBatchConfig parseBatchConfig(final Config rawConfig) {
    Preconditions.checkNotNull(rawConfig);

    // check essential config items

    // check config of libraries
    Preconditions.checkArgument(rawConfig.hasPath(ConfigTags.LIBRARIES_TAG), "Illegal config file.");
    final List<String> libraries = rawConfig.getStringList(ConfigTags.LIBRARIES_TAG);

    final Optional<AtgConfig> atgConfig = ConfigUtil2.getOptionalConfig(rawConfig, ConfigTags.ATG_CONFIG_TAG)
                                                     .map(AtgConfig::parse);

    final Optional<ExecutorConfig> executorConfig = ConfigUtil2.getOptionalConfig(rawConfig, ConfigTags.EXECUTOR_TAG)
                                                               .map(ExecutorConfig::parse);

    final List<? extends Config> rawBatchItemConfigs = rawConfig.getConfigList(ConfigTags.BATCH_ITEMS_TAG);

    final BranchCoverageBatchItemConfigBuilder builder = new BranchCoverageBatchItemConfigBuilder();
    final List<BranchCoverageBatchItemConfig> batchItems = rawBatchItemConfigs.stream().map((final Config rawBatchItem) -> {
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.BATCH_FILE_TAG));
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.BATCH_FUNCTION_TAG));

      BatchItemConfigBase.parse(builder, rawBatchItem);
      builder.batchFunction = rawBatchItem.getString(ConfigTags.BATCH_FUNCTION_TAG);

      if (rawBatchItem.hasPath(ConfigTags.ATG_CONFIG_TAG)) {
        builder.atgConfig = Optional.of(AtgConfig.parse(rawBatchItem.getConfig(ConfigTags.ATG_CONFIG_TAG)));
      }

      if (rawBatchItem.hasPath(ConfigTags.TARGET_NODES_TAG)) {
        final List<? extends Config> rawTargetNodeConfigs = rawBatchItem.getConfigList(ConfigTags.TARGET_NODES_TAG);

        final List<TargetNodeConfig> targetNodes =
            rawTargetNodeConfigs.stream().map(TargetNodeConfig::parse)
                                .collect(Collectors.toList());
        builder.targetNodes = Optional.of(targetNodes);
      }

      return builder.build();
    }).collect(Collectors.toList());

    return new BranchCoverageBatchConfig(libraries, atgConfig, executorConfig, batchItems);
  }
}
