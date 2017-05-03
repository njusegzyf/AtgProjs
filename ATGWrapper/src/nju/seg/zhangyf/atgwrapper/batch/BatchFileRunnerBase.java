package nju.seg.zhangyf.atgwrapper.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.typesafe.config.Config;

import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.ATG;
import nju.seg.zhangyf.atgwrapper.AtgWrapperPluginSettings;
import nju.seg.zhangyf.atgwrapper.config.AtgConfig;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchConfigBase;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchConfigs;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchItemConfigBase;
import nju.seg.zhangyf.atgwrapper.outcome.BatchFileOutcome;
import nju.seg.zhangyf.atgwrapper.outcome.TestOutcome;
import nju.seg.zhangyf.util.ResourceAndUiUtil;
import nju.seg.zhangyf.util.SwtUtil;
import nju.seg.zhangyf.util.Util;

/**
 * Note: This class is thread hostile, which means it can only be executed in a single thread environment.
 * 
 * @author Zhang Yifan
 */
public abstract class BatchFileRunnerBase<TBatchItem extends BatchItemConfigBase, TBatchConfig extends BatchConfigBase<TBatchItem>, TTestOutcome extends TestOutcome> {

  /**
   * Processes the batch file asynchronously.
   */
  public final ListenableFuture<List<TaskOutcome<TTestOutcome>>> processBatchItemAsync(final IFile configFile) {
    Preconditions.checkNotNull(configFile);

    // check that there is no processing running
    if (!BatchFileRunnerBase.isProcessingBatchFile.compareAndSet(false, true)) {
      Futures.immediateFailedFuture(new IllegalStateException("Some processing is already running."));
    }

    if (!(configFile.isAccessible() && configFile.getName().endsWith(".conf"))) {
      // handle if not a config file is passed
      SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                    configFile.getFullPath().toString() + " is not an accessible config file.")
             .open();

      // Note: Instead of throw, this async method should return a failed future and reset the processing flag.
      return BatchFileRunnerBase.createFailedFutureAndResetProcessingFlag();
    }

    // read the config file
    final Optional<TBatchConfig> optionalBatchConfig = BatchConfigs.tryParseAndShowErrorIfFailed(configFile,
                                                                                                 this::parseConfig);
    if (!optionalBatchConfig.isPresent()) {
      return BatchFileRunnerBase.createFailedFutureAndResetProcessingFlag();
    }
    final TBatchConfig batchConfig = optionalBatchConfig.get();

    AtgWrapperPluginSettings.doIfDebug(() -> BatchFileRunnerBase.printProcessConfigFile(configFile.getName()));

    // get the project the config file belongs to, which will be used as the fallback (default) project
    final IProject fallbackProject = configFile.getProject();

    for (final String library : batchConfig.libraries) {
      if (!BatchFileRunnerBase.loadLibrary(library)) { // if we failed to load library
        return BatchFileRunnerBase.createFailedFutureAndResetProcessingFlag();
      }
    }

    // create an executor to run tests
    final ListeningExecutorService executor = MoreExecutors.listeningDecorator(batchConfig.createExecutorService());
    // collect the tasks that run works
    final ArrayList<ListenableFuture<TTestOutcome>> workTaskList = Lists.newArrayList();

    // create an executor that run tasks to handle work timeout and collect results
    final ListeningExecutorService collectExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
    final Optional<Duration> singleFunctionTimeout = batchConfig.executorConfig.flatMap(e -> e.singleFunctionTimeout);
    final ArrayList<ListenableFuture<TaskOutcome<TTestOutcome>>> collectTaskOutcomeList = Lists.newArrayList();

    // enable atg settings if present
    batchConfig.atgConfig.ifPresent(atgConfig -> {
      AtgConfig.enableAtgConfig(atgConfig);
      if (atgConfig.isCopyConfigToResultFolder) {
        final File configFileAsSource = ResourceAndUiUtil.eclipseFileToJavaFile(configFile);
        final ByteSource source = Files.asByteSource(configFileAsSource);
        final ByteSink sink = Files.asByteSink(Paths.get(ATG.resultFolder).resolve(configFile.getName()).toFile());
        try {
          // copy config file to result folder
          source.copyTo(sink);
        } catch (final IOException ex) {
          SwtUtil.createErrorMessageBoxWithActiveShell(
                                                       "Failed to copy config file from: " + configFileAsSource.toString()
                                                           + "\nto folder: " + ATG.resultFolder
                                                           + "\nwith exception: " + ex.toString())
                 .open();
        }
      }
    });

