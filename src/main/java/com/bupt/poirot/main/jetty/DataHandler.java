package com.bupt.poirot.main.jetty;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.poirot.data.mongodb.FetchData;
import com.bupt.poirot.utils.Client;
import com.bupt.poirot.utils.Config;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


public class DataHandler extends AbstractHandler {
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");

		String path = request.getPathInfo();
		System.out.println(path);

		Map<String, String[]> params = request.getParameterMap();
		if (path.endsWith("data")) {
			Document doc = getResult(params);

			response.setContentType("text/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			if (doc == null) {
				System.out.println("no results");
				doc = new Document();
				doc.put("res", "no result");
			}
			response.getWriter().println(doc.toString());
			response.flushBuffer();
		} else if (path.endsWith("deduce")) {
			deal(params);
		}

	}

	private Document getResult(Map<String, String[]> params) {
		if (params.get("id") == null || params.get("id").length == 0) {
			return null;
		}
		String id = params.get("id")[0];
		MongoDatabase mongoDatabase = FetchData.getMongoClient().getDatabase(Config.getString("mongo.db"));
		MongoCollection mongoCollection = mongoDatabase.getCollection("traffic");

		Document filter = new Document();
		filter.put("id", id);
		Document document = (Document) mongoCollection.findOneAndDelete(filter);

		return document;
	}

	private void deal(Map<String, String[]> params) {
	    Client client = new Client(params);
	    client.workflow();
	}

}
