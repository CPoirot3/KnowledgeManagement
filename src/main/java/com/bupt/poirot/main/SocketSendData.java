/**
 * 2015年12月28日
 * Poirot
 * 下午4:06:26
 * KnowledgeManagement
 */
package com.bupt.poirot.main;

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
                File file = new File("D:\\shenzhen\\track_exp");
                System.out.println(file.listFiles().length);
				new Thread(new ThreadSolve(s, file)).start();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block0
 			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
