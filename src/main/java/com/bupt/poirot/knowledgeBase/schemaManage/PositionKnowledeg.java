package com.bupt.poirot.knowledgeBase.schemaManage;

/**
 * Created by hui.chen on 2016/12/23.
 */
public class PositionKnowledeg implements Knowledge {
    public String domain;

    public PositionKnowledeg(String domain) {
        this.domain = domain;
    }

    @Override
    public String getDomain() {
        return domain;
    }
}
