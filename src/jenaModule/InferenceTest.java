/**
 * 2015年11月30日
 * Poirot
 */
package jenaModule;

import java.util.Iterator;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory; 
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.util.FileManager; 

/**
 * @author Poirot
 *
 */
public class InferenceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model schema = FileManager.get().loadModel("rdfsDemoSchema.rdf");
		Model data = FileManager.get().loadModel("rdfsDemoData.rdf");
		InfModel infmodel = ModelFactory.createRDFSModel(schema, data);

//		Resource colin = infmodel.getResource("urn:x-hp:eg/colin");
		System.out.println("colin has types:");
//		printStatements(infmodel, colin, RDF.type, null);

//		Resource Person = infmodel.getResource("urn:x-hp:eg/Person");
		System.out.println("\nPerson has types:");
//		printStatements(infmodel, Person, RDF.type, null);

		ValidityReport validity = infmodel.validate();
		if (validity.isValid()) {
			System.out.println("\nOK");
		} else {
			System.out.println("\nConflicts");
			for (Iterator<Report> i = validity.getReports(); i.hasNext();) {
				ValidityReport.Report report = (ValidityReport.Report) i.next();
				System.out.println(" - " + report);
			}
		}
	}

}
