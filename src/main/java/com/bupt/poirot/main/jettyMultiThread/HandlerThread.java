package com.bupt.poirot.main.jettyMultiThread;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HandlerThread extends Thread {
	private int id;
	private AbstractHandler _realHandler;
	private String target;
	private Request baseRequest;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private boolean finished = false;
	private Exception exception = null;
	private Map<Integer, RequestInfo> requestInfo;

	@Override
	public void run() {
		try {
			_realHandler.handle(target, baseRequest, request, response);
		} catch (Exception e) {
			exception = e;
		} finally {
			requestInfo.remove(id);
			finished = true;
		}
	}

	public boolean finished() {
		return finished;
	}

	public Exception getException() {
		return exception;
	}

	public HandlerThread(int id, AbstractHandler _realHandler, String target,
			Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, Map<Integer, RequestInfo> requestInfo) {
		super("Handler #" + String.valueOf(id));
		this.id = id;
		this._realHandler = _realHandler;
		this.target = target;
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
		this.requestInfo = requestInfo;
	}
}
