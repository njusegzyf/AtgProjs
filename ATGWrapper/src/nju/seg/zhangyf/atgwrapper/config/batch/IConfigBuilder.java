package nju.seg.zhangyf.atgwrapper.config.batch;

/**
 * @author Zhang Yifan
 * @param <TConfig>
 */
public interface IConfigBuilder<TConfig> {

  void reset();

  void checkVaild();

  // @Nonnull
  TConfig build();
}
