package nju.seg.zhangyf.atgwrapper.batch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import cn.nju.seg.atg.parse.TestBuilder;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.outcome.SingleTestOutcome;
import nju.seg.zhangyf.util.ResourceAndUiUtil;
import nju.seg.zhangyf.util.SwtUtil;
import nju.seg.zhangyf.util.Util;

/**
 * @author Zhang Yifan
 */
public abstract class BatchFileHandlerBase<TBatchItem extends BatchItemBase, TBatchConfig extends BatchConfigBase<TBatchItem>, TSingleTestOutcome extends SingleTestOutcome>
    extends AbstractHandler {

  /* (non-Javadoc)
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final Optional<IFile> selectedFile = ResourceAndUiUtil.getFirstActiveSelectionAs(IFile.class);
    if (!selectedFile.isPresent()) {
      // handle not a `IFIle` is selected
      SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                    "The selection is not a file.")
             .open();
      return null;
    }

    this.processBatchItem(selectedFile.get());
    return null;
  }

  @Override
  public boolean isEnabled() {
    // return super.isEnabled();
    return true;
  }

  public boolean processBatchItem(final IFile configFile) {
    Preconditions.checkNotNull(configFile);

    if (!(configFile.isAccessible() && configFile.getName().endsWith(".conf"))) {
      // handle if not a config file is passed
      SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                    configFile.getFullPath().toString() + " is not an accessible config file.")
             .open();
      return false;
    }

    // read the config file
    final TBatchConfig batchConfig;
    try {
      batchConfig = this.parseConfig(configFile);
    } catch (final Exception e) {
      // handle failed to parse config
      SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                    "Failed to parse the config file: \n"
                                     + configFile.getLocation().toString()
                                     + "with exception: \n"
                                     + e.toString())
             .open();
      return false;
    }

    AtgWrapperPluginSettings.doIfDebug(() -> BatchFileHandlerBase.printProcessConfigFile(configFile.getName()));

    // get the project the config file belongs to, which will be used as the fallback (default) project
    final IProject fallbackProject = configFile.getProject();

    for (final String library : batchConfig.libraries) {
      if (!BatchFileHandlerBase.loadLibrary(library)) { // if we failed to load library
        return false;
      }
    }

    // create an executor to run tests
    final ListeningExecutorService executor = MoreExecutors.listeningDecorator(batchConfig.createExecutorService());
    // collect the tasks that run works
    final ArrayList<ListenableFuture<TSingleTestOutcome>> workTaskList = Lists.newArrayList();

    // create an executor that run tasks to handle work timeout and collect results
    final ListeningExecutorService collectExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
    final Optional<Duration> singleFunctionTimeout = batchConfig.executorConfig.flatMap(e -> e.singleFunctionTimeout);
    final ArrayList<ListenableFuture<Map.Entry<String, Boolean>>> resultTaskList = Lists.newArrayList();

    // enable atg settings if present
    batchConfig.atgConfig.ifPresent(atgConfig -> {
      AtgConfig.enableAtgConfig(atgConfig);
    });

    for (final TBatchItem batchItem : batchConfig.getBatchItems()) {
      // handle a batch item
      try {
        final IProject project = BatchFileHandlerBase.getProjectWithFallback(batchItem.project, fallbackProject);

        // get the target batch file (cpp file, a translation unit)
        final IFile batchFile = project.getFile(batchItem.batchFile);
        final ITranslationUnit tu = CoreModelUtil.findTranslationUnit(batchFile);
        if (tu == null) {
          // handle if the target batch file is not a translation unit
          final boolean stopFlag = BatchFileHandlerBase.handleBatchItemError(batchItem.batchFile,
                                                                             "Cannot find translation unit in: " + batchFile.getLocation().toString());

          if (stopFlag) {
            // break if this error should stop processing remaining batch items
            break;
          } else {
            continue;
          }
        }

        // create a `Predicate` to filter `IFunctionDeclaration`s
        final Predicate<IFunctionDeclaration> predicate = this.getFunctionFilter(batchItem);

        // visit the `ITranslationUnit`, and performs operations on `IFunctionDeclaration`s
        tu.accept(new ICElementVisitor() {
          @Override
          public boolean visit(ICElement element) throws CoreException {
            final Optional<IFunctionDeclaration> function = Util.asOptional(element, IFunctionDeclaration.class);
            // handle `IFunctionDeclaration`
            if (function.isPresent()) {
              if (predicate.test(function.get())) { // if we need to process this function
                // for debug, print the function
                AtgWrapperPluginSettings.doIfDebug(() -> BatchFileHandlerBase.printProcessFunction(function.get()));

                // used to record the `workTask`
                final SettableFuture<ListenableFuture<TSingleTestOutcome>> workTaskFuture = SettableFuture.create();

                // submit a task to run the work, this task returns a SingleTestOutcome and may be canceled
                final ListenableFuture<TSingleTestOutcome> workTask = executor.submit(() -> {
                  // submit a task to handle timeout and collect result
                  // Note: This task should be submitted in the work task instead of after submitting the work task,
                  // which enables that the timing begins just before the work is going to run.
                  final ListenableFuture<Map.Entry<String, Boolean>> resultTask = collectExecutor.submit(() -> {
                    // get the `workTask`
                    final ListenableFuture<TSingleTestOutcome> workTaskRef = workTaskFuture.get();

                    try {
                      if (singleFunctionTimeout.isPresent()) { // try to get the result in timeout if defined
                        // the actual timeout is timeout for single function multiply repeat count
                        final Duration singleTimeout = singleFunctionTimeout.get();
                        final Duration totalTimeout = singleTimeout.multipliedBy(TestBuilder.repetitionNum);

                        // FIXME use scheduled executor instead of busy waiting
                        workTaskRef.get(totalTimeout.getSeconds(), TimeUnit.SECONDS);
                      } else { // wait without timeout
                        workTaskRef.get();
                      }
                      return Maps.immutableEntry(function.get().getSignature(), true);
                    } catch (final TimeoutException e) {
                      // handle timeout, cancel the work
                      // Note: Calling `cancel` do not force the executor to stop the work,
                      // it just call `interrupt` on the thread running the work and the work itself is required to check it.
                      // The work may still be running even `cancel` returns `true`.
                      final boolean cancelResult = workTaskRef.cancel(true);
                      AtgWrapperPluginSettings.doIfDebug(() -> {
                        System.out.println("Cancel processing function: " + function.get().getSignature() + " for timeout, result: " + cancelResult + ".\n");
                      });
                      return Maps.immutableEntry(function.get().getSignature(), false);
                    } catch (final CancellationException | java.util.concurrent.ExecutionException e) {
                      // TODO handle for execution exception
                      return Maps.immutableEntry(function.get().getSignature(), false);
                    }
                  });
                  resultTaskList.add(resultTask);

                  return BatchFileHandlerBase.this.runTest(function.get(), batchConfig, batchItem);

                });
                workTaskFuture.set(workTask);
                workTaskList.add(workTask);
              }

              // not visit children of a function
              return false;
            }

            // handle other sub types of `ICElement`
            if (element instanceof IType) {
              // not visit children of an `IType`
              return false;
            } else {
              // for other sub types of `ICElement`, visit their children
              return true;
            }
          }
        });
      } catch (Throwable e) {
        AtgWrapperPluginSettings.doIfDebug(() -> e.printStackTrace());

        // handle exception happened in processing a batch item
        if (BatchFileHandlerBase.handleBatchItemError(batchItem.batchFile, e)) {
          break;
        }
      }
    }

    // submit a task to print results when all the result tasks are done
    final ListenableFuture<List<Entry<String, Boolean>>> resultListFuture = Futures.whenAllComplete(workTaskList) // wait all work done
                                                                                   .callAsync(() -> { // collect results
                                                                                     return Futures.allAsList(resultTaskList);
                                                                                   }, collectExecutor);
    Futures.addCallback(resultListFuture, new FutureCallback<List<Entry<String, Boolean>>>() {

      @Override
      public void onSuccess(final List<Entry<String, Boolean>> result) {
        final List<String> succeedResults = result.stream()
                                                  .filter(v -> v.getValue())
                                                  .map(entry -> entry.getKey())
                                                  .collect(Collectors.toList());
        final List<String> failedResults = result.stream()
                                                 .filter(v -> !v.getValue())
                                                 .map(entry -> entry.getKey())
                                                 .collect(Collectors.toList());

        final BatchFileOutcome batchFileOutcome = new BatchFileOutcome(succeedResults, failedResults);
        // if we are debug, print the outcome to `System.out`
        AtgWrapperPluginSettings.doIfDebug(() -> {
          batchFileOutcome.printOutcome(System.out);
        });

      }

      @Override
      public void onFailure(Throwable t) {}

    });

    return true;
  }

  /** Handles the error occurred in processing a batch item, returns whether it should stop processing remaining batch items. */
  public static boolean handleBatchItemError(final String batchItemName, final String errorMessage) {
    assert (batchItemName != null && errorMessage != null);

    final MessageBox errorMsgBox = SwtUtil.createMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                                            SWT.YES | SWT.NO | SWT.ICON_ERROR,
                                                            "Error in handle batch item: " + batchItemName + "\n"
                                                                + errorMessage + '\n'
                                                                + "Do you want to abort remaining processing?",
                                                            "Error");
    final int res = errorMsgBox.open();

    // indicate that the error should stop processing remaining batch items
    return res == SWT.YES;
  }

  /** Handles the error occurred in processing a batch item, returns whether it should stop processing remaining batch items. */
  public static boolean handleBatchItemError(final String batchItemName, final Throwable exception) {
    assert (batchItemName != null && exception != null);

    return BatchFileHandlerBase.handleBatchItemError(batchItemName, exception.getMessage());
  }

  private static void printProcessConfigFile(final String configFileName) {
    assert (configFileName != null);

    System.out.println();
    System.out.println("About to process config file: " + configFileName);
    System.out.println();
  }

  private static void printProcessFunction(final IFunctionDeclaration function) throws CModelException {
    assert (function != null);

    BatchFileHandlerBase.printProcessFunctionBlank();
    System.out.println("About to process function: " + function.getSignature());
  }

  private static void printProcessFunctionBlank() {
    System.out.println();
  }

  private static IProject getProjectWithFallback(final Optional<String> projectName, final IProject fallbackProject) {
    if (projectName.isPresent()) {
      // return the specified project
      return ResourceAndUiUtil.getProject(projectName.get());
    } else {
      // return the fallback
      return fallbackProject;
    }
  }

  private final static Set<String> loadedLibraries = Sets.<String> newHashSet();

  private static boolean loadLibrary(final String library) {
    assert (!Strings.isNullOrEmpty(library));

    if (BatchFileHandlerBase.loadedLibraries.contains(library)) {
      // if the library is already loaded, do not load again
      return true;
    }

    try {
      // load the library
      // FIXME Since the libraries must be loaded before or with the class that contains native methods (typically in the static init block of the class),
      // we can not simply load libraries here.
      // Currently. all libraries are loaded in the static init block of class `cn.nju.seg.atg.callCPP` using `CallCPPLibLoader`.
      // System.loadLibrary(library);

      // record the loaded library
      BatchFileHandlerBase.loadedLibraries.add(library);

      // for debug, print the path of libraryFile
      AtgWrapperPluginSettings.doIfDebug(() -> System.out.println("Load library in: " + library));

      return true;
    } catch (Throwable e) {
      // for debug, print the path of libraryFile
      AtgWrapperPluginSettings.doIfDebug(() -> System.out.println("Failed to load library in: " + library));

      // handle failed to load JNI native library
      SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                    "Cannot load callCPP library in location: " + library + ":\n" + e.toString())
             .open();

      return false;
    }
  }

  protected abstract TBatchConfig parseConfig(final IFile configFile) throws Exception;


  protected abstract Predicate<IFunctionDeclaration> getFunctionFilter(final TBatchItem batchItem);


  protected abstract TSingleTestOutcome runTest(final IFunctionDeclaration function,
                                                final TBatchConfig batchConfig,
                                                final TBatchItem batchItem);
}
