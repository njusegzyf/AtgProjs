package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.util.ConfigUtil2;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.parse.TestBuilder;

/**
 * Configuration for ATG.
 * 
 * @see cn.nju.seg.atg.util.ATG
 * @author Zhang Yifan
 */
public final class AtgConfig {

  public final Optional<String> action;

  /**
   * @see cn.nju.seg.atg.util.ATG#resultFolder
   */
  public final Optional<String> resultFolder;

  /**
   * @see cn.nju.seg.atg.parse.TestBuilder#repetitionNum
   */
  public final Optional<Integer> countOfRepeation;

  /**
   * @see cn.nju.seg.atg.util.ATG#MAX_NUM_OF_PREDICT_PARAM
   */
  public final Optional<Integer> maxNumOfPredictParam;

  /**
   * @see cn.nju.seg.atg.util.ATG#MAX_NUM_OF_GENERATE_CYCLE
   */
  public final Optional<Integer> maxNumOfGenerateCycle;

  /**
   * @see cn.nju.seg.atg.util.ATG#PREDICT_BOUNDARY
   */
  public final Optional<Double> predictBoundary;

  /**
   * @see cn.nju.seg.atg.util.ATG#MAX_STEP
   */
  public final Optional<Double> maxStep;

  // TODO handle other settings
  
  // /**
  // * 变量搜索方式
  // * <p>SEARCH_STRATEGY_ALL = 0,<p>SEARCH_STRATEGY_ONE_BY_ONE = 1
  // */
  // public static int SEARCH_STRATEGY = 0;
  // /**
  // * 是否需要精细搜索
  // */
  // public static boolean NEED_REFINED_SEARCH = true;
  // /**
  // * 最大预测区间
  // */
  // public static final double MAX_PREDICT_BOUNDARY = 20.0;
  // /**
  // * 最小预测区间
  // */
  // public static final double MIN_PREDICT_BOUNDARY = 0.1;
  // /**
  // * 取值调整粒度
  // */
  // public static final double ADJUST_RATIO_DOUBLE_ZERO = 1e-40;
  // public static final double ADUJST_RATIO_DOUBLE = 1e-15;
  // public static final int ADJUST_GRANULARITY_INTEGER = 1;

  private AtgConfig(final Optional<String> action,
                    final Optional<String> resultFolder,
                    final Optional<Integer> countOfRepeation,
                    final Optional<Integer> maxNumOfPredictParam,
                    final Optional<Integer> maxNumOfGenerateCycle,
                    final Optional<Double> predictBoundary,
                    final Optional<Double> maxStep) {
    assert action != null;
    assert resultFolder != null;
    assert countOfRepeation != null;
    assert maxNumOfPredictParam != null;
    assert maxNumOfGenerateCycle != null;
    assert predictBoundary != null;
    assert maxStep != null;

    this.action = action;
    this.resultFolder = resultFolder;
    this.countOfRepeation = countOfRepeation;
    this.maxNumOfPredictParam = maxNumOfPredictParam;
    this.maxNumOfGenerateCycle = maxNumOfGenerateCycle;
    this.predictBoundary = predictBoundary;
    this.maxStep = maxStep;
  }

  public static AtgConfig parse(final Config rawConfig) {
    Preconditions.checkNotNull(rawConfig);

    final AtgConfigBuilder builder = new AtgConfigBuilder();

    // TODO parse `rawConfig`
    builder.action = ConfigUtil2.getOptionalString(rawConfig, AtgConfig.ACTION_TAG);
    builder.resultFolder = ConfigUtil2.getOptionalString(rawConfig, AtgConfig.RESULT_FOLDER_TAG);
    builder.countOfRepeation = ConfigUtil2.getOptionalInteger(rawConfig, AtgConfig.COUNT_OF_REPEATION_TAG);
    builder.maxNumOfPredictParam = ConfigUtil2.getOptionalInteger(rawConfig, AtgConfig.MAX_NUM_OF_PREDICT_PARAM_TAG);
    builder.maxNumOfGenerateCycle = ConfigUtil2.getOptionalInteger(rawConfig, AtgConfig.MAX_NUM_OF_GENERATE_CYCLE_TAG);
    builder.predictBoundary = ConfigUtil2.getOptionalDouble(rawConfig, AtgConfig.PREDICT_BOUNDARY_TAG);
    builder.maxStep = ConfigUtil2.getOptionalDouble(rawConfig, AtgConfig.MAX_STEP_TAG);

    return builder.build();
  }

