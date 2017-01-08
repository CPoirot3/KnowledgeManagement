package com.bupt.poirot.main;

import com.bupt.poirot.jettyServer.jetty.DataHandler;
import com.bupt.poirot.jettyServer.jetty.JettyService;
import org.apache.log4j.spi.LoggerFactory;

import java.util.logging.Logger;

public class Main {

	public static void main(String[] args) throws Exception {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 15100;
		System.out.println("Begin listening in 15100:");
		JettyService jettyService = new JettyService();
		jettyService.start(port, new DataHandler());
	}
}
