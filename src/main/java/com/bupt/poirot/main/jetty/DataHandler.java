package com.bupt.poirot.main.jetty;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.atlas.json.JsonObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


public class DataHandler extends AbstractHandler {
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		 
		response.setHeader("Access-Control-Allow-Origin", "*");
		Map<String, String[]> params = request.getParameterMap();

//		System.out.println(params == null);

		JsonObject jsonObject = deal(params);
		response.setContentType("text/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		if (jsonObject == null) {
			System.out.println("no results");
		}
		response.getWriter().println(jsonObject.toString());
		response.flushBuffer();
	}
	
	private JsonObject deal(Map<String, String[]> params) {
		Client client = new Client(params);
		System.out.println("get result");
		return client.getResult();
	}

}
