package nju.seg.zhangyf.atgwrapper.batch;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

import nju.seg.zhangyf.util.ConfigUtil2;

public final class ExecutorConfig {

  public final ExecutorServiceType type;
  public final Optional<Integer> arg;
  public final Optional<Duration> singleFunctionTimeout;

  private ExecutorConfig(final ExecutorServiceType type,
                         final Optional<Integer> arg,
                         final Optional<Duration> singleFunctionTimeout) {
    this.type = type;
    this.arg = arg;
    this.singleFunctionTimeout = singleFunctionTimeout;
  }

  public ExecutorService createExecutor() {

    final ExecutorService executorService;
    switch (this.type) {
    case SINGLE_THREAD_EXECUTOR:
      executorService = Executors.newSingleThreadExecutor();
      break;

    case FIXED_THREAD_POOL:
      executorService = Executors.newFixedThreadPool(this.arg.get());
      break;

    case CACHED_THREAD_POOL:
      executorService = Executors.newCachedThreadPool();
      break;

    case SINGLE_THREAD_SCHEDULED_EXECUTOR:
      executorService = Executors.newSingleThreadScheduledExecutor();
      break;

    case SCHEDULED_THREAD_POOL:
      executorService = Executors.newScheduledThreadPool(this.arg.get());
      break;

    case WORK_STEALING_POOL:
      if (this.arg.isPresent()) {
        executorService = Executors.newWorkStealingPool(this.arg.get());
      } else {
        executorService = Executors.newWorkStealingPool();
      }
      break;

    default:
      throw new IllegalStateException();
    }

    return executorService;
  }

  public static ExecutorConfig parse(final Config rawConfig) {
    Preconditions.checkArgument(rawConfig.hasPath(BatchTags.TYPE_TAG));

    final ExecutorServiceType type = rawConfig.getEnum(ExecutorServiceType.class, BatchTags.TYPE_TAG);
    // switch (rawConfig.getString(ExecutorConfig.TYPE_TAG)) {
    // case ExecutorConfig.SINGLE_THREAD_EXECUTOR_TAG:
    // type = ExecutorServiceType.SINGLE_THREAD_EXECUTOR;
    // break;
    // case ExecutorConfig.FIXED_THREAD_POOL_TAG:
    // type = ExecutorServiceType.FIXED_THREAD_POOL;
    // break;
    // case ExecutorConfig.CACHED_THREAD_POOL_TAG:
    // type = ExecutorServiceType.CACHED_THREAD_POOL;
    // break;
    // case ExecutorConfig.SINGLE_THREAD_SCHEDULED_EXECUTOR_TAG:
    // type = ExecutorServiceType.SINGLE_THREAD_SCHEDULED_EXECUTOR;
    // break;
    // case ExecutorConfig.SCHEDULED_THREAD_POOL_TAG:
    // type = ExecutorServiceType.SCHEDULED_THREAD_POOL;
    // break;
    // case ExecutorConfig.WORK_STEALING_POOL_TAG:
    // type = ExecutorServiceType.WORK_STEALING_POOL;
    // break;
    // default:
    // throw new IllegalArgumentException();
    // }

    final Optional<Integer> arg = ConfigUtil2.getOptionalInteger(rawConfig, BatchTags.ARG_TAG);
    // Preconditions.checkArgument(type != null);
    if (type == ExecutorServiceType.SINGLE_THREAD_EXECUTOR || type == ExecutorServiceType.CACHED_THREAD_POOL) {
      Preconditions.checkArgument(!arg.isPresent());
    } else if (type == ExecutorServiceType.FIXED_THREAD_POOL || type == ExecutorServiceType.SCHEDULED_THREAD_POOL) {
      // check that `arg` is legal for creating an `FixedThreadPool` that can need a arg
      Preconditions.checkArgument(arg.isPresent() && arg.get() > 0);
    } else {
      // check that `arg` is legal for creating an executor that can optionally accepts a arg
      Preconditions.checkArgument(!arg.isPresent() || arg.get() > 0);
    }

    final Optional<Duration> timeout = ConfigUtil2.getOptionalDuration(rawConfig, ExecutorConfig.SINGLE_FUNCTION_TIMEOUT_TAG);
    timeout.ifPresent(t -> {
      assert !t.isNegative() && !t.isZero();
    });

    return new ExecutorConfig(type, arg, timeout);
  }

  public static enum ExecutorServiceType {
    SINGLE_THREAD_EXECUTOR,
    FIXED_THREAD_POOL,
    CACHED_THREAD_POOL,
    SINGLE_THREAD_SCHEDULED_EXECUTOR,
    SCHEDULED_THREAD_POOL,
    WORK_STEALING_POOL;
  }

  public static final String EXECUTOR_CONFIG_TAG = "ExecutorConfig";

  public static final String SINGLE_THREAD_EXECUTOR_TAG = "SingleThreadExecutor";
  public static final String FIXED_THREAD_POOL_TAG = "FixedThreadPool";
  public static final String CACHED_THREAD_POOL_TAG = "CachedThreadPool";
  public static final String SINGLE_THREAD_SCHEDULED_EXECUTOR_TAG = "SingleThreadScheduledExecutor";
  public static final String SCHEDULED_THREAD_POOL_TAG = "ScheduledThreadPool";
  public static final String WORK_STEALING_POOL_TAG = "WorkStealingPool";

  public static final String SINGLE_FUNCTION_TIMEOUT_TAG = "SingleFunctionTimeout";
}
