package nju.seg.zhangyf.atg.util;

/**
 * @since 0.1
 * @author Zhang Yifan
 */
@FunctionalInterface
public interface ThrowableAction {
  public void apply() throws Exception;
}
