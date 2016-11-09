/**
 * 2015年11月9日
 * Poirot
 */
package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;



/**
 * @author Poirot
 *
 */
public class ParseOWL {
	public Set<DLClause> parseOwl(File file){
		Reasoner reasoner = null;
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
			reasoner = new Reasoner(new Configuration(), ontology);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (reasoner != null) {
			return reasoner.getDLOntology().getDLClauses();
		} else {
			return new HashSet<>();
		}
	}
	public static void main(String[] args){
	
	}
}
