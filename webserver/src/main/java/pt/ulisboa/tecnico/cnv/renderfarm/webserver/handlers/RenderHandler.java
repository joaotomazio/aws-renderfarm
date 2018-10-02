package pt.ulisboa.tecnico.cnv.renderfarm.webserver.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.security.SecureRandom;
import java.math.BigInteger;

import com.sun.net.httpserver.HttpExchange;

import pt.ulisboa.tecnico.cnv.renderfarm.webserver.controllers.Raytracer;

import pt.ulisboa.tecnico.cnv.renderfarm.mss.controllers.*;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.*;



public class RenderHandler implements HttpHandler {

	private static final String HOMEPATH = "/home/ec2-user/";

	public void handle(HttpExchange t) throws IOException {
		Long start = (new Date()).getTime();
		String response = "";
		String realInstructions = "";
		try{
			HashMap<String,String> query = getQueryMap(t.getRequestURI().getQuery());

			String scene = query.get("f");
			String output_file = output();
			Raytracer raytracer = new Raytracer(scene, output_file);

			String result = raytracer.render(query);
			System.out.println("result: "+result);
			realInstructions = this.analyzeInstructions(result);

			DataMetrics metrics = new DataMetrics(query, Long.parseLong(realInstructions));
			System.out.println("metrics create: ");
			MetricsDB.getInstance().addItem(metrics);
			System.out.println("added database: ");

			//String ip = findMyIP();
			//response = "http://" + ip + ":8000/output/"+ output_file;
			//t.sendResponseHeaders(200, response.length());

			System.out.println(new Date() + " GET /output");

			//String name = t.getRequestURI().getPath().split("output/")[1];
			//System.out.println("name: "+name);
			String name = output_file;
			Headers headers = t.getResponseHeaders();
			headers.set("Content-Type","image/bmp");

			File file = new File(HOMEPATH + "output/"+name);
			t.sendResponseHeaders(200, file.length());

			OutputStream outputStream = t.getResponseBody();
			Files.copy(file.toPath(), outputStream);
			outputStream.close();
		}
		catch(Exception e){
			response = "Error 500";
			e.printStackTrace();
			t.sendResponseHeaders(500, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}

		Long end = (new Date()).getTime();
		Long duration = end - start;
		TimeDB.getInstance().addItem(realInstructions, Long.toString(duration));
	}

	public static HashMap<String, String> getQueryMap(String query){
        String[] params = query.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String param : params){
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }
	public static String output(){
		SecureRandom random = new SecureRandom();
		return (new BigInteger(130, random).toString(32))+".bmp";
	}
	public static String findMyIP(){
		try{
			String cmd = "curl checkip.amazonaws.com";
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			sb.append(line);
			return sb.toString();
		}
		catch(Exception e){
			return "localhost";
		}
	}

	public String analyzeInstructions(String results){
		String analysis = results.split("---DYNAMIC ANALYSIS---\n")[1];
		String[] metrics = analysis.split("\n");
		return metrics[2].split(":")[1];
	}

}
