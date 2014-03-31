package edu.stanford.epad.epadws.handlers.dicom;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import edu.stanford.epad.common.util.EPADConfig;
import edu.stanford.epad.common.util.EPADLogger;
import edu.stanford.epad.epadws.handlers.HandlerUtil;

/**
 * WADO Handler
 */
public class WadoHandler extends AbstractHandler
{
	private static final EPADLogger log = EPADLogger.getInstance();
	private static final EPADConfig config = EPADConfig.getInstance();

	private static final String INTERNAL_EXCEPTION_MESSAGE = "Internal error in WADO route";
	private static final String MISSING_QUERY_MESSAGE = "No query in WADO request";
	private static final String INVALID_METHOD = "Only GET methods valid for the WADO route";

	private static final String INVALID_SESSION_TOKEN_MESSAGE = "Session token is invalid on WADO route";

	@Override
	public void handle(String s, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
	{
		ServletOutputStream responseStream = null;
		String origin = httpRequest.getHeader("Origin"); // CORS request should have Origin header
		int statusCode;

		// Origin header indicates a possible CORS requests, which we support to allow drawing on canvas in GWT Dev Mode.
		if (origin != null) {
			httpResponse.setHeader("Access-Control-Allow-Origin", origin);
			httpResponse.setHeader("Access-Control-Allow-Credentials", "true"); // Needed to allow cookies
		} else {
			httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		}

		httpResponse.setContentType("image/jpeg");
		request.setHandled(true);

		String method = httpRequest.getMethod();
		if ("GET".equalsIgnoreCase(method)) {
			try {
				responseStream = httpResponse.getOutputStream();

				// if (XNATOperations.hasValidXNATSessionID(httpRequest)) {
				if (dummy()) { // TODO Re-enable authentication
					String queryString = httpRequest.getQueryString();
					queryString = URLDecoder.decode(queryString, "UTF-8");
					if (queryString != null) {
						performWADOQuery(queryString, responseStream);
						statusCode = HttpServletResponse.SC_OK;
					} else {
						statusCode = HandlerUtil.warningResponse(HttpServletResponse.SC_BAD_REQUEST, MISSING_QUERY_MESSAGE, log);
					}
					responseStream.flush();
				} else {
					statusCode = HandlerUtil.invalidTokenResponse(INVALID_SESSION_TOKEN_MESSAGE, log);
				}
			} catch (Throwable t) {
				statusCode = HandlerUtil.internalErrorResponse(INTERNAL_EXCEPTION_MESSAGE, log);
			}
		} else {
			httpResponse.setHeader("Access-Control-Allow-Methods", "GET");
			statusCode = HandlerUtil.warningResponse(HttpServletResponse.SC_METHOD_NOT_ALLOWED, INVALID_METHOD, log);
		}
		httpResponse.setStatus(statusCode);
	}

	private boolean dummy()
	{
		return true;
	}

	private void performWADOQuery(String queryString, ServletOutputStream outputStream) throws IOException, HttpException
	{
		String wadoHost = config.getStringPropertyValue("NameServer");
		int wadoPort = config.getIntegerPropertyValue("DicomServerWadoPort");
		String wadoBase = config.getStringPropertyValue("WadoUrlExtension");
		String wadoUrl = buildWADOURL(wadoHost, wadoPort, wadoBase, queryString);

		HandlerUtil.streamGetResponse(wadoUrl, outputStream, log);
	}

	private String buildWADOURL(String host, int port, String base, String queryString)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(host);
		sb.append(":").append(port);
		sb.append(base);
		sb.append(queryString);
		return sb.toString();
	}
}