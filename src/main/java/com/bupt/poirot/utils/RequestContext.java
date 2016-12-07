package com.bupt.poirot.utils;

public class RequestContext {
    public int id;
    public String topic;
    public String roadName;
    public int minCars;

    public RequestContext(int id, String topic, String roadName, int minCars) {
        this.id = id;
        this.topic = topic;
        this.roadName = roadName;
        this.minCars = minCars;
    }
}
