package nju.seg.zhangyf.atgwrapper.batch;

/**
 * Tags that are used in batch items.
 * 
 * @author Zhang Yifan
 */
public final class BatchTags {

  public static final String NAME_TAG = "Name";
  public static final String TYPE_TAG = "Type";
  public static final String ARG_TAG = "Arg";

  public static final String LIBRARIES_TAG = "Libraries";
  public static final String LIBRARY_NAME_TAG = "LibraryName";
  public static final String LIBRARY_PATH_TAG = "LibraryPath";
  public static final String LIBRARY_PROJECT_TAG = "LibraryProject";
  public static final String BATCH_ITEMS_TAG = "BatchItems";
  public static final String PROJECT_TAG = "Project";
  public static final String BATCH_FILE_TAG = "BatchFile";
  public static final String BATCH_FUNCTIONS_TAG = "BatchFunctions";
  public static final String EXCLUDED_BATCH_FUNCTIONS_TAG = "ExcludedBatchFunctions";

  // tags for branch coverage config

  public static final String BATCH_FUNCTION_TAG = "BatchFunction";
  public static final String TARGET_NODES_TAG = "TargetNodes";
  public static final String TARGET_PATHS_TAG = "TargetPaths";
  public static final String PATH_TAG = "Path";

  // tags for `StorageConfig`

  public static final String RESULT_FOLDER_TAG = "ResultFolder";
  public static final String IS_COPY_CONFIG_TO_RESULT_FOLDER_TAG = "CopyConfigToResultFolder";

  // tags for `AtgConfig`

  public static final String ATG_CONFIG_TAG = "ATG";
  public static final String ACTION_TAG = "Action";
  public static final String COUNT_OF_REPEATION_TAG = "CountOfRepeation";
  public static final String MAX_NUM_OF_PREDICT_PARAM_TAG = "MaxNumOfPredictParam";
  public static final String MAX_NUM_OF_GENERATE_CYCLE_TAG = "MaxNumOfGenerateCycle";
  public static final String PREDICT_BOUNDARY_TAG = "PredictBoundary";
  public static final String MAX_STEP_TAG = "MaxStep";
  public static final String START_POINT_TAG = "StartPoint";

  // tags for `ExecutorConfig`

  public static final String EXECUTOR_TAG = "Executor";
  public static final String SINGLE_THREAD_EXECUTOR_TAG = "SingleThreadExecutor";
  public static final String FIXED_THREAD_POOL_TAG = "FixedThreadPool";
  public static final String CACHED_THREAD_POOL_TAG = "CachedThreadPool";
  public static final String SINGLE_THREAD_SCHEDULED_EXECUTOR_TAG = "SingleThreadScheduledExecutor";
  public static final String SCHEDULED_THREAD_POOL_TAG = "ScheduledThreadPool";
  public static final String WORK_STEALING_POOL_TAG = "WorkStealingPool";
  public static final String SINGLE_FUNCTION_TIMEOUT_TAG = "SingleFunctionTimeout";

  // paths
  public static final String ATG_ACTION_PATH = "ATG.Action";
  
  @Deprecated
  private BatchTags() {}
}
