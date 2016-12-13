package com.bupt.poirot.jena.datasets;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.engine.iterator.QueryIteratorResultSet;

import java.util.StringTokenizer;

public class SparqlQuery {
    public Query q;
    public Dataset dataset;

    public SparqlQuery(Query query, Dataset dataset) {
        this.q = query;
        this.dataset = dataset;
    }

    public QueryIteratorResultSet query() {
        QueryIteratorResultSet resultSet = null;

        return resultSet;
    }

    public static void main(String[] args) {
        StringTokenizer stringTokenizer = new StringTokenizer("a b c");
        while (stringTokenizer.hasMoreTokens()) {
            System.out.println(stringTokenizer.nextToken());
        }
    }
}
