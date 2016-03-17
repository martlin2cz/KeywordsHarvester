package cz.martlin.kh.logic.harvest3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;

/**
 * Similarly to HarvestDataDumperInporter, implements importing, dumping and
 * loading of {@link TreeHarvestProcessData} instances.
 * 
 * @author martin
 * 
 */
public class TreeHarvestDataDumperInporter {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;

	public TreeHarvestDataDumperInporter(Config config) {
		this.config = config;
	}

	/**
	 * Saves given data into file {@link Config#getHwDataDumpFile()} (and, if
	 * can, does the backup of the file before it is overriden).
	 * 
	 * @param data
	 * @return true if success.
	 */
	public boolean save(TreeHarvestProcessData data) {
		File file = config.getHwDataDumpFile();
		File backup = config.getHwDataDumpBackupFile();

		tryToBackUp(file, backup);

		try {

			writeToBIN(file, data);

			log.info("Harvest data saved into file {}", file);
			return true;
		} catch (Exception e) {
			log.error("Error during saving harvest data", e);
			return false;
		}
	}

	/**
	 * Tries to do the backup of given file.
	 */
	private void tryToBackUp(File file, File backup) {
		InputStream ins = null;
		OutputStream ous = null;

		try {
			ins = new FileInputStream(file);
			ous = new FileOutputStream(backup);
			IOUtils.copy(ins, ous);

			log.info("Backed up file " + file.getAbsolutePath() + " to "
					+ backup.getAbsolutePath());
		} catch (IOException e) {
			log.warn("Cannot backup file " + file.getAbsolutePath() + " to "
					+ backup.getAbsolutePath(), e);
		} finally {
			IOUtils.closeQuietly(ins);
			IOUtils.closeQuietly(ous);
		}
	}

	/**
	 * Saves data into file.
	 * 
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	private void writeToBIN(File file, TreeHarvestProcessData data)
			throws IOException {

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(data);
		} catch (Exception e) {
			throw new IOException("Cannot write harvest data object", e);
		} finally {
			IOUtils.closeQuietly(oos);
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Loads data from {@link Config#getQueuesDumpFile()} file.
	 * 
	 * @return data or null if fails.
	 */
	public TreeHarvestProcessData load(boolean backup) {
		File file;
		if (!backup) {
			file = config.getHwDataDumpFile();
		} else {
			file = config.getHwDataDumpBackupFile();
		}

		try {
			TreeHarvestProcessData data = loadFromBIN(file);

			log.info("Harvest data successfully loaded from file {}", file);
			return data;
		} catch (IOException e) {
			log.error("Error during loading harvest data from file", e);
			return null;
		}
	}

	/**
	 * Loads data from file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private TreeHarvestProcessData loadFromBIN(File file) throws IOException {
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);

			Object object = ois.readObject();
			TreeHarvestProcessData data = (TreeHarvestProcessData) object;

			return data;
		} catch (Exception e) {
			throw new IOException("Cannot load harvest data object", e);
		} finally {
			IOUtils.closeQuietly(ois);
			IOUtils.closeQuietly(fis);
		}

	}

	/**
	 * Imports into data from given file using given keywords separator regex.
	 * 
	 * @param file
	 * @param separator
	 * @param data
	 * @return true if success
	 */
	public boolean importFromTextFile(File file, String separator,
			TreeHarvestProcessData data) {
		Reader reader = null;
		try {
			reader = new FileReader(file);
			String content = IOUtils.toString(reader);
			String array[] = content.split(separator);
			Set<String> set = new LinkedHashSet<>(Arrays.asList(array));

			data.addToData(set);
			return true;
		} catch (Exception e) {
			log.error(
					"Error during importing keywords from file "
							+ file.getPath(), e);
			return false;
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

}
