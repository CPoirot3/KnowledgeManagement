package com.bupt.poirot.performanceTest;

import com.bupt.poirot.knowledgeBase.datasets.DatasetFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

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

    public static ResultSet queryWithOneWhereClause(Dataset dataset) {

        ResultSet rs = null;
        if (dataset == null) {
            System.out.println("there is no such dataset");
            return rs;
        }

        String qs4 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT * where {" +
                " ?o info:hasX1 ?x1 .}" ;

        dataset.begin(ReadWrite.READ);
        try(QueryExecution qExec = QueryExecutionFactory.create(qs4, dataset)) {
            rs = qExec.execSelect() ;
        } finally {
            dataset.end();
        }
        return rs;
    }

    public static ResultSet queryWithTwoWhereClause(Dataset dataset) {

        ResultSet rs = null;
        if (dataset == null) {
            System.out.println("there is no such dataset");
            return rs;
        }

        String qs4 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT * where {" +
                " ?o info:hasX1 ?x1 . ?o info:hasY1 ?y1 . }" ;

        dataset.begin(ReadWrite.READ);
        try(QueryExecution qExec = QueryExecutionFactory.create(qs4, dataset)) {
            rs = qExec.execSelect() ;
//            System.out.println(rs.getResourceModel().size());
//            ResultSetFormatter.out(rs);
        } finally {
            dataset.end();
        }
        return rs;
    }

    public static ResultSet queryWithThreeWhereClause(Dataset dataset) {

        ResultSet rs = null;
        if (dataset == null) {
            System.out.println("there is no such dataset");
            return rs;
        }

        String qs4 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT * where {" +
                " ?o info:hasX1 ?x1 . ?o info:hasY1 ?y1 . ?o info:hasX2 ?x2 .}" ;

//        String qs4 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT ?o ?x where {" +
//                " {?s info:hasPosition ?o } UNION {?o info:hasX1 ?x}} ORDER BY ASC(?x)" ;

        dataset.begin(ReadWrite.READ);
        try(QueryExecution qExec = QueryExecutionFactory.create(qs4, dataset)) {
            rs = qExec.execSelect() ;
//            System.out.println(rs.getResourceModel().size());
//            ResultSetFormatter.out(rs);
        } finally {
            dataset.end();
        }
        return rs;
    }

    public static ResultSet queryWithFiveWhereClause(Dataset dataset) {

        ResultSet rs = null;
        if (dataset == null) {
            System.out.println("there is no such dataset");
            return rs;
        }

        String qs4 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT * where {" +
                " {?o info:hasX1 ?x1 . ?o info:hasY1 ?y1 . ?o info:hasX2 ?x2 . ?o info:hasY2 ?y2}  {?s info:hasPosition ?o }}" ;

        dataset.begin(ReadWrite.READ);
        try(QueryExecution qExec = QueryExecutionFactory.create(qs4, dataset)) {
            rs = qExec.execSelect() ;
//            System.out.println(rs.getResourceModel().size());
//            ResultSetFormatter.out(rs);
        } finally {
            dataset.end();
        }
        return rs;
    }


    public static void testForSparql(Dataset dataset) {
        ResultSet resultSet = null;
        Date begin = null;
        Date end = null;
        for (int i = 0; i < 15000; i++) {
            resultSet = queryWithOneWhereClause(dataset);
        }

        // one where clause
        System.out.println("one where clause");
        begin = new Date();
        for (int i = 0; i < 10000; i++) {
            resultSet = queryWithOneWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 10000);


        begin = new Date();
        for (int i = 0; i < 30000; i++) {
            resultSet = queryWithOneWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 30000);


        begin = new Date();
        for (int i = 0; i < 50000; i++) {
            resultSet = queryWithOneWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 50000);


        begin = new Date();
        for (int i = 0; i < 100000; i++) {
            resultSet = queryWithOneWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 100000);




        // two where clause
        System.out.println("two where clause");
        begin = new Date();
        for (int i = 0; i < 10000; i++) {
            resultSet = queryWithTwoWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 10000);


        begin = new Date();
        for (int i = 0; i < 30000; i++) {
            resultSet = queryWithTwoWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 30000);


        begin = new Date();
        for (int i = 0; i < 50000; i++) {
            resultSet = queryWithTwoWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 50000);

        begin = new Date();
        for (int i = 0; i < 100000; i++) {
            resultSet = queryWithTwoWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 100000);



        // three
        System.out.println("three where clause");
        begin = new Date();
        for (int i = 0; i < 10000; i++) {
            resultSet = queryWithThreeWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 10000);

        begin = new Date();
        for (int i = 0; i < 30000; i++) {
            resultSet = queryWithThreeWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 30000);

        begin = new Date();
        for (int i = 0; i < 50000; i++) {
            resultSet = queryWithThreeWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 50000);

        begin = new Date();
        for (int i = 0; i < 100000; i++) {
            resultSet = queryWithThreeWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 100000);



        // Five where clause
        System.out.println("five where clause");
        begin = new Date();
        for (int i = 0; i < 10000; i++) {
            resultSet = queryWithFiveWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 10000);

        begin = new Date();
        for (int i = 0; i < 30000; i++) {
            resultSet = queryWithFiveWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 30000);

        begin = new Date();
        for (int i = 0; i < 50000; i++) {
            resultSet = queryWithFiveWhereClause(dataset);
        }
        end = new Date();
        System.out.println((end.getTime() - begin.getTime()));
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 50000);

        begin = new Date();
        for (int i = 0; i < 100000; i++) {
            resultSet = queryWithFiveWhereClause(dataset);
        }
        end = new Date();
        System.out.println(end.getTime() - begin.getTime());
        System.out.println((end.getTime() - begin.getTime()) * 1000 / 100000);

    }
    public static void main(String[] args) throws InterruptedException {
        DatasetFactory datasetFactory = new DatasetFactory();
        Dataset dataset = datasetFactory.getDatasetByName("test");

        if (dataset == null) {
            System.out.println("there is no such dataset");
            return;
        }
        testForSparql(dataset);

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
//        String qs1 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT * { info:福中路p1 ?p ?o }" ;
//        String qs1 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT ?s ?p ?o where {" +
//                " ?s ?p ?o . " +
//                " ?s info:hasX1 ?o}" ;


//        String qs1 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT ?s ?p ?o where {" +
//                " ?s info:hasX1 ?o . FILTER (?o > 114.0834)}" ;
//
//        String qs2 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT ?o ?x where {" +
//                " {?s info:hasPosition ?o } UNION {?o info:hasX1 ?x}} ORDER BY ASC(?x)" ;
//
//        String qs3 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT ?o ?x1 ?y1 ?x2 ?y2 where {" +
//                " {?o info:hasX1 ?x1 . ?o info:hasY1 ?y1 . ?o info:hasX2 ?x2 . ?o info:hasY2 ?y2}  {?s info:hasPosition ?o }}" ;
//
//        String qs4 = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> SELECT ?s where {" +
//                " ?s info:hasPosition info:福中路p1}" ;
//
//
//        dataset.begin(ReadWrite.READ);
//        try(QueryExecution qExec = QueryExecutionFactory.create(qs4, dataset)) {
//            ResultSet rs = qExec.execSelect() ;
//            System.out.println(rs.getResourceModel().size());
//            ResultSetFormatter.out(rs);
//        } finally {
//            dataset.end();
//        }



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
