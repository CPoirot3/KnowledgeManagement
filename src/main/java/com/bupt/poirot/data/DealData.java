package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.bson.Document;

import com.bupt.poirot.data.modelLibrary.Domain;
import com.bupt.poirot.mongodb.SaveToMongo;

public class DealData {
	public static double MAXX = Double.MIN_VALUE;
	public static double MINX = Double.MAX_VALUE;
	public static double MAXY = Double.MIN_VALUE;
	public static double MINY = Double.MAX_VALUE;

	HashMap<String, LinkedList<SingleData>> map;
	LinkedList<SingleData> totalDataByTime;
	SaveToMongo saveToMongo;
	DateFormat formater;
	public DealData() {
		map = new HashMap<>();
		totalDataByTime = new LinkedList<>();
		saveToMongo = new SaveToMongo();
		formater = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
	}
	
	public static void stringToOwl(String message) {
		if (message == null || message.length() == 0) {
			return;
		}
		String[] strs = message.split(",");

		String carName = strs[0]; // 车名
		String time = strs[1]; // 时间
		String x = strs[2]; // 纬度
		String y = strs[3]; // 经度
		String states = strs[4]; // 状态
		String speed = strs[5]; // 速度
		String direction = strs[6]; // 方向
		
		System.out.println(time + " " + x + " " + y);

		for (String string : strs) {
			System.out.print(string + " ");
		}
		System.out.println();

		String carURI = "http://bupt/wangfu/" + carName;
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		Property stateProperty = model.createProperty(carURI, "STATE");
		Property speedProperty = model.createProperty(carURI, "SPEED");
		Property directionProperty = model.createProperty(carURI, "DIRECTION");
		Resource car = model.createResource(carURI).addProperty(stateProperty, states).addProperty(speedProperty, speed)
				.addProperty(directionProperty, direction);
		System.out.println(car);
		System.out.println("RDF/XML format : ");
		model.write(System.out);
		System.out.println();
	}

	public void deduce(String message) {

	}

	public void dealSingleData(String line) {
		String[] strs = line.split(",");
		if (strs.length < 4)
			return;
		double y = Double.parseDouble(strs[2]);
		double x = Double.parseDouble(strs[3]);
		if (x <= 10 || x >= 40 || y <= 100 || y >= 140) {
			return;
		}
		MAXX = Math.max(x, MAXX);
		MINX = Math.min(x, MINX);
		MAXY = Math.max(y, MAXY);
		MINY = Math.min(y, MINY);
	}
	
	public void deduceWithSection(int count) {
		
	}
	
	public void deal(String message) {
		if (message == null || message.length() == 0) {
			return;
		}
		String[] strs = message.split(",");

		String carName = strs[0]; // 车名
		String time = strs[1]; // 时间
		String x = strs[2]; // 经度
		String y = strs[3]; // 纬度
		String states = strs[4]; // 状态
		String speed = strs[5]; // 速度
		String direction = strs[6]; // 方向
		
		try {
			Date date = formater.parse(time);
			System.out.println(date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(time);
		// JsonObject jsonObject = new JsonObject();
		// jsonObject.put("CarName", carName);
		// jsonObject.put("Time", time);
		// jsonObject.put("Longitude", x);
		// jsonObject.put("Latitude", y);
		// jsonObject.put("Speed", speed);
		// jsonObject.put("Status", states);
		// jsonObject.put("Direction", direction);
		
		// get SingleData
		String id = "" + (new Date().getTime());
		SingleData singleData = new SingleData(id, message, Domain.getDomain(message));
		
		if (map.containsKey(carName)) {
			map.get(carName).addLast(singleData);
		} else {
			LinkedList<SingleData> queue = new LinkedList<>();
			queue.addLast(singleData);
			map.put(carName, queue);
		}
		totalDataByTime.addLast(singleData);
		
		// get Document
		HashMap<String, Object> map = new HashMap<>();
		map.put("CarName", carName);
		map.put("Time", time);
		map.put("Longitude", x);
		map.put("Latitude", y);
		map.put("Speed", speed);
		map.put("Status", states);
		map.put("Direction", direction);
		Document document = new Document(map);
		saveToMongo.save(document);
	}

	public void deal(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deal(f);
			}
		}
		if (!file.getName().startsWith("粤")) {
			return;
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"))) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] strs = line.split(",");
				if (strs.length < 4)
					continue;
				double y = Double.parseDouble(strs[2]);
				double x = Double.parseDouble(strs[3]);
				if (x <= 10 || x >= 40 || y <= 100 || y >= 140) {
					continue;
				}
				MAXX = Math.max(x, MAXX);
				MINX = Math.min(x, MINX);
				MAXY = Math.max(y, MAXY);
				MINY = Math.min(y, MINY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// test file
//		File file = new File("/Users/hui.chen/Documents/track_exp");
//		deal(file);
//		System.out.println(MAXX);
//		System.out.println(MINX);
//		System.out.println(MAXY);
//		System.out.println(MINY);
		
		
	}
}
