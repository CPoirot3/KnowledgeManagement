package com.bupt.poirot.utils;

import com.bupt.poirot.data.modelLibrary.FetchModelClient;
import com.bupt.poirot.z3.parseAndDeduceOWL.ParseOWLToOWLOntology;
import org.semanticweb.HermiT.model.DLClause;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Created by hui.chen on 12/5/16.
 */
public class Test {

    private static InputStream fetchModel(String domain, String host, String query) {
        FetchModelClient fetchModelClient = new FetchModelClient();
        if (host == null) {
            host = "http://localhost:3030/";
        }
        if (query == null) {
            query = ""; // TODO
        }
        InputStream inputStream = fetchModelClient.fetch(host, domain, query);
        if (inputStream == null) {
            throw new RuntimeException("fetch model failed");
        }
        return inputStream;
    }
    private static void fillRoadnameToOWLTriplets(String roadName) {
        String owlSyntax = Client.roadNameToOWLSyntax.get(roadName);
        System.out.println(owlSyntax);

        String query = "SELECT ?subject ?predicate ?object\n" +
                "WHERE {\n" +
                "  <http://www.co-ode.org/ontologies/ont.owl#福中路起点> ?predicate ?object  \t\n" +
                "}\n" +
                "LIMIT 25";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fetchModel("traffic", null, query)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done");

        InputStream inputStream = fetchModel("traffic", null, query);
        Set<DLClause> set = ParseOWLToOWLOntology.owlToDLClsuses(inputStream);
        set.stream().forEach(System.out::println);
    }

    public static void main(String[] args) {

        fillRoadnameToOWLTriplets("金田路");

    }
}
