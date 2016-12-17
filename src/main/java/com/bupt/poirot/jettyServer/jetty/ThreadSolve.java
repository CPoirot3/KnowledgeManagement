/**
 * 2015年12月28日
 * Poirot
 * 下午4:39:01
 * KnowledgeManagement
 */
package com.bupt.poirot.jettyServer.jetty;

import java.io.*;
import java.net.Socket;

/**
 * @author Poirot
 *
 */
public class ThreadSolve implements Runnable {
	Socket socket;
	File file;
	public ThreadSolve(Socket socket, File file) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.file = file;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		sendData(file);
	}
	
	public void sendData(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				sendData(f);
			}
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
			PrintStream ps = new PrintStream(socket.getOutputStream());
			
            String line;
           
			while ((line = reader.readLine()) != null) {
                ps.println(line);
                Thread.sleep(500);
            }
		} catch (Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
