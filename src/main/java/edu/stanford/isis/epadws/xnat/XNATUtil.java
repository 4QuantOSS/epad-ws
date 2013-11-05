package edu.stanford.isis.epadws.xnat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import edu.stanford.isis.epad.common.util.EPADConfig;
import edu.stanford.isis.epad.common.util.EPADLogger;

/**
 * @author martin
 */
public class XNATUtil
{
	public static final String XNAT_PROJECT_BASE = "/xnat/data/projects";
	public static final String XNAT_SUBJECT_BASE = "/xnat/data/subjects";

	private static final EPADLogger log = EPADLogger.getInstance();
	private static final EPADConfig config = EPADConfig.getInstance();

	private static final String XNAT_SESSION_BASE = "/xnat/data/JSESSION";
	private static final String LOGIN_EXCEPTION_MESSAGE = "Internal login error";
	private static final String XNAT_UNAUTHORIZED_MESSAGE = "XNAT login not successful";
	private static final String XNAT_LOGIN_ERROR_MESSAGE = "Unexpected XNAT login response";

	public static final class XNATSessionResponse
	{
		public final int statusCode;
		public final String response;

		public XNATSessionResponse(int responseCode, String response)
		{
			this.statusCode = responseCode;
			this.response = response;
		}
	}

	/**
	 * 
	 * @param httpResponse Status set to HttpServletResponse.SC_OK on success,
	 *          HttpServletResponse.SC_INTERNAL_SERVER_ERROR on failure.
	 * @param username
	 * @param password
	 * @return A JSESSIONID value on success; and error string otherwise
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static XNATSessionResponse invokeXNATSessionIDService(HttpServletRequest httpRequest) throws IOException
	{
		String xnatHost = config.getStringConfigurationParameter("XNATServer");
		int xnatPort = config.getIntegerConfigurationParameter("XNATPort");
		String xnatSessionURL = buildURLString(xnatHost, xnatPort, XNAT_SESSION_BASE);
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(xnatSessionURL);
		String username = extractUserNameFromAuthorizationHeader(httpRequest);
		String password = extractPasswordFromAuthorizationHeader(httpRequest);
		String authString = buildAuthorizatonString(username, password);
		XNATSessionResponse xnatSessionResponse;

		log.info("Invoking XNAT session service at " + xnatSessionURL);
		postMethod.setRequestHeader("Authorization", "Basic " + authString);
		int statusCode = client.executeMethod(postMethod);

		if (statusCode == HttpServletResponse.SC_OK) {
			log.info("Successfully invoked XNAT session service");
			InputStreamReader isr = null;
			try {
				StringBuilder sb = new StringBuilder();
				isr = null;
				try {
					isr = new InputStreamReader(postMethod.getResponseBodyAsStream());
					int read = 0;
					char[] chars = new char[128];
					while ((read = isr.read(chars)) > 0) {
						sb.append(chars, 0, read);
					}
				} finally {
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
							log.warning("Error closing XNAT session response stream", e);
						}
					}
				}
				String jsessionID = sb.toString();
				log.info("JSESSIONID returned from XNAT");
				xnatSessionResponse = new XNATSessionResponse(HttpServletResponse.SC_OK, jsessionID);
			} catch (IOException e) {
				log.warning(LOGIN_EXCEPTION_MESSAGE, e);
				xnatSessionResponse = new XNATSessionResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						LOGIN_EXCEPTION_MESSAGE + ": " + e.getMessage());
			} finally {
				if (isr != null)
					isr.close();
			}
		} else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
			log.warning(XNAT_UNAUTHORIZED_MESSAGE);
			xnatSessionResponse = new XNATSessionResponse(statusCode, XNAT_UNAUTHORIZED_MESSAGE);
		} else {
			log.warning(XNAT_LOGIN_ERROR_MESSAGE + "; XNAT status code = " + statusCode);
			xnatSessionResponse = new XNATSessionResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					XNAT_LOGIN_ERROR_MESSAGE + "; XNAT status code = " + statusCode);
		}
		return xnatSessionResponse;
	}

	public static int invalidateXNATSessionID(HttpServletRequest httpRequest) throws IOException
	{
		String xnatHost = config.getStringConfigurationParameter("XNATServer");
		int xnatPort = config.getIntegerConfigurationParameter("XNATPort");
		String xnatSessionURL = buildURLString(xnatHost, xnatPort, XNAT_SESSION_BASE);
		HttpClient client = new HttpClient();
		DeleteMethod deleteMethod = new DeleteMethod(xnatSessionURL);
		String jsessionID = getJSessionIDFromRequest(httpRequest);

		deleteMethod.setRequestHeader("Cookie", "JSESSIONID=" + jsessionID);

		int statusCode = client.executeMethod(deleteMethod);

		if (statusCode != HttpServletResponse.SC_OK)
			log.warning("XNAT delete session call returned status code " + statusCode);

		return statusCode;
	}

	public static boolean hasValidXNATSessionID(HttpServletRequest httpRequest) throws IOException
	{
		String xnatHost = config.getStringConfigurationParameter("XNATServer");
		int xnatPort = config.getIntegerConfigurationParameter("XNATPort");
		String xnatSessionURL = buildURLString(xnatHost, xnatPort, XNAT_SESSION_BASE);
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(xnatSessionURL);
		String jsessionID = getJSessionIDFromRequest(httpRequest);
		boolean isValidSessionID = false;

		getMethod.setRequestHeader("Cookie", "JSESSIONID=" + jsessionID);

		int statusCode = client.executeMethod(getMethod);

		if (statusCode == HttpServletResponse.SC_OK) {
			isValidSessionID = true;
		} else {
			isValidSessionID = false;
		}
		return isValidSessionID;
	}

	public static String extractUserNameFromAuthorizationHeader(HttpServletRequest httpRequest)
	{
		String credentials = extractCredentialsFromAuthorizationHeader(httpRequest);
		String[] values = credentials.split(":", 2);

		if (values.length != 0 && values[0] != null)
			return values[0];
		else
			return "";
	}

	public static String buildURLString(String host, int port, String base)
	{
		return buildURLString(host, port, base, "");
	}

	public static String buildURLString(String host, int port, String base, String ext)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(host);
		sb.append(":").append(port);
		sb.append(base);
		sb.append(ext);

		return sb.toString();
	}

	public static String getJSessionIDFromRequest(HttpServletRequest servletRequest)
	{
		String jSessionID = "";

		Cookie[] cookies = servletRequest.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("JSESSIONID".equalsIgnoreCase(cookie.getName())) {
					jSessionID = cookie.getValue();
					break;
				}
			}
		}
		if (jSessionID.length() == 0)
			log.warning("No JSESESSIONID cookie present in request");
		return jSessionID;
	}

	private static String buildAuthorizatonString(String username, String password)
	{
		String authString = username + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		return authStringEnc;
	}

	private static String extractPasswordFromAuthorizationHeader(HttpServletRequest request)
	{
		String credentials = extractCredentialsFromAuthorizationHeader(request);
		String[] values = credentials.split(":", 2);
		if (values.length > 1 && values[1] != null)
			return values[1];
		else
			return "";
	}

	private static String extractCredentialsFromAuthorizationHeader(HttpServletRequest request)
	{
		String authorizationHeader = request.getHeader("Authorization");
		String credentials = "";

		if (authorizationHeader != null && authorizationHeader.startsWith("Basic")) {
			String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
			credentials = new String(Base64.decodeBase64(base64Credentials), Charset.forName("UTF-8"));
		}
		return credentials;
	}
}
