package com.bupt.poirot.jenaInf;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;

public class ReasonTest {

	public static void main(String[] args) {
		 
//		 Model model = ModelFactory.createDefaultModel();
//		 String inputFileName = "test.data";
//		 // use the FileManager to find the input file
//		 InputStream in = FileManager.get().open( inputFileName );
//		if (in == null) {
//		    throw new 
//		    IllegalArgumentException("File: " + inputFileName + " not found");
//		}
//
//		// read the RDF/XML file
//		model.read(in, null);
//		
//		// write it to standard out
//		model.write(System.out);
		
		String NS = "urn:x-hp-jena:eg/";
		Model rdfsExample = ModelFactory.createDefaultModel();
		Property A = rdfsExample.createProperty(NS, "A");
		Property B = rdfsExample.createProperty(NS, "B");
		Property C = rdfsExample.createProperty(NS, "C");
		Property D = rdfsExample.createProperty(NS, "D");
		rdfsExample.add(A, RDFS.subPropertyOf, B);
		rdfsExample.add(B, RDFS.subPropertyOf, C);
		rdfsExample.add(C, RDFS.subPropertyOf, D);
		
		String rules = "[rule1: (?a eg:p ?b) (?b eg:p ?c) -&gt; (?a eg:p ?c)]";
		Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
		reasoner.setDerivationLogging(true);
		InfModel inf = ModelFactory.createInfModel(reasoner, rdfsExample);
		
		PrintWriter out = new PrintWriter(System.out);
		for (StmtIterator i = inf.listStatements(); i.hasNext(); ) {
		    Statement s = i.nextStatement();
		    System.out.println("Statement is " + s);
		    for (Iterator id = inf.getDerivation(s); id.hasNext(); ) {
		        Derivation deriv = (Derivation) id.next();
		        deriv.printTrace(out, true);
		    }
		}
		out.flush();
		
		Model model = ModelFactory.createDefaultModel();
		Literal literal = model.createLiteral("name");
		Property property = model.createProperty("");
		
		
	}

}
