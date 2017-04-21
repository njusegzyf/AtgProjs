package cn.nju.seg.atg.gui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public final class Configuration extends Wizard implements INewWizard {

  private EditListsConfigWizardPage editListsConfigPage;

  // public Configuration() {}

  @Override
  public void addPages() {
    this.editListsConfigPage = new EditListsConfigWizardPage("CLFF-ATG Configuration");
    addPage(this.editListsConfigPage);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {}

  @Override
  public boolean performFinish() {
    return false;
  }
}
