package cz.martlin.kh;

import java.util.LinkedHashSet;
import java.util.Set;

import cz.martlin.kh.gui.JMainFrame;
import cz.martlin.kh.gui.MainFrameHarvestListener;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.export.AbstractExporter;
import cz.martlin.kh.logic.export.CSVExporter;
import cz.martlin.kh.logic.export.Exporter;
import cz.martlin.kh.logic.export.XLSXExporter;
import cz.martlin.kh.logic.harvest2.TreeRelKeywsHarvest;
import cz.martlin.kh.logic.picwf.Picworkflower;
import cz.martlin.kh.logic.subkeyw.AbstractServiceWrapper;
import cz.martlin.kh.logic.subkeyw.IStockphotoWrapper;
import cz.martlin.kh.logic.subkeyw.ShutterstockWrapper;
import cz.martlin.kh.logic.subkeyw.Subkeyworder;

/**
 * Implements simplified access to various class's instances ( {@link Config},
 * {@link TreeRelKeywsHarvest}, {@link Subkeyworder}, {@link Picworkflower},
 * {@link Exporter}) and lists subkeywording's services wrappers and exporting
 * impls. The class is static.
 * 
 * @author martin
 * 
 */
public class StuffProvider {
	private static Config config;

	private StuffProvider() {
	}

	/**
	 * If not yet - tries to load config and returns. If load fails returns
	 * default config.
	 * 
	 * @return
	 */
	public static Config getConfig() {
		if (config == null) {
			config = Config.loadOrDefault();
		}

		return config;
	}

	/**
	 * Creates keywords harvester with frame listener.
	 * 
	 * @param config
	 * @param frame
	 * @return
	 */
	public static TreeRelKeywsHarvest createHarvester(Config config,
			JMainFrame frame) {
		MainFrameHarvestListener listener = new MainFrameHarvestListener(frame);
		return new TreeRelKeywsHarvest(config, listener);
	}

	/**
	 * Creates subkeyworder.
	 * 
	 * @param config
	 * @return
	 */
	public static Subkeyworder getSubkeyworder(Config config) {
		return new Subkeyworder(config, getServices(config));
	}

	/**
	 * Creates picworkflower.
	 * 
	 * @param config
	 * @return
	 */
	public static Picworkflower getPicworkflower(Config config) {
		return new Picworkflower(config);
	}

	/**
	 * Creates exporter.
	 * 
	 * @param config
	 * @return
	 */
	public static Exporter getExporter(Config config) {
		return new Exporter(config, getExporters(config));
	}

	/**
	 * Creates and returns list of subkeywording services.
	 * 
	 * @return
	 */
	public static Set<AbstractServiceWrapper> getServices(Config config) {

		Set<AbstractServiceWrapper> result = new LinkedHashSet<>();
		initializeServices(config, result);

		return result;
	}

	/**
	 * Creates and returns list of exporters.
	 * 
	 * @return
	 */
	public static Set<AbstractExporter> getExporters(Config config) {

		Set<AbstractExporter> result = new LinkedHashSet<>();
		initializeExporters(config, result);

		return result;
	}

	/**
	 * Initializes subkeywording services into given set.
	 * 
	 * @param config
	 * @param services
	 */
	private static void initializeServices(Config config,
			Set<AbstractServiceWrapper> services) {

		services.add(new IStockphotoWrapper(config));
		services.add(new ShutterstockWrapper(config));
	}

	/**
	 * Initializes exporters into given set.
	 * 
	 * @param config
	 * @param exporters
	 */
	private static void initializeExporters(Config config,
			Set<AbstractExporter> exporters) {

		exporters.add(new XLSXExporter(config));
		exporters.add(new CSVExporter(config));
	}
}
