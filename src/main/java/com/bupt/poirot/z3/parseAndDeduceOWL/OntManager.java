package com.bupt.poirot.z3.parseAndDeduceOWL;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntManager {
    public OWLOntologyManager ontologyManager;

    public OntManager() {
        this.ontologyManager = OWLManager.createOWLOntologyManager();
    }

}
