package cz.martlin.kh.logic.export;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

public class ExporterImporterTest {

	private Config initConfig(String sfx) throws IOException {
		Config config = new Config();

		String tmpdir = System.getProperty("java.io.tmpdir");
		String filename = "testing-export" + System.currentTimeMillis() + "." + sfx;
		File file = new File(tmpdir, filename);

		config.setExExportFile(file);

		System.out.println("Will export to " + file);

		return config;
	}

	private Set<Keyword> createKeywords() {
		Set<Keyword> kws = new LinkedHashSet<>();

		kws.add(new Keyword("Computer", 2, 1000, 999, 1024, 0.5));
		kws.add(new Keyword("Me", 5, 42, 111, 666, 0.0));

		return kws;
	}

	@Test
	public void testCSV() throws IOException {
		Config config = initConfig(CSVExporterImporter.SUFFIX);
		CSVExporterImporter exporter = new CSVExporterImporter(config);

		// write
		exporter.initializeExporterToWrite();
		exporter.exportInitial(new LinkedHashSet<Keyword>());

		// exporter.exportInitial(createKeywords());
		Set<Keyword> input = createKeywords();
		exporter.export(input);

		exporter.finishExporterToWrite();

		// read
		exporter.initializeExporterToRead();

		Set<Keyword> output = exporter.importKeywords();

		exporter.finishExporterToRead();

		// check
		assertEquals(input, output);
	}

	@Test
	public void testXLSX() throws IOException {
		Config config = initConfig(XLSXAppendingExporterImporter.SUFFIX);
		XLSXAppendingExporterImporter exporter = new XLSXAppendingExporterImporter(config);

		// write
		exporter.initializeExporterToWrite();

		// exporter.exportInitial(createKeywords());
		Set<Keyword> input = createKeywords();
		exporter.export(input);

		exporter.finishExporterToWrite();

		// read
		exporter.initializeExporterToRead();

		Set<Keyword> output = exporter.importKeywords();

		exporter.finishExporterToRead();

		// check
		assertEquals(input, output);
	}

}
