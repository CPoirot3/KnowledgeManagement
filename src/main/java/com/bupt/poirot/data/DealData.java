package com.bupt.poirot.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.jena.atlas.json.JsonObject;
import org.bson.Document;

import com.bupt.poirot.data.modelLibrary.Domain;
import com.bupt.poirot.mongodb.SaveToMongo;
import com.bupt.poirot.z3.ProveResult;
import com.mysql.cj.api.jdbc.Statement;
import com.mysql.cj.jdbc.PreparedStatement;

public class DealData {
	
	public static double MAXX = Double.MIN_VALUE;
	public static double MINX = Double.MAX_VALUE;
	public static double MAXY = Double.MIN_VALUE;
	public static double MINY = Double.MAX_VALUE;

	HashMap<String, LinkedList<SingleData>> carToInfoMap;
//	LinkedList<SingleData> totalDataByTime;
	LinkedList<SingleData> dataSortByTime = new LinkedList<>();
	SaveToMongo saveToMongo;
	DateFormat formater;
	ProveResult proveResult;
	int countLines;
	int sectionNumber;
	public LinkedList<JsonObject> results;
	
	public DealData(int sectionNumber) {
		this.sectionNumber = sectionNumber;
		carToInfoMap = new HashMap<>();
		dataSortByTime = new LinkedList<>();
		saveToMongo = new SaveToMongo();
		formater = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
		countLines = 0;
		results = new LinkedList<>();
	}
	
	

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	private void writeAllDataToMysql() {
		// TODO Auto-generated method stub
		try {
			String dbConnectString = "jdbc:mysql://localhost/hotShows?user=root&password=Poirot373&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false";
			Connection connection = DriverManager.getConnection(dbConnectString);

			Statement stmt = (Statement) connection.createStatement();

			String createTable = "CREATE TABLE IF NOT EXISTS showCount" + "(showName varchar(200) NOT NULL,"
					+ "id varchar(200) NOT NULL," + "score int NOT NULL," + "time TIMESTAMP NOT NULL,"
					+ "type varchar(30)," + "PRIMARY KEY(id, time)" + ") DEFAULT CHARSET=utf8";

			if (stmt.executeUpdate(createTable) == 0) {
				System.out.println("create table success!");
			}

			String query = "insert into showCount (name, time, x, y, status, speed, direction)"
					+ " values (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name) , time=VALUES(time)";
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			for (SingleData singleData : dataSortByTime) {

				ps.setString(1, singleData.carName);
				ps.setLong(2, singleData.time);
				ps.setFloat(3, singleData.x);
				ps.setFloat(4, singleData.y);
				ps.setBoolean(5, singleData.status);
				ps.setFloat(6, singleData.speed);
				ps.setInt(7, singleData.direction);
				ps.executeUpdate();
			}
			connection.close();
		} catch (SQLException e) {

			e.printStackTrace();
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
			// e.printStackTrace();
			return;
		}
		if (x <= 10 || x >= 40 || y <= 100 || y >= 140) {
			return;
		}
		// System.out.println(x + " " + y);
		MAXX = Math.max(x, MAXX);
		MINX = Math.min(x, MINX);
		MAXY = Math.max(y, MAXY);
		MINY = Math.min(y, MINY);
		long t = 0;
		try {
			t = formater.parse(time).getTime();
		} catch (ParseException e1) {
			e1.printStackTrace();
			return;
		}
		
		SingleData singleData = new SingleData(carName, t, Domain.getDomain(message), x, y, states, speed, direction);

//		if (carToInfoMap.containsKey(carName)) {
//			carToInfoMap.get(carName).addLast(singleData);
//		} else {
//			LinkedList<SingleData> queue = new LinkedList<>();
//			queue.addLast(singleData);
//			carToInfoMap.put(carName, queue);
//		}

		dataSortByTime.addLast(singleData);
		countLines++;
		if (countLines > 10000) {
			System.out.println("deduce" + dataSortByTime.size());
			countLines = 0;
			deduce();
		}
	}
	
	public void deduce() {
		System.out.println(sectionNumber * sectionNumber);
		int[] count = new int[sectionNumber * sectionNumber];
		double width = (MAXX - MINX) / sectionNumber;
		double height = (MAXY - MINY) / sectionNumber;
		System.out.println("count length" + count.length);
		System.out.println("total size : " + dataSortByTime.size());
//		System.out.println(width + "  " + height);
//		System.out.println(MINX + " " + MINY);
//		System.out.println(MAXX + " " + MAXY);
		
		for (SingleData singleData : dataSortByTime) {
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
			
//			System.out.println(m * sectionNumber + n);
			int s  = m * sectionNumber + n;
			if (s < 0 || s >= count.length) return;
			count[m * sectionNumber + n]++;
			
		}
		
		for (int i = 0; i < count.length; i++) {
			System.out.print(count[i] + " ");
		}
		
		JsonObject jsonObject = new JsonObject();
		Document document = new Document();
		for (int i = 0; i < count.length; i++) {
			document.append(String.valueOf(i), String.valueOf(count[i]));
			jsonObject.put(String.valueOf(i), String.valueOf(count[i]));
		}
		results.add(jsonObject);
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
