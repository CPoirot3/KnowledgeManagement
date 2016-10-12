package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

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
//		Collections.sort(totalDataByTime, new Comparator<SingleData>() {
//
//			@Override
//			public int compare(SingleData o1, SingleData o2) {
//				// TODO Auto-generated method stub
//				return (int)(o1.time - o2.time);
//			}
//		});
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
//		System.out.println(message);
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
		
		if (totalDataByTime.size() > 10000) {
			writeToMysql();
		}
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

	private void writeToMysql() {
		// TODO Auto-generated method stub
		try {
			Connection connection = DriverManager.getConnection(dbConnectString);

			Statement stmt = (Statement) connection.createStatement();
			
			String createTable = "CREATE TABLE IF NOT EXISTS showCount"
					+ "(showName varchar(200) NOT NULL,"
					+ "id varchar(200) NOT NULL,"
					+ "score int NOT NULL,"
					+ "time TIMESTAMP NOT NULL,"
					+ "type varchar(30),"
					+ "PRIMARY KEY(id, time)"
					+ ") DEFAULT CHARSET=utf8";

	        if(stmt.executeUpdate(createTable)==0)  {
    			System.out.println("create table success!");
    		}

		    String query = "insert into showCount (name, time, x, y, status, speed, direction)"
		        + " values (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name) , time=VALUES(time)";
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement(query,  
	                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			Timestamp timestamp = new Timestamp(endTime);
	        for (SingleData singleData : totalDataByTime) {
	        	
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

	public List<BufferedReader> list = new ArrayList<>(); 
	
	
	public String readIthLine(int i, File file) {
		String line = null;
		int count = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"))) {
			boolean mark = true;
			while (count < i) {
				count++;
				reader.readLine();
//				if (reader.readLine() == null) {
//					System.out.println("tes");
//					mark = false;
//					break;
//				}
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
	
	public void dealFile(File[] files) {
		
		for (int i = 1; i <= 100; i++) {
			for (File file : files) {
				String line = readIthLine(i, file);
				System.out.println(line);
			}
		}
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DealData dealData = new DealData();
		// test file
		File file = new File("/Users/hui.chen/data/track_exp/");
		dealData.dealFile(file.listFiles());
		dealData.deduce(10);
			
//		dealData.deduce(10);
//		System.out.println(MAXX);
//		System.out.println(MINX);
//		System.out.println(MAXY);
//		System.out.println(MINY);
		
	}

	public void writeToSingleFile() {
		boolean found = true;
		File output = new File("data.txt");
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(output));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		System.out.println("test " + list.size());
		while (found) {
			found = false;
			for (BufferedReader reader : list) {
				try {
					String line = reader.readLine();
					System.out.println(line);
					if (line != null) {
						bufferedWriter.write(line);
						found = true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
			try {
				bufferedWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void dealDirectory(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				dealDirectory(f);
			}
			return;
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
				deal(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}

