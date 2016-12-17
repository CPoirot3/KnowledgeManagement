package com.bupt.poirot.data.dataPreProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import com.bupt.poirot.data.mongodb.SaveToMongo;


public class ParseSingleData {
	public static double MAXX = Double.MIN_VALUE;
	public static double MINX = Double.MAX_VALUE;
	public static double MAXY = Double.MIN_VALUE;
	public static double MINY = Double.MAX_VALUE;

	HashMap<String, LinkedList<SingleData>> carToInfoMap;
	LinkedList<SingleData> totalDataByTime;
	SaveToMongo saveToMongo;
	DateFormat formater;
	public ParseSingleData() {
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
		}
		
		for (int i = 0; i < count.length; i++) {
			System.out.print(count[i] + " ");
		}
	}
	
	public void deal(String message) {
		if (message == null || message.length() == 0) {
			return;
		}
		String[] strs = message.split(",");
		if (strs.length < 7) {
			return;
		}
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
		SingleData singleData = new SingleData(carName, t, "traffic", x, y, states, speed, direction);

		totalDataByTime.addLast(singleData);
		
		if (totalDataByTime.size() > 10000) {
//			writeToMysql();
		}

	}


 
	
	public String readIthLine(int i, File file) {
		String line = null;
		int count = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"))) {
			boolean mark = true;
			while (count < i) {
				count++;
				reader.readLine();
			}
			if (mark) {
				return reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return line;
	}
}

