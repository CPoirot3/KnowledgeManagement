package com.bupt.poirot.performanceTest;

import com.bupt.poirot.data.modelLibrary.FetchModelClient;

import java.awt.datatransfer.FlavorEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by hui.chen on 12/14/16.
 */
public class SparqlQueryPerformanceTest {

    public static String query = "SELECT ?subject ?predicate ?object\n" +
            "WHERE {\n" +
            "  <http://www.co-ode.org/ontologies/ont.owl#福中路起点> ?predicate ?object  \t\n" +
            "}\n" +
            "LIMIT 25";


    public static void main(String[] args) throws InterruptedException {
        FetchModelClient fetchModelClient = new FetchModelClient();
        Date begin = new Date();
        System.out.println("main begin : " + begin);
        int threadNumber = 4;
        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new SparqlQueryPerformanceMultiThreadTest(fetchModelClient, query, begin, threadNumber));
            threads[i].start();
        }
    }
}
