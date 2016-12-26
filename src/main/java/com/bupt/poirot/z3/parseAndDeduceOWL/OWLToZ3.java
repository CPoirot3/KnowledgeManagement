package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.Constant;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.HermiT.model.Individual;

public class OWLToZ3 {

    public BoolExpr parseFromStream(Context context, InputStream inputStream) {
        ParseOWLToDLClauses parseOWLToDLClauses = new ParseOWLToDLClauses();
        Set<DLClause> set = parseOWLToDLClauses.owlToDLClsuses(inputStream);

        System.out.println("DLClause number : " + set.size());
        BoolExpr res = null;

        QuantifierGenerate quantifierGenerate = new QuantifierGenerate();

        for (DLClause dlClause : set) {
//            System.out.println(dlClause.toString());
            Quantifier quantifier = quantifierGenerate.mkQuantifier(context, dlClause);
            if (quantifier != null) {
                if (res != null) {
                    res = context.mkAnd(res, quantifier);
                } else {
                    res = quantifier;
                }
            }
        }



        Solver solver = context.mkSolver();

        ParseOWLToOWLOntology parseOWLToOWLOntology = new ParseOWLToOWLOntology();
        Reasoner reasoner = new Reasoner(new Configuration(), parseOWLToOWLOntology.parse(inputStream));
        DLOntology dlOntology = reasoner.getDLOntology();
        Set<Individual> setIndividuals = dlOntology.getAllIndividuals();
        for (Individual individual : setIndividuals) {
            System.out.println(individual.getIRI());
            System.out.println(individual);
        }

        Set<AtomicRole> set1 = dlOntology.getAllAtomicDataRoles();
        for (AtomicRole atomicRole : set1) {
            System.out.println(atomicRole);
        }

        System.out.println("getAllAtomicConcepts");
        Set<AtomicConcept> setAtoms = dlOntology.getAllAtomicConcepts();
        for (AtomicConcept atomicConcept :setAtoms) {
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

        System.out.println("getAllIndividuals");
        Set<Individual> individuals = dlOntology.getAllIndividuals();
        for (Individual atomicConcept : individuals) {
            System.out.println(atomicConcept);
        }
        System.out.println();

        System.out.println("数据属性 :");
        Map<AtomicRole,Map<Individual,Set<Constant>>> m_dataPropertyAssertions = dlOntology.getDataPropertyAssertions();
        for (AtomicRole atomicRole : m_dataPropertyAssertions.keySet()) {
            System.out.println("数据属性  " + atomicRole + " :");
            Map<Individual, Set<Constant>> map = m_dataPropertyAssertions.get(atomicRole);
            for (Individual individual : map.keySet()) {
                System.out.print(individual + "\t: ");
                for (Constant constant : map.get(individual)) {
                    System.out.print(constant + "  ");
                }
                System.out.println();
            }
            System.out.println();
        }
        for (String str : quantifierGenerate.stringToFuncMap.keySet()) {
            System.out.println(str + " " + quantifierGenerate.stringToFuncMap.get(str));
        }
//        BoolExpr boolExpr = context.mkApp()
        return res;
    }


    public static void main(String[] args) {
//        File schemaFile = new File("data/models/model_rdf.owl");
        File schemaFile = new File("./data/models/new_model.owl");

        try {
            InputStream inputStream = new FileInputStream(schemaFile);
            OWLToZ3 owlToZ3 = new OWLToZ3();
            owlToZ3.parseFromStream(new Context(), inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
