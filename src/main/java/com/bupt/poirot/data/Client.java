package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.atlas.json.JsonObject;

import com.bupt.poirot.main.Config;
public class Client {
	
	DealData dealData;
	Map<String, String[]> paramsMap;
	public Client(Map<String, String[]> paramsMap) {
		this.paramsMap = paramsMap;
		int sections = Integer.parseInt(paramsMap.get("sections")[0]);
		System.out.println("sections " + sections);
		this.dealData = new DealData(sections);
	}
	
	public JsonObject getOne() {
		return dealData.gerOne();
	}
	
	public void accept() {
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
		File file = new File(Config.getValue("mac"));
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
//			PrintStream ps = new PrintStream(socket.getOutputStream());
            String line = null;
           
			while ((line = reader.readLine()) != null) {
                dealData.deal(line);
                Thread.sleep(1);
            }
		} catch (Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("begin accept");
		
		HashMap<String, String[]> map = new HashMap<>();
		String[] values = {"10"};
		map.put("sections", values);
		Client client = new Client(map);
		
		client.accept();
	}
	
}
