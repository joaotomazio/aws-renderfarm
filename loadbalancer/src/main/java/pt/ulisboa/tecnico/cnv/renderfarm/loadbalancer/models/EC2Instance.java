package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EC2Instance{

    private String ip;
    private ConcurrentHashMap<String, Request> requests = new ConcurrentHashMap<String, Request>();

    public EC2Instance(String ip){
        this.ip = ip;
    }

    public ConcurrentHashMap<String, Request> getRequests(){
        return this.requests;
    }

    public String getIP(){
        return this.ip;
    }

    public Long getTotalTime(){
        Long totalTime = 0L;
        for(Map.Entry<String, Request> request : this.requests.entrySet()){
            Long duration = request.getValue().getDurationLeft();
            if(duration < 0) requests.remove(request.getValue().getID());
            else totalTime += duration;
        }
        return totalTime;
    }
}
