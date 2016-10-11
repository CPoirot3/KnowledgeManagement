/**
 * 2015年12月1日
 * Poirot
 * 上午10:32:01
 * JenaTest
 */
package jenaModule;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF; 

/**
 * @author Poirot
 *
 */
public class OWLResonerTest {
	public static void printStatements(Model m, Resource s, Property p, Resource o) {
		int count = 0;
		for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
			Statement stmt = i.nextStatement();
			count++;
			System.out.println(" - " + PrintUtil.print(stmt));
		}
		System.out.println(count);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Model schema = FileManager.get().loadModel("file:data/owlDemoSchema.owl");
//		Model data = FileManager.get().loadModel("file:data/owlDemoData.rdf");
		Model schema = FileManager.get().loadModel("owlDemoSchema.owl");
		Model data = FileManager.get().loadModel("owlDemoData.owl");
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);
		InfModel infmodel = ModelFactory.createInfModel(reasoner, data);

		//A typical example operation on such a model would be to find out all we know about
		//a specific instance, for example the nForce mother board. This can be done using:
		Resource nForce = infmodel.getResource("urn:x-hp:eg/nForce");
		System.out.println("nForce *:");
		printStatements(infmodel, nForce, null, null);

		
		Resource gamingComputer = infmodel.getResource("urn:x-hp:eg/GamingComputer");
		Resource whiteBox = infmodel.getResource("urn:x-hp:eg/whiteBoxZX");
		
		if (infmodel.contains(whiteBox, RDF.type, gamingComputer)) {
		    System.out.println("White box recognized as gaming computer");
		} else {
		    System.out.println("Failed to recognize white box correctly");
		}
		
		System.out.println();
		printStatements(infmodel, whiteBox, null, null);

	}

}
