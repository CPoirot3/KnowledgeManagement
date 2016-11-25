package com.bupt.poirot.main;

import com.bupt.poirot.main.jetty.DataHandler;
import com.bupt.poirot.main.jetty.JettyService;

public class Main {
	public static void main(String[] args) throws Exception {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 15100;
//		ManagedHandler handler = new ManagedHandler("miscsearch", port, "Misc Search Engine") ;
//		handler.setRealHandler(new DataHandler());
		System.out.println("Begin listening in 15100:");
		JettyService.main(port, new DataHandler());
	}
}
