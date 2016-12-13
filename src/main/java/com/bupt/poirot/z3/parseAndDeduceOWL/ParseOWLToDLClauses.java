package com.bupt.poirot.z3.parseAndDeduceOWL;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.Constant;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.HermiT.model.Individual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Poirot
 *
 */
public class ParseOWLToDLClauses {

	public Set<DLClause> owlToDLClsuses(InputStream inputStream){
		Reasoner reasoner = null;
		try {
			ParseOWLToOWLOntology parseOWLToOWLOntology = new ParseOWLToOWLOntology();

			reasoner = new Reasoner(new Configuration(), parseOWLToOWLOntology.parse(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (reasoner != null) {
			DLOntology dlOntology = reasoner.getDLOntology();
			return dlOntology.getDLClauses();
		} else {
			return new HashSet<>();
		}
	}
}





//public class ParseOWLToDLClauses {
//
//	public Set<DLClause> owlToDLClsuses(InputStream inputStream){
//		Reasoner reasoner = null;
//		try {
//			ParseOWLToOWLOntology parseOWLToOWLOntology = new ParseOWLToOWLOntology();
//
//			reasoner = new Reasoner(new Configuration(), parseOWLToOWLOntology.parse(inputStream));
//
////			DLOntology dlOntology = reasoner.getDLOntology();
////
////			System.out.println("getAllAtomicConcepts");
////			Set<AtomicConcept> setAtoms = dlOntology.getAllAtomicConcepts();
////			for (AtomicConcept atomicConcept :ot allowed in prolog. setAtoms) {
////				System.out.println(atomicConcept);
////			}
////			System.out.println();
////
////			System.out.println("getAllAtomicDataRoles");
////			Set<AtomicRole> atomicRoles = dlOntology.getAllAtomicDataRoles();
////			for (AtomicRole atomicConcept : atomicRoles) {
////				System.out.println(atomicConcept);
////			}
////			System.out.println();
////
////			System.out.println("getAllAtomicObjectRoles");
////			Set<AtomicRole> atomicObjectRoles = dlOntology.getAllAtomicObjectRoles();
////			for (AtomicRole atomicConcept : atomicObjectRoles) {
////				System.out.println(atomicConcept);
////			}
////			System.out.println();
////
////			System.out.println("getAllComplexObjectRoles");
////			Set<Role> roles = dlOntology.getAllComplexObjectRoles();
////			for (Role role : roles) {
////				System.out.println(role);
////			}
////
////			System.out.println("getAllIndividuals");
////			Set<Individual> individuals = dlOntology.getAllIndividuals();
////			for (Individual atomicConcept : individuals) {
////				System.out.println(atomicConcept);
////			}
////			System.out.println();
////
////			System.out.println("getAllUnknownDatatypeRestrictions");
////			Set<DatatypeRestriction> datatypeRestrictions = dlOntology.getAllUnknownDatatypeRestrictions();
////			for (DatatypeRestriction atomicConcept : datatypeRestrictions) {
////				System.out.println(atomicConcept);
////			}
////			System.out.println();
////
////			System.out.println("getAllDescriptionGraphs");
////			Set<DescriptionGraph> descriptionGraphs = dlOntology.getAllDescriptionGraphs();
////			for (DescriptionGraph atomicConcept : descriptionGraphs) {
////				System.out.println(atomicConcept);
////			}
////			System.out.println();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (reasoner != null) {
//			DLOntology dlOntology = reasoner.getDLOntology();
////			System.out.println();
////			System.out.println("数据属性 :");
////			Map<AtomicRole,Map<Individual,Set<Constant>>> m_dataPropertyAssertions = dlOntology.getDataPropertyAssertions();
////			for (AtomicRole atomicRole : m_dataPropertyAssertions.keySet()) {
////				System.out.println("数据属性  " + atomicRole + " :");
////				Map<Individual, Set<Constant>> map = m_dataPropertyAssertions.get(atomicRole);
////				for (Individual individual : map.keySet()) {
////					System.out.print(individual + "\t:");
////					for (Constant constant : map.get(individual)) {
////						System.out.print(constant + "  ");
////					}
////					System.out.println();
////				}
////				System.out.println();
////			}
////			Set<Individual> individualSet = dlOntology.getAllIndividuals();
////			for (Individual individual : individualSet) {
////				System.out.println(individual);
////			}
//			return dlOntology.getDLClauses();
//		} else {
//			return new HashSet<>();
//		}
//	}
//}
