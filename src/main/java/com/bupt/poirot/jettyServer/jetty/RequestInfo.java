package com.bupt.poirot.jettyServer.jetty;

import java.util.Map;

public class RequestInfo {
    public Map<String, String> infos;

    public RequestInfo(Map<String, String[]> params) {
        // construce RequestInfo from the request params
        for (String key : params.keySet()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String value : params.get(key)) {
                stringBuilder.append(value + "#");
            }
            infos.put(key, stringBuilder.toString());
        }

    }
}
