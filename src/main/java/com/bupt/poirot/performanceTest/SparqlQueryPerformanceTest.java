package com.bupt.poirot.performanceTest;

import com.bupt.poirot.knowledgeBase.datasets.DatasetFactory;
import com.bupt.poirot.knowledgeBase.datasets.SparqlQuery;
import com.bupt.poirot.knowledgeBase.fusekiLibrary.FetchModelClient;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class SparqlQueryPerformanceTest {

    public static String singleFilterQuery = "SELECT ?subject ?predicate ?object\n" +
            "WHERE {\n" +
            "  <http://www.co-ode.org/ontologies/ont.owl#福中路> ?predicate ?object  \t\n" +
            "}\n" +
            "LIMIT 25";

    public static String multiFilterQuery = "SELECT ?subject ?predicate ?object\n" +
            "WHERE {\n" +
            "  <http://www.co-ode.org/ontologies/ont.owl#福中路> ?predicate ?object  \t\n" +
            "  <http://www.co-ode.org/ontologies/ont.owl#红玲中路> ?predicate ?object  \t\n" +
            "}\n" +
            "LIMIT 25";

    public static void main(String[] args) throws InterruptedException {
        DatasetFactory datasetFactory = new DatasetFactory();
        datasetFactory.createDatasetByName("test");
        Dataset dataset = datasetFactory.getDatasetByName("test");

        if (dataset == null) {
            System.out.println("there is no such dataset");
            return;
        }
//        dataset.begin(ReadWrite.WRITE);
//        try {
//            Model model = dataset.getDefaultModel();
//            model.removeAll();
//
//            Model m = ModelFactory.createOntologyModel();
//            try (InputStream in = new FileInputStream(new File("./data/models/new_model.owl"))) {
//                m.read(in, null);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            model.add(m);
//            dataset.commit();
//        } finally {
//            dataset.end();
//        }

//        String qs1 = "SELECT * {<http://www.semanticweb.org/traffic-ontology#Road> ?p ?o}" ;
        String qs1 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT * { info:福中路p1 info:hasX2 ?o }" ;
        dataset.begin(ReadWrite.READ);
        try(QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
            ResultSet rs = qExec.execSelect() ;
            System.out.println(rs.getResourceModel().size());
            ResultSetFormatter.out(rs) ;
        } finally {
            dataset.end();
        }

//        SparqlQuery sparqlQuery =

//        FetchModelClient fetchModelClient = new FetchModelClient();
//        Date begin = new Date();
//        System.out.println("main begin : " + begin);
//        int threadNumber = 4;
//        Thread[] threads = new Thread[threadNumber];
//        for (int i = 0; i < threads.length; i++) {
//            threads[i] = new Thread(new SparqlQueryPerformanceMultiThreadTest(fetchModelClient, singleFilterQuery, begin, threadNumber));
//            threads[i].start();
//        }
    }
}