    for (final TBatchItem batchItem : batchConfig.getBatchItems()) {
      // handle a batch item
      try {
        final IProject project = BatchFileRunnerBase.getProjectWithFallback(batchItem.project, fallbackProject);

        // get the target batch file (cpp file, a translation unit)
        final IFile batchFile = project.getFile(batchItem.batchFile);
        final ITranslationUnit tu = CoreModelUtil.findTranslationUnit(batchFile);
        if (tu == null) {
          // handle if the target batch file is not a translation unit
          final boolean stopFlag = BatchFileRunnerBase.handleBatchItemError(batchItem.batchFile,
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

          private boolean hasResetAtgConfig = false;

          @Override
          public boolean visit(final ICElement element) throws CoreException {
            final Optional<IFunctionDeclaration> optioanlFunction = Util.asOptional(element, IFunctionDeclaration.class);
            // handle `IFunctionDeclaration`
            if (optioanlFunction.isPresent()) {
              final IFunctionDeclaration function = optioanlFunction.get();
              if (predicate.test(function)) { // if we need to process this function
                // for debug, print the function
                AtgWrapperPluginSettings.doIfDebug(() -> BatchFileRunnerBase.printProcessFunction(function));

                // first check the test config is OK
                final List<String> testErrors = BatchFileRunnerBase.this.checkTestConfig(function, batchConfig, batchItem);
                if (!testErrors.isEmpty()) {
                  // if there are some errors, show the errors and stop processing the function
                  final StringBuilder errorMessageBuilder = new StringBuilder();
                  Util.appendAllWithNewLine(errorMessageBuilder,
                                            "There are some errors in the batch item: ", batchItem.toString());
                  Joiner.on(Util.LINE_SEPATATOR).appendTo(errorMessageBuilder, testErrors);

                  SwtUtil.createErrorMessageBoxWithActiveShell(errorMessageBuilder.toString())
                         .open();
                  return false;
                }

                // used to record the `workTask`
                final SettableFuture<ListenableFuture<TTestOutcome>> workTaskFuture = SettableFuture.create();

                // submit a task to run the work, this task returns a SingleTestOutcome and may be canceled
                final ListenableFuture<TTestOutcome> workTask = executor.submit(() -> {
                  // Since we allow each batch item to customize config, we should reset the atg config to the default,
                  // and then apply the custom atg config when the first work in a batch item is going to be executed.
                  if (!this.hasResetAtgConfig) {
                    // reset the default config defined in the batch file
                    batchConfig.atgConfig.ifPresent(AtgConfig::enableAtgConfig);
                    // enable the custom config defined in the batch item
                    batchItem.atgConfig.ifPresent(AtgConfig::enableAtgConfig);
                    this.hasResetAtgConfig = true;
                  }

                  // In the work task, we first submit another task which handles timeout and collects the work task's result.
                  // Note: This task should be submitted in the work task instead of after submitting the work task,
                  // which enables that the timing begins just before the work is going to run.
                  final ListenableFuture<TaskOutcome<TTestOutcome>> resultTask = collectExecutor.submit(() -> {
                    // get the `workTask`
                    final ListenableFuture<TTestOutcome> workTaskRef = workTaskFuture.get();

                    try {
                      final TTestOutcome workTaskRes;
                      if (singleFunctionTimeout.isPresent()) { // try to get the result in timeout if defined
                        // the actual timeout is timeout for single function multiply repeat count
                        final Duration singleTimeout = singleFunctionTimeout.get();
                        final Duration totalTimeout = singleTimeout.multipliedBy(TestBuilder.repetitionNum);

                        // FIXME use scheduled executor instead of busy waiting
                        workTaskRes = workTaskRef.get(totalTimeout.getSeconds(), TimeUnit.SECONDS);
                      } else { // wait without timeout
                        workTaskRes = workTaskRef.get();
                      }
                      return TaskOutcome.create(function.getSignature(), workTaskRes);
                    } catch (final TimeoutException e) {
                      // handle timeout, cancel the work
                      // Note: Calling `cancel` do not force the executor to stop the work,
                      // it just call `interrupt` on the thread running the work and the work itself is required to check it.
                      // The work may still be running even `cancel` returns `true`.
                      final boolean cancelResult = workTaskRef.cancel(true);
                      AtgWrapperPluginSettings.doIfDebug(() -> {
                        System.out.println("Cancel processing function: " + function.getSignature() + " for timeout, result: " + cancelResult + ".\n");
                      });
                      return TaskOutcome.create(function.getSignature());
                    } catch (final CancellationException | java.util.concurrent.ExecutionException e) {
                      // TODO handle for execution exception
                      return TaskOutcome.create(function.getSignature());
                    }
                  });
                  collectTaskOutcomeList.add(resultTask);

                  return BatchFileRunnerBase.this.runTest(function, batchConfig, batchItem);

                });
                workTaskFuture.set(workTask);
                workTaskList.add(workTask);
              }

              // not visit children of a function
              return false;
            }

            // handle other sub types of `ICElement`
            if (element instanceof IType) { // not visit children of an `IType`, as we do not handle member functions
              return false;
            } else { // for other sub types of `ICElement`, visit their children
              return true;
            }
          }
        });
      } catch (Throwable e) {
        AtgWrapperPluginSettings.doIfDebug(() -> e.printStackTrace());

        // handle exception happened in processing a batch item
        if (BatchFileRunnerBase.handleBatchItemError(batchItem.batchFile, e)) {
          break;
        }
      }
    }

