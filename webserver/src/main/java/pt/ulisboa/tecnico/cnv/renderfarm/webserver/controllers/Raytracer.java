package pt.ulisboa.tecnico.cnv.renderfarm.webserver.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import java.lang.NullPointerException;

public class Raytracer {

	private static final String HOMEPATH = "/home/ec2-user/";

	private String scene;
	private String output;
	private Process process;

	public Raytracer(String scene, String output){
		this.scene = scene;
		this.output = output;
	}

	private String getParam(HashMap<String,String> query, String key) throws NullPointerException{
		String value = query.get(key);
		System.out.println(key+" "+value);
		if(value==null) throw new NullPointerException();
		return value;
	}

	public String render(HashMap<String,String> query) throws NullPointerException{
		String sceneColumns = getParam(query,"sc");
		String sceneRows = getParam(query,"sr");

		String windowColumns = getParam(query,"wc");
		String windowRows = getParam(query,"wr");

		String columnOffset = getParam(query,"coff");
		String rowOffset = getParam(query,"roff");

		return render(sceneColumns, sceneRows, windowColumns, windowRows, columnOffset, rowOffset);
	}

	public String render(String sceneColumns, String sceneRows, String windowColumns, String windowRows, String columnOffset, String rowOffset) {

		String files = HOMEPATH+"raytracer/"+scene+" "+HOMEPATH+"output/"+output;
		String args = sceneColumns+" "+sceneRows+" "+windowColumns+" "+windowRows+" "+columnOffset+" "+rowOffset+" ";
		String command = "sh "+HOMEPATH+"raytracer.sh "+files+" "+args;
		System.out.println("Command: "+command);

		try{
			process = Runtime.getRuntime().exec(command);
			process.waitFor();

			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line).append("\n");

			return sb.toString();
		}
		catch(Exception e){
			return "";
		}

	}
}
