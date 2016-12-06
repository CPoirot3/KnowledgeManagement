package com.bupt.poirot.utils;

import com.bupt.poirot.main.jetty.TimeData;

public class RequestContext {
    public int id;
    public String topic;
    public String target;
    public String roadName;
    public TimeData timeData;
    public int minCars;

    public RequestContext(int id, String topic, String target, String roadName, TimeData timeData, int minCars) {
        this.id = id;
        this.topic = topic;
        this.target = target;
        this.roadName = roadName;
        this.timeData = timeData;
        this.minCars = minCars;
    }
}
