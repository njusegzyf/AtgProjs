package nju.seg.zhangyf.atgwrapper.config.batch;

import java.util.Optional;

import com.google.common.base.Strings;

/**
 * @author Zhang Yifan
 */
public abstract class BatchItemConfigBase {

  public final Optional<String> project;
  public final String batchFile;

  protected BatchItemConfigBase(final Optional<String> project,
                                final String batchFile) {
    assert project != null;
    assert !Strings.isNullOrEmpty(batchFile);

    this.project = project;
    this.batchFile = batchFile;
  }
}
