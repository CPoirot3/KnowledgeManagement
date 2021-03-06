package com.bupt.poirot.knowledgeBase.schemaManage;

import com.bupt.poirot.jettyServer.utils.Config;

import java.util.Map;

public class SchemaManager {
    public Map<String, Schema> schemaMap;

    public SchemaManager() {
        init();
    }

    public Schema getSchemaByName(String name) {
        return schemaMap.get(name);
    }

    public void putSchemaByName(String name, Schema schema) {
        schemaMap.put(name, schema);
    }

    public void init() {
        Config config = new Config();

    }
}
