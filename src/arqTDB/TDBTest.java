/**
 * 2016年1月20日
 * Poirot
 * 上午9:07:00
 * KnowledgeManagement
 */
package arqTDB;

import java.util.function.Consumer;

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
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.vocabulary.VCARD;

/**
 * @author Poirot
 *
 */
public class TDBTest {

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
		// Get model inside the transaction
		Model model = dataset.getDefaultModel();
		try {
			

			// create the resource
			// and add the properties cascading style
		    model.createResource(personURI).addProperty(VCARD.FN, fullName).addProperty(VCARD.N,
					model.createResource().addProperty(VCARD.Given, givenName).addProperty(VCARD.Family, familyName));
			// dataset.commit();
			dataset.commit();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dataset.end();
		}
		
		
		// ...
		dataset.begin(ReadWrite.READ);
		model = dataset.getDefaultModel();
		model.listStatements().forEachRemaining(new Consumer<Statement>() {

			@Override
			public void accept(Statement t) {
				// TODO Auto-generated method stub
				System.out.println("test");
				System.out.println(t.toString());
			}
		});
		;
		dataset.end();

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
