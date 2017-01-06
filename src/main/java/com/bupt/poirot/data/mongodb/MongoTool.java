package com.bupt.poirot.data.mongodb;

import com.bupt.poirot.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoTool {

    public static class ClientHelper {
        static String host = Config.getString("mongo.host");
        static int port = Integer.valueOf(Config.getString("mongo.port"));
        public static final MongoClient mongoClient = new MongoClient(host, port);
    }

    public MongoClient getMongoClient() {
        return ClientHelper.mongoClient;
    }


    public void flushToDatabase(Document document) {
        String dbName = Config.getString("mongo.db");
        String collectionName = Config.getString("mongo.collection");

        MongoDatabase mongoDatabase = getMongoClient().getDatabase(dbName);
        MongoCollection mongoCollection = mongoDatabase.getCollection(collectionName);
        mongoCollection.insertOne(document);
    }

}
