package com.bupt.poirot.mongodb;

import org.apache.jena.atlas.json.JsonObject;
import org.bson.Document;
import org.eclipse.jetty.server.Request;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class GetDataFromMongoDB {
	MongoClient client;
	MongoDatabase database;
	MongoCollection<Document> collection;
	
	public GetDataFromMongoDB () {
		this.client = new MongoClient("localhost");
		this.database = client.getDatabase("gd");
		this.collection = database.getCollection("alarm");
	}
	
	public JsonObject get(Request request) {
		JsonObject jsonObject = new JsonObject();
		return jsonObject;
		
	}
}
