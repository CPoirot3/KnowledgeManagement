/**
 * 2016年1月20日
 * Poirot
 * 上午9:07:00
 * KnowledgeManagement
 */
package arqTDB;

import java.util.function.Consumer;

import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.base.Sys;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.GraphStoreFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.VCARD;

/**
 * @author Poirot
 *
 */
public class TDBTest2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// some definitions
		String personURI = "http://somewhere/JohnSmith";
		String givenName = "John";
		String familyName = "Smith";
		String fullName = givenName + " " + familyName;

		// Make a TDB-backed dataset
		String directory = "Dataset1";
		Dataset dataset = TDBFactory.createDataset(directory);
		// ...
		dataset.begin(ReadWrite.WRITE);

		try {
			Model model = dataset.getDefaultModel();
			// API calls to a model in the dataset

			// model.add()

			// A SPARQL query will see the new statement added.
			try (QueryExecution qExec = QueryExecutionFactory.create("SELECT (count(*) AS ?count) { ?s ?p ?o} LIMIT 4",
					dataset)) {
				ResultSet rs = qExec.execSelect();
				ResultSetFormatter.out(rs);
				 
			}

			// ... perform a SPARQL Update
			String sparqlUpdateString = StrUtils.strjoinNL("PREFIX bupt:<http://example/>",
					"INSERT { bupt:s bupt:p ?now } WHERE { BIND(now() AS ?now) }");

			UpdateRequest request = UpdateFactory.create(sparqlUpdateString);
			UpdateProcessor proc = UpdateExecutionFactory.create(request, dataset);
			proc.execute();

			// Finally, commit the transaction.
			dataset.commit();
			// Or call .abort()
		} finally {
			dataset.end();
		}

		System.out.println("test");
		Location location = Location.create("Dataset1");
		dataset = TDBFactory.createDataset(location);
		dataset.begin(ReadWrite.READ);
		String qs1 = "SELECT * {?s ?p ?o} LIMIT 10";
		try (QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
			ResultSet rs = qExec.execSelect();
			ResultSetFormatter.out(rs);
		}

		System.out.println("test");
		String qs2 = "SELECT * {?s ?p ?o} OFFSET 2 LIMIT 10";
		try (QueryExecution qExec = QueryExecutionFactory.create(qs2, dataset)) {
			ResultSet rs = qExec.execSelect();
			ResultSetFormatter.out(rs);
		}
	}

}
