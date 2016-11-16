package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.jena.atlas.json.JsonObject;

import com.bupt.poirot.main.Config;
public class Client {
	
	public DealData dealData;
	public LinkedList<String> resultQueue; 
	
	public String domain;
	public String topic;
	public Client(Map<String, String[]> paramsMap) {
		topic = paramsMap.get("topic")[0];
		domain = paramsMap.get("domain")[0];
//		this.dealData = new DealData(sections);
		this.resultQueue = new LinkedList<>();
		accept();
	}

	
	
	
	public JsonObject getResult() {
		return dealData.gerOne();
	}
	
	public void accept() {
		// 通过sparql获得定义好的owl
		
		
		File file = new File(Config.getValue("mac"));
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
            String line = null;
            int count = 0;
			while ((line = reader.readLine()) != null) {
                resultQueue.addLast(line);
                count++;
                if (count > 100000) {
                	return;
                }
                if (resultQueue.size() >= 10000) {
//                	System.out.println("count " + count);
                	deduceWithOneSection();
                	resultQueue.clear();
                	
                }
//                Thread.sleep(1);
            }
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private void deduceWithOneSection() {
//		System.out.println("list size : " + linkedList.size());
//		System.out.println("before deal  dataSortByTime size : " + dealData.dataSortByTime.size());
		for (String message : resultQueue) {
			dealData.deal(message);
		}
		
		dealData.deduce();
	}

	public static void main(String[] args) {
//		System.out.println("begin accept");
		
//		HashMap<String, String> map = new HashMap<>();
//		String values = "10";
//		map.put("sections", values);
//		new Client(map);
		
	}
	
	public void acceptSocket() {
//		try (Socket socket = new Socket("127.0.0.1", 30000)) {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			String message = null;
//			while ((message = reader.readLine()) != null) {
////				System.out.println(message);
//				dealData.deal(message);
//				Thread.sleep(1000);
//			}
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}
