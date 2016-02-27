package cz.martlin.kh.logic.export;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Represents abstract exporter of keywords set. Expected implementation should
 * export into some "table" file with header line (can be used
 * {@link #HEADER_FIELDS}). Before and after use must be initialized by methods
 * {@link #initializeExporter()} and {@link #finishExporter()}. For concrete
 * subclasses are provided methods {@link #openFile()} and {@link #closeFile()}
 * to open and close file (they're not used in this class).
 * 
 * @author martin
 * 
 */
public abstract class AbstractExporter {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Array of keywords' header fields.
	 */
	protected final String[] HEADER_FIELDS = new String[] { "keyword", "count",
			"downloads", "downloads per file", "views", "views per file",
			"language", "rating" };

	protected final Config config;

	public AbstractExporter(Config config) {
		super();
		this.config = config;
	}

	/**
	 * Returns export file suffix.
	 * 
	 * @return
	 */
	public abstract String getSuffix();

	/**
	 * Returns description of export format, i.e.: "Java source file (*.java)"
	 * 
	 * @return
	 */
	public abstract String getFormatDescription();

	/**
	 * Initializes exporter.
	 * 
	 * @throws IOException
	 */
	public abstract void initializeExporter() throws IOException;

	/**
	 * Closes exporter.
	 * 
	 * @throws IOException
	 */
	public abstract void finishExporter() throws IOException;

	/**
	 * Opens file to write.
	 * 
	 * @throws IOException
	 */
	public abstract void openFile() throws IOException;

	/**
	 * Closes file.
	 * 
	 * @throws IOException
	 */
	public abstract void closeFile() throws IOException;

	/**
	 * Exports some header line or something like that.
	 * 
	 * @throws IOException
	 */
	public abstract void exportHeaderOrShit() throws IOException;

	/**
	 * Particullary (by concrete type of exporter) exports given keywords set.
	 * 
	 * @param keywords
	 * @throws IOException
	 */
	public abstract void export(Set<Keyword> keywords) throws IOException;

	/**
	 * Only exports given set of keywords using {@link #exportKeyword(Keyword)}.
	 * 
	 * @param keywords
	 * @throws IOException
	 */
	protected void exportKeywords(Set<Keyword> keywords) throws IOException {
		for (Keyword keyword : keywords) {
			exportKeyword(keyword);
		}
	}

	/**
	 * Only exports given keyword.
	 * 
	 * @param keyword
	 * @throws IOException
	 */
	protected abstract void exportKeyword(Keyword keyword) throws IOException;

	/**
	 * If file ({@link Config#getExportFile()}) exists, creates its backup.
	 * Returns true if succeeds, false if fails and null if no backup required.
	 * 
	 * @param file
	 * @return
	 */
	public Boolean tryBackupFile() {
		File file = config.getExExportFile();

		if (!file.exists()) {
			return null;
		}

		String backupPath = file.getAbsolutePath() + "_"
				+ new Date().toString() + "." + getSuffix();

		File backup = new File(backupPath);

		try {
			Files.copy(file.toPath(), backup.toPath());
		} catch (Exception e) {
			log.error("File " + file.getPath() + " backup failed", e);
			return false;
		}

		log.info("File {} have been backed up into file {}", file.getPath(),
				backup.getPath());

		return true;
	}

	/**
	 * If file ({@link Config#getExportFile()}) not existed writes some header (
	 * {@link #exportHeaderOrShit()}) in the file.
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean tryToWriteHeader() throws IOException {
		File file = config.getExExportFile();
		boolean exists = file.exists();

		if (!exists) {
			exportHeaderOrShit();
			return true;
		} else {
			return false;
		}

	}

	/**
	 * In given exporters find first, which matches suffix to given file's.
	 * 
	 * @param exporters
	 * @param file
	 * @return
	 */
	public static AbstractExporter getBySuffix(Set<AbstractExporter> exporters,
			File file) {

		String suffix = FilenameUtils.getExtension(file.getPath());

		for (AbstractExporter exporter : exporters) {
			if (exporter.getSuffix().equalsIgnoreCase(suffix)) {
				return exporter;
			}
		}

		return null;
	}

}