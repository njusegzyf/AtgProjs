package cn.nju.seg.atg.gui.handlers;

import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.nju.seg.atg.parse.ConditionCoverage;
import cn.nju.seg.atg.parse.CoverageCriteria;
import cn.nju.seg.atg.parse.PathCoverage;

/**
 * @version 0.1 Change to extends `AbstractHandler` and remove useless methods. 
 * @author zy
 * @author Zhang Yifan
 */
public final class mainHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    // get workbench window
    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    // set selection service
    ISelectionService service = window.getSelectionService();
    // set structured selection
    IStructuredSelection structured = (IStructuredSelection) service.getSelection();

    // check if it is an IFunctionDeclaration
    if (structured.getFirstElement() instanceof IFunctionDeclaration) {
      // get the selected IFunctionDeclaration
      IFunctionDeclaration ifd = (IFunctionDeclaration) structured.getFirstElement();

      // set the coverage criteria
      final String action = "atg-pc";
      final CoverageCriteria cc;
      if (action.equals("atg-tsc") || action.equals("atg-pc")) {
        cc = new PathCoverage(action);
      } else {
        cc = new ConditionCoverage(action);
      }
      cc.run(ifd);

      // create a configuration wizard for parameters setting
      // Configuration configuration = new Configuration();
      // configuration.init(window.getWorkbench(), structured);
      // WizardDialog dialog = new WizardDialog(window.getShell(), configuration);
      // dialog.open();
    }

    return null;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean isHandled() {
    return true;
  }
}
