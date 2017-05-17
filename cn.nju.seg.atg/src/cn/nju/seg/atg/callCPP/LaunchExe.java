package cn.nju.seg.atg.callCPP;

import java.io.IOException;

public class LaunchExe {

  public static void execute(String path, String[] parameters) {
    String command = "cmd /c ".concat(path);
    for (String parameter : parameters) {
      command = command.concat(" ").concat(parameter);
    }
    try {
      Process p = Runtime.getRuntime().exec(command);
      p.destroy();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
