package com.bupt.poirot.knowledgeBase.incidents;

import java.util.Date;

/**
 * Created by hui.chen on 2016/12/26.
 */
public class TemperatureIncident implements Incident {

    String domain;
    String name;
    float temperature;
    Date time;

    public TemperatureIncident(String domain, String name, float temperature, Date time) {
        this.domain = domain;
        this.name = name;
        this.temperature = temperature;
        this.time = time;
    }

    @Override
    public String getDomain() {
        return domain;
    }
}
