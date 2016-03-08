package cz.martlin.kh.logic.export;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Exports into XLSX file. Each export export file overrides.
 * 
 * @author martin
 * 
 */
public class XLSXExporterImporter extends RewriteExporterImporter {
	public static final String SUFFIX = "xlsx";
	private static final String DESCRIPTION = "Microsoft excel format (2007-?) (*.XLSX)";

	private OutputStream ous;
	private XSSFWorkbook writeworkbook;
	private XSSFSheet writesheet;
	private int nextRowIndex;

	private InputStream ins;
	private Iterator<Row> rows;

	public XLSXExporterImporter(Config config) {
		super(config);
	}

	@Override
	public String getFormatDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getSuffix() {
		return SUFFIX;
	}

	/**
	 * Creates style for header row.
	 * 
	 * @return
	 */
	private CellStyle createHeaderCellsStyle() {
		Font font = writeworkbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		CellStyle style = writeworkbook.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);

		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.getIndex());

		return style;
	}

	@Override
	public void openFileToWrite() throws IOException {
		ous = new FileOutputStream(config.getExExportFile());
		writeworkbook = new XSSFWorkbook();
		writesheet = writeworkbook.createSheet("Keywords");

		nextRowIndex = 0;
	}

	@Override
	public void closeFileToWrite() throws IOException {
		writeworkbook.write(ous);

		IOUtils.closeQuietly(ous);

		ous = null;
		writeworkbook = null;
		writesheet = null;
		nextRowIndex = -1;
	}

	@Override
	public void exportHeaderOrShit() throws IOException {
		Row row = writesheet.createRow(0);

		CellStyle style = createHeaderCellsStyle();

		for (int i = 0; i < HEADER_FIELDS.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(HEADER_FIELDS[i]);
			cell.setCellStyle(style);
		}

		nextRowIndex = 1;
	}

	@Override
	protected void exportKeyword(Keyword keyword) throws IOException {
		Row row = writesheet.createRow(nextRowIndex);

		appendCell(0, row, keyword.getKeyword());
		appendCell(1, row, keyword.getCount());
		appendCell(2, row, keyword.getDownloads());
		appendCell(3, row, keyword.getDownloadsPerFile());
		appendCell(4, row, keyword.getViews());
		appendCell(5, row, keyword.getViewsPerFile());
		appendCell(6, row, keyword.getLang());
		appendCell(7, row, keyword.getRating());

		nextRowIndex++;

	}

	// //////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void openFileToRead() throws IOException {
		ins = new FileInputStream(config.getExExportFile());
		XSSFWorkbook readworkbook = new XSSFWorkbook(ins);
		XSSFSheet readsheet = readworkbook.getSheetAt(0);
		rows = readsheet.rowIterator();
	}

	@Override
	public void closeFileToRead() throws IOException {
		IOUtils.closeQuietly(ins);
		ins = null;
		rows = null;
	}

	@Override
	protected boolean checkFile() {
		Row row = rows.next();

		List<String> infile = new ArrayList<>();
		int cont = HEADER_FIELDS.length;

		for (int i = 0; i < cont; i++) {
			String cell = getCellValue(i, row, String.class);
			infile.add(cell);
		}

		List<String> headers = new ArrayList<>(Arrays.asList(HEADER_FIELDS));

		boolean succ = headers.equals(infile);

		if (succ) {
			log.debug("Export file format ok.");
		} else {
			log.error("Export file format mismatch: Expected headers "
					+ headers + ", but found " + infile);
		}

		return succ;
	}

	@Override
	public Keyword importNextKeyword() throws IOException {
		if (!rows.hasNext()) {
			return null;
		}

		Row row = rows.next();

		String keyword = getCellValue(0, row, String.class);
		int count = getCellValue(1, row, int.class);
		int downloads = getCellValue(2, row, int.class);
		int views = getCellValue(4, row, int.class);
		int lang = getCellValue(6, row, int.class);
		double rating = getCellValue(7, row, double.class);

		return new Keyword(keyword, lang, count, views, downloads, rating);
	}

	/**
	 * Just method which creates cell on given cellIndex in given row with
	 * given, please not-null, value. On float or double value saves with czech
	 * "," instead of english "."
	 * 
	 * @param cellIndex
	 * @param row
	 * @param value
	 */
	private void appendCell(int cellIndex, Row row, Object value) {
		Cell cell = row.createCell(cellIndex);

		String str;
		if (value instanceof Float || value instanceof Double) {
			str = value.toString().replace('.', ',');
		} else {
			str = value.toString();
		}

		cell.setCellValue(str);
	}

	@SuppressWarnings("unchecked")
	private <T> T getCellValue(int cellIndex, Row row, Class<T> type) {
		Cell cell = row.getCell(cellIndex);

		String str = cell.getStringCellValue();
		if (type.equals(String.class)) {
			return (T) str;

		} else if (type.equals(int.class)) {
			return (T) new Integer(Integer.parseInt(str));

		} else if (type.equals(float.class)) {
			str = str.replace(',', '.');
			return (T) new Float(Float.parseFloat(str));

		} else if (type.equals(double.class)) {
			str = str.replace(',', '.');
			return (T) new Double(Double.parseDouble(str));

		} else {
			throw new UnsupportedOperationException("Unsupported type " + type);
		}

	}

}
