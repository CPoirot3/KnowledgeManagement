package com.bupt.poirot.jettyServer.jetty;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bupt.poirot.data.mongodb.MongoTool;
import com.bupt.poirot.z3.deduce.Client;
import com.bupt.poirot.z3.deduce.TargetInfo;
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
		System.out.println(request.getRequestURL());

		Map<String, String[]> params = request.getParameterMap();
		if (path.endsWith("data")) {
			System.out.println("Begin get result from mongodb");
			Document doc = getResult(params);
			response.setContentType("text/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			if (doc == null) {
				System.out.println("no results");
				doc = new Document();
				doc.put("res", "no result");
			}
			System.out.println(doc.toJson());
			response.getWriter().println(doc.toJson());
			response.flushBuffer();
			System.out.println("end get result from mongodb");

		} else if (path.endsWith("deduce")) {
			System.out.println("Begin deduce");
			deal(params);
			System.out.println("end deduce");
		}
	}

	private Document getResult(Map<String, String[]> params) {
		if (params.get("id") == null || params.get("id").length == 0) {
			return null;
		}
		String id = params.get("id")[0];
		System.out.println("id : " + id);
		MongoTool mongoTool = new MongoTool();
		MongoDatabase mongoDatabase = mongoTool.getMongoClient().getDatabase("traffic");
		MongoCollection mongoCollection = mongoDatabase.getCollection("traffic");

		Document filter = new Document();
		filter.put("id", Integer.valueOf(id));
		Document document = (Document) mongoCollection.findOneAndDelete(filter);
		return document;
	}

	private void deal(Map<String, String[]> params) {
		ParamsParse paramsParse = new ParamsParse(params);
		int id = Integer.valueOf(paramsParse.infos.get("id"));
		System.out.println("id : " + id);
		String scope = paramsParse.infos.get("scope");
		System.out.println("scope : " + scope);
		String topic = paramsParse.infos.get("topic");
		System.out.println("topic : " + topic);
		String min = paramsParse.infos.get("min");
		System.out.println("min : " + min);
		String a = paramsParse.infos.get("severe");
		System.out.println("severe : " + a);
		String b = paramsParse.infos.get("medium");
		System.out.println("medium : " + b);
		String c = paramsParse.infos.get("slight");
		System.out.println("slight : " + c);
		String speed = paramsParse.infos.get("speed");
		System.out.println("speed : " + speed);
//		System.out.println(id + " " + scope + " " + topic + " " + minCars + " " + a + " " + b + " " + c + " " + speed);

		TargetInfo targetInfo = new TargetInfo(id, topic, scope, min, a, b, c, speed);
	    Client client = new Client(targetInfo);
	    client.workflow();
	}
}
