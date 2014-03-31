package edu.stanford.epad.epadws.handlers.xnat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import edu.stanford.epad.common.util.EPADLogger;
import edu.stanford.epad.epadws.handlers.HandlerUtil;
import edu.stanford.epad.epadws.xnat.XNATQueryUtil;
import edu.stanford.epad.epadws.xnat.XNATSessionOperations;

/**
 * XNAT-based subject retrieval handler. At present, a pretty thin wrapper around an XNAT subjects call. Primarily used
 * for search.
 * <p>
 * <code>
 * curl -b JSESSIONID=<session_id> -X GET "http://[host:port]/epad/subjects?project=*&src=*nice*&columns=label,src" 
 * </code>
 * 
 * @author martin
 */
public class XNATSubjectHandler extends AbstractHandler
{
	private static final EPADLogger log = EPADLogger.getInstance();

	private static final String INVALID_SESSION_TOKEN_MESSAGE = "Invalid session token on XNAT subject route";
	private static final String INTERNAL_EXCEPTION_MESSAGE = "Internal error invoking XNAT on subject route";
	private static final String XNAT_INVOCATION_ERROR_MESSAGE = "Error invoking XNAT on subject route";
	private static final String INVALID_METHOD_MESSAGE = "Only GET, POST, PUT, DELETE methods valid for the XNAT subject route";

	@Override
	public void handle(String base, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
	{
		ServletOutputStream responseStream = null;
		int statusCode;

		httpResponse.setContentType("application/json");
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		request.setHandled(true);

		try {
			responseStream = httpResponse.getOutputStream();

			if (XNATSessionOperations.hasValidXNATSessionID(httpRequest))
				statusCode = invokeXNATSubjectService(base, httpRequest, httpResponse, responseStream);
			else
				statusCode = HandlerUtil.invalidTokenJSONResponse(INVALID_SESSION_TOKEN_MESSAGE, log);
			responseStream.flush();
		} catch (Throwable t) {
			statusCode = HandlerUtil.internalErrorJSONResponse(INTERNAL_EXCEPTION_MESSAGE, t, log);
		}
		httpResponse.setStatus(statusCode);
	}

	private int invokeXNATSubjectService(String base, HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			OutputStream responseStream) throws IOException
	{
		String xnatURL = XNATQueryUtil.buildSubjectsURL(base);
		HttpClient client = new HttpClient();
		String jsessionID = XNATSessionOperations.getJSessionIDFromRequest(httpRequest);
		String queryString = httpRequest.getQueryString();
		int xnatStatusCode;

		if (queryString != null) {
			queryString = queryString.trim();
			xnatURL = xnatURL.replaceFirst("/$", "") + "?" + queryString;
		}

		HttpMethodBase xnatMethod = null;
		String method = httpRequest.getMethod();
		if ("GET".equalsIgnoreCase(method)) {
			xnatMethod = new GetMethod(xnatURL);
		} else if ("DELETE".equalsIgnoreCase(method)) {
			xnatMethod = new DeleteMethod(xnatURL);
		} else if ("POST".equalsIgnoreCase(method)) {
			xnatMethod = new PostMethod(xnatURL);
		} else if ("PUT".equalsIgnoreCase(method)) {
			xnatMethod = new PutMethod(xnatURL);
		}

		if (xnatMethod != null) {
			log.info("Invoking " + xnatMethod.getName() + " on XNAT at " + xnatURL);
			xnatMethod.setRequestHeader("Cookie", "JSESSIONID=" + jsessionID);
			xnatStatusCode = client.executeMethod(xnatMethod);
			if (xnatStatusCode == HttpServletResponse.SC_OK) {
				InputStream xnatResponse = null;
				try {
					xnatResponse = xnatMethod.getResponseBodyAsStream();
					int read = 0;
					byte[] bytes = new byte[4096];
					while ((read = xnatResponse.read(bytes)) != -1) {
						responseStream.write(bytes, 0, read);
					}
				} finally {
					if (xnatResponse != null) {
						try {
							xnatResponse.close();
						} catch (IOException e) {
							log.warning("Error closing XNAT response stream", e);
						}
					}
				}
			} else {
				log.info(XNAT_INVOCATION_ERROR_MESSAGE + "; status code=" + xnatStatusCode);
			}
		} else {
			httpResponse.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE");
			xnatStatusCode = HandlerUtil.warningResponse(HttpServletResponse.SC_METHOD_NOT_ALLOWED, INVALID_METHOD_MESSAGE
					+ "; got " + method, log);
		}
		return xnatStatusCode;
	}
}