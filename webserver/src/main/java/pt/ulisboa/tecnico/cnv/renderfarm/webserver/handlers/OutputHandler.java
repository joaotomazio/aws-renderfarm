package pt.ulisboa.tecnico.cnv.renderfarm.webserver.handlers;

import java.util.Date;
import com.sun.net.httpserver.HttpHandler;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import java.io.IOException;

public class OutputHandler implements HttpHandler {

	private static final String HOMEPATH = "/home/ec2-user/";

	public void handle(HttpExchange t) throws IOException {

		System.out.println(new Date() + " GET /output");

		String name = t.getRequestURI().getPath().split("output/")[1];
		System.out.println("name: "+name);
		Headers headers = t.getResponseHeaders();
        headers.set("Content-Type","image/bmp");

		File file = new File(HOMEPATH + "output/"+name);
		t.sendResponseHeaders(200, file.length());

		OutputStream outputStream = t.getResponseBody();
		Files.copy(file.toPath(), outputStream);
		outputStream.close();

	}
}
