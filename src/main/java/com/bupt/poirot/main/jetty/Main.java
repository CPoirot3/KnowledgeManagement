package com.bupt.poirot.main.jetty;

public class Main {
	public static void main(String[] args) throws Exception {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 15100;
//		ManagedHandler handler = new ManagedHandler("miscsearch", port, "Misc Search Engine") ;
//		handler.setRealHandler(new DataHandler());
		JettyService.main(port, new DataHandler());
	}
}
