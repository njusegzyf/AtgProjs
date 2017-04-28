package nju.seg.zhangyf.atgwrapper.config.batch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import nju.seg.zhangyf.atgwrapper.config.AtgConfig;
import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
import nju.seg.zhangyf.atgwrapper.config.ExecutorConfig;
import nju.seg.zhangyf.util.ConfigUtil2;

public final class BatchPathCoverageConfig extends BatchConfigBase<BatchPathCoverageConfig.BatchPathCoverageItemConfig> {

  final List<BatchPathCoverageItemConfig> batchItems;

  @Override
  public List<BatchPathCoverageItemConfig> getBatchItems() {
    return this.batchItems;
  }

  private BatchPathCoverageConfig(final List<String> libraries,
                                  final List<BatchPathCoverageItemConfig> batchItems,
                                  final Optional<AtgConfig> atgConfig,
                                  final Optional<ExecutorConfig> executorConfig) {
    super(libraries, atgConfig, executorConfig);

    assert batchItems != null;

    this.batchItems = batchItems;
  }

  private BatchPathCoverageConfig(final List<String> libraries, final List<BatchPathCoverageItemConfig> batchItems) {
    this(libraries, batchItems, Optional.empty(), Optional.empty());
  }

  public static enum BatchItemMode {
    ALL,
    SPECIFY_INCLUDED,
    SPECIFY_EXCLUDED
  }

  static final class BatchPathCoverageItemConfigBuilder {

    Optional<String> project;
    String batchFile;
    private BatchItemMode mode;
    private ImmutableList<String> includedBatchFunctions;
    private ImmutableList<String> excludedBatchFunctions;

    public BatchPathCoverageItemConfigBuilder() {
      this.reset();
    }

    public void setModeAll() {
      this.mode = BatchItemMode.ALL;
      this.includedBatchFunctions = BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
      this.excludedBatchFunctions = BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
    }

    public void setModeSpecifyIncluded(final ImmutableList<String> includedBatchFunctions) {
      this.mode = BatchItemMode.SPECIFY_INCLUDED;
      this.includedBatchFunctions = includedBatchFunctions;
      this.excludedBatchFunctions = BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
    }

    public void setModeSpecifyExcluded(final ImmutableList<String> excludedBatchFunctions) {
      this.mode = BatchItemMode.SPECIFY_EXCLUDED;
      this.includedBatchFunctions = BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
      this.excludedBatchFunctions = excludedBatchFunctions;
    }

    public BatchPathCoverageItemConfig build() {
      this.checkVaild();

      final BatchPathCoverageItemConfig result = new BatchPathCoverageItemConfig(this.project,
                                                                                 this.batchFile,
                                                                                 this.mode,
                                                                                 this.includedBatchFunctions,
                                                                                 this.excludedBatchFunctions);
      this.reset();
      return result;
    }

    public void checkVaild() {
      assert this.project != null;
      assert this.batchFile != null;

      assert this.mode != null;
      switch (this.mode) {
      case ALL:
        assert this.includedBatchFunctions == BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST
            && this.excludedBatchFunctions == BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
        break;

      case SPECIFY_INCLUDED:
        assert this.includedBatchFunctions != BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST
            && this.excludedBatchFunctions == BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
        break;

      case SPECIFY_EXCLUDED:
        assert this.includedBatchFunctions == BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST
            && this.excludedBatchFunctions != BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
        break;

      default:
        throw new IllegalStateException();
      }
    }

    public void reset() {
      this.project = Optional.empty();
      this.batchFile = null;
      this.mode = null;
      this.includedBatchFunctions = BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
      this.excludedBatchFunctions = BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST;
    }
  }

  public static final class BatchPathCoverageItemConfig extends BatchItemConfigBase {

    public final BatchItemMode mode;
    public final ImmutableList<String> includedBatchFunctions;
    public final ImmutableList<String> excludedBatchFunctions;

    private BatchPathCoverageItemConfig(final Optional<String> project,
                                        final String batchFile,
                                        final BatchItemMode mode,
                                        final ImmutableList<String> includedBatchFunctions,
                                        final ImmutableList<String> excludedBatchFunctions) {
      super(project, batchFile);
      assert mode != null;
      assert includedBatchFunctions != null;
      assert excludedBatchFunctions != null;

      this.mode = mode;
      this.includedBatchFunctions = includedBatchFunctions;
      this.excludedBatchFunctions = excludedBatchFunctions;
    }

