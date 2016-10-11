/**
 * 2015年12月28日
 * Poirot
 * 下午4:39:01
 * KnowledgeManagement
 */
package com.bupt.poirot.data;

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
		if (!file.getName().startsWith("粤")) {
			return;
		}
		System.out.println(file.getAbsolutePath());
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"))) {
//			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new PrintStream(socket.getOutputStream()), "utf-8"));
			PrintStream ps = new PrintStream(socket.getOutputStream());
//			int count = 0;
            String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
//				writer.write(line);
//				writer.newLine();
                ps.println(line);
                Thread.sleep(100);
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
