package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.handlers;

import java.io.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Date;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.DataMetrics;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.TimeMetrics;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.AWS;

import java.net.HttpURLConnection;
import java.net.URL;

import pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.models.*;
import pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.regression.Regression;

public class RequestHandler implements HttpHandler{

    private static ConcurrentHashMap<String, EC2Instance> instances = new ConcurrentHashMap<String, EC2Instance>();
    public static ConcurrentHashMap<String, ArrayList<DataMetrics>> history_metrics = new ConcurrentHashMap<String, ArrayList<DataMetrics>>();
    public static CopyOnWriteArrayList<TimeMetrics> history_time = new CopyOnWriteArrayList<TimeMetrics>();

    public void handle(HttpExchange t) throws IOException {
        System.out.println("--------------------");
        System.out.println("GET /r.html: " + (new Date()).getTime());
        String response = "";
        try{
            HashMap<String,String> query = getQueryMap(t.getRequestURI().getQuery());
            if(!history_metrics.containsKey(query.get("f"))) history_metrics.put(query.get("f"), new ArrayList<DataMetrics>());
            System.out.println("HISTORY METRICS: " + history_metrics.get(query.get("f")).size());
            System.out.println("HISTORY TIME: " + history_time.size());
            Long insEstimative = 0L;
            Long timeEstimative = 0L;
            if(history_metrics.get(query.get("f")).size() > 6){
                DataMetrics request_metrics = new DataMetrics(query, null);
                ArrayList<DataMetrics> metrics_List = history_metrics.get(query.get("f"));
                insEstimative = Regression.computeInstructions(metrics_List, request_metrics);
                if(insEstimative < 0) insEstimative = 0L;
                TimeMetrics request_time = new TimeMetrics(insEstimative);
                timeEstimative = Regression.computeTime(new ArrayList<TimeMetrics>(history_time), request_time);
                if(timeEstimative < 0) timeEstimative = 0L;
            }
            System.out.println("INSTRUCTIONS: " + insEstimative);
            System.out.println("TIME: " + timeEstimative);

            SecureRandom random = new SecureRandom();
            String requestID = new BigInteger(100, random).toString(32);
            Request req = new Request(requestID, new Date().getTime(), timeEstimative);

            printInstancesState();
            checkMachines();
            String ip = getBestMachine(req);
            Headers headers = t.getResponseHeaders();
            headers.set("Content-Type","image/bmp");
            sendGet(ip, t);
            //t.sendResponseHeaders(200, response.length());
        }
        catch(Exception e){
            e.printStackTrace();
            response = "Error 500";
            t.sendResponseHeaders(500, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static HashMap<String, String> getQueryMap(String query){
        String[] params = query.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for(String param : params){
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    private void sendGet(String ip, HttpExchange t) throws Exception {
        String paramsStr = t.getRequestURI().getQuery();
        String url = "http://" + ip + ":8000/r.html?" + paramsStr;
        System.out.println(url);
        System.out.println("--------------------");

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Raytracer/1.0");

        int length = con.getContentLength();
        t.sendResponseHeaders(200,length);

        InputStream in = con.getInputStream();
        OutputStream out = t.getResponseBody();

        byte[] buffer = new byte[1*1024*1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1){
            out.write(buffer, 0, bytesRead);
        }
        out.close();
    }

    public static void checkMachines(){
        ConcurrentHashMap<String, EC2Instance> newMap = new ConcurrentHashMap<String, EC2Instance>();
        HashSet<String> activeIPs = AWS.getEC2Instances();
        HashSet<String> runningIPs = new HashSet<String>();
        for(String a_ip : activeIPs){
            try{
                String url = "http://" + a_ip + ":8000/check";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Raytracer/1.0");
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                runningIPs.add(a_ip);

            } catch(Exception e){
                e.printStackTrace();
            }
        }

        for(String r_ip : runningIPs){
            if(instances.containsKey(r_ip)) newMap.put(r_ip, instances.get(r_ip));
            else newMap.put(r_ip, new EC2Instance(r_ip));
        }

        instances = newMap;

    }

    public void printInstancesState(){
        for(Map.Entry<String, EC2Instance> instance : instances.entrySet()){
            for(Map.Entry<String, Request> request : instance.getValue().getRequests().entrySet()){
                if(request.getValue().getDurationLeft() > 0) System.out.println(request.getValue().getID() + ": " + request.getValue().getDurationLeft());
            }
            System.out.println(instance.getValue().getIP() + ": " + instance.getValue().getTotalTime());
        }
    }

    public static String getBestMachine(Request req){
        Long bestSum = 0L;
        String bestIP = "";
        for(Map.Entry<String, EC2Instance> instance : instances.entrySet()){
            Long machineSum = instance.getValue().getTotalTime();
            if(bestIP.equals("") || machineSum < bestSum){
                bestSum = machineSum;
                bestIP = instance.getValue().getIP();
            }
        }
        instances.get(bestIP).getRequests().put(req.getID(), req);
        System.out.println("BEST IP: " + bestIP);
        return bestIP;
    }
}
