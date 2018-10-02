package pt.ulisboa.tecnico.cnv.renderfarm.webserver.controllers;

import java.lang.Thread;
import java.util.Date;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.HashMap;

public class Metrics {

	public final long threadId = Thread.currentThread().getId();
	public final Date date = new Date();
	public String ip = "localhost";
	public long meth, bb, inst, obj, pri_arr, cplx_arr, md_arr, fie_get, fie_set, load_ins, store_ins;
	private static final String HOMEPATH = "/home/ec2-user";
	private static final String FILENAME = HOMEPATH + "/metrics.dat";
	private HashMap<String, String> queries;

	public Metrics(HashMap<String, String> queries){
		this.queries = queries;
	}

	public void setIP(String ip){
		this.ip = ip;
	}
	public void analyze(String results){
		String analysis = results.split("---DYNAMIC ANALYSIS---\n")[1];
		String[] metrics = analysis.split("\n");
		Long[] values = new Long[metrics.length];
		for(int i = 0; i < metrics.length; i++){
			String metric = metrics[i].split(":")[1];
			values[i] = Long.parseLong(metric);
		}
		this.meth = values[0];
		this.bb = values[1];
		this.inst = values[2];
		this.obj = values[3];
		this.pri_arr = values[4];
		this.cplx_arr = values[5];
		this.md_arr = values[6];
		this.fie_get = values[7];
		this.fie_set = values[8];
		this.load_ins = values[9];
		this.store_ins = values[10];
	}

	public String renderArgs(){
		String args = "";
		for(String key : queries.keySet()){
			args += key + "=" + queries.get(key) + " ";
		}
		return args;
	}

	public static synchronized void write(Metrics m){
		try{
			FileWriter fw = new FileWriter(FILENAME, true);
			BufferedWriter bw = new BufferedWriter(fw);
			String content = "--------" +
						"\nInstance IP: " + m.ip +
						"\nHandler thread: " + m.threadId +
						"\nDate: " + m.date +
						"\nRender Args: " + m.renderArgs() +
						"\nMethods count: " + m.meth +
						"\nBasic blocks count: " + m.bb +
						"\nInstructions count: " + m.inst +
						"\nObjects allocated: " + m.obj +
						"\nArrays of primitive types allocated: " + m.pri_arr +
						"\nArrays of complex types allocated: " + m.cplx_arr +
						"\nMultidimensaional arrays allocated: " + m.md_arr +
						"\nFields fetched from Objects: " + m.fie_get +
						"\nFields set on Objects: " + m.fie_set +
						"\nLoad Instructions: " + m.load_ins +
						"\nStore Instructions: " + m.store_ins +
						"\n";
			bw.write(content);
			bw.close();
		} catch(Exception e){
			System.out.println(e.toString());
		}
	}
}
