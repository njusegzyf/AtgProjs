package nju.seg.zhangyf.atgwrapper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public final class AtgWrapperActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "nju.seg.zhangyf.AtgWrapper"; //$NON-NLS-1$

	// The shared instance
	private static AtgWrapperActivator plugin;
	
	public AtgWrapperActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		AtgWrapperActivator.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		AtgWrapperActivator.plugin  = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AtgWrapperActivator getDefault() {
		return AtgWrapperActivator.plugin;
	}

	/**
	 * Logs the specified status with this plug-in's log.
	 *
	 * @param status
	 *        status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Logs an internal error with the specified {@code Throwable}.
	 *
	 * @param t
	 *        the {@code Throwable} to be logged
	 */
	public static void log(Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "Internal Error", t)); //$NON-NLS-1$
	}

	/**
	 * Logs an internal error with the specified message.
	 *
	 * @param message
	 *        the error message to log
	 */
	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, null));
	}

	/**
	 * Logs an internal error with the specified message and {@code Throwable}.
	 *
	 * @param message
	 *        the error message to log
	 * @param t
	 *        the {@code Throwable} to be logged
	 *
	 * @since 2.1
	 */
	public static void log(String message, Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, t));
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}

