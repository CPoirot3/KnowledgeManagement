/**
 * 2016年1月18日
 * Poirot
 * 下午2:29:28
 * KnowledgeManagement
 */
package arqSPARQLProcessorTest;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution; 
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.ResultSetMgr;
import org.apache.jena.util.FileManager;

/**
 * @author Poirot
 *
 */
public class ARQSPARQLProcessorTest2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model model = ModelFactory.createDefaultModel();
		String inputFileName = "data/sparqlQuery/vc-db-1.rdf";
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName + " not found");
		}

		// read the RDF/XML file
		model.read(in, null);
//		String queryString = "SELECT ?x WHERE { ?x  <http://www.w3.org/2001/vcard-rdf/3.0#FN>  \"John Smith\" } ";
		String queryString = "SELECT ?sub ?obj WHERE { ?sub  <http://www.w3.org/2001/vcard-rdf/3.0#FN>  ?obj } ";
		Query query = QueryFactory.create(queryString);
		
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			ResultSetRewindable resultSetRewindable = ResultSetFactory.copyResults(results);
			for (; resultSetRewindable.hasNext();) {
				QuerySolution soln = resultSetRewindable.nextSolution();
				System.out.println(soln);
				System.out.println(soln.get("x"));
				 
//				RDFNode x = soln.get("varName"); // Get a result variable by
//													// name.
//				Resource r = soln.getResource("VarR"); // Get a result variable
//														// - must be a resource
//				Literal l = soln.getLiteral("VarL"); // Get a result variable -
//														// must be a literal
			}
			
			// 采用ResultSetRewindable 接口，可重置并再次遍历
//			resultSetRewindable.reset();
//			QuerySolution soln = resultSetRewindable.nextSolution();
//			System.out.println(soln);
//			System.out.println(soln.get("x"));
			
			 
			System.out.println();
			ResultSetMgr resultSetMgr = new ResultSetMgr();
			ResultSetFormatter.out(System.out, resultSetRewindable, query);
		}
	}

}
