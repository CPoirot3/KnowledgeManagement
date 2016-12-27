package com.bupt.poirot.knowledgeBase.incidents;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by hui.chen on 2016/12/26.
 */
public class IncidentFactory {
    private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public Incident converIncident(String domain, String message) { // name is map to ClassName, lower case

        Incident incident = null;
        if (StringUtils.equalsIgnoreCase(domain, "Traffic")) {
            String[] strs = message.split(",");
//            String carName = strs[0];
//            String time = strs[1]; // 时间
//            String longitude = strs[2];
//            String latitude = strs[3];
//            String states = strs[4];
//            String speed = strs[5];
//            String direction = strs[6];
            float x, y, speed;
            boolean status;
            long time;
            byte direction;
            try {
                time = formater.parse(strs[1]).getTime();
                x = Float.parseFloat(strs[2]); // 经度
                y = Float.parseFloat(strs[3]); // 纬度
                status = Boolean.parseBoolean(strs[4]);
                speed = Float.parseFloat(strs[5]); // 速度
                direction = Byte.parseByte(strs[6]);
            } catch (ParseException e1) {
                return null;
            }

            // 根据
            incident = new TrafficIncident("traffic", "TrafficIncident", strs[0], time, x, y, status, speed, direction);
        }

        return incident;
    }
}
