package pt.ulisboa.tecnico.cnv.renderfarm.webserver;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import pt.ulisboa.tecnico.cnv.renderfarm.webserver.handlers.*;

public class Main {

	public static void main(String[] args) throws Exception {

		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/r.html", new RenderHandler());
		server.createContext("/check", new CheckHandler());
		server.createContext("/output", new OutputHandler());
		server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
		server.start();
		System.out.println("Server listening...");
	}
}
