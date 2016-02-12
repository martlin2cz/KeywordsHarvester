package cz.martlin.kh.logic;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.export.AbstractExporter;
import cz.martlin.kh.logic.export.CSVExporter;
import cz.martlin.kh.logic.export.XLSXExporter;
import cz.martlin.kh.logic.harvest.HarvestProcessData;
import cz.martlin.kh.logic.harvest.ParalellHarvester;
import cz.martlin.kh.logic.harvest.RelatedKeywordsHarvester;
import cz.martlin.kh.logic.picwf.PicworkflowQuery;
import cz.martlin.kh.logic.picwf.PicworkflowWrapper;
import cz.martlin.kh.logic.subkeyw.AbstractServiceWrapper;
import cz.martlin.kh.logic.subkeyw.IStockphotoWrapper;
import cz.martlin.kh.logic.subkeyw.ShutterstockWrapper;
import cz.martlin.kh.logic.utils.ConfigStorerLoader;

@Deprecated
public class Testing {

	protected static final Config config = new Config();

	protected static final IStockphotoWrapper isw = //
	new IStockphotoWrapper(config);

	protected static final ShutterstockWrapper ssw = //
	new ShutterstockWrapper(config);

	protected static final CSVExporter csv = //
	new CSVExporter(config);

	protected static final XLSXExporter xlsx = //
	new XLSXExporter(config);

	public static void main(String[] args) {
		System.out.println("Uncomment something to run test:");

		// config.setExportFile(new File("test.csv"));
		new ConfigStorerLoader().save(config);
		System.out.println("Config saved.");

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

	protected static void testExporter(Config config, AbstractExporter export) {
		try {
			System.out.println("init:" + export + ", into: "
					+ config.getExportFile().getPath());
			export.initializeExporter();

			Set<Keyword> keywords1 = TestingKeywords.createTestingKeywordsA();
			System.out.println("export1:");
			export.export(keywords1);

			System.out.println("wait");
			Thread.sleep(60 * 1000);

			System.out.println("export2:");
			Set<Keyword> keywords2 = TestingKeywords.createTestingKeywordsB();
			export.export(keywords2);

			System.out.println("finish:");
			export.finishExporter();

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

	@Deprecated
	public static void testHarvester(Config config, IStockphotoWrapper isw,
			ShutterstockWrapper ssw) {

		Set<String> initialKeywords = new LinkedHashSet<>();
		initialKeywords.add("blue");
		initialKeywords.add("Brno");

		HarvestProcessData data = HarvestProcessData.createNew(config,
				initialKeywords);

		final RelatedKeywordsHarvester harv = //
		new RelatedKeywordsHarvester(config, null, null);// XXX nulls

		try {
			harv.initialize(data);

			harv.doSubkeywording();
			harv.doPicworkflowing();
			harv.doExporting();

			// HarvestProcessData data;
			// data = harv.initialize(initialKeywords);
			// harv.harvest(data);
			// harv.finish();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public static void testParallelHarvester(Config config,
			IStockphotoWrapper isw, ShutterstockWrapper ssw) {
		try {
			// wrappers~services

			// initials keywords
			Set<String> initialKeywords = new LinkedHashSet<>();
			initialKeywords.add("cocoa");

			// harvester
			HarvestProcessData data = HarvestProcessData.createNew(config,
					initialKeywords);
			ParalellHarvester harvester;

			// run firstly
			// harvester = new ParalellHarvester(config, wrappers);
			// harvester.start(data);
			//
			// Thread.sleep(1 * 60 * 1000);
			// harvester.stop();

			// run secondly
			harvester = new ParalellHarvester(config, null, null); // XXX ->
																	// nulls
			harvester.start(data);

			Thread.sleep(1 * 30 * 60 * 1000);
			harvester.stop();

			// save and load
			System.out.println("Before Save: " + data);
			data.saveToDumpFile(config);
			data = HarvestProcessData.loadFromDumpFile(config);
			System.out.println("After Save:  " + data);

			// run thirdly
			harvester = new ParalellHarvester(config, null, null); // XXX ->
																	// nulls
			harvester.start(data);

			Thread.sleep(1 * 30 * 60 * 1000);
			harvester.stop();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void testConfigLoadStore() {
		ConfigStorerLoader csl = new ConfigStorerLoader();

		Config oldConfig = TestingKeywords.createTestingConfig();
		csl.save(oldConfig);

		Config newConfig = new Config();
		csl.load(newConfig);

		if (!oldConfig.equals(newConfig)) {
			System.err.println("Saved and loaded configs are different!");
			System.err.println("Saved:  " + oldConfig.toString());
			System.err.println("Loaded: " + newConfig.toString());
		} else {
			System.out.println("Yeah, loaded and saved configs are same");
		}

		csl.save(config);
		System.out.println("Default config saved over testing.");

	}

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
