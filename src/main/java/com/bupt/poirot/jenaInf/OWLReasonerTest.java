package com.bupt.poirot.jenaInf;

import java.util.Iterator;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.reasoner.rulesys.OWLFBRuleReasoner;
import org.apache.jena.reasoner.rulesys.OWLFBRuleReasonerFactory;
import org.apache.jena.reasoner.rulesys.OWLMicroReasoner;
import org.apache.jena.reasoner.rulesys.OWLMicroReasonerFactory;
import org.apache.jena.reasoner.rulesys.OWLMiniReasoner;
import org.apache.jena.reasoner.rulesys.OWLMiniReasonerFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;

public class OWLReasonerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String relativelyPath = System.getProperty("user.dir"); 
		System.out.println(relativelyPath);
		
		
		Model schema = FileManager.get().loadModel(relativelyPath + "/data/owlDemoSchema.xml");
		Model data = FileManager.get().loadModel(relativelyPath + "/data/owlDemoData.xml");
		Resource nForce = data.getResource("urn:x-hp:eg/nForce");
//		System.out.println("nForce *:");
//		printStatements(data, null, null, nForce);
		
		
		
//		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
//		Reasoner reasoner = new OWLFBRuleReasoner(OWLFBRuleReasonerFactory.theInstance());
//		Reasoner reasoner = new OWLMiniReasoner(OWLMiniReasonerFactory.theInstance());
		Reasoner reasoner = new OWLMicroReasoner(OWLMicroReasonerFactory.theInstance());
		reasoner = reasoner.bindSchema(schema);
		InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
		
		ValidityReport validityReport = infmodel.validate();
		if (validityReport.isValid()) {
			System.out.println("yes");
		} else {
			System.out.println("Conflicts");
		    for (Iterator<Report> i = validityReport.getReports(); i.hasNext(); ) {
		        System.out.println(" - " + i.next());
		    }
		}
		nForce = infmodel.getResource("urn:x-hp:eg/nForce");
		System.out.println("nForce *:");
		printStatements(infmodel, nForce, null, null);
	}

	public static void printStatements(Model m, Resource s, Property p, Resource o) {
	    for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) {
	        Statement stmt = i.nextStatement();
	        System.out.println(" - " + PrintUtil.print(stmt));
	    }
	}
}
