/**
 * 2015年11月9日
 * Poirot
 */
package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.HermiT.model.DatatypeRestriction;
import org.semanticweb.HermiT.model.DescriptionGraph;
import org.semanticweb.HermiT.model.Individual;
import org.semanticweb.HermiT.model.Role;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Poirot
 *
 */
public class ParseOWL {
	public static Set<DLClause> parseOwl(File file){
		Reasoner reasoner = null;
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
			reasoner = new Reasoner(new Configuration(), ontology);
			DLOntology dlOntology = reasoner.getDLOntology();
			
			System.out.println("getAllAtomicConcepts");
			Set<AtomicConcept> setAtoms = dlOntology.getAllAtomicConcepts(); 
			for (AtomicConcept atomicConcept : setAtoms) {
				System.out.println(atomicConcept);
			}
			System.out.println();
			
			System.out.println("getAllAtomicDataRoles");
			Set<AtomicRole> atomicRoles = dlOntology.getAllAtomicDataRoles();
			for (AtomicRole atomicConcept : atomicRoles) {
				System.out.println(atomicConcept);
			}
			System.out.println();
			
			System.out.println("getAllAtomicObjectRoles");
			Set<AtomicRole> atomicObjectRoles = dlOntology.getAllAtomicObjectRoles();
			for (AtomicRole atomicConcept : atomicObjectRoles) {
				System.out.println(atomicConcept);
			}
			System.out.println();
			
			System.out.println("getAllComplexObjectRoles");
			Set<Role> roles = dlOntology.getAllComplexObjectRoles();
			for (Role role : roles) {
				System.out.println(role);
			}
			System.out.println();
			
			System.out.println("getAllIndividuals");
			Set<Individual> individuals = dlOntology.getAllIndividuals();
			for (Individual atomicConcept : individuals) {
				System.out.println(atomicConcept);
			}
			System.out.println();
			
			System.out.println("getAllUnknownDatatypeRestrictions");
			Set<DatatypeRestriction> datatypeRestrictions = dlOntology.getAllUnknownDatatypeRestrictions();
			for (DatatypeRestriction atomicConcept : datatypeRestrictions) {
				System.out.println(atomicConcept);
			}
			System.out.println();
			
			System.out.println("getAllDescriptionGraphs");
			Set<DescriptionGraph> descriptionGraphs = dlOntology.getAllDescriptionGraphs();
			for (DescriptionGraph atomicConcept : descriptionGraphs) {
				System.out.println(atomicConcept);
			}
			System.out.println();
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
