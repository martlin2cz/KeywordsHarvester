package cz.martlin.kh.logic;

import java.util.List;
import java.util.Set;

import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.export.AbstractExporterImporter;
import cz.martlin.kh.logic.export.CSVExporterImporter;
import cz.martlin.kh.logic.picwf.PicworkflowQuery;
import cz.martlin.kh.logic.picwf.PicworkflowWrapper;
import cz.martlin.kh.logic.subkeyw.AbstractServiceWrapper;
import cz.martlin.kh.logic.subkeyw.IStockphotoWrapper;
import cz.martlin.kh.logic.subkeyw.ShutterstockWrapper;

@Deprecated
public class Testing {

	protected static final Config config = new Config();

	protected static final IStockphotoWrapper isw = //
	new IStockphotoWrapper(config);

	protected static final ShutterstockWrapper ssw = //
	new ShutterstockWrapper(config);

	protected static final CSVExporterImporter csv = //
	new CSVExporterImporter(config);

//	protected static final XLSXNativeExporterImporter xlsx = //
//	new XLSXNativeExporterImporter(config);

	public static void main(String[] args) {
		System.out.println("Uncomment something to run test:");

		 //config.setExportFile(new File("test.csv"));

//		new ConfigStorerLoader().save(config);
//		System.out.println("Config saved.");

		// testExporter(config, csv);

		// testConfigLoadStore();

		// testPickworkflowWrapper(config);

		// testServiceWrapper(isw);

		// testServiceWrapper(ssw);

		// testHarvester(config, isw, ssw);

		// testParallelHarvester(config, isw, ssw);

		// testPicworkflowStability(config);

		System.out.println("End.");

	}

	protected static void testExporter(Config config, AbstractExporterImporter export) {
		try {
			System.out.println("init:" + export + ", into: "
					+ config.getExExportFile().getPath());
			export.initializeExporterToWrite();

			Set<Keyword> keywords1 = TestingKeywords.createTestingKeywordsA();
			System.out.println("export1:");
			export.export(keywords1);

			System.out.println("wait");
			Thread.sleep(60 * 1000);

			System.out.println("export2:");
			Set<Keyword> keywords2 = TestingKeywords.createTestingKeywordsB();
			export.export(keywords2);

			System.out.println("finish:");
			export.finishExporterToWrite();

			System.out.println("done: " + export);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testServiceWrapper(AbstractServiceWrapper service) {
		System.out.println("Testing service: " + service.getName());

		List<String> imagesIds = service
				.searchImagesIDsOfKeyword("Olomouc", 10);
		System.out.println(imagesIds);

		List<String> imageKeywords = service.searchImageKeywords(imagesIds
				.get(0));
		System.out.println(imageKeywords);

		Set<String> keywords1 = service.getRelatedKeywords("Olomouc", 1);
		System.out.println(keywords1);

		Set<String> keywords2 = service.getRelatedKeywords("Olomouc", 10);
		System.out.println(keywords2);
		System.out.println();
	}

	@Deprecated
	public static void testPickworkflowWrapper(Config config) {

		PicworkflowWrapper pw = new PicworkflowWrapper(config);

		Set<String> keyws1 = TestingKeywords.testKeywords4();

		// Set<String> keyws2 = testKeywords3();

		try {
			pw.initialize();
			Thread.sleep(1000);

			PicworkflowQuery q1 = pw.createQuerry(keyws1);
			q1.runQuery();

			Set<Keyword> kws1 = null;
			Thread.sleep(1000);
			/*
			 * PicworkflowQuery q2 = pw.createQuerry(keyws2); Set<Keyword> kws2
			 * = q2.runQuery(); Thread.sleep(1000);
			 */
			pw.finish();
			Thread.sleep(1000);

			System.out.println(kws1);
			// System.out.println(kws2);
		} catch (NetworkException | InterruptedException e) {
			e.printStackTrace();
		}
	}

//	public static void testConfigLoadStore() {
//		ConfigStorerLoader csl = new ConfigStorerLoader();
//
//		Config oldConfig = TestingKeywords.createTestingConfig();
//		csl.save(oldConfig);
//
//		Config newConfig = new Config();
//		csl.load(newConfig);
//
//		if (!oldConfig.equals(newConfig)) {
//			System.err.println("Saved and loaded configs are different!");
//			System.err.println("Saved:  " + oldConfig.toString());
//			System.err.println("Loaded: " + newConfig.toString());
//		} else {
//			System.out.println("Yeah, loaded and saved configs are same");
//		}
//
//		csl.save(config);
//		System.out.println("Default config saved over testing.");
//
//	}

	public static void testPicworkflowStability(Config config) {
		PicworkflowStabilityTester test = new PicworkflowStabilityTester(config);
		long sleep = 60 * 1000;

		try {
			// test.testWithOneConnection(10, 10, 0);
			// Thread.sleep(sleep);
			// test.testWithOneConnection(10, 100, 0);
			// Thread.sleep(sleep);
			// test.testWithOneConnection(10, 10, 30 * 1000);
			// Thread.sleep(sleep);
			// test.testWithOneConnection(10, 100, 30 * 1000);
			// Thread.sleep(sleep);
			// test.testWithEachConnection(10, 10, 0);
			// Thread.sleep(sleep);
			// test.testWithEachConnection(10, 100, 0);
			// Thread.sleep(sleep);
			// test.testWithEachConnection(10, 10, 30 * 1000);
			// Thread.sleep(sleep);
			// test.testWithEachConnection(10, 100, 30 * 1000);
			// Thread.sleep(sleep);

			// test.testWithEachConnection(10, 30, 0);
			// Thread.sleep(sleep);

			// test.testWithEachConnection(10, 30, 30 * 1000);
			// Thread.sleep(sleep);
			//
			// test.testWithReconnect(10, 30, 30 * 1000);
			// Thread.sleep(sleep);
			//

			test.testWithReconnect(10, 30, 0);
			Thread.sleep(sleep);

			test.testWithReconnect(10, 50, 0);
			Thread.sleep(sleep);

			test.testWithReconnect(20, 100, 0);
			Thread.sleep(sleep);

		} catch (InterruptedException e) {
			System.err.println(e);
		}

	}

}
