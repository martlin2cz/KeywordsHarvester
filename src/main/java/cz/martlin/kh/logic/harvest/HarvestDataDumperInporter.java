package cz.martlin.kh.logic.harvest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;

/**
 * Implements dumping harvester data (HarvestProcessData) into file and loading
 * back.
 * 
 * @author martin
 * 
 */
public class HarvestDataDumperInporter {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;

	public HarvestDataDumperInporter(Config config) {
		this.config = config;
	}

	public boolean save(HarvestProcessData data) {
		try {
			File file = config.getQueuesDumpFile();
			writeToBIN(file, data);

			log.info("Harvest data saved into file {}", file);
			return true;
		} catch (Exception e) {
			log.error("Error during saving harvest data", e);
			return false;
		}
	}

	private void writeToBIN(File file, HarvestProcessData data)
			throws IOException {

		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(data);

		IOUtils.closeQuietly(oos);
		IOUtils.closeQuietly(fos);
	}

	public HarvestProcessData load() {
		File file = config.getQueuesDumpFile();
		try {
			HarvestProcessData data = loadFromBIN(file);

			log.info("Harvest data successfully loaded from file {}", file);
			return data;
		} catch (Exception e) {
			log.error("Error during loading harvest data from file", e);
			return null;
		}
	}

	private HarvestProcessData loadFromBIN(File file) throws IOException {

		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);

		try {
			Object object = ois.readObject();
			HarvestProcessData data = (HarvestProcessData) object;

			return data;
		} catch (Exception e) {
			throw new IOException("Cannot load harvest data object", e);
		} finally {
			IOUtils.closeQuietly(ois);
			IOUtils.closeQuietly(fis);
		}

	}

	public boolean importFromTextFile(File file, String separator,
			HarvestProcessData data) {
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
