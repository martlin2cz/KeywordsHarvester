package cz.martlin.kh;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.gui.JMainFrame;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.subkeyw.AbstractServiceWrapper;
import cz.martlin.kh.logic.utils.ConfigStorerLoader;

/**
 * Main class for applications. Implements {@link #main(String[])} method,
 * contains {@link #APP_NAME}, {@link #VERSION} and {@link #AUTHOR}, services
 * list ({@link #getServices(Config)}, see {@link AbstractServiceWrapper}) and
 * shutdown hook.
 * 
 * @author martin
 * 
 */
public class KHMain {
	// private static final Logger log = LoggerFactory.getLogger("Main");

	public final static String APP_NAME = "Keywords Harvester";
	public final static String VERSION = "1.8";
	public final static String AUTHOR = "m@rtlin";

	/**
	 * Returns string description about app, version and author.
	 * 
	 * @return
	 */
	public static String getAbout() {
		return APP_NAME + " " + VERSION + " by " + AUTHOR;
	}

	public static void main(String[] args) {
		if (args.length == 1 && //
				(args[0].equals("-h") || args[0].equals("--help")
						|| args[0].equals("-v") || args[0].equals("--version"))) {
			System.out.println(getAbout());
			return;
		}

		Config config = Config.loadOrDefault();
		JMainFrame frame = new JMainFrame(config);

		registerExcetptionHandlers();

		Runtime.getRuntime().addShutdownHook(
				new ShutdownHookThread(frame, config));
		frame.setVisible(true);
	}

	/**
	 * Registers exception handlers. To current thread, via system property and
	 * via swing events queue.
	 */
	private static void registerExcetptionHandlers() {
		final LoggingUncaughtExceptionHandler handler = //
		new LoggingUncaughtExceptionHandler();

		Thread.setDefaultUncaughtExceptionHandler(handler);
		System.setProperty("sun.awt.exception.handler", handler.getClass()
				.getName());

		// same as previous
		// try {
		// SwingUtilities.invokeAndWait(new Runnable() {
		// public void run() {
		// Thread.currentThread().setUncaughtExceptionHandler(handler);
		// }
		// });
		// } catch (Exception e) {
		// handler.uncaughtException(Thread.currentThread(), e);
		// }

	}

	/***
	 * Implements finishing of work. Stops harvester in main frame and saves
	 * (modified) config into file.
	 * 
	 * @author martin
	 * 
	 */
	public static class ShutdownHookThread extends Thread {

		private final JMainFrame frame;
		private final Config config;

		public ShutdownHookThread(JMainFrame frame, Config config) {
			this.frame = frame;
			this.config = config;
			setUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		}

		@Override
		public synchronized void start() {
			if (frame != null) {
				frame.stop();
			}

			if (config != null) {
				new ConfigStorerLoader().save(config);
			}
		}
	}

	/**
	 * Exception handler which simply logs via logger.
	 * 
	 * @author martin
	 * 
	 */
	public static class LoggingUncaughtExceptionHandler implements
			UncaughtExceptionHandler {

		private static final Logger log = LoggerFactory
				.getLogger(LoggingUncaughtExceptionHandler.class);

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			log.error("Unhandled exception " + e + " in thread " + t, e);
		}

	}
}
