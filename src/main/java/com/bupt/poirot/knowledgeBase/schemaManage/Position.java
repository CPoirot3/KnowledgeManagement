package com.bupt.poirot.knowledgeBase.schemaManage;

/**
 * Created by hui.chen on 2016/12/23.
 */
public class Position implements Knowledge {

    public String domain;
    public double x1;
    public double y1;
    public double x2;
    public double y2;

    public Position(String domain, double x1, double y1, double x2, double y2) {
        this.domain = domain;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public String getDomain() {
        return domain;
    }
}
