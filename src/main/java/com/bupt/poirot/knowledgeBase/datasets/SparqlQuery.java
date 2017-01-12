package com.bupt.poirot.knowledgeBase.datasets;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;

import java.util.StringTokenizer;

public class SparqlQuery {
    public Dataset dataset;
    public SystemState systemState;

    public SparqlQuery(Dataset dataset) {

        this.dataset = dataset;
    }

    public ResultSet queryModel(String queryString, Model model) {
        ResultSet resultSet = null;
        Query query = QueryFactory.create(queryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        resultSet = qexec.execSelect();
        return resultSet;
    }

    public ResultSet queryDataset(String queryString) {
        ResultSet resultSet = null;
        Query query = QueryFactory.create(queryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        resultSet = qexec.execSelect();
        return resultSet;
    }

    public static void main(String[] args) {

        DatasetFactory datasetFactory = new DatasetFactory();
        Dataset dataset = datasetFactory.getDatasetByName("test");

        SparqlQuery sparqlQuery = new SparqlQuery(dataset);
        String queryString = "PREFIX info: <http://www.semanticweb.org/traffic-ontology#> " +
                "SELECT * where {" +
                "info:福中路 ?p ?o }" ;
        ResultSet resultSet = sparqlQuery.queryDataset(queryString);

        ResultSetFormatter.out(resultSet);
    }
}
