package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.handlers.RequestHandler;
import pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.handlers.CheckDBs;

public class Main {

    public static void main(String[] args) throws Exception {
        CheckDBs checkRun = new CheckDBs();
        checkRun.start();
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.createContext("/r.html", new RequestHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server listening...");
    }
}
