package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	
	DealData dealData;
	public Client() {
		this.dealData = new DealData();
	}
	
	public void accept() {
		try (Socket socket = new Socket("127.0.0.1", 30000)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message = null;
			while ((message = reader.readLine()) != null) {
				dealData.deal(message);
				Thread.sleep(1000);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		System.out.println("begin accept");
		Client client = new Client();
		
		
		client.accept();
	}
}
