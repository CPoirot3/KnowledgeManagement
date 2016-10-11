package com.bupt.poirot.z3;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService 
public class TestAddWB {
	public int add(int a, int b){
		return a+b;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Endpoint.publish("http://10.108.167.245:8080/ws/TestAddWB", new TestAddWB()); 
	}
}
