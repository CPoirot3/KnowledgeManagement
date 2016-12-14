package com.bupt.poirot.jena.datasets;

import java.util.Iterator ;

import javax.servlet.http.HttpServletRequest ;

import org.apache.jena.atlas.web.ContentType ;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.jena.graph.Graph ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.Triple ;
import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.Literal ;
import org.apache.jena.rdf.model.Model ;
import org.apache.jena.rdf.model.RDFNode ;

public class FusekiLib {




    /** Parse the query string - do not process the body even for a form */
    public static Multimap<String, String> parseQueryString(HttpServletRequest req) {
        Multimap<String, String> map = ArrayListMultimap.create() ;

        // Don't use ServletRequest.getParameter or getParamterNames
        // as that reads form data. This code parses just the query string.
        if ( req.getQueryString() != null ) {
            String[] params = req.getQueryString().split("&") ;
            for (int i = 0; i < params.length; i++) {
                String p = params[i] ;
                String[] x = p.split("=", 2) ;
                String name = null ;
                String value = null ;

                if ( x.length == 0 ) { // No "="
                    name = p ;
                    value = "" ;
                } else if ( x.length == 1 ) { // param=
                    name = x[0] ;
                    value = "" ;
                } else { // param=value
                    name = x[0] ;
                    value = x[1] ;
                }
                map.put(name, value) ;
            }
        }
        return map ;
    }


    // ---- Helper code
    public static ResultSet query(String string, Model m) {
        return query(string, m, null, null) ;
    }

    public static ResultSet query(String string, Dataset ds) {
        return query(string, ds, null, null) ;
    }

    public static ResultSet query(String string, Model m, String varName, RDFNode value) {
        Query query = QueryFactory.create(SystemState.PREFIXES + string) ;
        QuerySolutionMap initValues = null ;
        if ( varName != null )
            initValues = querySolution(varName, value) ;
        try ( QueryExecution qExec = QueryExecutionFactory.create(query, m, initValues) ) {
            return ResultSetFactory.copyResults(qExec.execSelect()) ;
        }
    }

    public static ResultSet query(String string, Dataset ds, String varName, RDFNode value) {
        Query query = QueryFactory.create(SystemState.PREFIXES + string) ;
        QuerySolutionMap initValues = null ;
        if ( varName != null )
            initValues = querySolution(varName, value) ;
        try ( QueryExecution qExec = QueryExecutionFactory.create(query, ds, initValues) ) {
            return ResultSetFactory.copyResults(qExec.execSelect()) ;
        }
    }

    private static QuerySolutionMap querySolution(String varName, RDFNode value) {
        QuerySolutionMap qsm = new QuerySolutionMap() ;
        querySolution(qsm, varName, value) ;
        return qsm ;
    }

    public static QuerySolutionMap querySolution(QuerySolutionMap qsm, String varName, RDFNode value) {
        qsm.add(varName, value) ;
        return qsm ;
    }


}
