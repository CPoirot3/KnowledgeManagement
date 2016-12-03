package com.bupt.poirot.data.mongodb;

import com.bupt.poirot.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by hui.chen on 11/27/16.
 */
public class FetchData {

    public static class ClientHelper {
//        String host = Config.getString("mongo.host");
//        String port = Config.getString("mongo.port");
        public static final MongoClient mongoClient = new MongoClient("localhost", 27017);
    }

    public static MongoClient getMongoClient() {
        return ClientHelper.mongoClient;
    }

    public static void main(String[] args) {
        String dbName = Config.getString("mongo.db.dev");
        String collectionName = "traffic";
        System.out.println(dbName);

        MongoDatabase mongoDatabase = FetchData.getMongoClient().getDatabase(dbName);
        MongoCollection mongoCollection = mongoDatabase.getCollection(collectionName);

        Document filter = new Document();
        filter.put("id", 1);
        Document document = (Document) mongoCollection.findOneAndDelete(filter);

        System.out.println(document);
    }
}
