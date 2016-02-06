package cz.martlin.kh.logic.subkeyw;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.exception.ResultParserException;
import cz.martlin.kh.logic.utils.Interruptable;
import cz.martlin.kh.logic.utils.NetworkRequest;

public abstract class AbstractServiceWrapper implements Interruptable {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;
	private final String name;

	private boolean interrupted;

	public AbstractServiceWrapper(Config config, String name) {
		super();
		this.config = config;
		this.name = name;
	}

	public Config getConfig() {
		return config;
	}

	public String getName() {
		return name;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	@Override
	public void interrupt() {
		this.interrupted = true;
	}

	protected abstract String createSelectURL(String keyword, int count)
			throws NetworkException;

	protected abstract String createImageDataURL(String imageId)
			throws NetworkException;

	protected abstract String getSelectMethod();

	protected abstract String getImageDataMethod();

	protected void doAuthorisation(NetworkRequest request) {
	}

	protected abstract List<String> getImageKeywords(Reader result)
			throws ResultParserException;

	protected abstract List<String> getImagesIds(Reader result)
			throws ResultParserException;

	public List<String> searchImageKeywords(String imageId) {
		try {
			String adress = createImageDataURL(imageId);
			String method = getImageDataMethod();

			NetworkRequest request = new NetworkRequest(method, adress);

			doAuthorisation(request);

			Reader result = request.submit();

			List<String> keywords = getImageKeywords(result);
			return keywords;
		} catch (Exception e) {
			log.error("Error during search image keywords with " + this.name, e);
		}
		return null;
	}

	public List<String> searchImagesIDsOfKeyword(String keyword, int count) {
		try {
			String adress = createSelectURL(keyword, count);
			String method = getSelectMethod();

			NetworkRequest request = new NetworkRequest(method, adress);
			doAuthorisation(request);

			Reader result = request.submit();

			List<String> keywords = getImagesIds(result);
			return keywords;
		} catch (Exception e) {
			log.error("Error during search images with " + this.name, e);
		}

		return null;
	}

	public Set<String> getRelatedKeywords(String keyword, int count) {
		interrupted = false;

		Set<String> keywords = new LinkedHashSet<>();

		log.info("Querying " + name + " to get {} related keywords of  {}",
				count, keyword);

		List<String> imagesIds = searchImagesIDsOfKeyword(keyword, count);
		if (imagesIds == null) {
			return null;
		}

		for (String imageId : imagesIds) {
			if (interrupted) {
				return null;
			}

			List<String> imageKeyws = searchImageKeywords(imageId);
			if (imageKeyws == null) {
				return null;
			}
			keywords.addAll(imageKeyws);
		}

		return keywords;
	}

	/**
	 * Encodes given string to valid URL string.
	 * 
	 * @param word
	 * @return
	 * @throws NetworkException
	 */
	public static String enc(String string) throws NetworkException {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new NetworkException("UTF8 not supported", e);
		}
	}

}