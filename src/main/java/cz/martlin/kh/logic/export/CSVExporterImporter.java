package cz.martlin.kh.logic.export;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

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
public class CSVExporterImporter extends AppendExporterImporter {
	public static final String SUFFIX = "csv";
	private static final String DESCRIPTION = "Coma separated values (*.CSV)";
	private static CSVFormat format = CSVFormat.EXCEL.withDelimiter(';');

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
	public void openFileToWrite() throws IOException {
		writer = new FileWriter(config.getExExportFile(), true); // TODO FIXME
																	// true? or
																	// false?
		printer = new CSVPrinter(writer, format);
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
		printer.printRecord((Object[]) HEADER_FIELDS);
	}

	@Override
	protected void exportKeyword(Keyword keyword) throws IOException {
		Object[] line = keywordToLine(keyword);
		printer.printRecord(line);
		log.debug("Keyword {} exported to " + getSuffix(), keyword);
	}

	@Override
	protected void flush() throws IOException {
		printer.flush();
		writer.flush();
		log.debug("Keywords flushed.");
	}

	// ////////////////////////////////////////////////////////////////////////////

	public void initializeExporterToRead() throws IOException {
		openFileToRead();
	}

	@Override
	public void finishExporterToRead() throws IOException {
		closeFileToRead();
	}

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
		// List<String> expected = Arrays.asList(HEADER_FIELDS);
		// List<String> infile = new
		// ArrayList<>(records.next().toMap().keySet());
		//
		// boolean succ = expected.equals(infile);
		//
		// if (succ) {
		// log.debug("Export file format ok.");
		// } else {
		// log.error("Export file format mismatch: Expected headers "
		// + expected + ", but found " + infile);
		// }

		boolean succ = true;
		log.warn("File check out of order. Assuming file is OK.");
		return succ;

	}

	@Override
	public Keyword importNextKeyword() throws IOException {
		if (!records.hasNext()) {
			return null;
		}
		CSVRecord record = records.next();

		String keyword = record.get(0);
		int count = Integer.parseInt(record.get(1));
		int downloads = Integer.parseInt(record.get(2));
		// downlaods per file
		int views = Integer.parseInt(record.get(4));
		// wievs per file
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
				keyword.getCount(),//
				keyword.getDownloads(), //
				keyword.getDownloadsPerFile(),//
				keyword.getViews(), //
				keyword.getViewsPerFile(),//
				keyword.getLang(), //
				keyword.getRating() //
		};
	}

}
