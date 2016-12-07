package com.bupt.poirot.z3.parseAndDeduceOWL;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyManagerSingleton {
    private OntologyManagerSingleton() { }

    private static class SingletonHelper {
        private static final OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
    }

    public static OWLOntologyManager getSingleton() {
        return SingletonHelper.owlOntologyManager;
    }
}
