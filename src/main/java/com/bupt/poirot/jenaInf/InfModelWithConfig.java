package com.bupt.poirot.jenaInf;

import java.util.Iterator;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.ReasonerVocabulary;

public class InfModelWithConfig {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String NS = "urn:x-hp-jena:eg/";

		// Build a trivial example data set
		Model rdfsExample = ModelFactory.createDefaultModel();
		Property p = rdfsExample.createProperty(NS, "p");
		Property q = rdfsExample.createProperty(NS, "q");
		rdfsExample.add(p, RDFS.subPropertyOf, q);
		rdfsExample.createResource(NS+"a").addProperty(p, "foo");
		
		// easy way 
//		InfModel inf = ModelFactory.createRDFSModel(rdfsExample);  // [1]
		
		// get reasoner way [2]
		Resource config = ModelFactory.createDefaultModel()
                .createResource()
                .addProperty(ReasonerVocabulary.PROPsetRDFSLevel, "simple");
		Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(config);
		InfModel inf = ModelFactory.createInfModel(reasoner, rdfsExample);
		
		// get reasoner way [3]
//		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
//		InfModel inf = ModelFactory.createInfModel(reasoner, rdfsExample);
		
		StmtIterator stmtIterator = inf.listStatements();
		int count = 0;
		while (stmtIterator.hasNext()) {
			System.out.println(stmtIterator.next());
			count++;
		}
		System.out.println(count);
		
		System.out.println(reasoner.getGraphCapabilities());
		System.out.println(reasoner.getReasonerCapabilities());
		
		
		
		
//		Model data = FileManager.get().loadModel(fname);
//		InfModel infmodel = ModelFactory.createRDFSModel(data);
//		ValidityReport validity = infmodel.validate();
//		if (validity.isValid()) {
//		    System.out.println("OK");
//		} else {
//		    System.out.println("Conflicts");
//		    for (Iterator i = validity.getReports(); i.hasNext(); ) {
//		        System.out.println(" - " + i.next());
//		    }
//		}
	}

}
