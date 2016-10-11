/**
 * 2015年11月30日
 * Poirot
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
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.ReasonerVocabulary;

//import org.apache.jena.ontology.*;
/**
 * @author Poirot
 *
 */
public class JenaTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String NS = "urn:x-hp-jena:eg/";

		// Build a trivial example data set
		Model rdfsExample = ModelFactory.createDefaultModel();

		Property p = rdfsExample.createProperty(NS, "p");
		Property q = rdfsExample.createProperty(NS, "q");
		rdfsExample.add(p, RDFS.subPropertyOf, q);
		rdfsExample.createResource(NS + "a").addProperty(p, "foo");

		// InfModel inf = ModelFactory.createRDFSModel(rdfsExample); // [1]
		// we could replace the [1] by the codes below
		// Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
		// InfModel inf = ModelFactory.createInfModel(reasoner, rdfsExample);

		// or replace by
		// Reasoner reasoner =
		// RDFSRuleReasonerFactory.theInstance().create(null);
		// InfModel inf = ModelFactory.createInfModel(reasoner, rdfsExample);
		
		
		Resource config = ModelFactory.createDefaultModel().createResource()
				.addProperty(ReasonerVocabulary.PROPsetRDFSLevel, "simple");
		Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(config);
		// up two lines can be replaced by 
//		Reasoner reasoner = (Reasoner) RDFSRuleReasonerFactory.theInstance().create(null);
//		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel,
//		                      ReasonerVocabulary.RDFS_SIMPLE);
		InfModel inf = ModelFactory.createInfModel(reasoner, rdfsExample);
		
		
		Resource a = inf.getResource(NS + "a");
		System.out.println("Statement: " + a.getProperty(p));

		// System.out.println(inf.getReasoner());
		
		// print the simple axioms include by the InfModel
		int count = 0;
		StmtIterator stmtIterator = inf.listStatements();
		while (stmtIterator.hasNext()) {
			count++;
			Statement statement = stmtIterator.next();
//			Iterator<Derivation> derivations = inf.getDerivation(statement);
			System.out.println(statement);
//			while (derivations.hasNext()) {
//				System.out.println(derivations.next().toString());
//			}
		}
		System.out.println(count);

		//supposing you have a more complex set of schema information, defined in a Model called schema, 
		//and you want to apply this schema to several sets of instance data without redoing 
		//too many of the same intermediate deductions. 
		//This can be done by using the SPI level methods:
		
		//schema stored data
//		Reasoner boundReasoner = reasoner.bindSchema(schema);
//		InfModel inf2 = ModelFactory.createInfModel(boundReasoner, data);
	}

}
