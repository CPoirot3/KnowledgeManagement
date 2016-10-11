package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream; 
import java.io.InputStreamReader;

public class DealData {
	public static double MAXX = Double.MIN_VALUE;
	public static double MINX = Double.MAX_VALUE;
	public static double MAXY = Double.MIN_VALUE;
	public static double MINY = Double.MAX_VALUE;
	
	public static void deal(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deal(f);
			}
		}
		if (!file.getName().startsWith("粤")) {
			return;
		}
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"))) {			
            String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
                String[] strs = line.split(",");
                if (strs.length < 4) continue;
                double y = Double.parseDouble(strs[2]);
                double x = Double.parseDouble(strs[3]);
                if (x <= 10 || x >= 40 || y <= 100 || y >= 140) {
                	continue;
                }
                MAXX = Math.max(x, MAXX);
                MINX = Math.min(x, MINX);
                MAXY = Math.max(y, MAXY);
                MINY = Math.min(y, MINY);
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
		File file = new File("/Users/hui.chen/Documents/track_exp");
		deal(file);
		System.out.println(MAXX);
		System.out.println(MINX);
		System.out.println(MAXY);
		System.out.println(MINY);
	}
}

