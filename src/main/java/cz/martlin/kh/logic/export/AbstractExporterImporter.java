package cz.martlin.kh.logic.export;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Represents abstract exporter and importer of keywords set.
 * 
 * TODO doc
 * 
 * @author martin
 * 
 */
public abstract class AbstractExporterImporter {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Array of keywords' header fields.
	 */
	protected final static String[] HEADER_FIELDS = new String[] { //
			"keyword", "count", "downloads", "downloads per file", "views", "views per file", "language", "rating" };

	protected final Config config;

	public AbstractExporterImporter(Config config) {
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
	 * Initializes exporter (opens file). If file not yet existed, exports
	 * headers (by calling {@link #exportHeaderOrShit()}).
	 * 
	 * @return true if file yet existed and was not empty
	 * @throws IOException
	 * 
	 */
	public boolean initializeExporterToWrite() throws IOException {
		boolean existed = openFileToWrite();

		if (!existed) {
			exportHeaderOrShit();
		}

		return existed;
	}

	/**
	 * Closes exporter (closes file).
	 * 
	 * @throws IOException
	 */
	public void finishExporterToWrite() throws IOException {
		closeFileToWrite();
	}

	/**
	 * Opens file to write. Should (just for case) create the file, but not
	 * really.
	 * 
	 * @return true if file exists (or existed if it have been created during
	 *         this method) and contains yet some keywords (ok, just whether
	 *         contains something)
	 * 
	 * @throws IOException
	 */
	public abstract boolean openFileToWrite() throws IOException;

	/**
	 * Closes file.
	 * 
	 * @throws IOException
	 */
	public abstract void closeFileToWrite() throws IOException;

	/**
	 * Exports some header line or something like that.
	 * 
	 * @throws IOException
	 */
	public abstract void exportHeaderOrShit() throws IOException;

	/**
	 * Exports given keywords and flushes changes.
	 * 
	 * @param keywords
	 * @throws IOException
	 */
	public void export(Set<Keyword> keywords) throws IOException {
		beforeExport();
		exportKeywords(keywords);
		afterExport();
	}

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
	 * Exports given keyword. Nothing more.
	 * 
	 * @param keyword
	 * @throws IOException
	 */
	protected abstract void exportKeyword(Keyword keyword) throws IOException;

	/**
	 * Does something before the set of keywords is exported (i.e. loads
	 * somethig from exported file)
	 */
	protected abstract void beforeExport() throws IOException;

	/**
	 * Does something after the set of keywords is exported (probably save
	 * changes to disk).
	 */
	protected abstract void afterExport() throws IOException;
	// ////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes exporter to read.
	 * 
	 * @throws IOException
	 */
	public void initializeExporterToRead() throws IOException {
		openFileToRead();
	}

	/**
	 * Closes exporter.
	 * 
	 * @throws IOException
	 */
	public void finishExporterToRead() throws IOException {
		closeFileToRead();
	}

	/**
	 * Opens file to read.
	 * 
	 * @throws IOException
	 */
	public abstract void openFileToRead() throws IOException;

	/**
	 * Closes file.
	 * 
	 * @throws IOException
	 */
	public abstract void closeFileToRead() throws IOException;

	/**
	 * Checks (opened) file if is OK, you know ...
	 * 
	 * @return
	 */
	protected abstract boolean checkFile();

	/**
	 * Imports keywords from file. Opens, checks, and repeatedly imports
	 * keywords. Then closes.
	 * 
	 * @return
	 * @throws IOException
	 *             on error (or if file is in bad format)
	 */
	public Set<Keyword> importKeywords() throws IOException {
		openFileToRead();
		boolean succ = checkFile();

		if (!succ) {
			IllegalArgumentException e = new IllegalArgumentException("File is in bad format");
			throw new IOException("Bad file", e);
		}

		Set<Keyword> result = new LinkedHashSet<>();
		do {
			Keyword keyword = importNextKeyword();
			if (keyword == null) {
				break;
			} else {
				result.add(keyword);
			}
		} while (true);

		closeFileToRead();
		return result;
	}

	/**
	 * Loads, parses and imports next keyword. If there is no such keyword in
	 * file, returns null. On error (i.e. not a number in numeric field), throws
	 * exception.
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract Keyword importNextKeyword() throws IOException;

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

		String backupPath = file.getAbsolutePath() + "_" + new Date().toString() + "." + getSuffix();

		File backup = new File(backupPath);

		try {
			Files.copy(file.toPath(), backup.toPath());
		} catch (Exception e) {
			log.error("File " + file.getPath() + " backup failed", e);
			return false;
		}

		log.info("File {} have been backed up into file {}", file.getPath(), backup.getPath());

		return true;
	}

	/**
	 * In given exporters find first, which matches suffix to given file's.
	 * 
	 * @param exporters
	 * @param file
	 * @return
	 */
	public static AbstractExporterImporter getBySuffix(Set<AbstractExporterImporter> exporters, File file) {

		String suffix = FilenameUtils.getExtension(file.getPath());

		for (AbstractExporterImporter exporter : exporters) {
			if (exporter.getSuffix().equalsIgnoreCase(suffix)) {
				return exporter;
			}
		}

		return null;
	}

}