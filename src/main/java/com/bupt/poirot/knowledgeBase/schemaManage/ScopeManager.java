package com.bupt.poirot.knowledgeBase.schemaManage;

import java.util.HashMap;
import java.util.Map;

public class ScopeManager {
    public IRIManage iriManage;

    public Map<String, Knowledge> map;
    public ScopeManager() {
         map = new HashMap<>();
         iriManage = new IRIManage();
    }

    public Knowledge getKnowledge(String scope) {
        return map.get(scope);
    }

    public void addTarget(String scope, String domain) {
        String scopeIRI = iriManage.knowledgeName(scope);
        System.out.println(scope + " " + scopeIRI);
        TargetKnowledge targetKnowledge = new TargetKnowledge(scopeIRI, domain, scope);
        map.put(scope, targetKnowledge);
    }

    public static void main(String[] args) {
        ScopeManager positionMap = new ScopeManager();

    }
}
