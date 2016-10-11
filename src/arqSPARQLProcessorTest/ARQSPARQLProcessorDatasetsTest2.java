/**
 * 2016年1月18日
 * Poirot
 * 下午2:29:28
 * KnowledgeManagement
 */
package arqSPARQLProcessorTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
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
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

/**
 * @author Poirot
 *
 */
public class ARQSPARQLProcessorDatasetsTest2 {
	public static String getQueryStringFromFile(String fileName) {
		File file = new File(fileName);
		StringBuilder stringBuilder = new StringBuilder();
		try (
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub 
		
		String queryString = getQueryStringFromFile("q-ds-1.rq");

		String dftGraphURI = "outputModel.owl" ; 
		List<String> namedGraphURIs = new ArrayList<>() ;
		namedGraphURIs.add("ds-ng-1.ttl") ; 
		namedGraphURIs.add("ds-ng-2.ttl") ; 
		Query query = QueryFactory.create(queryString) ; 
		
		Dataset dataset = DatasetFactory.create(dftGraphURI, namedGraphURIs) ;
		try(QueryExecution qExec = QueryExecutionFactory.create(query, dataset)) {
			ResultSet results = qExec.execSelect();
			ResultSetRewindable resultSetRewindable = ResultSetFactory.copyResults(results);
			for (; resultSetRewindable.hasNext();) {
				QuerySolution soln = resultSetRewindable.nextSolution();
				System.out.println(soln);
//				System.out.println(soln.get("x"));
//				RDFNode x = soln.get("varName"); // Get a result variable by
//													// name.
//				Resource r = soln.getResource("VarR"); // Get a result variable
//														// - must be a resource
//				Literal l = soln.getLiteral("VarL"); // Get a result variable -
//														// must be a literal
			}
			ResultSetFormatter.out(System.out, resultSetRewindable, query);
		}
		
		
//		TDBFactory.createDataset(location)
//		dataset.getNamedModel(uri)
	}

}
