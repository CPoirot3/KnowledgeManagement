package com.bupt.poirot.knowledgeBase.schemaManage;

/**
 * Created by hui.chen on 2016/12/27.
 */
public class TargetKnowledge implements Knowledge {

    public String IRI;
    public String domain;
    public String name;

    public TargetKnowledge(String domain, String IRI, String name) {
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
        return IRI;
    }
}