  public static class AtgConfigBuilder {

    public Optional<String> action;
    public Optional<String> resultFolder;
    public Optional<Integer> countOfRepeation;
    public Optional<Integer> maxNumOfPredictParam;
    public Optional<Integer> maxNumOfGenerateCycle;
    public Optional<Double> predictBoundary;
    public Optional<Double> maxStep;

    public AtgConfigBuilder() {
      this.clear();
    }

    public void clear() {
      this.action = Optional.empty();
      this.resultFolder = Optional.empty();
      this.countOfRepeation = Optional.empty();
      this.maxNumOfPredictParam = Optional.empty();
      this.maxNumOfGenerateCycle = Optional.empty();
      this.predictBoundary = Optional.empty();
      this.maxStep = Optional.empty();
    }

    public void checkVaild() {
      Preconditions.checkState(this.action != null);
      Preconditions.checkState(this.resultFolder != null);
      Preconditions.checkState(this.countOfRepeation != null);
      this.countOfRepeation.ifPresent(i -> Preconditions.checkState(i > 0));
      Preconditions.checkState(this.maxNumOfPredictParam != null);
      this.maxNumOfPredictParam.ifPresent(i -> Preconditions.checkState(i > 0));
      Preconditions.checkState(this.maxNumOfGenerateCycle != null);
      this.maxNumOfGenerateCycle.ifPresent(i -> Preconditions.checkState(i > 0));
      Preconditions.checkState(this.predictBoundary != null);
      this.predictBoundary.ifPresent(i -> Preconditions.checkState(i > 0.0));
      Preconditions.checkState(this.maxStep != null);
      this.maxStep.ifPresent(i -> Preconditions.checkState(i > 0.0));
    }

    public AtgConfig build() {
      this.checkVaild();

      return new AtgConfig(this.action,
                           this.resultFolder,
                           this.countOfRepeation,
                           this.maxNumOfPredictParam,
                           this.maxNumOfGenerateCycle,
                           this.predictBoundary,
                           this.maxStep);
    }
  }

  public static final String ATG_CONFIG_TAG = "ATG";
  public static final String ACTION_TAG = "Action";
  public static final String RESULT_FOLDER_TAG = "ResultFolder";
  public static final String COUNT_OF_REPEATION_TAG = "CountOfRepeation";
  public static final String MAX_NUM_OF_PREDICT_PARAM_TAG = "MaxNumOfPredictParam";
  public static final String MAX_NUM_OF_GENERATE_CYCLE_TAG = "MaxNumOfGenerateCycle";
  public static final String PREDICT_BOUNDARY_TAG = "PredictBoundary";
  public static final String MAX_STEP_TAG = "MaxStep";

  public static void enableAtgConfig(final AtgConfig atgConfig) {
    Preconditions.checkNotNull(atgConfig);

    atgConfig.resultFolder.ifPresent(resFolder -> {
      ATG.resultFolder = resFolder;
      AtgWrapperPluginSettings.doIfDebug(() -> System.out.println("Set ATG result folder to : " + resFolder));
    });
    atgConfig.countOfRepeation.ifPresent(v -> {
      TestBuilder.repetitionNum = v;
    });
    atgConfig.maxNumOfPredictParam.ifPresent(v -> {
      ATG.MAX_NUM_OF_PREDICT_PARAM = v;
    });
    atgConfig.maxNumOfGenerateCycle.ifPresent(v -> {
      ATG.MAX_NUM_OF_GENERATE_CYCLE = v;
    });
    atgConfig.predictBoundary.ifPresent(v -> {
      ATG.PREDICT_BOUNDARY = v;
    });
    atgConfig.maxStep.ifPresent(v -> {
      ATG.MAX_STEP = v;
    });
  }
}
