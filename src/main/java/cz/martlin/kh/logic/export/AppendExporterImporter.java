package cz.martlin.kh.logic.export;

import java.io.IOException;
import java.util.Set;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Extends abstract exporter as appending exporter. In export initialization (
 * {@link #initializeExporterToWrite()}) opens file ({@link #openFileToWrite()}) (and exports
 * header ({@link #exportHeaderOrShit()})), on each export ({@link #export(Set)}
 * ) exports keywords and flushes added keywords to disk ({@link #flush()}).
 * File is closed ({@link #closeFileToWrite()}) in {@link #finishExporterToWrite()}. To put
 * previously created data (but not in exported file) is designed method
 * {@link #exportInitial(Set)}.
 * 
 * @author martin
 * 
 */
public abstract class AppendExporterImporter extends AbstractEI {

	public AppendExporterImporter(Config config) {
		super(config);
	}

	@Override
	public void initializeExporterToWrite() throws IOException {
		tryBackupFile();

		openFileToWrite();
		tryToWriteHeader();

		log.info("Will appendly export {} to {}.", getSuffix(), config
				.getExExportFile().getAbsoluteFile());
	}

	@Override
	public void finishExporterToWrite() throws IOException {
		closeFileToWrite();

		log.info("Appending exporter closed.");
	}

	@Override
	public void export(Set<Keyword> keywords) throws IOException {
		exportKeywords(keywords);
		flush();
	}

	/**
	 * Puts keywords which are initially in data, but not in file.
	 * 
	 * @param initialKeywords
	 * @throws IOException
	 */
	public void exportInitial(Set<Keyword> initialKeywords) throws IOException {
		exportKeywords(initialKeywords);
		flush();
	}

	/**
	 * Flushes written keywords to disk.
	 * 
	 * @throws IOException
	 */
	protected abstract void flush() throws IOException;

}
