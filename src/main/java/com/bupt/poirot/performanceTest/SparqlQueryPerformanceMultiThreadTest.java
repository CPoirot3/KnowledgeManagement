package com.bupt.poirot.performanceTest;

import com.bupt.poirot.data.modelLibrary.FetchModelClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;

public class SparqlQueryPerformanceMultiThreadTest implements Runnable {
    Date begin;
    FetchModelClient fetchModelClient;
    String query;
    int threadNumber;

    public SparqlQueryPerformanceMultiThreadTest(FetchModelClient fetchModelClient, String query, Date begin, int threadNumber) {
        this.fetchModelClient = fetchModelClient;
        this.query = query;
        this.begin = begin;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        HttpGet httpGet = new HttpGet();
        HttpClient httpClient = HttpClients.createDefault();
        InputStream inputStream;
        URI uri = null;
        try {
            uri = new URI("http://localhost:3030/traffic?query=" + URLEncoder.encode(query, "utf-8"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 1000; i++) {
            try {
                httpGet.setURI(uri);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedOperationException | IOException e) {
                e.printStackTrace();
            }
        }
        Date end = new Date();
        System.out.println("main end : " + end);
        System.out.println((end.getTime() - begin.getTime()) / (1000 * threadNumber));
    }
}
