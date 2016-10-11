package com.bupt.poirot.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SaveToMongo {

	public static void main(String[] args) {
		MongoClient client = new MongoClient("localhost");
		MongoDatabase database = client.getDatabase("gd");
		MongoCollection<Document> collection = database.getCollection("alarm");

		client.close();
	}

}
