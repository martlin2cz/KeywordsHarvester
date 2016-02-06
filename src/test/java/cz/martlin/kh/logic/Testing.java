package cz.martlin.kh.logic;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.martlin.kh.KHMain;
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

//		testPickworkflowWrapper(config);

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

			Set<Keyword> keywords1 = createTestingKeywordsA();
			System.out.println("export1:");
			export.export(keywords1);

			System.out.println("wait");
			Thread.sleep(60 * 1000);

			System.out.println("export2:");
			Set<Keyword> keywords2 = createTestingKeywordsB();
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

	public static void testPickworkflowWrapper(Config config) {

		PicworkflowWrapper pw = new PicworkflowWrapper(config);

		Set<String> keyws1 = testKeywords4();

		// Set<String> keyws2 = testKeywords3();

		try {
			pw.initialize();
			Thread.sleep(1000);

			PicworkflowQuery q1 = pw.createQuerry(keyws1);
			Set<Keyword> kws1 = q1.runQuery();

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

	public static void testHarvester(Config config, IStockphotoWrapper isw,
			ShutterstockWrapper ssw) {

		Set<String> initialKeywords = new LinkedHashSet<>();
		initialKeywords.add("blue");
		initialKeywords.add("Brno");

		HarvestProcessData data = HarvestProcessData.createNew(config,
				initialKeywords);

		final RelatedKeywordsHarvester harv = //
		new RelatedKeywordsHarvester(config, //
				KHMain.getServices(config),//
				KHMain.getExporters(config));
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

	public static void testParallelHarvester(Config config,
			IStockphotoWrapper isw, ShutterstockWrapper ssw) {
		try {
			// wrappers~services
			Set<AbstractServiceWrapper> wrappers = KHMain.getServices(config);
			Set<AbstractExporter> exporters = KHMain.getExporters(config);

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
			harvester = new ParalellHarvester(config, wrappers, exporters);
			harvester.start(data);

			Thread.sleep(1 * 30 * 60 * 1000);
			harvester.stop();

			// save and load
			System.out.println("Before Save: " + data);
			data.saveToDumpFile(config);
			data = HarvestProcessData.loadFromDumpFile(config);
			System.out.println("After Save:  " + data);

			// run thirdly
			harvester = new ParalellHarvester(config, wrappers, exporters);
			harvester.start(data);

			Thread.sleep(1 * 30 * 60 * 1000);
			harvester.stop();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void testConfigLoadStore() {
		ConfigStorerLoader csl = new ConfigStorerLoader();

		Config oldConfig = createTestingConfig();
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

	public static Set<String> testKeywords3() {
		Set<String> keyws2 = new LinkedHashSet<>();
		keyws2.add("celebrity");
		keyws2.add("london");
		keyws2.add("city");
		keyws2.add("ligh");
		keyws2.add("snow");
		keyws2.add("male");
		keyws2.add("happy");
		keyws2.add("candle");
		keyws2.add("sweet");
		keyws2.add("cake");
		keyws2.add("kontrmelec");
		keyws2.add("kakao");
		keyws2.add("chleba");
		keyws2.add("cream");
		keyws2.add("snow");
		keyws2.add("lead");
		keyws2.add("winter");
		keyws2.add("snowman");
		keyws2.add("carrot");
		keyws2.add("milk");
		return keyws2;
	}

	public static Set<String> testKeywords2() {
		Set<String> keyws1 = new LinkedHashSet<>();
		keyws1.add("political");
		keyws1.add("portugal");
		keyws1.add("region");
		keyws1.add("republic");
		keyws1.add("serbia");
		keyws1.add("silhouette");
		keyws1.add("slovakia");
		keyws1.add("spain");
		keyws1.add("sweden");
		keyws1.add("switzerland");
		keyws1.add("symbol");
		keyws1.add("territory");
		keyws1.add("ukraine");
		keyws1.add("union");
		keyws1.add("world");
		keyws1.add("hall");
		return keyws1;
	}

	public static Set<String> testKeywords1() {
		Set<String> kw = new LinkedHashSet<>();
		kw.add("Blue");
		kw.add("Sky");
		kw.add("Textured Effect");
		kw.add("Backgrounds");
		kw.add("Sparse");
		kw.add("Summer");
		kw.add("Brightly Lit");
		kw.add("Cumulus Cloud");
		kw.add("Environment");
		kw.add("Wind");
		kw.add("Idyllic");
		kw.add("Clean");
		kw.add("Light - Natural Phenomenon");
		kw.add("Freedom");
		kw.add("Empty");
		kw.add("Clear Sky");
		kw.add("Simplicity");
		kw.add("Sunlight");
		kw.add("Springtime");
		kw.add("Scenics");
		kw.add("Below");
		kw.add("Vitality");
		kw.add("Overcast");
		kw.add("Cloud - Sky");
		kw.add("High Up");
		kw.add("Dreamlike");
		kw.add("Beautiful");
		kw.add("Lightweight");
		kw.add("Beauty In Nature");
		kw.add("Transparent");
		kw.add("Sun");
		kw.add("Day Dreaming");
		kw.add("Abstract");
		kw.add("Vibrant Color");
		kw.add("Bright");
		kw.add("Day");
		kw.add("White");
		kw.add("Beauty");
		kw.add("Stratosphere");
		kw.add("Climate");

		return kw;
	}

	/**
	 * All theese keywords should NOT probably be picworkflowed in real time (ok, in fact I added one keyword, which should). 
	 * 
	 * @return
	 */
	private static Set<String> testKeywords4() {
		Set<String> kw = new LinkedHashSet<>();

		kw.add("5th Avenue Candy Bar");
		kw.add("of");
		kw.add("and");
		kw.add("honey");
		kw.add("Houston Astros");
		kw.add("PitchUAE2015");
		kw.add("fabric");

		return kw;
	}

	private static Set<Keyword> createTestingKeywordsB() {
		Set<Keyword> kw = new LinkedHashSet<>();

		kw.add(new Keyword("Hello World", 2, 42, 14, 11, 0.999));
		kw.add(new Keyword("Lorem", 3, 111, 10, 101, 0.1));
		kw.add(new Keyword("Ipsum", 8, 89, 99, 88, 8.98));

		return kw;
	}

	private static Set<Keyword> createTestingKeywordsA() {
		Set<Keyword> kw = new LinkedHashSet<>();

		kw.add(new Keyword("coffe", 2, 4, 15, 40, 26.3));
		kw.add(new Keyword("night", 3333, 444, 55, 6, 777.707));

		return kw;
	}

	private static Config createTestingConfig() {
		Config c = new Config();

		c.setWaitStep(-111);
		c.setSamplesCount(-256);

		c.setSsClientid("HAHAHEHE");
		c.setSsClientSecret("You Shall not Pass!!");

		c.setPwQueryTimeout(-999);
		c.setPwBatchSize(-89);
		c.setPwFailedFile(new File("C:\ba-dum-tss.html"));

		c.setWaitBtwSubkeywordingQrs(-77);
		c.setWaitBtwPicflowQrs(-1024);
		c.setWaitBtwExports(-603);

		c.setHwToPicworkflowQueueSize(-604);
		c.setExportQueueSize(-446);

		c.setExportBatchSize(-1091);
		c.setExportFile(new File("/home/r/simpson.csv"));
		c.setQueuesDumpFile(new File("Dumb_as_dump.txt"));

		return c;
	}

}
