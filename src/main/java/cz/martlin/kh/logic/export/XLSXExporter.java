package cz.martlin.kh.logic.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Exports into XLSX file. Each export export file overrides.
 * 
 * @author martin
 * 
 */
public class XLSXExporter extends RewriteExporter {
	public static final String SUFFIX = "xlsx";
	private static final String DESCRIPTION = "Microsoft excel format (2007-?) (*.XLSX)";

	private OutputStream ous;
	private SXSSFWorkbook workbook;
	private Sheet sheet;
	private int nextRowIndex;

	public XLSXExporter(Config config) {
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
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		CellStyle style = workbook.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);

		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.getIndex());

		return style;
	}

	@Override
	public void openFile() throws IOException {
		ous = new FileOutputStream(config.getExExportFile());
		workbook = new SXSSFWorkbook(config.getHwProcessIterationSize());
		sheet = workbook.createSheet("Keywords");

		nextRowIndex = 0;
	}

	@Override
	public void closeFile() throws IOException {
		workbook.write(ous);

		workbook.dispose();
		IOUtils.closeQuietly(ous);

		nextRowIndex = -1;
	}

	@Override
	public void exportHeaderOrShit() throws IOException {
		Row row = sheet.createRow(0);

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
		Row row = sheet.createRow(nextRowIndex);

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

}
