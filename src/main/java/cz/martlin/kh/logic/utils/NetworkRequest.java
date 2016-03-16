package cz.martlin.kh.logic.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import cz.martlin.kh.logic.exception.NetworkException;

/**
 * Encapsulates standart Java HTPPS connection API with some functions usefull
 * in KH app.
 * 
 * @author martin
 * 
 */
public class NetworkRequest {
	public static final String GET = "GET";
	public static final String POST = "POST";

	private final HttpsURLConnection conn;

	/**
	 * Creates HTTPS request. Fails if {@code urlstr} in bad format, not a HTTPS
	 * or simply cannot establish connection.
	 * 
	 * @param method
	 * @param urlStr
	 * @throws NetworkException
	 */
	public NetworkRequest(String method, String urlStr) throws NetworkException {

		super();

		try {
			URL url = new URL(urlStr);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod(method);
		} catch (Exception e) {
			throw new NetworkException("Error in initialization of request", e);
		}
	}

	/**
	 * Sets to request header with basic auth method (see HTTP 1.0 spec).
	 * 
	 * @param username
	 * @param password
	 */
	public void setBasicAuth(String username, String password) {
		String authString = username + ":" + password;

		String authStringEnc = javax.xml.bind.DatatypeConverter.printBase64Binary(authString.getBytes());
		// TODO FIXME problem with java 1.7 WTF 
		// byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
		//String authStringEnc = new String(authEncBytes);

		conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

	}

	/**
	 * Submits request. Returns reader with response content.
	 * 
	 * @return
	 * @throws NetworkException
	 */
	public Reader submit() throws NetworkException {
		InputStream ins = null;
		InputStreamReader inr = null;
		BufferedReader br = null;

		try {
			ins = conn.getInputStream();
			inr = new InputStreamReader(ins);
			br = new BufferedReader(inr);

			return br;
		} catch (Exception e) {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(inr);
			IOUtils.closeQuietly(ins);

			throw new NetworkException("Error in processing of request", e);
		} finally {
			// leave opened!
		}
	}

	/**
	 * Submits request and output request prints to stdout. Each char
	 * {@code wrapOn} is followed by newline.
	 * 
	 * @param wrapOn
	 */
	public void submitAndPrintResult(char wrapOn) {
		Reader reader = null;
		try {
			reader = submit();
			printResult(reader, wrapOn);
		} catch (NetworkException e) {
			System.err.println(e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 * Outputs reader, each char {@code wrapOn} is followed by newline.
	 * 
	 * @param reader
	 * @param wrapOn
	 * @throws NetworkException
	 */
	public void printResult(Reader reader, char wrapOn) throws NetworkException {
		try {
			int c = reader.read();
			int count = 0;

			while (c != -1) {
				System.out.print((char) c);
				if (c == wrapOn) {
					System.out.println();
				}
				c = reader.read();

				if (count++ > 1000) {
					break;
				}
			}
		} catch (Exception e) {
			throw new NetworkException("Error in printing of request result", e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
}
