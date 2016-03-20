package cz.martlin.kh.logic.export;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;
import cz.martlin.kh.logic.harvest3.TreeHarvestProcessData;

/**
 * Does exporting stuff.
 * 
 * @author martin
 * 
 */
public class Exporter {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final AbstractExporterImporter exporter;

	public Exporter(Config config, Set<AbstractExporterImporter> exporters) {
		super();
		this.exporter = chooseExporter(config, exporters);
	}

	/**
	 * For given config and set of exporters chooses exporter which matches
	 * config's export file ( {@link Config#getExportFile()})'s extension.
	 * Returns null if no such exporter is avaible.
	 * 
	 * @param config
	 * @param exporters
	 * @return
	 */
	private AbstractExporterImporter chooseExporter(Config config, Set<AbstractExporterImporter> exporters) {

		AbstractExporterImporter export = AbstractExporterImporter.getBySuffix(exporters, config.getExExportFile());

		if (export == null) {
			log.error("Unkown exporter for exporter file {}", config.getExExportFile());
		} else {
			log.info("Exporter {} ready to exporter into file {}", export, config.getExExportFile());
		}

		return export;
	}

	/**
	 * Initializes currently choosen exporter.
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void initializeExporter() throws IOException {
		exporter.initializeExporterToWrite();
	}

	/**
	 * Exports data with initialized exporter (using
	 * {@link #initializeExporter()}).
	 * 
	 * @param data
	 * @param keywords
	 * @return
	 */
	public Set<Keyword> export(Set<Keyword> keywords, TreeHarvestProcessData data) {

		try {
			exporter.export(keywords);

			return keywords;
		} catch (IOException e) {
			log.error("An error occured during exporting", e);
			return null;
		}

	}

}
