package nju.seg.zhangyf.atgwrapper.config.batch;

import java.util.Optional;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.typesafe.config.Config;

import nju.seg.zhangyf.atgwrapper.config.AtgConfig;
import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
import nju.seg.zhangyf.util.ConfigUtil2;

/**
 * @author Zhang Yifan
 */
public abstract class BatchItemConfigBase {

  public final Optional<String> project;
  public final String batchFile;
  public final Optional<AtgConfig> atgConfig;

  protected BatchItemConfigBase(final Optional<String> project,
                                final String batchFile,
                                final Optional<AtgConfig> atgConfig) {
    assert project != null;
    assert !Strings.isNullOrEmpty(batchFile);
    assert atgConfig != null;

    this.project = project;
    this.batchFile = batchFile;
    this.atgConfig = atgConfig;

    atgConfig.ifPresent(config -> Preconditions.checkArgument(this.isAtgConfigValid(config)));
  }

  protected BatchItemConfigBase(final Optional<String> project,
                                final String batchFile) {
    this(project, batchFile, Optional.empty());
  }

  /**
   * Sub classes can override this method to check if the {@link AtgConfig} is valid,
   * e.g it does not define a {@link AtgConfig#action}, which should be defined in the upper layer batch file.
   */
  @SuppressWarnings("unused")
  protected boolean isAtgConfigValid(final AtgConfig atgConfig) {
    return !atgConfig.action.isPresent();
  }

  /**
   * The base builder class for classes that extends {@link BatchItemConfigBase}.
   * 
   * @author Zhang Yifan
   * @param <TConfig>
   */
  public static abstract class BatchItemConfigBuilderBase<TConfig extends BatchItemConfigBase> implements IConfigBuilder<TConfig> {

    protected Optional<String> project;
    protected String batchFile;
    protected Optional<AtgConfig> atgConfig;

    protected BatchItemConfigBuilderBase() {
      // As the sub classes should call `reset` method in their constructor, it is useless to call `reset` here. 
      // this.reset();
    }

    @Override @OverridingMethodsMustInvokeSuper
    public void reset() {
      this.project = Optional.empty();
      this.batchFile = null;
      this.atgConfig = Optional.empty();
    }

    @Override @OverridingMethodsMustInvokeSuper
    public void checkVaild() {
      Preconditions.checkState(!Strings.isNullOrEmpty(this.batchFile));
    }
  }

  public static <TBuilder extends BatchItemConfigBuilderBase<?>>
      TBuilder parse(final TBuilder builder, final Config rawConfig) {
    Preconditions.checkNotNull(builder);
    Preconditions.checkNotNull(rawConfig);
    Preconditions.checkArgument(rawConfig.hasPath(ConfigTags.BATCH_FILE_TAG));

    builder.project = ConfigUtil2.getOptionalString(rawConfig, ConfigTags.PROJECT_TAG);
    builder.batchFile = rawConfig.getString(ConfigTags.BATCH_FILE_TAG);
    builder.atgConfig = ConfigUtil2.getOptionalConfig(rawConfig, ConfigTags.ATG_CONFIG_TAG)
                                   .map(AtgConfig::parse);

    return builder;
  }
}
