package com.bupt.poirot.jenaInf;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class InfDemo {
	public static void main(String[] args) {
		String NS = "urn:x-hp-jena:eg/";

		// Build a trivial example data set
		Model rdfsExample = ModelFactory.createDefaultModel();
		
		Property p = rdfsExample.createProperty(NS, "p");
		Property q = rdfsExample.createProperty(NS, "q");
		rdfsExample.add(p, RDFS.subPropertyOf, q);
		rdfsExample.createResource(NS+"a").addProperty(p, "foo");
		
		InfModel inf = ModelFactory.createRDFSModel(rdfsExample);  // [1]
		
		Resource a = inf.getResource(NS+"a");
		System.out.println("Statement: " + a.getProperty(q));
		
		
	}
}
