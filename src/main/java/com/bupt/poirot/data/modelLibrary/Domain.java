package com.bupt.poirot.data.modelLibrary;

import java.util.HashMap;

public class Domain {
	public static HashMap<String, String> domainMap;
	
	public static String getDomain(String message) {
		return domainMap.containsKey(message) ? domainMap.get(message) : "交通";
	}
}
