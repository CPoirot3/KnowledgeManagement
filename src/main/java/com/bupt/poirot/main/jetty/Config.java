package com.bupt.poirot.main.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	static Properties properties;
	static {
		try (InputStream inputStream = new FileInputStream(new File("conf.prop"))) {
			properties = new Properties();
			properties.load(inputStream);
		} catch (Exception e) {
			System.out.println("load failed");
			e.printStackTrace();
		}
	}
	
	public static String getValue(String key) {
		if (properties != null && properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		return null;
	}
}
