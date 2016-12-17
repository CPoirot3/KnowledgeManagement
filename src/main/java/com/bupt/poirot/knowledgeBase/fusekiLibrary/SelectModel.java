/**
 * Poirot
 * 2016年10月11日上午11:01:07
 * KnowledgeManagement
 */
package com.bupt.poirot.knowledgeBase.fusekiLibrary;

import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author Poirot
 *
 */
public class SelectModel {


    public InputStream select(String domain, String sparqlString) {
        FetchModelClient fetchModelClient = new FetchModelClient();

        InputStream inputStream = fetchModelClient.fetch("http://localhost:3030/", domain, sparqlString);
        return inputStream;
    }
}
