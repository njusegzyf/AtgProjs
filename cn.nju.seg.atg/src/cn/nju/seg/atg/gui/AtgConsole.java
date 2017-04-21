package cn.nju.seg.atg.gui;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * @since 0.1
 * @author Zhang Yifan
 */
public final class AtgConsole {

  public static final MessageConsole console = new MessageConsole("ATG Console", null);

  /**
   * 当前Eclispe实例的console流
   */
  public final static MessageConsoleStream consoleStream = AtgConsole.initConsoleStream();

  private static MessageConsoleStream initConsoleStream() {
    /************************************************
     * | 获取当前"测试Eclipse"的Console窗口 |
     * | 将其命名为"CLF Console"，为输出运行结果做准备 |
     ***********************************************/
    // 定义一个console窗口

    // 将console窗口添加到当前测试Eclipse中
    ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
    // 在当前测试Eclipse中显示该console的内容
    ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);

    // 获取console的信息流consoleStream，为输出运行结果做准备
    return console.newMessageStream();
  }

  @Deprecated
  private AtgConsole() {}
}
