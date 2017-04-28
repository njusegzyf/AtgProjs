package nju.seg.zhangyf.atgwrapper.config.batch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nju.seg.zhangyf.atgwrapper.config.AtgConfig;
import nju.seg.zhangyf.atgwrapper.config.ExecutorConfig;

/**
 * @author Zhang Yifan
 */
public abstract class BatchConfigBase<TBatchItem extends BatchItemConfigBase> {

  public final List<String> libraries;
  public final Optional<AtgConfig> atgConfig;
  public final Optional<ExecutorConfig> executorConfig;

  protected BatchConfigBase(final List<String> libraries,
                            final Optional<AtgConfig> atgConfig,
                            final Optional<ExecutorConfig> executorConfig) {
    assert libraries != null;
    assert atgConfig != null;
    assert executorConfig != null;

    this.libraries = libraries;
    this.atgConfig = atgConfig;
    this.executorConfig = executorConfig;
  }

  public ExecutorService createExecutorService() {
    return this.executorConfig.map(config -> config.createExecutor())
                              .orElseGet(() -> BatchConfigBase.createDefaultExecutorService());
  }

  public static ExecutorService createDefaultExecutorService() {
    // ensure FIFO
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    return executor;
  }
  
  public abstract List<TBatchItem> getBatchItems();
}
