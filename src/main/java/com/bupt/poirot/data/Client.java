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
	
	DealData dealData;
	Map<String, String[]> paramsMap;
	LinkedList<String> linkedList; 
	
	
	public Client(Map<String, String[]> paramsMap) {
		this.paramsMap = paramsMap;
		int sections = Integer.parseInt(paramsMap.get("sections")[0]);
		System.out.println("sections " + sections);
		this.dealData = new DealData(sections);
		this.linkedList = new LinkedList<>();
	}

	
	public JsonObject getOne() {
		return dealData.gerOne();
	}
	
	public void accept() {
		File file = new File(Config.getValue("mac"));
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
            String line = null;
            int count = 0;
			while ((line = reader.readLine()) != null) {
                linkedList.addLast(line);
                count++;
                if (count > 10000000) {
                	return;
                }
                if (linkedList.size() >= 100000) {
                	System.out.println("count " + count);
                	deduceWithOneSection();
                	linkedList.clear();
                	
                }
//                Thread.sleep(1);
            }
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private void deduceWithOneSection() {
		System.out.println("list size : " + linkedList.size());
		System.out.println("before deal  dataSortByTime size : " + dealData.dataSortByTime.size());
		for (String message : linkedList) {
			dealData.deal(message);
		}
		
		dealData.deduce();
	}

	public static void main(String[] args) {
		System.out.println("begin accept");
		
		HashMap<String, String[]> map = new HashMap<>();
		String[] values = {"10"};
		map.put("sections", values);
		Client client = new Client(map);
		client.accept();
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
