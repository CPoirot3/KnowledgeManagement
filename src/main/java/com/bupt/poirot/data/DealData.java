package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
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

	HashMap<String, LinkedList<SingleData>> carToInfoMap;
	LinkedList<SingleData> totalDataByTime;
	SaveToMongo saveToMongo;
	DateFormat formater;
	public DealData() {
		carToInfoMap = new HashMap<>();
		totalDataByTime = new LinkedList<>();
		saveToMongo = new SaveToMongo();
		formater = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
	}
	
	public static void stringToObject(String message) {
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
//		System.out.println(time + " " + x + " " + y);
//		for (String string : strs) {
//			System.out.print(string + " ");
//		}
//		System.out.println();

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

	public void deduce(int sectionsNumber) {
		int[] count = new int[sectionsNumber * sectionsNumber];
		double width = (MAXX - MINX) / sectionsNumber;
		double height = (MAXY - MINY) / sectionsNumber;
		System.out.println("total size : " + totalDataByTime.size());
		System.out.println(width + "  " + height);
		System.out.println(MINX + " " + MINY);
		System.out.println(MAXX + " " + MAXY);
		Collections.sort(totalDataByTime, new Comparator<SingleData>() {

			@Override
			public int compare(SingleData o1, SingleData o2) {
				// TODO Auto-generated method stub
				return (int)(o1.time - o2.time);
			}
		});
		for (SingleData singleData : totalDataByTime) {
			float x = singleData.x;
			float y = singleData.y;
			int m = (int)((x - MINX) / width);
			int n = (int)((y - MINY) / height);
			if (x == MAXX) {
				m--;
			} 
			if (y == MAXY) {
				n--;
			}
			count[m * sectionsNumber + n]++;
//			try {
//				count[m * sectionsNumber + n]++;
//			} catch (Exception e) {
//				// TODO: handle exception
//				System.out.println(x + " " + y + "  " + (m * sectionsNumber + n));
//			}
		}
		
		for (int i = 0; i < count.length; i++) {
			System.out.print(count[i] + " ");
		}
//		for ()
	}

//	public void dealSingleData(String line) {
//		String[] strs = line.split(",");
//		if (strs.length < 4)
//			return;
//		double y = Double.parseDouble(strs[2]);
//		double x = Double.parseDouble(strs[3]);
//		if (x <= 10 || x >= 40 || y <= 100 || y >= 140) {
//			return;
//		}
//		MAXX = Math.max(x, MAXX);
//		MINX = Math.min(x, MINX);
//		MAXY = Math.max(y, MAXY);
//		MINY = Math.min(y, MINY);
//	}
	
	public void deal(String message) {
		if (message == null || message.length() == 0) {
			return;
		}
		String[] strs = message.split(",");
		if (strs.length < 7) {
			return;
		}
//		System.out.println(message);
		String carName = strs[0]; // 车名
		String time = strs[1]; // 时间
		boolean states = strs[4].equals("1") ? true : false; // 状态
		float x, y, speed;
		byte direction;
		try {
			x = Float.parseFloat(strs[3]); // 经度
			y = Float.parseFloat(strs[2]); // 纬度
			speed = Float.parseFloat(strs[5]); // 速度
			direction = Byte.parseByte(strs[6]); // 方向
		} catch (Exception e) {
			// TODO: handle exception
//			System.out.println(message);
//			e.printStackTrace(); 
			return;
		}
		if (x <= 10 || x >= 40 || y <= 100 || y >= 140) {
			return;
		}
//		System.out.println(x + " " + y);
		MAXX = Math.max(x, MAXX);
		MINX = Math.min(x, MINX);
		MAXY = Math.max(y, MAXY);
		MINY = Math.min(y, MINY);
		long t = 0;
		try {
			t = formater.parse(time).getTime();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		// get SingleData
		SingleData singleData = new SingleData(carName, t, Domain.getDomain(message), x, y, states, speed, direction);
		
//		if (carToInfoMap.containsKey(carName)) {
//			carToInfoMap.get(carName).addLast(singleData);
//		} else {
//			LinkedList<SingleData> queue = new LinkedList<>();
//			queue.addLast(singleData);
//			carToInfoMap.put(carName, queue);
//		}
		totalDataByTime.addLast(singleData);
		
		// get Document
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("CarName", carName);
//		map.put("Time", time);
//		map.put("Longitude", x);
//		map.put("Latitude", y);
//		map.put("Speed", speed);
//		map.put("Status", states);
//		map.put("Direction", direction);
//		Document document = new Document(map);
//		saveToMongo.save(document);
	}

	public void dealFile(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				dealFile(f);
			}
		}
		if (!file.getName().startsWith("粤")) {
			return;
		}
		if (totalDataByTime.size() > 110000) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"))) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
//				dealSingleData(line);
				deal(line);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DealData dealData = new DealData();
		// test file
		File file = new File("/Users/hui.chen/Documents/track_exp");
		dealData.dealFile(file);
		dealData.deduce(10);
		System.out.println(MAXX);
		System.out.println(MINX);
		System.out.println(MAXY);
		System.out.println(MINY);
		
		
	}

}
