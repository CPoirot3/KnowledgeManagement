package com.bupt.poirot.main.jetty;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.atlas.json.JsonObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.bupt.poirot.data.Client;
 

public class DataHandler extends AbstractHandler {
	
	public static Client client;
	
	public HashMap<String, String> mem; 
	
	public DataHandler() {
		this.mem = new HashMap<>();
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Map<String, String[]> params = request.getParameterMap();

		System.out.println(params.containsKey("district") + " :" + params.get("district")[0]);
		begin(params.get("district")[0]);
		response.setContentType("text/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		JsonObject jsonObject = getDataFromDatabase(request);
		if (jsonObject == null) {
			System.out.println("no results");
		}
		System.out.println(client.getOne() == null);
		System.out.println(jsonObject.toString());
		response.getWriter().println(jsonObject.toString());
		response.flushBuffer();
	}

	private JsonObject getDataFromDatabase(HttpServletRequest request) {
		JsonObject jsonObject = client.getOne();
		return jsonObject;
	}
	
	private void begin(String string) {
		System.out.println(string);
		HashMap<String, String> map = new HashMap<>();
		String defaultValue = "10";
//		if (paramsMap.containsKey("district") && paramsMap.get("district")[0] != null) {
//			defaultValue = paramsMap.get("district")[0];
//		}
		if (string != null && string.length() > 0) {
			defaultValue = string;
		}
		if (mem == null) {
			mem = new HashMap<>();
		}
		map.put("sections", defaultValue);
		System.out.println("defaultvalue : " + defaultValue);
		if (client == null || !defaultValue.equals(mem.get("sections"))) {
			mem.put("sections", defaultValue);
			client = new Client(map);
		}
	}

//	public static void main(String[] args) throws Exception {
//		Server server = new Server(8080);
//		server.setHandler(new DataHandler());
//		server.start();
//		server.join();
//	}
}
