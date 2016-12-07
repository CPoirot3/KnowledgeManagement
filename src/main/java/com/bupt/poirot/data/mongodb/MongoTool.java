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

    public static MongoClient getMongoClient() {
        return ClientHelper.mongoClient;
    }


    public static void flushToDatabase(Document document) {
        String dbName = Config.getString("mongo.db");
        String collectionName = Config.getString("mongo.collection");

        MongoDatabase mongoDatabase = MongoTool.getMongoClient().getDatabase(dbName);
        MongoCollection mongoCollection = mongoDatabase.getCollection(collectionName);
        mongoCollection.insertOne(document);
    }

    public static void main(String[] args) {
//        String dbName = Config.getString("mongo.db");
//        String collectionName = Config.getString("mongo.collection");
//        System.out.println(dbName + "  " + collectionName);
//
//        MongoDatabase mongoDatabase = MongoTool.getMongoClient().getDatabase(dbName);
//        MongoCollection mongoCollection = mongoDatabase.getCollection(collectionName);

//        Document filter = new Document();
//        filter.put("id", 1);
//        Document document = (Document) mongoCollection.findOneAndDelete(filter);
//
//        System.out.println(document);

        flushToDatabase(new Document().append("id", 1));
    }
}