    // submit a task to print results when all the result tasks are done
    final ListenableFuture<List<TaskOutcome<TTestOutcome>>> resultListFuture = Futures.whenAllComplete(workTaskList) // wait all work done
                                                                                      .callAsync(() -> { // collect results
                                                                                        return Futures.allAsList(collectTaskOutcomeList);
                                                                                      }, collectExecutor);

    // Instead of use `Futures.addCallback` to attach the result processing,
    // we should use `Futures.transform` to append the work and returns the transformed future,
    // which ensures that the returned future is done until the result processing is done.
    return Futures.transform(resultListFuture, result -> {
      final List<TaskOutcome<TTestOutcome>> succeedResults = result.stream()
                                                                   .filter(v -> v.isTestSucceed())
                                                                   .collect(Collectors.toList());
      final List<TaskOutcome<TTestOutcome>> failedResults = result.stream()
                                                                  .filter(v -> !v.isTestSucceed())
                                                                  .collect(Collectors.toList());

      final BatchFileOutcome<TTestOutcome> batchFileOutcome =
          new BatchFileOutcome<TTestOutcome>(succeedResults, failedResults);
      // if we are debug, print the overview to `System.out`
      AtgWrapperPluginSettings.doIfDebug(() -> {
        batchFileOutcome.appendOverview(System.out);
      });

      BatchFileRunnerBase.this.processBatchResult(batchConfig, batchFileOutcome);

      // set `isProcessingBatchFile` to false, indicating that the processing is done
      final boolean setIsProcessingBatchFileResult = BatchFileRunnerBase.isProcessingBatchFile.compareAndSet(true, false);
      assert setIsProcessingBatchFileResult;

      return result;
    });
  }

  // Sub classes should implement the following methods.

  protected abstract TBatchConfig parseConfig(final Config rawConfig);// throws Exception;

  protected abstract Predicate<IFunctionDeclaration> getFunctionFilter(final TBatchItem batchItem);

  protected abstract TTestOutcome runTest(final IFunctionDeclaration function,
                                          final TBatchConfig batchConfig,
                                          final TBatchItem batchItem);

  // Sub classes can override the following methods.

  /**
   * Checks the test config.
   * 
   * @param function
   * @param batchConfig
   * @param batchItem
   * @return A list of error messages.
   */
  @SuppressWarnings("unused") // @OverridingMethodsMustInvokeSuper
  protected List<String> checkTestConfig(final IFunctionDeclaration function,
                                         final TBatchConfig batchConfig,
                                         final TBatchItem batchItem) {
    assert function != null && batchConfig != null && batchItem != null;

    return Collections.emptyList();
  }

  private void processBatchResult(final TBatchConfig batchConfig, final BatchFileOutcome<TTestOutcome> batchFileOutcome) {
    assert batchConfig != null;
    assert batchFileOutcome != null;

    final StringBuilder result = new StringBuilder();
    try {
      batchFileOutcome.appendOverview(result);
      Util.appendNewLine(result);
      batchFileOutcome.appendSucceedTaskOutcomes(result);
      this.processBatchResultExtra(batchConfig, batchFileOutcome, result);
    } catch (final IOException ignored) {}

    final File resultFile = Paths.get(ATG.resultFolder).resolve("batchResult.txt").toFile();
    try {
      // write result to file
      Files.asCharSink(resultFile, Charsets.US_ASCII).write(result);
    } catch (final IOException ex) {
      SwtUtil.createErrorMessageBoxWithActiveShell(
                                                   "Failed to write batch result to: " + resultFile.toString()
                                                       + "\nwith exception: " + ex.toString())
             .open();
    }

    final File resultBinaryFile = Paths.get(ATG.resultFolder).resolve("batchResultBinary").toFile();
    try (final FileOutputStream fo = new FileOutputStream(resultBinaryFile);
        final ObjectOutputStream oo = new ObjectOutputStream(fo)) {
      // write result to file
      oo.writeObject(batchFileOutcome);
    } catch (final IOException ex) {
      SwtUtil.createErrorMessageBoxWithActiveShell(
                                                   "Failed to write binary batch otucome to: " + resultBinaryFile.toString()
                                                       + "\nwith exception: " + ex.toString())
             .open();
    }
  }

  /**
   * Sub classes can override this method to perform extra work on the batch results.
   */
  @SuppressWarnings("unused")
  protected void processBatchResultExtra(final TBatchConfig batchConfig,
                                         final BatchFileOutcome<TTestOutcome> batchFileOutcome,
                                         final StringBuilder result) {}

  // Static help methods.

  /**
   * Handles the error occurred in processing a batch item, returns whether it should stop processing remaining batch items.
   */
  protected static boolean handleBatchItemError(final String batchItemName, final String errorMessage) {
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
  protected static boolean handleBatchItemError(final String batchItemName, final Throwable exception) {
    assert (batchItemName != null && exception != null);

    return BatchFileRunnerBase.handleBatchItemError(batchItemName, exception.getMessage());
  }

  private static void printProcessConfigFile(final String configFileName) {
    assert (configFileName != null);

    System.out.println();
    System.out.println("About to process config file: " + configFileName);
    System.out.println();
  }

  private static void printProcessFunction(final IFunctionDeclaration function) throws CModelException {
    assert (function != null);

    BatchFileRunnerBase.printProcessFunctionBlank();
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

    if (BatchFileRunnerBase.loadedLibraries.contains(library)) {
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
      BatchFileRunnerBase.loadedLibraries.add(library);

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

  /**
   * Represents the outcome of a single test.
   * 
   * @author Zhang Yifan
   * @param <TSingleTestOutcome>
   */
  public static class TaskOutcome<TSingleTestOutcome extends TestOutcome> implements Serializable {
    public final String testFunctionSignuature;
    
    /** 
     * The type of this filed is changed from Java {@link Optional} to Guava {@link com.google.common.base.Optional},
     * since Java {@code Optional} is not serializable.
     */
    public final com.google.common.base.Optional<TSingleTestOutcome> optioanlTestOutcome;

    public boolean isTestSucceed() {
      return this.optioanlTestOutcome.isPresent();
    }

    private TaskOutcome(final String testFunctionSignuature, final Optional<TSingleTestOutcome> optioanlTestOutcome) {
      assert testFunctionSignuature != null;
      assert optioanlTestOutcome != null;

      this.testFunctionSignuature = testFunctionSignuature;
      this.optioanlTestOutcome = com.google.common.base.Optional.fromJavaUtil(optioanlTestOutcome);
    }

    private static <T extends TestOutcome> TaskOutcome<T> create(final String testFunctionSignuature) {
      assert !Strings.isNullOrEmpty(testFunctionSignuature);

      return new TaskOutcome<>(testFunctionSignuature, Optional.empty());
    }

    public static <T extends TestOutcome> TaskOutcome<T> create(final String testFunctionSignuature, final T result) {
      Preconditions.checkArgument(!Strings.isNullOrEmpty(testFunctionSignuature));

      return new TaskOutcome<>(testFunctionSignuature, Optional.of(result));
    }
    
    private static final long serialVersionUID = 1L;
  }

  /**
   * Records whether there is some test running.
   * <p>
   * Note: Since the underlying ATG tool can not run tests in parallel, we use this static field to stop running tests in parallel.
   */
  private static final AtomicBoolean isProcessingBatchFile = new AtomicBoolean(false);

  private static <T> ListenableFuture<T> createFailedFutureAndResetProcessingFlag(final Supplier<Exception> exceptionSupplier) {
    assert exceptionSupplier != null;

    BatchFileRunnerBase.isProcessingBatchFile.set(false);
    return Futures.immediateFailedFuture(exceptionSupplier.get());
  }

  private static <T> ListenableFuture<T> createFailedFutureAndResetProcessingFlag() {
    return BatchFileRunnerBase.createFailedFutureAndResetProcessingFlag(IllegalArgumentException::new);
  }
}
