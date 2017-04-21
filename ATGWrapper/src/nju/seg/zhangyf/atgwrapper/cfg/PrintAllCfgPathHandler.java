package nju.seg.zhangyf.atgwrapper.cfg;

import java.util.Optional;

import org.eclipse.cdt.core.model.IFunction;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import cn.nju.seg.atg.gui.AtgConsole;
import nju.seg.zhangyf.util.ResourceAndUiUtil;
import nju.seg.zhangyf.util.SwtUtil;

/**
 * @author Zhang Yifan
 */
public final class PrintAllCfgPathHandler extends AbstractHandler {

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final Optional<IFunction> optionalSelectedFunction = ResourceAndUiUtil.getFirstActiveSelectionAs(IFunction.class);
    if (!optionalSelectedFunction.isPresent()) {
      // handle not a `IFunctionDeclaration` is selected
      SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                    "The selection is not a function.")
             .open();
      return null;
    }

    // CfgPathUtil.printAllCfgPaths(optionalSelectedFunction.get(), System.out);
    CfgPathUtil.printAllCfgPaths(optionalSelectedFunction.get(), AtgConsole.consoleStream);

    return null;
  }

}
