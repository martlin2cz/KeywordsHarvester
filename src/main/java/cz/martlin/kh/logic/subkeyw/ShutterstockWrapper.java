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

public class ShutterstockWrapper extends AbstractServiceWrapper {

	private final JSONParser parser = new JSONParser();

	public ShutterstockWrapper(Config config) {
		super(config, "Shutterstock");
	}

	@Override
	protected String createSelectURL(String keyword, int count)
			throws NetworkException {
		return "https://api.shutterstock.com/v2/images/search" //
				+ "?query=" + enc(keyword) + "&per_page=" + count;
	}

	@Override
	protected String createImageDataURL(String imageId) {
		return "https://api.shutterstock.com/v2/images/" + imageId;
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
	protected void doAuthorisation(NetworkRequest request) {
		super.doAuthorisation(request);

		String username = getConfig().getSsClientID();
		String password = getConfig().getSsClientSecret();

		request.setBasicAuth(username, password);
	}

	@Override
	protected List<String> getImageKeywords(Reader result)
			throws ResultParserException {
		try {
			JSONObject responseObj = (JSONObject) parser.parse(result);

			JSONArray keyws = (JSONArray) responseObj.get("keywords");
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

			JSONArray images = (JSONArray) responseObj.get("data");
			List<String> imagesIDs = new ArrayList<>(images.size());

			for (Object imageObj : images) {
				JSONObject image = (JSONObject) imageObj;
				String id = (String) image.get("id");
				imagesIDs.add(id);
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
