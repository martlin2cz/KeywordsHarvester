package cz.martlin.kh.logic.picwf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Keyword;

public class KeywordsJsonMetadataParser {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public KeywordsJsonMetadataParser() {
	}

	/**
	 * Creates keyword from given JSON metadata from server. If creation fails,
	 * logs error and returns null.
	 * 
	 * @param jsonData
	 * @return
	 */
	public Keyword createKeywordOrNull(HashMap<String, Object> jsonData) {
		try {
			return createKeywordOrThrow(jsonData);
		} catch (Exception e) {
			log.error("Error while parsing keyword metadata " + jsonData, e);
			return null;
		}
	}

	/**
	 * From given JSON keywords metadata recieved from server tries to create
	 * Keyword object. If creation fails, throws raw exception (ie.
	 * NumberFormatException).
	 * 
	 * @param jsonData
	 */
	@SuppressWarnings("unchecked")
	public Keyword createKeywordOrThrow(HashMap<String, Object> jsonData) {

		Map<String, Object> fields = (Map<String, Object>) jsonData
				.get("fields");
		List<Object> log = (List<Object>) fields.get("log");

		Object lastObject = log.get(log.size() - 1);

		Map<String, Object> obj = (Map<String, Object>) lastObject;
		String t = (String) obj.get("t"); // name WTF...
		int d = asInt(obj, "d");// downloads
		int l = asInt(obj, "l");// langugage
		int n = asInt(obj, "n");// count
		double r = asDouble(obj, "r");// rating
		int v = asInt(obj, "v");// views

		return new Keyword(t, l, n, v, d, r);

	}

	/**
	 * Finds key in map and tries to convert to int (allowed value types are
	 * {@link Number} or {@link String}).
	 * 
	 * @param map
	 * @param key
	 * @throws NumberFormatException
	 * @return
	 */
	private int asInt(Map<String, Object> map, String key)
			throws NumberFormatException {
		Object val = map.get(key);

		try {
			if (val instanceof String) {
				return Integer.parseInt((String) val);
			} else if (val instanceof Number) {
				return ((Number) val).intValue();
			} else {
				throw new ClassCastException("Not a number");
			}

		} catch (Exception e) {
			throw new NumberFormatException(val
					+ " cannot be converted to number because: "
					+ e.getMessage());
		}
	}

	/**
	 * Finds key in map and tries to convert to double (allowed value types are
	 * {@link Number} or {@link String}).
	 * 
	 * @param map
	 * @param key
	 * @throws NumberFormatException
	 * @return
	 */
	private static double asDouble(Map<String, Object> map, String key)
			throws NumberFormatException {
		Object val = map.get(key);

		try {
			if (val instanceof String) {
				return Integer.parseInt((String) val);
			} else if (val instanceof Number) {
				return ((Number) val).doubleValue();
			} else {
				throw new ClassCastException("Not a number");
			}

		} catch (Exception e) {
			throw new NumberFormatException(val
					+ " cannot be converted to number because: "
					+ e.getMessage());
		}
	}
}
