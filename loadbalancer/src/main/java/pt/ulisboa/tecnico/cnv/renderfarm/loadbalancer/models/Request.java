package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.models;

import java.util.Date;

public class Request{

    private String id;
    private Long start;
    private Long duration;

    public Request(String id, Long start, Long duration){
        this.id = id;
        this.start = start;
        this.duration = duration;
    }

    public String getID(){
        return this.id;
    }

    public Long getDurationLeft(){
        return start + duration - (new Date()).getTime();
    }
}
