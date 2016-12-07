/**
 * 2015年11月9日
 * Poirot
 */
package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.InputStream;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Poirot
 *
 */
public class ParseOWLToOWLOntology {

	public OWLOntology parse(InputStream inputStream){
		OWLOntology owlOntology = null;
		try {
			OWLOntologyManager manager = OntologyManagerSingleton.getSingleton();
			owlOntology = manager.loadOntologyFromOntologyDocument(inputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return owlOntology;
	}

	public OWLOntology parse(File file){
		OWLOntology owlOntology = null;
		try {
			OWLOntologyManager manager = OntologyManagerSingleton.getSingleton();
			owlOntology = manager.loadOntologyFromOntologyDocument(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return owlOntology;
	}
}
