package com.bupt.poirot.jettyServer.jetty;

import java.util.HashMap;
import java.util.Map;

public class ParamsParse {
    public Map<String, String> infos;

    public ParamsParse(Map<String, String[]> params) {
        infos = new HashMap<>();

        for (String key : params.keySet()) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] values = params.get(key);
            for (String value : values) {
                stringBuilder.append(value);
            }
//            System.out.println(key + " : " + stringBuilder.toString());
            infos.put(key, stringBuilder.toString());
        }

    }

    public String get(String key) {
        return infos.get(key);
    }
}
