package cz.martlin.kh.logic.subkeyw;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.exception.ResultParserException;
import cz.martlin.kh.logic.utils.NetworkRequest;

/**
 * @see http://www.istockphoto.com/search/jsondoc#31search-term
 * @author martin
 * 
 */
public class IStockphotoWrapper extends AbstractServiceWrapper {

	private final JSONParser parser = new JSONParser();

	public IStockphotoWrapper(Config config) {
		super(config, "iStockphoto");
	}

	@Override
	protected String createImageDataURL(String imageId) {
		return "https://www.istockphoto.com/json/search/?filters={" + //
				"\"facets\":{\"50\":[\"" + imageId + "\"], \"51\":[\"26\"]}}";
	}

	@Override
	protected String createSelectURL(String keyword, int count)
			throws NetworkException {
		return "https://www.istockphoto.com/json/search/?filters={"
				+ //
				"\"facets\":{\"35\":[\"" + enc(keyword) + "\"], \"30\":\""
				+ count + "\"}}";
	}

	@Override
	protected String getImageDataMethod() {
		return NetworkRequest.GET;
	}

	@Override
	protected String getSelectMethod() {
		return NetworkRequest.GET;
	}

	@Override
	protected List<String> getImageKeywords(Reader result)
			throws ResultParserException {
		try {
			JSONObject responseObj = (JSONObject) parser.parse(result);

			JSONArray images = (JSONArray) responseObj.get("results");
			JSONObject image = (JSONObject) images.get(0);
			JSONArray keyws = (JSONArray) image.get("Keywords");
			List<String> keywords = new ArrayList<>(keyws.size());

			for (Object keywordObj : keyws) {
				String keyword = (String) keywordObj;
				keywords.add(keyword);
			}

			return keywords;
		} catch (Exception e) {
			throw new ResultParserException("Error in parsing image keywords",
					e);
		} finally {
			IOUtils.closeQuietly(result);
		}
	}

	@Override
	protected List<String> getImagesIds(Reader result)
			throws ResultParserException {
		try {

			JSONObject responseObj = (JSONObject) parser.parse(result);

			JSONArray images = (JSONArray) responseObj.get("results");
			List<String> imagesIDs = new ArrayList<>(images.size());

			for (Object imageObj : images) {
				JSONObject image = (JSONObject) imageObj;
				Long id = (Long) image.get("id");
				imagesIDs.add(Long.toString(id));
			}

			return imagesIDs;
		} catch (IOException | ParseException e) {
			throw new ResultParserException(
					"Error in parsing images' ids list", e);
		} finally {
			IOUtils.closeQuietly(result);
		}
	}

}
