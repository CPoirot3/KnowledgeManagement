package com.bupt.poirot.knowledgeBase.schemaManage;

public class IRIManage {
    public static String IRI_PREFIX =  "http://www.semanticweb.org/traffic-ontology#";

    public IRIManage() {

    }

    public String knowledgeNameWithSeparator(String name) {
        String[] strs = name.split("#");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strs) {
            stringBuilder.append(string);
        }
        return "<" + IRI_PREFIX + stringBuilder.toString() + ">";
    }

    public String knowledgeName(String name) {
        return "<" + IRI_PREFIX + name + ">";
    }
}
