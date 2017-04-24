package nju.seg.zhangyf.atgwrapper.batch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import nju.seg.zhangyf.util.ConfigUtil2;

public final class BatchConfig extends BatchConfigBase<BatchConfig.BatchItem> {

  final List<BatchItem> batchItems;

  @Override
  public List<BatchItem> getBatchItems() {
    return this.batchItems;
  }
  
  private BatchConfig(final List<String> libraries,
                      final List<BatchItem> batchItems,
                      final Optional<AtgConfig> atgConfig,
                      final Optional<ExecutorConfig> executorConfig) {
    super(libraries, atgConfig, executorConfig);

    assert batchItems != null;

    this.batchItems = batchItems;
  }

  private BatchConfig(final List<String> libraries, final List<BatchItem> batchItems) {
    this(libraries, batchItems, Optional.empty(), Optional.empty());
  }

  public static enum BatchItemMode {
    ALL,
    SPECIFY_INCLUDED,
    SPECIFY_EXCLUDED
  }

  static final class BatchItemBuilder {

    Optional<String> project;
    String batchFile;
    private BatchItemMode mode;
    private ImmutableList<String> includedBatchFunctions;
    private ImmutableList<String> excludedBatchFunctions;

    public BatchItemBuilder() {
      this.reset();
    }

    public void setModeAll() {
      this.mode = BatchItemMode.ALL;
      this.includedBatchFunctions = BatchItem.EMPTY_FUNCTIONS_LIST;
      this.excludedBatchFunctions = BatchItem.EMPTY_FUNCTIONS_LIST;
    }

    public void setModeSpecifyIncluded(final ImmutableList<String> includedBatchFunctions) {
      this.mode = BatchItemMode.SPECIFY_INCLUDED;
      this.includedBatchFunctions = includedBatchFunctions;
      this.excludedBatchFunctions = BatchItem.EMPTY_FUNCTIONS_LIST;
    }

    public void setModeSpecifyExcluded(final ImmutableList<String> excludedBatchFunctions) {
      this.mode = BatchItemMode.SPECIFY_EXCLUDED;
      this.includedBatchFunctions = BatchItem.EMPTY_FUNCTIONS_LIST;
      this.excludedBatchFunctions = excludedBatchFunctions;
    }

    public BatchItem build() {
      this.checkVaild();

      final BatchItem result = new BatchItem(this.project,
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
        assert this.includedBatchFunctions == BatchItem.EMPTY_FUNCTIONS_LIST
            && this.excludedBatchFunctions == BatchItem.EMPTY_FUNCTIONS_LIST;
        break;

      case SPECIFY_INCLUDED:
        assert this.includedBatchFunctions != BatchItem.EMPTY_FUNCTIONS_LIST
            && this.excludedBatchFunctions == BatchItem.EMPTY_FUNCTIONS_LIST;
        break;

      case SPECIFY_EXCLUDED:
        assert this.includedBatchFunctions == BatchItem.EMPTY_FUNCTIONS_LIST
            && this.excludedBatchFunctions != BatchItem.EMPTY_FUNCTIONS_LIST;
        break;

      default:
        throw new IllegalStateException();
      }
    }

    public void reset() {
      this.project = Optional.empty();
      this.batchFile = null;
      this.mode = null;
      this.includedBatchFunctions = BatchItem.EMPTY_FUNCTIONS_LIST;
      this.excludedBatchFunctions = BatchItem.EMPTY_FUNCTIONS_LIST;
    }
  }

  static final class BatchItem extends BatchItemBase {

    final BatchItemMode mode;
    final ImmutableList<String> includedBatchFunctions;
    final ImmutableList<String> excludedBatchFunctions;

