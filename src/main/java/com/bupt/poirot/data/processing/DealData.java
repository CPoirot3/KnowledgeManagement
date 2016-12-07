package com.bupt.poirot.data.processing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.jena.atlas.json.JsonObject;
import org.bson.Document;

import com.bupt.poirot.mongodb.SaveToMongo;
import com.bupt.poirot.z3_z3backup.ProveResult;

public class DealData {
	
	public static double MAXX = Double.MIN_VALUE;
	public static double MINX = Double.MAX_VALUE;
	public static double MAXY = Double.MIN_VALUE;
	public static double MINY = Double.MAX_VALUE;

	HashMap<String, LinkedList<SingleData>> carToInfoMap;
//	LinkedList<SingleData> totalDataByTime;
	LinkedList<SingleData> dataSortByTime;
	SaveToMongo saveToMongo;
	DateFormat formater;
	ProveResult proveResult;
	int sectionNumber;
	public LinkedList<JsonObject> results;
	private boolean find;
	
	public DealData(String sectionNumber) {
		try {
			this.sectionNumber = Integer.parseInt(sectionNumber);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		carToInfoMap = new HashMap<>();
		dataSortByTime = new LinkedList<>();
		saveToMongo = new SaveToMongo();
		formater = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
		results = new LinkedList<>();
		find = false;
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
		if (!find) {
			MAXX = Math.max(x, MAXX);
			MINX = Math.min(x, MINX);
			MAXY = Math.max(y, MAXY);
			MINY = Math.min(y, MINY);
		}
		long t = 0;
		try {
			t = formater.parse(time).getTime();
		} catch (ParseException e1) {
			return;
		}
	}
	
	public void deduce() {
		int[] count = new int[sectionNumber * sectionNumber];
		double width = (MAXX - MINX) / sectionNumber;
		double height = (MAXY - MINY) / sectionNumber; 

		for (int i = 0; i < dataSortByTime.size(); i++) {
			SingleData singleData = dataSortByTime.removeFirst();
			float x = singleData.x;
			float y = singleData.y;
			int m = (int)((x - MINX) / width);
			int n = (int)((y - MINY) / height);
			if (x == MAXX) {
				m--;
			}
			if (y == MAXY) {
				m--;
			}
			int s  = m * sectionNumber + n;
			if (s < 0 || s >= count.length) continue;
			count[m * sectionNumber + n]++;
		} 
		
		int index = 0;
		for (int i = 0; i < count.length; i++) {
//			System.out.print(count[i] + " ");
			index = count[i] > count[index] ? i : index;
		}
//		System.out.println();
		
		if (!find) {
			System.out.println("find crowded section");
			System.out.println("max Index :  " + index);
			MINX += (index / sectionNumber) * width;
			MINY += (index % sectionNumber) * height;
			MAXX = MINX + width;
			MAXY = MINY + height;
			System.out.println(MINX + " " + MINY);
			System.out.println(MAXX + " " + MAXY);		
			find = true;
		}
		
		JsonObject jsonObject = new JsonObject();
		Document document = new Document();
		for (int i = 0; i < count.length; i++) {
			document.append(String.valueOf(i), String.valueOf(count[i]));
			jsonObject.put(String.valueOf(i), String.valueOf(count[i]));
		}
		
		results.addLast(jsonObject);
		saveToMongo.save(document);
	}
	
	public JsonObject gerOne() {
		return results.isEmpty() ? null : results.removeFirst();
	}
	
	public void writeMongo(String message) {
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
}
