package com.bupt.poirot.knowledgeBase.incidents;

/**
 * Created by hui.chen on 2016/12/23.
 */
public class TrafficIncident {
    public String domain;

    public String carName; // 车名
    public long time;

    //	String time; // 时间
    public  float x; // 纬度
    public float y; // 经度
    public boolean status; // 状态 0: 空车, 1: 载人
    public float speed; // 速度
    public byte direction; // 方向 0, 1, 2, 3, 4, 5, 6, 7


    public TrafficIncident(String domain, String carName, long time, float x, float y, boolean status,
                      float speed, byte direction) {
        this.domain = domain;
        this.carName = carName;
        this.time = time;
        this.x = x;
        this.y = y;
        this.status = status;
        this.speed = speed;
        this.direction = direction;
    }
}
