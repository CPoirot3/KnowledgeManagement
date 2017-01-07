/**
 * 2015年11月9日
 * Poirot
 */
package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.InputStream;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Poirot
 *
 */
public class ParseOWLToDLOntology {

	public DLOntology parse(InputStream inputStream){
		DLOntology dlOntology = null;
		try {
			OntManager ontManager = new OntManager();
			OWLOntology owlOntology = ontManager.ontologyManager.loadOntologyFromOntologyDocument(inputStream);
			Reasoner reasoner = new Reasoner(new Configuration(), owlOntology);
			dlOntology = reasoner.getDLOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dlOntology;
	}

	public DLOntology parse(File file){
		DLOntology dlOntology = null;
		try {
			OntManager ontManager = new OntManager();
			OWLOntology owlOntology = ontManager.ontologyManager.loadOntologyFromOntologyDocument(file);
			Reasoner reasoner = new Reasoner(new Configuration(), owlOntology);
			dlOntology = reasoner.getDLOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dlOntology;
	}
}
