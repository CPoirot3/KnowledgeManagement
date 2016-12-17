package com.bupt.poirot.z3.deduce;

public class DeduceData {
    public float speed;
    public float x;
    public float y;
    public long t;
    public String latestTime;

    public DeduceData(float x, float y, long t, float speed, String latestTime) {
        this.x = x;
        this.y = y;
        this.t = t;
        this.speed = speed;
        this.latestTime = latestTime;
    }
}
