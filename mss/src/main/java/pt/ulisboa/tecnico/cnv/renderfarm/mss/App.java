package pt.ulisboa.tecnico.cnv.renderfarm.mss;

import java.util.HashSet;

public class App {

	public static void main(String[] args){
		System.out.println("OK");

		try{
			HashSet<String> instances = AWS.getEC2Instances();
			System.out.println("# instances: "+instances.size());
			for(String ip : instances){
				System.out.println(ip);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
