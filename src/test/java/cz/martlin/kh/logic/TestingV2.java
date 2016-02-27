package cz.martlin.kh.logic;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cz.martlin.kh.logic.harvest2.RelatedKeywordsTree;
import cz.martlin.kh.logic.harvest2.TreeHarvestProcessData;
import cz.martlin.kh.logic.harvest2.TreeRelKeywsHarvest;
import cz.martlin.kh.logic.picwf.PicwfQueryResult;
import cz.martlin.kh.logic.picwf.Picworkflower;

public class TestingV2 {

	private static Config config = new Config();

	public static void main(String[] args) {
		System.out.println("Uncomment something to run test:");

		 testRelatedKeywordsTree();
		// FIXME testTreeHarvester();
		// testTreeHarvesterWithInterrupts();
		//testPicworkflower();

		System.out.println("Done.");
	}

	public static void testTreeHarvesterWithInterrupts() {
		// first
		try {
			runTreeHarvester(5000, null, "No People at Moon", "Space", "NASA",
					"Texas");
		} catch (InterruptedException e) {
		}
		try {
			runTreeHarvester(5000, 4000, "No People at Moon", "Space", "NASA",
					"Texas");
		} catch (InterruptedException e) {
		}

		// Second
		try {
			runTreeHarvester(5000, null, TestingKeywords.testKeywords1());
		} catch (InterruptedException e) {
		}
		try {
			runTreeHarvester(5000, 400, TestingKeywords.testKeywords1());
		} catch (InterruptedException e) {
		}

		// third
		try {
			runTreeHarvester(5000, null, TestingKeywords.testKeywords2());
		} catch (InterruptedException e) {
		}
		try {
			runTreeHarvester(5000, 40, TestingKeywords.testKeywords2());
		} catch (InterruptedException e) {
		}

	}

	public static void runTreeHarvester(Integer count, Integer interruptAfter,
			String... keywords) throws InterruptedException {
		List<String> list = Arrays.asList(keywords);
		Set<String> set = new LinkedHashSet<String>(list);
		runTreeHarvester(count, interruptAfter, set);
	}

	/**
	 * 
	 * @param count
	 * @param interruptAfter
	 *            probably does not work properly
	 * @param keywords
	 * @throws InterruptedException
	 */
	private static void runTreeHarvester(Integer count, Integer interruptAfter,
			final Set<String> keywords) throws InterruptedException {
		final TreeRelKeywsHarvest harvest = new TreeRelKeywsHarvest(config);

		System.out.println("Initing @" + new Date());
		Thread t;
		t = runHarestThread(keywords, harvest);
		Thread.sleep(10000);
		System.out.println("Initited and waited @" + new Date());
		while (true) {
			// System.out.println("Done " +
			// harvest.getPublicData().getDoneCount() + " of " + count);
			if (count != null
					&& harvest.getCurrentData().getDoneCount() > count) {
				break;
			}

			Thread.sleep(100000);
			if (interruptAfter != null
					&& harvest.getCurrentData().getDoneCount() < count) {
				Thread.sleep(interruptAfter);

				System.out.println("Interrupting @" + new Date());
				harvest.interrupt();
				t.interrupt();
				t.join();
				System.out.println("Interrupted @" + new Date());

				System.out.println("Starting again @" + new Date());
				TreeHarvestProcessData data = harvest.getCurrentData();
				TreeRelKeywsHarvest newHarvest = new TreeRelKeywsHarvest(config);
				t = runHarestThread(data, newHarvest);
				System.out.println("Started again @" + new Date());
			}

		}

		System.out.println("Finishing @" + new Date());
		harvest.interrupt();
		t.interrupt();
		t.join();
		System.out.println("Finished @" + new Date());

	}

	private static Thread runHarestThread(final TreeHarvestProcessData data,
			final TreeRelKeywsHarvest harvest) {
		Thread harvThread = new Thread(new Runnable() {

			@Override
			public void run() {
				harvest.run(data);
			}
		}, "HarvT");

		harvThread.start();
		return harvThread;
	}

	private static Thread runHarestThread(final Set<String> keywords,
			final TreeRelKeywsHarvest harvest) {
		Thread harvThread = new Thread(new Runnable() {

			@Override
			public void run() {
				TreeHarvestProcessData data = TreeHarvestProcessData.createNew(
						config, keywords);
				harvest.run(data);
			}
		}, "HarvT");

		harvThread.start();
		return harvThread;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void testRelatedKeywordsTree() {
		final int count = 1000;
		final List<String> initials = Arrays.asList(new String[] {//
				"No People at Moon", });

		TreeHarvestProcessData data = TreeHarvestProcessData.createNew(config,
				new HashSet<String>());
		RelatedKeywordsTree tree = new RelatedKeywordsTree(config, initials,
				data);
		int i = 0;
		for (String keyword : tree) {
			System.err.flush();

			System.out.println(i + ": \t " + keyword + " \t at " + new Date()
					+ " on tree with " + tree.getItemsCount() + " items");

			System.out.flush();

			if (i >= count) {
				break;
			} else {
				i++;
			}
		}

		System.out.println("Done.");
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void testPicworkflower() {
		runPicworkflower("No People at Moon", "Space", "NASA", "Texas");
		System.out.println("------");
		runPicworkflower(TestingKeywords.testKeywords1());
		System.out.println("------");
		runPicworkflower(TestingKeywords.testKeywords2());
		System.out.println("------");
		runPicworkflower(TestingKeywords.testKeywords3());
		System.out.println("------");
	}

	protected static void runPicworkflower(String... keyws) {
		List<String> list = Arrays.asList(keyws);
		Set<String> set = new LinkedHashSet<String>(list);
		runPicworkflower(set);
	}

	protected static void runPicworkflower(Set<String> keyws) {
		Picworkflower picworkflower = new Picworkflower(config);

		System.out.println("Initing...");

		System.out.println("Runing... " + keyws);
		PicwfQueryResult result = picworkflower.run(keyws);

		System.out.println("Completed with " + result.getSuccessRatio() * 100
				+ "% success:");
		System.out.println(result);
		System.out.println("Reqest: " + new TreeSet<>(result.getRequested()));
		System.out.println("Done:   " + new TreeSet<>(result.getDone()));
		System.out.println("Nodone: " + new TreeSet<>(result.getNotdone()));
		System.out.println("Really: "
				+ new TreeSet<>(result.getReallyNotdone()));
		for (Keyword keyword : result.getMetadatas()) {
			System.out.println(" -> " + keyword);
		}
	}

}