    public boolean isProcessAll() {
      return this.mode == BatchItemMode.ALL;
    }
    
    
    @Override
    public String toString() {
      return "BatchPathCoverageItemConfig [batchFile=" + this.batchFile + "]";
    }

    /**
     * Creates a batch item with {@code isProcessAllFunctions} set to {@code true} and
     * {@code batchFunctions} set to an empty array.
     */
    @Deprecated
    public static BatchPathCoverageItemConfig createBatchItemWithAll(final String batchFile, final Optional<String> project) {
      assert batchFile != null;
      assert project != null;

      return new BatchPathCoverageItemConfig(project,
                                             batchFile,
                                             BatchItemMode.ALL,
                                             BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST,
                                             BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST);
    }

    @Deprecated
    public static BatchPathCoverageItemConfig createBatchItemWithIncluded(final String batchFile,
                                                                          final ImmutableList<String> includedFunctions,
                                                                          final Optional<String> project) {
      assert batchFile != null;
      assert project != null;

      return new BatchPathCoverageItemConfig(project,
                                             batchFile,
                                             BatchItemMode.SPECIFY_INCLUDED,
                                             includedFunctions,
                                             BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST);
    }

    /**
     * Creates a batch item with {@code isProcessAllFunctions} set to {@code true} and
     * {@code batchFunctions} set to an empty array.
     */
    @Deprecated
    public static BatchPathCoverageItemConfig createBatchItemWithExcluded(final String batchFile,
                                                                          final ImmutableList<String> excludedFunctions,
                                                                          final Optional<String> project) {
      assert batchFile != null;
      assert project != null;

      return new BatchPathCoverageItemConfig(project,
                                             batchFile,
                                             BatchItemMode.SPECIFY_EXCLUDED,
                                             BatchPathCoverageItemConfig.EMPTY_FUNCTIONS_LIST,
                                             excludedFunctions);
    }

    private static final ImmutableList<String> EMPTY_FUNCTIONS_LIST = ImmutableList.of();
  }

  public static BatchPathCoverageConfig parseBatchConfig(final Path configPath) {
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

    final BatchPathCoverageItemConfigBuilder builder = new BatchPathCoverageItemConfigBuilder();
    final List<BatchPathCoverageItemConfig> batchItems = rawBatchItemConfigs.stream().<BatchPathCoverageItemConfig> map((final Config rawBatchItem) -> {
      Preconditions.checkState(rawBatchItem.hasPath(ConfigTags.BATCH_FILE_TAG));

      // final Optional<String> project = ConfigUtil2.getStringOptional(batchItem, BatchConfig.PROJECT_TAG);
      builder.project = ConfigUtil2.getOptionalString(rawBatchItem, ConfigTags.PROJECT_TAG);

      // final String batchFile = batchItem.getString(BatchConfig.BATCH_FILE_TAG);
      builder.batchFile = rawBatchItem.getString(ConfigTags.BATCH_FILE_TAG);

      if (rawBatchItem.hasPath(ConfigTags.BATCH_FUNCTIONS_TAG)) {
        final List<String> includedBatchFunctions = rawBatchItem.getStringList(ConfigTags.BATCH_FUNCTIONS_TAG);
        builder.setModeSpecifyIncluded(ImmutableList.copyOf(includedBatchFunctions));
      } else if (rawBatchItem.hasPath(ConfigTags.EXCLUDED_BATCH_FUNCTIONS_TAG)) {
        final List<String> excludedBatchFunctions = rawBatchItem.getStringList(ConfigTags.EXCLUDED_BATCH_FUNCTIONS_TAG);
        builder.setModeSpecifyExcluded(ImmutableList.copyOf(excludedBatchFunctions));
      } else {
        builder.setModeAll();
      }

      return builder.build();
    }).collect(Collectors.<BatchPathCoverageItemConfig> toList());

    return new BatchPathCoverageConfig(libraries, batchItems, atgConfig, executorConfig);
  }
}
