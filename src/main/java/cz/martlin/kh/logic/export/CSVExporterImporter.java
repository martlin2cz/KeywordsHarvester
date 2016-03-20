package cz.martlin.kh.logic.export;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Implements exporting keywords lists into CSV file. Keywords are appended in
 * each {@link #export(java.util.Set)} call and flushed immediatelly.
 * 
 * @author martin
 * 
 */
public class CSVExporterImporter extends AbstractExporterImporter {
	public static final String SUFFIX = "csv";
	private static final String DESCRIPTION = "Coma separated values (*.CSV)";

	private static CSVFormat format = CSVFormat.EXCEL.withDelimiter(';').withHeader(HEADER_FIELDS);

	private Writer writer;
	private CSVPrinter printer;

	private Reader reader;
	private CSVParser parser;
	private Iterator<CSVRecord> records;

	public CSVExporterImporter(Config config) {
		super(config);
	}

	@Override
	public String getSuffix() {
		return SUFFIX;
	}

	@Override
	public String getFormatDescription() {
		return DESCRIPTION;
	}

	@Override
	public boolean openFileToWrite() throws IOException {
		File file = config.getExExportFile();
		long size = file.length();

		writer = new FileWriter(file, true);
		printer = new CSVPrinter(writer, format);

		return size > 0;
	}

	@Override
	public void closeFileToWrite() throws IOException {
		IOUtils.closeQuietly(writer);
		IOUtils.closeQuietly(printer);

		writer = null;
		printer = null;
	}

	@Override
	public void exportHeaderOrShit() throws IOException {
		// printer.print(HEADER_FIELDS);
	}

	@Override
	protected void exportKeyword(Keyword keyword) throws IOException {
		Object[] line = keywordToLine(keyword);
		printer.printRecord(line);
		log.debug("Keyword {} exported to " + getSuffix(), keyword);
	}

	@Override
	protected void beforeExport() throws IOException {
		// nothing
	}

	@Override
	protected void afterExport() throws IOException {
		printer.flush();
		writer.flush();
		log.debug("Keywords flushed.");
	}

	// ////////////////////////////////////////////////////////////////////////////

	@Override
	public void openFileToRead() throws IOException {
		reader = new FileReader(config.getExExportFile());
		parser = new CSVParser(reader, format);

		records = parser.iterator();
	}

	@Override
	public void closeFileToRead() throws IOException {
		IOUtils.closeQuietly(reader);
		IOUtils.closeQuietly(parser);

		reader = null;
		parser = null;
		records = null;
	}

	@Override
	protected boolean checkFile() {

		Set<String> expected = new HashSet<>(Arrays.asList(HEADER_FIELDS));

		CSVRecord line = records.next();
		Set<String> infile = new HashSet<String>(line.toMap().keySet());

		boolean succ = expected.equals(infile);

		if (succ) {
			log.debug("Export file format ok.");
		} else {
			log.error("Export file format mismatch: Expected headers " + expected + ", but found " + infile);
		}

		return succ;

	}

	@Override
	public Keyword importNextKeyword() throws IOException {
		if (!records.hasNext()) {
			return null;
		}
		CSVRecord record = records.next();
		log.debug("Importing record from CSV record: " + record.toMap());

		String keyword = record.get(0);
		int count = Integer.parseInt(record.get(1));
		int downloads = Integer.parseInt(record.get(2));
		// downloads per file
		int views = Integer.parseInt(record.get(4));
		// views per file
		int lang = Integer.parseInt(record.get(6));
		double rating = Double.parseDouble(record.get(7));

		return new Keyword(keyword, lang, count, views, downloads, rating);
	}

	/**
	 * Creates object array from given keyword.
	 * 
	 * @param keyword
	 * @return
	 */
	private static Object[] keywordToLine(Keyword keyword) {
		return new Object[] { keyword.getKeyword(), //
				keyword.getCount(), //
				keyword.getDownloads(), //
				keyword.getDownloadsPerFile(), //
				keyword.getViews(), //
				keyword.getViewsPerFile(), //
				keyword.getLang(), //
				keyword.getRating() //
		};
	}

}
