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
		Map<String, String[]> map = request.getParameterMap();
		for (String key: map.keySet()) {
			System.out.print(key + "  :");
			for (String string : map.get(key)) {
				System.out.print(string + " ");
			}
			System.out.println();
		}
	
		response.setContentType("text/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		JsonObject jsonObject = new JsonObject();
		jsonObject.put("name", "poirot");
		jsonObject.put("age", 30);
		System.out.println(jsonObject.toString());
		response.getWriter().println(jsonObject.toString());
	}

//	public static void main(String[] args) throws Exception {
//		Server server = new Server(8080);
//		server.setHandler(new DataHandler());
//		server.start();
//		server.join();
//	}
}
