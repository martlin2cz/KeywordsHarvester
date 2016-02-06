package cz.martlin.kh;

import java.util.LinkedHashSet;
import java.util.Set;

import cz.martlin.kh.gui.JMainFrame;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.export.AbstractExporter;
import cz.martlin.kh.logic.export.CSVExporter;
import cz.martlin.kh.logic.export.XLSXExporter;
import cz.martlin.kh.logic.subkeyw.AbstractServiceWrapper;
import cz.martlin.kh.logic.subkeyw.IStockphotoWrapper;
import cz.martlin.kh.logic.subkeyw.ShutterstockWrapper;
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
	public final static String VERSION = "1.5";
	public final static String AUTHOR = "m@rtlin";

	/**
	 * Returns string description about app, version and author.
	 * 
	 * @return
	 */
	public static String getAbout() {
		return APP_NAME + " " + VERSION + " by " + AUTHOR;
	}

	/**
	 * Returns set of currently use-to services. See
	 * {@link AbstractServiceWrapper} for more info.
	 * 
	 * @param config
	 * @return
	 */
	public static Set<AbstractServiceWrapper> getServices(Config config) {
		Set<AbstractServiceWrapper> result = new LinkedHashSet<>();

		result.add(new IStockphotoWrapper(config));
		result.add(new ShutterstockWrapper(config));

		return result;
	}

	public static Set<AbstractExporter> getExporters(Config config) {
		Set<AbstractExporter> result = new LinkedHashSet<>();

		result.add(new XLSXExporter(config));
		result.add(new CSVExporter(config));

		return result;
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

		Runtime.getRuntime().addShutdownHook(
				new ShutdownHookThread(frame, config));
		frame.setVisible(true);
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
}
