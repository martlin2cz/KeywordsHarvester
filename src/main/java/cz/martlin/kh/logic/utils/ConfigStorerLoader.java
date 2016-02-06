package cz.martlin.kh.logic.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.KHMain;
import cz.martlin.kh.logic.Config;

/**
 * Saves {@link Config} instances int XML file and reads from file
 * {@link #CONFIG_FILE}. Uses {@link Properties}, so it can be simply changed to
 * .properties file.
 * 
 * @author martin
 * 
 */
public class ConfigStorerLoader {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final File CONFIG_FILE = new File("config.xml");

	private static final String COMMENT = KHMain.APP_NAME + " "
			+ KHMain.VERSION + " configuration file";

	/**
	 * Saves given config into file. Returns true if succeeds.
	 * 
	 * @param config
	 * @return
	 */
	public boolean save(Config config) {
		Properties props = toProperties(config);
		if (props == null) {
			return false;
		}

		return saveProperties(props);
	}

	/**
	 * Loads data into given config instance. Returns true if succeeds.
	 * 
	 * @param config
	 * @return
	 */
	public boolean load(Config config) {
		Properties props = loadProperties();
		if (props == null) {
			return false;
		}

		return fromProperties(props, config);

	}

	/**
	 * Saves properties into file and returns true if succeeds.
	 * 
	 * @param props
	 * @return
	 */
	public boolean saveProperties(Properties props) {
		OutputStream ous = null;
		try {
			ous = new FileOutputStream(CONFIG_FILE);

			props.storeToXML(ous, COMMENT);

			log.info("Config file saved into {}.", CONFIG_FILE);
		} catch (IOException e) {
			log.error("Problem during saving app configuration.", e);
			return false;
		} finally {
			IOUtils.closeQuietly(ous);
		}
		return true;
	}

	/**
	 * Loads properties from file and returns. Returns null if failed.
	 * 
	 * @return
	 */
	private Properties loadProperties() {
		InputStream ins = null;

		Properties props = new Properties();
		try {
			ins = new FileInputStream(CONFIG_FILE);
			props.loadFromXML(ins);
			log.info("Config have been loaded from {}", CONFIG_FILE);
			return props;
		} catch (Exception e) {
			log.error("Problem during loading app configuration.", e);
			return null;
		} finally {
			IOUtils.closeQuietly(ins);
		}
	}

	/**
	 * Creates new properties instance and puts given config data into it.
	 * 
	 * @param c
	 * @return
	 */
	private Properties toProperties(Config c) {
		Properties p = new Properties();

		// global
		p.put("WaitStep", //
				Long.toString(c.getWaitStep()));

		// Shuterstock
		p.put("Shuterstock.ClientId", //
				c.getSScliendID());
		p.put("Shuterstock.ClientSecret", //
				c.getSSclientSecret());

		// Subkeywording
		p.put("Subkeywording.SamplesCount",//
				Integer.toString(c.getSamplesCount()));
		p.put("Subkeyword.WaitBetweenServices",//
				Integer.toString(c.getSKWaitBetweenServices()));
		p.put("Subkeyword.WaitBetweenQueries",//
				Long.toString(c.getWaitBetweenSubkeywordQueries()));

		// Pickworkflow
		p.put("Picworkflow.QueryTimeout", //
				Long.toString(c.getPWQueryTimeout()));
		p.put("Picworkflow.BatchSize", //
				Integer.toString(c.getPWBatchSize()));
		p.put("Picworkflow.WaitBetweenQueries",//
				Long.toString(c.getWaitBetweenPicflowQueries()));
		p.put("Picworkflow.FailedFile", //
				c.getPwFailedFile().getPath());
		p.put("Picworkflow.FailedWait", //
				Integer.toString(c.getPwFailedWait()));

		// Export
		p.put("Export.BatchSize", //
				Integer.toString(c.getExExportBatchSize()));
		p.put("Export.WaitBetweenQueries", //
				Long.toString(c.getWaitBetweenExports()));
		p.put("Export.ExportFile", //
				c.getExportFile().getPath());

		// Harvester
		p.put("Harvester.ToPicworkflowQueueSize",//
				Integer.toString(c.getHWToPicworkflowQueueSize()));
		p.put("Harvester.ToExportQueueSize", //
				Integer.toString(c.getHWToExportQueueSize()));
		p.put("Harvester.QueuesDumpFile", //
				c.getQueuesDumpFile().getPath());

		// GUI
		p.put("GUI.FormUpdateInterval",//
				Long.toString(c.getFormUpdateInterval()));
		p.put("GUI.MemoryCleanInterval",//
				Long.toString(c.getMemoryCleanInterval()));

		return p;
	}

	/**
	 * Tries to get data from given properties into given config. Returns false
	 * if fails (description of error logs).
	 * 
	 * @param p
	 * @param c
	 * @return
	 */
	private boolean fromProperties(Properties p, Config c) {
		try {
			// global
			c.setWaitStep(Integer.parseInt(//
					(String) p.get("WaitStep")));

			// Shuterstock
			c.setSsClientid(//
			(String) p.get("Shuterstock.ClientId"));
			c.setSsClientSecret(//
			(String) p.get("Shuterstock.ClientSecret"));

			// Subkeywording
			c.setSamplesCount(Integer.parseInt(//
					(String) p.get("Subkeywording.SamplesCount")));
			c.setSKWaitBetweenServices(Integer.parseInt(//
					(String) p.get("Subkeyword.WaitBetweenServices")));
			c.setWaitBtwSubkeywordingQrs(Integer.parseInt(//
					(String) p.get("Subkeyword.WaitBetweenQueries")));

			// Pickworkflow
			c.setPwQueryTimeout(Integer.parseInt(//
					(String) p.get("Picworkflow.QueryTimeout")));

			c.setPwBatchSize(Integer.parseInt(//
					(String) p.get("Picworkflow.BatchSize")));
			c.setWaitBtwPicflowQrs(Integer.parseInt(//
					(String) p.get("Picworkflow.WaitBetweenQueries")));
			c.setPwFailedFile(new File(//
					(String) p.get("Picworkflow.FailedFile")));
			c.setPwFailedWait(Integer.parseInt(//
					(String) p.get("Picworkflow.FailedWait")));

			// Export
			c.setExportBatchSize(Integer.parseInt(//
					(String) p.get("Export.BatchSize")));
			c.setWaitBtwExports(Integer.parseInt(//
					(String) p.get("Export.WaitBetweenQueries")));

			c.setExportFile(new File(//
					(String) p.get("Export.ExportFile")));

			// Harvester
			c.setHwToPicworkflowQueueSize(Integer.parseInt(//
					(String) p.get("Harvester.ToPicworkflowQueueSize")));
			c.setExportQueueSize(Integer.parseInt(//
					(String) p.get("Harvester.ToExportQueueSize")));
			c.setQueuesDumpFile(new File(//
					(String) p.get("Harvester.QueuesDumpFile")));

			// GUI
			c.setFormUpdateInterval(Long.parseLong(//
					(String) p.get("GUI.FormUpdateInterval")));
			c.setMemoryCleanInterval(Long.parseLong(//
					(String) p.get("GUI.MemoryCleanInterval")));

		} catch (Exception e) {
			log.error("Error during parsing data from config file.", e);
			return false;
		}
		return true;
	}
}
