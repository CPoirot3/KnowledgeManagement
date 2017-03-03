package com.bupt.poirot;

import com.bupt.poirot.jettyServer.jetty.DataHandler;
import com.bupt.poirot.jettyServer.jetty.JettyService;

import java.util.Date;

public class Main {

	public static void main(String[] args) throws Exception {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 15100;
		System.out.println("Server started at : " + new Date());
		JettyService jettyService = new JettyService();
		jettyService.start(port, new DataHandler());
	}
}
