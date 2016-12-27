package com.bupt.poirot.knowledgeBase.schemaManage;

import com.bupt.poirot.jettyServer.utils.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TargetToKnowledge {
    public IRIManage iriManage;

    public Map<String, Knowledge> map;
    public TargetToKnowledge() {
         map = new HashMap<>();
         iriManage = new IRIManage();
    }

    public Knowledge getKnowledge(String scope) {
        return map.get(scope);
    }

    public void addTarget(String scope) {
        String scopeIRI = iriManage.knowledgeName(scope);
        TargetKnowledge targetKnowledge = new TargetKnowledge(scopeIRI, "", scope);
        map.put(scope, targetKnowledge);
    }

    public static void main(String[] args) {
        TargetToKnowledge positionMap = new TargetToKnowledge();

    }
}
