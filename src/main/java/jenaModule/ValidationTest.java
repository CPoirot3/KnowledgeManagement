/**
 * 2015年11月30日
 * Poirot
 */
package jenaModule;

import java.util.Iterator;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.util.FileManager;

/**
 * @author Poirot
 *
 */
public class ValidationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//For example, to check a data set and list any problems one could do something like:
		String fname = "model.owl";
		Model model = FileManager.get().loadModel(fname);
		 
		
		
		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			System.out.println(stmtIterator.next());
		}
		
		InfModel infmodel = ModelFactory.createRDFSModel(model);
		ValidityReport validity = infmodel.validate();
		if (validity.isValid()) {
		    System.out.println("OK");
		} else {
		    System.out.println("Conflicts");
		    for (Iterator<Report> i = validity.getReports(); i.hasNext(); ) {
		        System.out.println(" - " + i.next());
		    }
		}
	}

}
