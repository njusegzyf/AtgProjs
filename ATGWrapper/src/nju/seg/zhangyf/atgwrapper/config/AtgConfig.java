package nju.seg.zhangyf.atgwrapper.config;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
public final class AtgConfig extends StorageConfig {

  public final Optional<String> action;

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

  /**
   * @see cn.nju.seg.atg.util.ATG#START_POINT
   */
  public final Optional<Double> startPoint;

  /**
   * @see cn.nju.seg.atg.util.ATG#CUSTOMIZED_PARAMS
   */
  public final Optional<List<Double>> customizedParmas;

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

  private AtgConfig(final Optional<String> resultFolder,
                    final boolean isCopyConfigToResultFolder,
                    final Optional<String> action,
                    final Optional<Integer> countOfRepeation,
                    final Optional<Integer> maxNumOfPredictParam,
                    final Optional<Integer> maxNumOfGenerateCycle,
                    final Optional<Double> predictBoundary,
                    final Optional<Double> maxStep,
                    final Optional<Double> startPoint,
                    final Optional<List<Double>> customizedParmas) {
    super(resultFolder, isCopyConfigToResultFolder);

    assert action != null;
    assert countOfRepeation != null;
    assert maxNumOfPredictParam != null;
    assert maxNumOfGenerateCycle != null;
    assert predictBoundary != null;
    assert maxStep != null;
    assert startPoint != null;
    assert customizedParmas != null;

    this.action = action;
    this.countOfRepeation = countOfRepeation;
    this.maxNumOfPredictParam = maxNumOfPredictParam;
    this.maxNumOfGenerateCycle = maxNumOfGenerateCycle;
    this.predictBoundary = predictBoundary;
    this.maxStep = maxStep;
    this.startPoint = startPoint;
    this.customizedParmas = customizedParmas;
  }

  public static class AtgConfigBuilder extends StorageConfig.StrorageConfigBuilder {

    public Optional<String> action;
    public Optional<Integer> countOfRepeation;
    public Optional<Integer> maxNumOfPredictParam;
    public Optional<Integer> maxNumOfGenerateCycle;
    public Optional<Double> predictBoundary;
    public Optional<Double> maxStep;
    public Optional<Double> startPoint;
    public Optional<List<Double>> customizedParmas;

    public AtgConfigBuilder() {
      this.reset();
    }

    public AtgConfig build() {
      this.checkVaild();

      final AtgConfig result = new AtgConfig(this.resultFolder,
                                             this.isCopyConfigToResultFolder,
                                             this.action,
                                             this.countOfRepeation,
                                             this.maxNumOfPredictParam,
                                             this.maxNumOfGenerateCycle,
                                             this.predictBoundary,
                                             this.maxStep,
                                             this.startPoint,
                                             this.customizedParmas);
      this.reset();
      return result;
    }

    @Override
    public void checkVaild() {
      super.checkVaild();

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
      Preconditions.checkState(this.startPoint != null);
      Preconditions.checkState(this.customizedParmas != null);
    }

    @Override
    public void reset() {
      super.reset();

      this.action = Optional.empty();
      this.resultFolder = Optional.empty();
      this.countOfRepeation = Optional.empty();
      this.maxNumOfPredictParam = Optional.empty();
      this.maxNumOfGenerateCycle = Optional.empty();
      this.predictBoundary = Optional.empty();
      this.maxStep = Optional.empty();
      this.startPoint = Optional.empty();
      this.customizedParmas = Optional.empty();
    }
  }

  public static AtgConfig parse(final Config rawConfig) {
    Preconditions.checkNotNull(rawConfig);

    final AtgConfigBuilder builder = new AtgConfigBuilder();

    builder.resultFolder = ConfigUtil2.getOptionalString(rawConfig, ConfigTags.RESULT_FOLDER_TAG);
    builder.isCopyConfigToResultFolder = ConfigUtil2.getOptionalBoolean(rawConfig, ConfigTags.IS_COPY_CONFIG_TO_RESULT_FOLDER_TAG).orElse(Boolean.FALSE).booleanValue();

    builder.action = ConfigUtil2.getOptionalString(rawConfig, ConfigTags.ACTION_TAG);
    builder.countOfRepeation = ConfigUtil2.getOptionalInteger(rawConfig, ConfigTags.COUNT_OF_REPEATION_TAG);
    builder.maxNumOfPredictParam = ConfigUtil2.getOptionalInteger(rawConfig, ConfigTags.MAX_NUM_OF_PREDICT_PARAM_TAG);
    builder.maxNumOfGenerateCycle = ConfigUtil2.getOptionalInteger(rawConfig, ConfigTags.MAX_NUM_OF_GENERATE_CYCLE_TAG);
    builder.predictBoundary = ConfigUtil2.getOptionalDouble(rawConfig, ConfigTags.PREDICT_BOUNDARY_TAG);
    builder.maxStep = ConfigUtil2.getOptionalDouble(rawConfig, ConfigTags.MAX_STEP_TAG);
    builder.startPoint = ConfigUtil2.getOptionalDouble(rawConfig, ConfigTags.START_POINT_TAG);
    builder.customizedParmas = ConfigUtil2.getOptionalDoubleList(rawConfig, ConfigTags.CUSTOMIZED_PARAMS_TAG);

    return builder.build();
  }

  public static void enableAtgConfig(final AtgConfig atgConfig) {
    Preconditions.checkNotNull(atgConfig);

    atgConfig.resultFolder.ifPresent(resFolder -> {
      assert !Strings.isNullOrEmpty(resFolder);

      ATG.resultFolder = resFolder;
      AtgWrapperPluginSettings.doIfDebug(() -> System.out.println("Set ATG result folder to : " + resFolder));
      // create the folder
      try {
        java.nio.file.Files.createDirectories(Paths.get(resFolder));
      } catch (final IOException ignored) {}
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
    atgConfig.startPoint.ifPresent(v -> {
      ATG.START_POINT = v;
    });

    final double[] customizedParmas = atgConfig.customizedParmas.map(v -> v.stream().mapToDouble(Double::doubleValue).toArray())
                                                                .orElse(AtgConfig.DEFAULT_EMPTY_CUSTOMIZED_PARMAS);
    ATG.setCustomizedParams(customizedParmas);
  }

  public static final double[] DEFAULT_EMPTY_CUSTOMIZED_PARMAS = {};
}
