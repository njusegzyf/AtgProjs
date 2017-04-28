package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.cdt.internal.core.model.CContainer;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
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

    // store all files that should be processed
    final Queue<IFile> filesToProcess = Lists.newLinkedList();

    // used to visit the resource and collect all the files that should be processed
    final IResourceVisitor resourceVisitor = new IResourceVisitor() {

      @Override
      public boolean visit(IResource resource) throws CoreException {
        final Optional<IFile> file = Util.asOptional(resource, IFile.class);
        if (file.isPresent()) {
          final IFile configFile = file.get();
          if (configFile.isAccessible() && configFile.getName().endsWith(".conf")) {
            filesToProcess.offer(configFile);
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
        BatchFolderHandler.startProcessBatchItems(filesToProcess);
      } catch (final CoreException e) {
        SwtUtil.createErrorMessageBoxWithActiveShell("Failed to batch folder with exception:\n" + e.toString())
               .open();
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
        BatchFolderHandler.startProcessBatchItems(filesToProcess);
      } catch (final CoreException e) {
        SwtUtil.createErrorMessageBoxWithActiveShell("Failed to batch folder with exception:\n" + e.toString())
        .open();
      }
      return null;
    }

    // handle not a `IFolder` or a `CContainer` is selected
    SwtUtil.createErrorMessageBoxWithActiveShell("The selection is not a folder or a container.")
           .open();
    return null;
  }

  @Override
  public boolean isEnabled() {
    // return super.isEnabled();
    return true;
  }

  private static void startProcessBatchItems(final Queue<IFile> filesToProcess) {
    assert filesToProcess != null;

    final Executor executor = Executors.newSingleThreadExecutor();
    BatchFolderHandler.processBatchItem(filesToProcess, executor);
  }

  private static void processBatchItem(final Queue<IFile> filesToProcess, final Executor executor) {
    assert filesToProcess != null;
    assert executor != null;

    if (filesToProcess.isEmpty()) {
      return;
    }

    final IFile batchFile = filesToProcess.poll();
    assert batchFile != null;

    final Config rawBatchConfig = ConfigFactory.parseFile(ResourceAndUiUtil.eclipseFileToJavaFile(batchFile));

    if (rawBatchConfig.hasPath(ConfigTags.ATG_ACTION_PATH)) {
      // create a handler to process the batch file

      // get action from config file
      final String action = rawBatchConfig.getString(ConfigTags.ATG_ACTION_PATH);

      final BatchFileRunnerBase<?, ?, ?> runner;
      try {
        // create handler to process the batch file based on the action
        runner = BatchFileRunners.createBatchFileRunner(action);
      } catch (final Throwable ex) {
        // if we failed to create a runner to process the batch file, show the error and call self recursively to handle the next item.
        SwtUtil.createErrorMessageBoxWithActiveShell(
                                                     "Can not create a runner for the batch file: " + batchFile.getFullPath().toOSString()
                                                         + ",\nskip processing the file.")
               .open();
        BatchFolderHandler.processBatchItem(filesToProcess, executor);
        return;
      }

      // process the batch file
      final ListenableFuture<?> processResult = runner.processBatchItemAsync(batchFile);
      // add a callback to start processing next batch file when the batch file is done
      processResult.addListener(() -> BatchFolderHandler.processBatchItem(filesToProcess, executor),
                                executor);
      return;
    } else {
      // if we can not get the action, skip it and just call self recursively to handle the next item.

      // Note: Since HOCON allows a config file includes other config files, it is not an error that a config file does not define the action.
      //
      // SwtUtil.createErrorMessageBoxWithActiveShell(
      // "Can not get action from config file: " + batchFile.getFullPath().toOSString()
      // + ",\nskip processing the file.")
      // .open();

      BatchFolderHandler.processBatchItem(filesToProcess, executor);
      return;
    }
  }

}
