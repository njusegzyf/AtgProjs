package nju.seg.zhangyf.atg.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

// import com.google.common.base.Preconditions;

/**
 * @version 0.1
 * @author Zhang Yifan
 */
public final class SwtUtil {

  public static MessageBox createMessageBox(final Shell shell, final int style, final String message, final String text) {
//    Preconditions.checkNotNull(shell);
//    Preconditions.checkNotNull(message);
//    Preconditions.checkNotNull(text);

    final MessageBox messageBox = new MessageBox(shell, style);
    messageBox.setMessage(message);
    messageBox.setText(text);

    return messageBox;
  }

  public static MessageBox createErrorMessageBox(final Shell shell, final String message, final String text) {
    return SwtUtil.createMessageBox(shell, SWT.ERROR, message, text);
  }

  public static MessageBox createErrorMessageBox(final Shell shell, final String message) {
    return SwtUtil.createErrorMessageBox(shell, message, "Error");
  }

  @Deprecated
  private SwtUtil() {}
}
