package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;

import com.google.common.base.Strings;

public abstract class BatchItemBase {

  final Optional<String> project;
  final String batchFile;

  protected BatchItemBase(final Optional<String> project,
                        final String batchFile) {
    assert project != null;
    assert !Strings.isNullOrEmpty(batchFile);

    this.project = project;
    this.batchFile = batchFile;
  }
}
