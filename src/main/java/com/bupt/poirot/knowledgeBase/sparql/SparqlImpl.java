package com.bupt.poirot.knowledgeBase.sparql;

import com.bupt.poirot.knowledgeBase.datasets.SparqlQuery;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class SparqlImpl {
    public ResultSet queryModel(String queryString) {
        Model model = ModelFactory.createOntologyModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open( "/Users/hui.chen/graduatedesign/git_projects/KnowledgeManagement/data/models/new_model.owl");
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + " not found");
        }

        // read the RDF/XML file
        model.read(in, null);


//        StmtIterator stmtIterator = model.listStatements();
//        while (stmtIterator.hasNext()) {
//            Statement statement = stmtIterator.nextStatement();
//            if (statement.getSubject().toString().equals("http://www.co-ode.org/ontologies/ont.owl#福中路")) {
//                System.out.println(statement);
//                statement.getSubject();
//                statement.getPredicate();
//                RDFNode rdfNode = statement.getObject();
//                if (rdfNode.isLiteral()) {
//                    System.out.println(rdfNode.asLiteral());
//                }
//            }
////            System.out.println(statement.getSubject());
//        }

        System.out.println();
        System.out.println();
        System.out.println();


        ResultSet resultSet;

        Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            for ( ; results.hasNext() ; )
            {
                QuerySolution soln = results.nextSolution() ;
                System.out.println(soln);
                System.out.println();
            }
            resultSet = qexec.execSelect();
        }
        return resultSet;
    }

    public static void main(String[] args) {
        SparqlImpl sparqlImpl = new SparqlImpl();
        sparqlImpl.queryModel("SELECT ?subject ?predicate ?object\n" +
                "WHERE {\n" +
                "  <http://www.co-ode.org/ontologies/ont.owl#福中路> ?predicate ?object  \t\n" +
                "}");
    }
}
