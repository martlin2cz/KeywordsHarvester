package cz.martlin.kh.logic.utils;

/**
 * Does nothing. Config in files support removed.
 * 
 * @author martin
 * 
 */
@Deprecated
public class ConfigStorerLoader {

	// private final Logger log = LoggerFactory.getLogger(this.getClass());
	//
	// private static final File CONFIG_FILE = new File("config.xml");
	//
	// private final JaxonConverter jaxon;
	//
	// public ConfigStorerLoader() {
	// cz.martlin.jaxon.config.Config config = new
	// cz.martlin.jaxon.config.Config();
	// this.jaxon = new JaxonConverter(config);
	// }
	//
	// /**
	// * Saves given config into file. Returns true if succeeds.
	// *
	// * @param config
	// * @return
	// */
	// public boolean save(Config config) {
	// try {
	// jaxon.objectToFile(config, CONFIG_FILE);
	// return true;
	// } catch (JaxonException e) {
	// log.error("Cannot save configuration", e);
	// return false;
	// }
	// }
	//
	// /**
	// * Loads data into given config instance. Returns true if succeeds.
	// *
	// * @param config
	// * @return
	// */
	// public boolean load(Config config) {
	// try {
	// Config newConfig = (Config) jaxon.objectFromFile(CONFIG_FILE);
	// config.setTo(newConfig);
	// return true;
	// } catch (JaxonException e) {
	// log.error("Cannot load config", e);
	// return false;
	// }
	// }

}
