package com.bupt.poirot.utils;

import com.bupt.poirot.knowledgeBase.fusekiLibrary.FetchModelClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test {

    private static void fillRoadnameToOWLTriplets(String roadName) {
        String owlSyntax = Client.roadNameToOWLSyntax.get(roadName);
        System.out.println(owlSyntax);

        String query = "SELECT ?subject ?predicate ?object\n" +
                "WHERE {\n" +
                "  <http://www.co-ode.org/ontologies/ont.owl#福中路起点> ?predicate ?object  \t\n" +
                "}\n" +
                "LIMIT 25";

        FetchModelClient fetchModelClient = new FetchModelClient();
        InputStream inputStream = fetchModelClient.fetch("http://localhost:3030", "traffic", query);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }

    public static void main(String[] args) {
        fillRoadnameToOWLTriplets("金田路");
    }
}