    private BatchItem(final Optional<String> project,
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

    /**
     * Creates a batch item with {@code isProcessAllFunctions} set to {@code true} and
     * {@code batchFunctions} set to an empty array.
     */
    @Deprecated
    public static BatchItem createBatchItemWithAll(final String batchFile, final Optional<String> project) {
      assert batchFile != null;
      assert project != null;

      return new BatchItem(project,
                           batchFile,
                           BatchItemMode.ALL,
                           BatchItem.EMPTY_FUNCTIONS_LIST,
                           BatchItem.EMPTY_FUNCTIONS_LIST);
    }

    @Deprecated
    public static BatchItem createBatchItemWithIncluded(final String batchFile,
                                                        final ImmutableList<String> includedFunctions,
                                                        final Optional<String> project) {
      assert batchFile != null;
      assert project != null;

      return new BatchItem(project,
                           batchFile,
                           BatchItemMode.SPECIFY_INCLUDED,
                           includedFunctions,
                           BatchItem.EMPTY_FUNCTIONS_LIST);
    }

    /**
     * Creates a batch item with {@code isProcessAllFunctions} set to {@code true} and
     * {@code batchFunctions} set to an empty array.
     */
    @Deprecated
    public static BatchItem createBatchItemWithExcluded(final String batchFile,
                                                        final ImmutableList<String> excludedFunctions,
                                                        final Optional<String> project) {
      assert batchFile != null;
      assert project != null;

      return new BatchItem(project,
                           batchFile,
                           BatchItemMode.SPECIFY_EXCLUDED,
                           BatchItem.EMPTY_FUNCTIONS_LIST,
                           excludedFunctions);
    }

    private static final ImmutableList<String> EMPTY_FUNCTIONS_LIST = ImmutableList.of();
  }

  public static BatchConfig parseBatchConfig(final Path configPath) {
    Preconditions.checkNotNull(configPath);
    Preconditions.checkArgument(Files.isRegularFile(configPath));

    final Config config = ConfigFactory.parseFile(configPath.toFile());

    // check essential config items

    // check config of libraries
    Preconditions.checkArgument(config.hasPath(BatchTags.LIBRARIES_TAG), "Illegal config file.");
    final List<String> libraries = config.getStringList(BatchTags.LIBRARIES_TAG);

    final Optional<AtgConfig> atgConfig = ConfigUtil2.getOptionalConfig(config, BatchTags.ATG_CONFIG_TAG)
                                                     .map(AtgConfig::parse);

    final Optional<ExecutorConfig> executorConfig = ConfigUtil2.getOptionalConfig(config, BatchTags.EXECUTOR_TAG)
                                                               .map(ExecutorConfig::parse);

    final List<? extends Config> rawBatchItemConfigs = config.getConfigList(BatchTags.BATCH_ITEMS_TAG);

    final BatchItemBuilder builder = new BatchItemBuilder();
    final List<BatchItem> batchItems = rawBatchItemConfigs.stream().<BatchItem> map((final Config rawBatchItem) -> {
      Preconditions.checkState(rawBatchItem.hasPath(BatchTags.BATCH_FILE_TAG));

      // final Optional<String> project = ConfigUtil2.getStringOptional(batchItem, BatchConfig.PROJECT_TAG);
      builder.project = ConfigUtil2.getOptionalString(rawBatchItem, BatchTags.PROJECT_TAG);

      // final String batchFile = batchItem.getString(BatchConfig.BATCH_FILE_TAG);
      builder.batchFile = rawBatchItem.getString(BatchTags.BATCH_FILE_TAG);

      if (rawBatchItem.hasPath(BatchTags.BATCH_FUNCTIONS_TAG)) {
        final List<String> includedBatchFunctions = rawBatchItem.getStringList(BatchTags.BATCH_FUNCTIONS_TAG);
        builder.setModeSpecifyIncluded(ImmutableList.copyOf(includedBatchFunctions));
      } else if (rawBatchItem.hasPath(BatchTags.EXCLUDED_BATCH_FUNCTIONS_TAG)) {
        final List<String> excludedBatchFunctions = rawBatchItem.getStringList(BatchTags.EXCLUDED_BATCH_FUNCTIONS_TAG);
        builder.setModeSpecifyExcluded(ImmutableList.copyOf(excludedBatchFunctions));
      } else {
        builder.setModeAll();
      }

      return builder.build();
    }).collect(Collectors.<BatchItem> toList());

    return new BatchConfig(libraries, batchItems, atgConfig, executorConfig);
  }
}
