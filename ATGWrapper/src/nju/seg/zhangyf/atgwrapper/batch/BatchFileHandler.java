package nju.seg.zhangyf.atgwrapper.batch;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;

import com.google.common.util.concurrent.ListenableFuture;
import com.typesafe.config.Config;

import nju.seg.zhangyf.atgwrapper.config.ConfigTags;
import nju.seg.zhangyf.atgwrapper.config.batch.BatchConfigs;
import nju.seg.zhangyf.util.ResourceAndUiUtil;
import nju.seg.zhangyf.util.SwtUtil;

public class BatchFileHandler extends AbstractHandler {

  /* (non-Javadoc)
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final Optional<IFile> optionalSelectedFile = ResourceAndUiUtil.getFirstActiveSelectionAs(IFile.class);
    if (!optionalSelectedFile.isPresent()) {
      // handle not a `IFIle` is selected
      SwtUtil.createErrorMessageBox(ResourceAndUiUtil.getActiveShell().get(),
                                    "The selection is not a file.")
             .open();
      return null;
    }

    final IFile batchConfigFile = optionalSelectedFile.get();
    final Optional<Config> optionalRawBatchConfig = BatchConfigs.tryParseAndShowErrorIfFailed(batchConfigFile);
    if (!optionalRawBatchConfig.isPresent()) {
      return null;
    }
    final Config rawBatchConfig = optionalRawBatchConfig.get();

    if (rawBatchConfig.hasPath(ConfigTags.ATG_ACTION_PATH)) {
      // create a handler to process the batch file

      // get action from config file
      final String action = rawBatchConfig.getString(ConfigTags.ATG_ACTION_PATH);

      final BatchFileRunnerBase<?, ?, ?> runner;
      try {
        // create handler to process the batch file based on the action
        runner = BatchFileRunners.createBatchFileRunner(action);
      } catch (final Throwable ex) {
        // if we failed to create a runner to process the batch file, show the error
        SwtUtil.createErrorMessageBoxWithActiveShell(
                                                     "Can not create a runner for the batch file: " + batchConfigFile.getFullPath().toOSString()
                                                         + ",\nskip processing the file.")
               .open();
        return null;
      }

      // process the batch file
      @SuppressWarnings("unused") final ListenableFuture<?> processResult = runner.processBatchItemAsync(batchConfigFile);
    } else {
      // if we can not get the action, show the error
      SwtUtil.createErrorMessageBoxWithActiveShell(
                                                   "Can not get action from config file: " + batchConfigFile.getFullPath().toOSString()
                                                       + ",\nskip processing the file.")
             .open();

    }

    return null;
  }

  @Override
  public boolean isEnabled() {
    // return super.isEnabled();
    return true;
  }
}
