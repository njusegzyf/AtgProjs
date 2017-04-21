package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;

import org.eclipse.cdt.internal.core.model.CContainer;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import nju.seg.zhangyf.util.ResourceAndUiUtil;
import nju.seg.zhangyf.util.SwtUtil;
import nju.seg.zhangyf.util.Util;

@SuppressWarnings("restriction")
public final class BatchFolderHandler extends AbstractHandler {

  /* (non-Javadoc)
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    // System.out.println(ResourceAndUiUtil.getFirstActiveSelection().get().getClass());
    final IResourceVisitor resourceVisitor = new IResourceVisitor() {

      final BatchFileHandler fileHandler = new BatchFileHandler();

      @Override
      public boolean visit(IResource resource) throws CoreException {
        final Optional<IFile> file = Util.asOptional(resource, IFile.class);
        if (file.isPresent()) {
          final IFile configFile = file.get();
          if (configFile.isAccessible() && configFile.getName().endsWith(".conf")) {
            this.fileHandler.processBatchItem(configFile);
          }
          // do not visit children of a `IFile`
          return false;
        } else {
          // do visit children of `IFolder`, which allows to process files nested within sub folders
          return true;
        }
      }
    };

    final Optional<IFolder> selectedFolder = ResourceAndUiUtil.getFirstActiveSelectionAs(IFolder.class);
    if (selectedFolder.isPresent()) {
      try {
        selectedFolder.get().accept(resourceVisitor);
      } catch (CoreException e) {
        e.printStackTrace();
      }
      return null;
    }

    // Note : In a C/C++ project, a folder is an instance of `org.eclipse.cdt.internal.core.model.SourceRoot`,
    // which extends `CContainer` and is not an instance of `IFolder` or `IContainer`.
    final Optional<CContainer> selectedCContainer = ResourceAndUiUtil.getFirstActiveSelectionAs(CContainer.class);
    if (selectedCContainer.isPresent()) {
      try {
        // Note : `CContainer`'s visit method is used to visit C/C++ elements, not for resources.
        selectedCContainer.get().getResource().accept(resourceVisitor);
      } catch (CoreException e) {
        e.printStackTrace();
      }
      return null;
    }

    // handle not a `IFolder` or a `CContainer` is selected
    SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                  "The selection is not a folder or a container.")
           .open();
    return null;

  }

  @Override
  public boolean isEnabled() {
    // return super.isEnabled();
    return true;
  }

}
