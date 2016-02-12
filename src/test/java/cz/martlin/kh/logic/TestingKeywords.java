package cz.martlin.kh.logic;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestingKeywords {

	/**
	 * Long list.
	 * 
	 * @return
	 */
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

	/**
	 * Normal-sized list.
	 * 
	 * @return
	 */
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

	/**
	 * Very long list.
	 * 
	 * @return
	 */
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
	 * All theese keywords should NOT probably be picworkflowed in real time
	 * (ok, in fact I added one keyword, which should).
	 * 
	 * @return
	 */
	public static Set<String> testKeywords4() {
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

	/**
	 * Hello World, Lorem & Ipsum.
	 * 
	 * @return
	 */
	public static Set<Keyword> createTestingKeywordsB() {
		Set<Keyword> kw = new LinkedHashSet<>();

		kw.add(new Keyword("Hello World", 2, 42, 14, 11, 0.999));
		kw.add(new Keyword("Lorem", 3, 111, 10, 101, 0.1));
		kw.add(new Keyword("Ipsum", 8, 89, 99, 88, 8.98));

		return kw;
	}

	/**
	 * coffe & night.
	 * 
	 * @return
	 */
	public static Set<Keyword> createTestingKeywordsA() {
		Set<Keyword> kw = new LinkedHashSet<>();

		kw.add(new Keyword("coffe", 2, 4, 15, 40, 26.3));
		kw.add(new Keyword("night", 3333, 444, 55, 6, 777.707));

		return kw;
	}

	public static Config createTestingConfig() {
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
