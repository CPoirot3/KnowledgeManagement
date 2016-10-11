package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Created by Poirot on 2016/6/15.
 */
public class Client {
	
	MongoCollection<Document> mongoCollection;
	
	public Client(MongoCollection<Document> collection) {
		this.mongoCollection = collection;
	}
	
	public static void stringToOwl(String message) {
//		System.out.println(message);
		
		if (message == null || message.length() == 0) {
			return;
		}
		String[] strs = message.split(",");
		
		String carName = strs[0]; // 车名
		String time = strs[1];  // 时间
		String x = strs[2];     // 纬度
		String y = strs[3];     // 经度
		String states = strs[4];  // 状态
		String speed = strs[5];  // 速度
		String direction = strs[6];  // 方向

//		for (String string : strs) {
//			System.out.print(string + " ");
//		}
//		System.out.println();
		
		String carURI  = "http://bupt/wangfu/" + carName;
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		// create the resource
		// and add the properties cascading style
//		Resource johnSmith = model.createResource(personURI).addProperty(VCARD.FN, fullName).addProperty(VCARD.N,
//				model.createResource().addProperty(VCARD.Given, givenName).addProperty(VCARD.Family, familyName));
		Property stateProperty = model.createProperty(carURI, "STATE");
		Property speedProperty = model.createProperty(carURI, "SPEED");
		Property directionProperty = model.createProperty(carURI, "DIRECTION");
		Resource car = model.createResource(carURI).addProperty(stateProperty, states).addProperty(speedProperty, speed).
				addProperty(directionProperty, direction);
		
		System.out.println("RDF/XML format : ");
		model.write(System.out);
		System.out.println();
	}
	
	public void deduce(String message) {
		
		
	}
	
	public void deal(String message) {
		if (message == null || message.length() == 0) {
			return;
		}
		String[] strs = message.split(",");
		
		String carName = strs[0]; // 车名
		String time = strs[1];  // 时间
		String x = strs[2];     // 经度
		String y = strs[3];     // 纬度
		String states = strs[4];  // 状态
		String speed = strs[5];  // 速度
		String direction = strs[6];  // 方向
		
//		JsonObject jsonObject = new JsonObject();
//		jsonObject.put("CarName", carName);
//		jsonObject.put("Time", time);
//		jsonObject.put("Longitude", x);
//		jsonObject.put("Latitude", y);
//		jsonObject.put("Speed", speed);
//		jsonObject.put("Status", states);
//		jsonObject.put("Direction", direction);
		HashMap<String, Object> map = new HashMap<>(); 
		map.put("CarName", carName);
		map.put("Time", time);
		map.put("Longitude", x);
		map.put("Latitude", y);
		map.put("Speed", speed);
		map.put("Status", states);
		map.put("Direction", direction);
		Document document = new Document(map);
		mongoCollection.insertOne(document);
	}

	public void accept() {
		try (Socket socket = new Socket("127.0.0.1", 30000)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message = null;
			while ((message = reader.readLine()) != null) {
//				stringToOwl(message);
				
				deal(message);
				
				Thread.sleep(1000);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		MongoClient mongoClient = new MongoClient("localhost");
		MongoDatabase database = mongoClient.getDatabase("gd");
		MongoCollection<Document> collection = database.getCollection("RawData");
		Client client = new Client(collection);
		client.accept();
		mongoClient.close();
	}
}
