package com.bupt.poirot.z3.deduce;

public class RequestContext {
    public int id;
    public String topic;
    public String scope;
    public String minCars;
    public String severe;
    public String conjection;
    public String slightConjection;
    public String speed;

    public RequestContext(int id, String topic, String scope, String minCars, String severe, String conjection, String slightConjection, String speed) {
        this.id = id;
        this.topic = topic;
        this.scope = scope;
        this.minCars = minCars;
        this.severe = severe;
        this.conjection = conjection;
        this.slightConjection = slightConjection;
        this.speed = speed;
    }
}
