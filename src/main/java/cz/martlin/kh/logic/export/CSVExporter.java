package cz.martlin.kh.logic.export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
public class CSVExporter extends AppendExporter {
	private static final String SUFFIX = "csv";
	private static final String DESCRIPTION = "Coma separated values (*.CSV)";
	private static CSVFormat format = CSVFormat.EXCEL;

	private Writer writer;
	private CSVPrinter printer;

	public CSVExporter(Config config) {
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
	public void openFile() throws IOException {
		writer = new FileWriter(config.getExportFile(), false);
		printer = new CSVPrinter(writer, format);
	}

	@Override
	public void closeFile() throws IOException {
		IOUtils.closeQuietly(printer);
		IOUtils.closeQuietly(printer);
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
