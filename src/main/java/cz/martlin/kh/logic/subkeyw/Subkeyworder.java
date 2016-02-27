package cz.martlin.kh.logic.subkeyw;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.StuffProvider;
import cz.martlin.kh.logic.Config;

/**
 * Implements "simple" subkeywording. This subkeyworder cannot be interrupted.
 * TODO add interruptabillity, it is recommanded to use minimal
 * {@link Config#getSKWaitBetweenServices()}.
 * 
 * @author martin
 * 
 */
public class Subkeyworder {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;
	private final List<AbstractServiceWrapper> services;

	/**
	 * You should use {@link StuffProvider#getSubkeyworder()} to instantite.
	 * 
	 * @param config
	 * @param services
	 */
	public Subkeyworder(Config config, Set<AbstractServiceWrapper> services) {
		this.config = config;
		this.services = new ArrayList<>(services);
	}

	/**
	 * Loads related keywords of given keyword using provided services.
	 * 
	 * @param keyword
	 * @return
	 */
	public LinkedHashSet<String> subkeyword(String keyword) {
		log.info("Subkeywording keyword {} with services {}", keyword, services);

		LinkedHashSet<String> result = new LinkedHashSet<>();
		int count = config.getSkSamplesCount();

		for (AbstractServiceWrapper service : services) {
			Set<String> subkeyws = service.getRelatedKeywords(keyword, count);

			if (subkeyws != null) {
				result.addAll(subkeyws);
			} else {
				log.warn("Service {} did not give response for keyword {}",
						service, keyword);
			}

			waitUntilNextService();
		}

		log.info("Subkeywording of keyword {} gave {} new keywords", keyword,
				result.size());

		return result;
	}

	/**
	 * Waits time given by config ({@link Config#getSKWaitBetweenServices()}).
	 */
	private void waitUntilNextService() {

		long wait = config.getSkWaitBetweenServices();

		try {
			Thread.sleep(wait);
		} catch (InterruptedException eIgnre) {
		}
	}
}
