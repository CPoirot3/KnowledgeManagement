package com.bupt.poirot.jettyServer.jetty;

import java.net.BindException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyService {
	public void start(int port, AbstractHandler handler) throws Exception {
		Server server = new Server(new QueuedThreadPool(50));
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });
		server.setHandler(handler);
		try {
			server.start();
		} catch (BindException e) {
			e.printStackTrace();
			System.exit(1);
		}
		server.join();
	}
}
