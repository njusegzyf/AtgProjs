package nju.seg.zhangyf.atgwrapper.config.batch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

import nju.seg.zhangyf.atgwrapper.config.AtgConfig;
import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
import nju.seg.zhangyf.atgwrapper.config.ExecutorConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BranchCoverageBatchConfig.BranchCoverageBatchItemConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BranchCoverageBatchConfig.BranchCoverageBatchItemConfigBuilder;
import nju.seg.zhangyf.atgwrapper.config.batch.BranchCoverageBatchConfig.TargetNodeConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.CollectBranchCoverageBatchConfig.CollectBranchCoverageBatchItemConfig;
import nju.seg.zhangyf.util.ConfigUtil2;

/**
 * @author Zhang Yifan
 */
public final class CollectBranchCoverageBatchConfig extends BatchConfigBase<CollectBranchCoverageBatchItemConfig> {

  public final List<CollectBranchCoverageBatchItemConfig> batchItems;

  @Override
  public List<CollectBranchCoverageBatchItemConfig> getBatchItems() {
    return this.batchItems;
  }

  CollectBranchCoverageBatchConfig(final List<String> libraries,
                                   final Optional<AtgConfig> atgConfig,
                                   final Optional<ExecutorConfig> executorConfig,
                                   final List<CollectBranchCoverageBatchItemConfig> batchItems) {
    super(libraries, atgConfig, executorConfig);

    Preconditions.checkNotNull(batchItems);
    this.batchItems = batchItems;
  }

  public static final class CollectBranchCoverageBatchItemConfig extends BranchCoverageBatchItemConfig {
    public final List<double[]> inputs;

    CollectBranchCoverageBatchItemConfig(final Optional<String> project,
                                         final String batchFile,
                                         final Optional<AtgConfig> atgConfig,
                                         final String batchFunction,
                                         final Optional<List<TargetNodeConfig>> targetNodes,
                                         final List<double[]> inputs) {
      super(project, batchFile, atgConfig, batchFunction, targetNodes);
      assert inputs != null && inputs.size() > 0;

      this.inputs = inputs;
    }

    @Override
    public String toString() {
      return "CollectBranchCoverageBatchItemConfig [batchFunction=" + this.batchFunction + ", batchFile=" + this.batchFile + ", inputSize=" + this.inputs.size() + "]";
    }
  }

  public static final class CollectBranchCoverageBatchItemConfigBuilder
      extends BranchCoverageBatchItemConfigBuilder
  /* implements IConfigBuilder<CollectBranchCoverageBatchItemConfig> */ {
    public List<double[]> inputs = null;

    public CollectBranchCoverageBatchItemConfigBuilder() {
      this.reset();
    }

    @Override
    public CollectBranchCoverageBatchItemConfig build() {
      this.checkVaild();

      final CollectBranchCoverageBatchItemConfig result = new CollectBranchCoverageBatchItemConfig(super.project,
                                                                                                   super.batchFile,
                                                                                                   super.atgConfig,
                                                                                                   this.batchFunction,
                                                                                                   this.targetNodes,
                                                                                                   this.inputs);

      this.reset();
      return result;
    }

    @Override
    public void checkVaild() {
      super.checkVaild();

      Preconditions.checkState(this.inputs != null);
    }

    @Override
    public void reset() {
      super.reset();

      this.inputs = null;
    }
  }

  // TODO Refactor the `parseBatchConfig` in this class and in `BranchCoverageBatchConfig` class with builder pattern to avoid duplication

  public static CollectBranchCoverageBatchConfig parseBatchConfig(final Config rawConfig) {
    Preconditions.checkNotNull(rawConfig);

    // check essential config items
    Preconditions.checkArgument(rawConfig.hasPath(ConfigTags.LIBRARIES_TAG), "Illegal config file.");

    // check config of libraries
    final List<String> libraries = rawConfig.getStringList(ConfigTags.LIBRARIES_TAG);

    final Optional<AtgConfig> atgConfig = ConfigUtil2.getOptionalConfig(rawConfig, ConfigTags.ATG_CONFIG_TAG)
                                                     .map(AtgConfig::parse);

    final Optional<ExecutorConfig> executorConfig = ConfigUtil2.getOptionalConfig(rawConfig, ConfigTags.EXECUTOR_TAG)
                                                               .map(ExecutorConfig::parse);

    final List<? extends Config> rawBatchItemConfigs = rawConfig.getConfigList(ConfigTags.BATCH_ITEMS_TAG);

    final CollectBranchCoverageBatchItemConfigBuilder builder = new CollectBranchCoverageBatchItemConfigBuilder();
    final List<CollectBranchCoverageBatchItemConfig> batchItems = rawBatchItemConfigs.stream().map((final Config rawBatchItem) -> {
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.BATCH_FILE_TAG));
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.BATCH_FUNCTION_TAG));
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.INPUTS_TAG));

      BatchItemConfigBase.parse(builder, rawBatchItem);
      builder.batchFunction = rawBatchItem.getString(ConfigTags.BATCH_FUNCTION_TAG);

      // parse inputs
      final List<double[]> inputs = rawBatchItem.getConfigList(ConfigTags.INPUTS_TAG).stream()
                                             .map(config -> config.getDoubleList(ConfigTags.INPUT_TAG))
                                             .map(CollectBranchCoverageBatchConfig::doubleListToDoubleArray)
                                             .collect(Collectors.toList());
      builder.inputs = inputs;
      
      if (rawBatchItem.hasPath(ConfigTags.ATG_CONFIG_TAG)) {
        builder.atgConfig = Optional.of(AtgConfig.parse(rawBatchItem.getConfig(ConfigTags.ATG_CONFIG_TAG)));
      }

      if (rawBatchItem.hasPath(ConfigTags.TARGET_NODES_TAG)) {
        final List<? extends Config> rawTargetNodeConfigs = rawBatchItem.getConfigList(ConfigTags.TARGET_NODES_TAG);

        final List<TargetNodeConfig> targetNodes = rawTargetNodeConfigs.stream().map(TargetNodeConfig::parse)
                                                                       .collect(Collectors.toList());
        builder.targetNodes = Optional.of(targetNodes);
      }

      return builder.build();
    }).collect(Collectors.toList());

    return new CollectBranchCoverageBatchConfig(libraries, atgConfig, executorConfig, batchItems);
  }

  private static double[] doubleListToDoubleArray(final List<Double> list) {
    assert list != null;

    return list.stream().mapToDouble(v -> v).toArray();
  }
}
