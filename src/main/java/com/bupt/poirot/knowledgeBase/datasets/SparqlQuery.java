package com.bupt.poirot.knowledgeBase.datasets;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import java.util.StringTokenizer;

public class SparqlQuery {
    public Query q;
    public Dataset dataset;
    public SystemState systemState;

    public SparqlQuery(Query query, Dataset dataset) {
        this.q = query;
        this.dataset = dataset;
    }

    public ResultSet queryModel(String queryString, Model model) {
        ResultSet resultSet = null;
        Query query = QueryFactory.create(queryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        resultSet = qexec.execSelect();
        return resultSet;
    }

    public ResultSet queryDataset(String queryString, Dataset dataset) {
        ResultSet resultSet = null;
        Query query = QueryFactory.create(queryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        resultSet = qexec.execSelect();
        return resultSet;
    }



    public static void main(String[] args) {
        StringTokenizer stringTokenizer = new StringTokenizer("a b c");
        while (stringTokenizer.hasMoreTokens()) {
            System.out.println(stringTokenizer.nextToken());
        }
    }
}
