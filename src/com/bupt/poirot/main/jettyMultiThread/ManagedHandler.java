package com.bupt.poirot.main.jettyMultiThread;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.DispatcherType;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ManagedHandler extends AbstractHandler {
	static class RequestInfo {
		public int id;
		public String url;
		public Date date;
		public String status = "Running";
	}
	static AtomicInteger number = new AtomicInteger(0);
	protected Logger debugLogger = Logger.getLogger("debug");
	protected Logger resultLogger = Logger.getLogger("result"); 

	AbstractHandler _realHandler = null;

	NCSARequestLog _requestLog = null;

	public ManagedHandler(String projectName, int port, String appName) {
		this(projectName, port, appName, false);
	}

	public ManagedHandler(String projectName, int port, String appName, boolean enableRequestLog) {
		String logPath = "/home/search/log/" + projectName + "-" + port;
		
		if (enableRequestLog) {
			try {
				if (new File(logPath).exists()) {
					_requestLog = new NCSARequestLog(logPath + "/jetty.request.log"); //or yyyy_mm_dd format for autorotate;
					_requestLog.setRetainDays(0); //we have rotate on deploy
					_requestLog.setLogTimeZone("PST");
					_requestLog.setAppend(true);
					_requestLog.setExtended(true);
					//_requestLog.setLogCookies(true);
					_requestLog.setLogLatency(true);
					_requestLog.setLogServer(true);
					_requestLog.setPreferProxiedForAddress(true);
					//this.setRequestLog(_requestLog); // if extends RequestLogHandler
					_requestLog.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		init();
	}

	public void setRealHandler(AbstractHandler realHandler) {
		_realHandler = realHandler;
	}

	public void init() { }

	public long timeLimitFor(String target) {
		if (target.endsWith("info")) {
			return 60000L;
		} else if (target.endsWith("captions")) {
			return 1200000L;
		}
		return 10000L;
	}

	protected void onTimeOut() {
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Connection", "close");
		if (target.endsWith("manager") || "/health_check".equalsIgnoreCase(target)) {
			if (request.getHeader("X-Real-Client-IP") != null) {
				Logger.getLogger("debug").info("blocked external /manager request!");
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			PrintWriter writer = response.getWriter();
			// redirect /health_check to /search/manager?healthcheck    -- minlin.zhang
			// Since adding a new server in lainie will use /health_check as health monitor, and I don't have authority
			// to configure it to use "search-http" method by NetScaler cli, I prefer to let search service support
			// standard "HTTP-GET-health_check" method. For more details, please check configurations of NetScaler:
			// http://git.hulu.com/repos/rancid-els-lb.git/tree/configs/slb01a.els.prod.hulu.com
			String method = "/health_check".equalsIgnoreCase(target) ? "healthcheck" : request.getParameter("method");
			response.setContentType("text/html");
			if (method == null) {
				writer.println("<br>please pass method= one of:<br>" +
						"healthcheck status info");
			} else if ("healthcheck".equals(method)) {
				if (resultLogger != null) resultLogger.info("manager: received a health check");
				writer.println("accept");
			} else if (method.equals("status")) {
				writer.println("AWAKE");
			} else if (method.equals("info")) {
				Properties sysProperties = System.getProperties();
				writer.println("<html><body>");
				writeExtraInfo(writer);
				List<RequestInfo> tmp = new LinkedList<RequestInfo>(requestInfo.values());
				Collections.sort(tmp, new Comparator<RequestInfo>() {
					@Override
					public int compare(RequestInfo o1, RequestInfo o2) {
						return (int)(o1.date.getTime() - o2.date.getTime());
					}
				});
				writer.println("active workers: " + tmp.size());
				writer.println("<br/><table border>");
				for (RequestInfo info : tmp) {
					writer.println("<tr><td>" + info.id + "</td><td>" + StringEscapeUtils.escapeXml10(info.url)
							+ "</td><td>" + info.date + "</td><td>" + info.status + "</td></tr>");
				}
				writer.println("</table><br/><br/><table>");
				for (Object key : sysProperties.keySet()) {
					writer.println("<tr><td>" + key + "</td><td>" + sysProperties.getProperty((String)key) + "</td></tr>");
				}
				writer.println("</table></body></html>");
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			writer.flush();
		} else {
			response.setCharacterEncoding("UTF-8");
			long startTime = System.currentTimeMillis();
			long timeOut = startTime + timeLimitFor(target);
			int id = number.getAndIncrement();
			String url = target + "?" + request.getQueryString();
			RequestInfo info = new RequestInfo();
			info.id = id;
			info.url = url;
			info.date = new Date();
			requestInfo.put(id, info);
			resultLogger.info(String.format("%d GET %s", id, url));
			try {
				HandlerThread handlerThread = new HandlerThread(id, _realHandler, target, baseRequest, request, response, requestInfo);
				handlerThread.start();
				while (System.currentTimeMillis() < timeOut && !handlerThread.finished()) {
					Thread.sleep(2L);
				}
				if (handlerThread.finished()) {
					if (handlerThread.getException() != null) {
						throw handlerThread.getException();
					}
				} else {
					resultLogger.info(String.format("%d TIMED OUT", id));
					requestInfo.get(id).status = "TIMED OUT";
					handlerThread.interrupt();
					onTimeOut();
					throw new RuntimeException("searcher thread timed out");
				}
			} catch (Exception e) {
				mailLogger.fatal(url, e);
				debugLogger.info(e);
				throw new RuntimeException(e);
			} finally {
				if (_requestLog != null && baseRequest.getDispatcherType().equals(DispatcherType.REQUEST)) {
					Response logResponse;
					if (response instanceof ServletResponseWrapper) {
						ServletResponseWrapper wrapper = (ServletResponseWrapper)response;
						logResponse = (Response)wrapper.getResponse();
					} else {
						logResponse = (Response)response;
					}
					_requestLog.log(baseRequest, logResponse);
				}
				resultLogger.info(String.format("%d RESPONSE TIME: %d", id, System.currentTimeMillis() - startTime));
			}
		}
	}

	protected void writeExtraInfo(PrintWriter writer) { }
}
