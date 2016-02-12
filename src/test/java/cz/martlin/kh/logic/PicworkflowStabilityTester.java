package cz.martlin.kh.logic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.picwf.PicwfQueryResult;
import cz.martlin.kh.logic.picwf.PicworkflowQuery;
import cz.martlin.kh.logic.picwf.PicworkflowWrapper;

public class PicworkflowStabilityTester {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;
	private static int id = 0;

	public PicworkflowStabilityTester(Config config) {
		super();
		this.config = config;

	}

	/**
	 * 
	 * @param queries
	 * @param size
	 * @param wait
	 */
	public void testWithOneConnection(int queries, int size, long wait) {
		id++;

		try {
			log.info("Test {} (OneC): Starting: queries={}, size={}, wait={}",
					id, queries, size, wait);

			PicworkflowWrapper pw = new PicworkflowWrapper(config);
			pw.initialize();

			for (int i = 0; i < queries; i++) {
				Set<String> keywords = createTestingsKeywords(size);
				PicworkflowQuery q = pw.createQuerry(keywords);
				PicwfQueryResult r = q.runQuery();

				log.info("Test {} (OneC) {}/{}: Done={}, Not done={}", id, i,
						queries, r.getDone().size(), r.getNotdone().size());

				if (wait > 0) {
					Thread.sleep(wait);
				}
			}

			pw.finish();
			log.info("Test {} (OneC): Done", id);

		} catch (Exception e) {
			log.error("Test " + id + " (OneC) error", e);
		}
	}

	/**
	 * 
	 * @param queries
	 * @param size
	 * @param wait
	 */
	public void testWithReconnect(int queries, int size, long wait) {
		id++;
		try {
			log.info("Test {} (Rcnt): Starting: queries={}, size={}, wait={}",
					id, queries, size, wait);

			PicworkflowWrapper pw = new PicworkflowWrapper(config);
			pw.initialize();

			for (int i = 0; i < queries; i++) {

				Set<String> keywords = createTestingsKeywords(size);
				PicworkflowQuery q = pw.createQuerry(keywords);
				PicwfQueryResult r = q.runQuery();
				pw.reconnect();

				log.info("Test {} (Rcnt) {}/{}: Done={}, Not done={}", id, i,
						queries, r.getDone().size(), r.getNotdone().size());

				if (wait > 0) {
					Thread.sleep(wait);
				}
			}

			log.info("Test {} (Rcnt): Done", id);
		} catch (Exception e) {
			log.error("Test " + id + " (Rcnt) error", e);
		}
	}

	/**
	 * 
	 * @param queries
	 * @param size
	 * @param wait
	 */
	public void testWithEachConnection(int queries, int size, long wait) {
		id++;
		try {
			log.info("Test {} (Each): Starting: queries={}, size={}, wait={}",
					id, queries, size, wait);

			for (int i = 0; i < queries; i++) {
				PicworkflowWrapper pw = new PicworkflowWrapper(config);
				pw.initialize();

				Set<String> keywords = createTestingsKeywords(size);
				PicworkflowQuery q = pw.createQuerry(keywords);
				PicwfQueryResult r = q.runQuery();

				log.info("Test {} (Each) {}/{}: Done={}, Not done={}", id, i,
						queries, r.getDone().size(), r.getNotdone().size());

				if (wait > 0) {
					Thread.sleep(wait);
				}
			}

			log.info("Test {} (Each): Done", id);
		} catch (Exception e) {
			log.error("Test " + id + " (Each) error", e);
		}
	}

	private Set<String> createTestingsKeywords(int size) {
		List<String> universe = new ArrayList<>(TestingKeywords.testKeywords1());
		Set<String> result = new LinkedHashSet<>();
		Random rand = new Random();

		for (int i = 0; i < size; i++) {
			int index = rand.nextInt(universe.size());
			String item = universe.get(index);
			result.add(item);
		}

		return result;

	}

}
