package pt.ulisboa.tecnico.cnv.renderfarm.webserver.handlers;

import java.util.Date;
import com.sun.net.httpserver.HttpHandler;
import java.io.OutputStream;
import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

public class CheckHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange t) throws IOException {

		System.out.println(new Date() + " GET /check");

		String response = "OK";

		t.sendResponseHeaders(200, response.length());

		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();

		System.out.println(response);
	}
}
