package nju.seg.zhangyf.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Preconditions;

/**
 * @apiNote Calling `getActiveXXX` methods will return empty if called from a non-UI thread.
 * 
 * @author Zhang Yifan
 */
public final class ResourceAndUiUtil {

  public static Optional<IWorkbenchWindow> getFirstWorkbenchWindow() { 
    final IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
    if (workbenchWindows.length > 0) {
      return Optional.of(workbenchWindows[0]);
    } else {
      return Optional.empty();
    }
  }
  
  public static Optional<IWorkbenchWindow> getActiveWorkbenchWindow() {
    return Optional.of(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
  }

  public static Optional<IWorkbenchPage> getActivePage() {
    return ResourceAndUiUtil.getActiveWorkbenchWindow().map(arg -> arg.getActivePage());
  }

  public static Optional<IEditorPart> getActiveEditor() {
    return ResourceAndUiUtil.getActivePage().map(arg -> arg.getActiveEditor());
  }

  public static <TEditor extends IEditorPart> Optional<TEditor> getActiveEditorAs(final Class<TEditor> editorClass) {
    Preconditions.checkNotNull(editorClass);

    return ResourceAndUiUtil.getActiveEditor()
                            .flatMap(arg -> Util.<TEditor> asOptional(arg, editorClass));
  }

  public static Optional<Shell> getActiveShell() {
    return ResourceAndUiUtil.getActiveWorkbenchWindow().map(arg -> arg.getShell());
  }

  public static Optional<IStructuredSelection> getActiveStructuredSelection() {
    return ResourceAndUiUtil.getActiveWorkbenchWindow()
                            .flatMap(window -> {
                              final ISelectionService selectionService = window.getSelectionService();
                              final ISelection selection = selectionService.getSelection();
                              return Util.<IStructuredSelection> asOptional(selection, IStructuredSelection.class);
                            });
  }

  public static Optional<Object> getFirstActiveSelection() {
    return ResourceAndUiUtil.getActiveStructuredSelection()
                            .<Object> map(selection -> selection.getFirstElement());
  }

  public static <TSelection> Optional<TSelection> getFirstActiveSelectionAs(final Class<TSelection> selectionClass) {
    Preconditions.checkNotNull(selectionClass);

    return ResourceAndUiUtil.getFirstActiveSelection()
                            .<TSelection> flatMap(selection -> Util.<TSelection> asOptional(selection, selectionClass));
  }

  public static IWorkspaceRoot getRootWorkspace() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }

  public static IProject getProject(final String projectName) {
    Preconditions.checkNotNull(projectName);

    return ResourceAndUiUtil.getRootWorkspace().getProject(projectName);
  }

  public static IFile getFile(final String projectName, final String filePath) {
    Preconditions.checkNotNull(projectName);
    Preconditions.checkNotNull(filePath);

    return ResourceAndUiUtil.getProject(projectName).getFile(filePath);
  }

  public static IFolder getFolder(final String projectName, final String folderPath) {
    Preconditions.checkNotNull(projectName);
    Preconditions.checkNotNull(folderPath);

    return ResourceAndUiUtil.getProject(projectName).getFolder(folderPath);
  }

  /** Converts a {@link IFile} to a {@link File} using its absolute path in the local file system. */
  public static File eclipseFileToJavaFile(final IFile file) {
    Preconditions.checkNotNull(file);

    return file.getLocation().toFile();
  }

  /** Converts a {@link IFile} to a {@link Path} using its absolute path in the local file system */
  public static Path eclipseFileToPath(final IFile file) {
    Preconditions.checkNotNull(file);

    return ResourceAndUiUtil.eclipseFileToJavaFile(file).toPath();
  }
  
  @Deprecated
  private ResourceAndUiUtil() {}
}
