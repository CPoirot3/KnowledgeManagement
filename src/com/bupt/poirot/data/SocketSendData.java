package com.bupt.poirot.data;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Poirot
 *
 */
public class SocketSendData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try (
				ServerSocket serverSocket = new ServerSocket(30000);
				){
//			BufferedWriter bufferedWriter = new BufferedWriter(s.getInputStream());
			while (true) {
				Socket s = serverSocket.accept();
                File file = new File("/Users/hui.chen/Documents/track_exp");
//                System.out.println(file.listFiles().length);
				new Thread(new ThreadSolve(s, file)).start();
			}
		} catch (UnknownHostException e) {
 			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
