package com.bupt.poirot.knowledgeBase.schemaManage;

public class Road implements Knowledge {
    public String IRI;
    public String domain;
    public String name;
    public Road(String IRI, String domain, String name) {
        this.IRI = IRI;
        this.domain = domain;
        this.name = name;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getIRI() {
        return null;
    }

    public String getName() {
        return IRI;
    }
}
