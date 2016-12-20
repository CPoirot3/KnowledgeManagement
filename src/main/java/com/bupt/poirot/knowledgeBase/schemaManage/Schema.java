package com.bupt.poirot.knowledgeBase.schemaManage;

import java.util.Map;

/**
 * Created by hui.chen on 2016/12/19.
 */
public class Schema {
    public String domain;
    public Map<String, String> fields;

    public Schema() {

    }

    public String getDomain() {
        return domain;
    }

    public Object getFiled(String key) {
        return fields.get(key);
    }
}
