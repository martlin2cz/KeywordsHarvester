package cz.martlin.kh.logic.export;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;
import cz.martlin.kh.logic.harvest2.TreeHarvestProcessData;

/**
 * Does exporting stuff.
 * 
 * @author martin
 * 
 */
public class Exporter {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final AbstractExporter exporter;

	public Exporter(Config config, Set<AbstractExporter> exporters) {
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
	private AbstractExporter chooseExporter(Config config,
			Set<AbstractExporter> exporters) {

		AbstractExporter export = AbstractExporter.getBySuffix(exporters,
				config.getExExportFile());

		if (export == null) {
			log.error("Unkown exporter for exporter file {}",
					config.getExExportFile());
		} else {
			log.info("Exporter {} ready to exporter into file {}", export,
					config.getExExportFile());
		}

		return export;
	}

	/**
	 * Initializes currently choosen exporter (by constructor) with given data.
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void initializeExporter(TreeHarvestProcessData data)
			throws IOException {
		exporter.initializeExporter();

		if (exporter instanceof AppendExporter) {
			AppendExporter ae = (AppendExporter) exporter;
			ae.exportInitial(data.getDone());
		}
	}

	/**
	 * Exports data with initialized exporter (using
	 * {@link #initializeExporter(TreeHarvestProcessData)}).
	 * 
	 * @param data
	 * @param keywords
	 * @return
	 */
	public Set<Keyword> export(Set<Keyword> keywords,
			TreeHarvestProcessData data) {

		try {
			if (exporter instanceof AppendExporter) {
				return exportBatch((AppendExporter) exporter, keywords, data);
			} else {
				return exportAll((RewriteExporter) exporter, keywords, data);
			}
		} catch (IOException e) {
			log.error("An error occured during exporting", e);
			return null;
		}
	}

	/**
	 * Exports batch of keywords.
	 * 
	 * @param data
	 * @param exporter
	 * @return
	 * 
	 * @throws IOException
	 */
	private Set<Keyword> exportBatch(AppendExporter appendExporter,
			Set<Keyword> batch, TreeHarvestProcessData data) throws IOException {

		log.info("Exporting batch of {} keywords: {}", batch.size(), batch);

		appendExporter.export(batch);

		log.info(
				"Exporting of {} keywords done, now is completelly done {} keywords",
				batch.size(), data.getDoneCount());

		return batch;
	}

	/**
	 * (Re)exports all keywords.
	 * 
	 * @param exporter
	 * @param data
	 * @return
	 * 
	 * @throws IOException
	 */
	private Set<Keyword> exportAll(RewriteExporter rewriteExport,
			Set<Keyword> newToExport, TreeHarvestProcessData data)
			throws IOException {

		Set<Keyword> yetExported = data.getDone();
		Set<Keyword> toExport = datasetToExport(yetExported, newToExport);
		log.info("Exporting all {} keywords.", toExport.size());

		rewriteExport.export(toExport);
		data.setToDone(newToExport);

		log.info(
				"Exporting of all {} keywords done, now is completelly done {} keywords",
				toExport.size(), data.getDoneCount());

		return newToExport;
	}

	/**
	 * Creates set of all keywords to export.
	 * 
	 * @param yetExported
	 * @param newToExport
	 * @return
	 */
	private Set<Keyword> datasetToExport(Set<Keyword> yetExported,
			Set<Keyword> newToExport) {
		int allSize = yetExported.size() + newToExport.size();
		Set<Keyword> all = new LinkedHashSet<>(allSize);

		all.addAll(yetExported);
		all.addAll(newToExport);

		return all;
	}
}
