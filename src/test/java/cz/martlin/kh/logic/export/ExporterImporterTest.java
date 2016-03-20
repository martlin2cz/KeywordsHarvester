package cz.martlin.kh.logic.export;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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

	private Set<Keyword> createKeywords1() {
		Set<Keyword> kws = new LinkedHashSet<>();

		kws.add(new Keyword("Computer", 2, 1000, 999, 1024, 0.5));
		kws.add(new Keyword("Me", 5, 42, 111, 666, 0.0));

		return kws;
	}

	private Set<Keyword> createKeywords2() {
		Set<Keyword> kws = new LinkedHashSet<>();

		kws.add(new Keyword("Music", 4, 15233, 1856, 8885, 0.3));
		kws.add(new Keyword("Olomouc", 4, 11, 255, 456, 0.4));
		kws.add(new Keyword("Drink", 3, 59, 222, 333, 0.1));

		return kws;
	}

	@Test
	public void testExporters() throws IOException {

		Config config1 = initConfig(CSVExporterImporter.SUFFIX);
		testExporter(new CSVExporterImporter(config1));

		Config config2 = initConfig(XLSXAppendingExporterImporter.SUFFIX);
		testExporter(new XLSXAppendingExporterImporter(config2));

	}

	public void testExporter(AbstractExporterImporter exporter) throws IOException {

		/////////////////////////////////
		// write
		exporter.initializeExporterToWrite();

		// export first
		Set<Keyword> keywords1 = createKeywords1();
		exporter.export(keywords1);

		// export second
		Set<Keyword> keywords2 = createKeywords2();
		exporter.export(keywords2);

		// export empty (just for case
		Set<Keyword> keywords3 = new LinkedHashSet<>();
		exporter.export(keywords3);

		exporter.finishExporterToWrite();

		/////////////////////////////////
		// read
		exporter.initializeExporterToRead();

		Set<Keyword> output = exporter.importKeywords();

		exporter.finishExporterToRead();

		/////////////////////////////////
		// check

		// create expected
		Set<Keyword> input = new HashSet<>();
		input.addAll(keywords1);
		input.addAll(keywords2);
		input.addAll(keywords3);

		// compare
		assertEquals(input, output);
	}
}
