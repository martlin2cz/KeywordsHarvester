package cz.martlin.kh.logic.picwf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;

/**
 * Keywords that picworkflow did not process will be exported into file.
 * 
 * @author martin
 * 
 */
public class UnpicworkflowableExporter {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static final CSVFormat FORMAT = CSVFormat.DEFAULT;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss");

	private final Config config;

	private CSVPrinter printer;
	private Writer writer;

	public UnpicworkflowableExporter(Config config) {
		this.config = config;
	}

	/**
	 * Initializes export.
	 * 
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		try {
			writer = new FileWriter(config.getPwFailedFile(), true);
			printer = new CSVPrinter(writer, FORMAT);

			log.info(
					"Unpicworkflowable keywords exporter ready to export to file {}",
					config.getPwFailedFile());
		} catch (IOException e) {
			log.error(
					"Could not initialize unpicworkfloable keywords exporter",
					e);
			throw e;
		}
	}

	/**
	 * Initializes, saves, closes. Returns true if succeeds.
	 * 
	 * @param keywords
	 * @return
	 */
	public boolean simplySave(Set<String> keywords) {
		boolean success;
		try {
			initialize();
		} catch (IOException e) {
			success = false;
			return success;
		}

		try {
			save(keywords);
			success = true;
		} catch (IOException e) {
			success = false;
		}

		finish();

		return success;
	}

	/**
	 * Saves given keywords into file.
	 * 
	 * @param keywords
	 * @throws IOException
	 */
	public void save(Set<String> keywords) throws IOException {
		Date now = new Date();
		try {
			for (String keyword : keywords) {
				printKeyword(now, keyword);
			}
		} catch (IOException e) {
			log.error("Cannot save keywords " + keywords, e);
			throw e;
		}

		printer.flush();
		log.info("Exported unpicworkflowable keywords {}", keywords);
	}

	/**
	 * Prints given keyword with given timestamp
	 * 
	 * @param keyword
	 * @throws IOException
	 */
	private void printKeyword(Date date, String keyword) throws IOException {
		String formatedDate = DATE_FORMAT.format(date);

		printer.printRecord(formatedDate, keyword);
	}

	/**
	 * Finishes work of exporter.
	 */
	public void finish() {
		IOUtils.closeQuietly(writer);
		IOUtils.closeQuietly(printer);

		writer = null;
		printer = null;

		log.info("Unpicworkflowable keywords exporter finished its work.");

	}
}
