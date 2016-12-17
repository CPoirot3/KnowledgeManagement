package com.bupt.poirot.data.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SaveToMongo {
	MongoClient client;
	MongoDatabase database;
	MongoCollection<Document> collection;
	
	public SaveToMongo () {
		this.client = new MongoClient("localhost");
		this.database = client.getDatabase("gd");
		this.collection = database.getCollection("alarm");
	}
	
	public void save(Document document) {
		collection.insertOne(document);
	}
}
