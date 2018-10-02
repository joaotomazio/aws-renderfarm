package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.handlers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

import pt.ulisboa.tecnico.cnv.renderfarm.mss.controllers.MetricsDB;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.controllers.TimeDB;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.DataMetrics;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.TimeMetrics;

public class CheckDBs implements Runnable{

    private Thread t;

    public void start(){
        if(t == null){
            t = new Thread(this);
            t.start();
        }
    }

    public void run(){
        try{
            MetricsDB dbMetrics = MetricsDB.getInstance();
            TimeDB dbTime = TimeDB.getInstance();
            while(true){
                for(Map.Entry<String, ArrayList<DataMetrics>> fileSet : RequestHandler.history_metrics.entrySet()){
                    ArrayList<DataMetrics> metrics_List = dbMetrics.getItem(fileSet.getKey());
                    RequestHandler.history_metrics.put(fileSet.getKey(), metrics_List);
                }
                ArrayList<TimeMetrics> time_List = dbTime.getItems();
                CopyOnWriteArrayList<TimeMetrics> time_concList = new CopyOnWriteArrayList<TimeMetrics>(time_List);
                RequestHandler.history_time = time_concList;

                Thread.sleep(10000);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
