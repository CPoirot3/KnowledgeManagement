package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FPNum;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.HermiT.model.Individual;
import org.semanticweb.HermiT.model.Term;

public class OWLToZ3 {

    public BoolExpr parseFromStream(Context context, InputStream inputStream) {
        ParseOWLToOWLOntology parseOWLToOWLOntology = new ParseOWLToOWLOntology();
        Reasoner reasoner = new Reasoner(new Configuration(), parseOWLToOWLOntology.parse(inputStream));
        DLOntology dlOntology = reasoner.getDLOntology();

//        ParseOWLToDLClauses parseOWLToDLClauses = new ParseOWLToDLClauses();
        Set<DLClause> set = dlOntology.getDLClauses();
        System.out.println("DLClause number : " + set.size());
        BoolExpr res = null;

        QuantifierGenerate quantifierGenerate = new QuantifierGenerate();

        for (DLClause dlClause : set) {
            System.out.println(dlClause.toString());
        }
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

        Map<String, FuncDecl> map = quantifierGenerate.stringToFuncMap;
        for (String str : map.keySet()) {
            System.out.println(str + " " + map.get(str));
        }
        Solver solver = context.mkSolver();

//        Set<Individual> setIndividuals = dlOntology.getAllIndividuals();
//        for (Individual individual : setIndividuals) {
////            System.out.println(individual);
//            String iri = individual.toString();
//            BoolExpr boolExpr;
//            if (Character.isDigit(iri.charAt(iri.length() - 2))) {
//                FuncDecl funcDecl = map.get(StrUtils.replaceSuffixName(iri, "Position"));
//                Sort[] sorts = funcDecl.getDomain();
//                Expr[] exprs = new Expr[sorts.length];
//                exprs[0] = context.mkConst(iri, sorts[0]);
//                boolExpr = context.mkEq(context.mkApp(funcDecl, exprs), context.mkTrue());
//            } else {
//                FuncDecl funcDecl = map.get(StrUtils.replaceSuffixName(iri, "Road"));
//                Sort[] sorts = funcDecl.getDomain();
//                Expr[] exprs = new Expr[sorts.length];
//                exprs[0] = context.mkConst(iri, sorts[0]);
//                boolExpr = context.mkEq(context.mkApp(funcDecl, exprs), context.mkTrue());
//            }
//            solver.add(boolExpr);
//            count++;
//            System.out.println(boolExpr);
//        }

        /*System.out.println();
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
            System.out.println();
        }
        System.out.println();

        System.out.println("getAllIndividuals");
        Set<Individual> individuals = dlOntology.getAllIndividuals();
        for (Individual atomicConcept : individuals) {
            System.out.println(atomicConcept);
        }
        System.out.println();

        System.out.println("getNegativeFacts");
        Set<Atom> setAtom = dlOntology.getNegativeFacts();
        for (Atom atom : setAtom) {
            System.out.println(atom);
        }
        System.out.println();*/

        System.out.println("getPositiveFacts");
        Set<Atom> positiveFacts = dlOntology.getPositiveFacts();
        System.out.println(positiveFacts.size());
        for (Atom atom : positiveFacts) {
            System.out.println(atom);
            String dlPredicateString = atom.getDLPredicate().toString();
//            System.out.println(dlPredicateString);
            FuncDecl funcDecl = map.get(dlPredicateString);

            Sort[] domains = funcDecl.getDomain();
            Expr[] exprs = new Expr[domains.length];
//            System.out.println(atom.getArity());
            for (int i = 0; i < atom.getArity(); i++) {
                Term term = atom.getArgument(i);
                String termString = term.toString();
//                System.out.println(termString);
                if (termString.endsWith("xsd:decimal")) {
                    String decimalString = termString.substring(1, termString.indexOf("^^") - 1);
//                    System.out.println(decimalString);
                    exprs[i] = context.mkReal(decimalString);
                } else {
                    exprs[i] = context.mkConst(termString, domains[i]);
                }
            }

            BoolExpr boolExpr = context.mkEq(context.mkApp(funcDecl, exprs), context.mkTrue());
            solver.add(boolExpr);
            System.out.println(boolExpr);
            System.out.println();
        }
        System.out.println();

//        System.out.println("数据属性 :");
//        Map<AtomicRole,Map<Individual,Set<Constant>>> m_dataPropertyAssertions = dlOntology.getDataPropertyAssertions();
//        for (AtomicRole atomicRole : m_dataPropertyAssertions.keySet()) {
//            System.out.println("数据属性  " + atomicRole + " :");
//
//            FuncDecl funcDecl = map.get(atomicRole.toString());
//            Sort[] domains = funcDecl.getDomain();
//
//            Map<Individual, Set<Constant>> dataRoleMap = m_dataPropertyAssertions.get(atomicRole);
//            for (Individual individual : dataRoleMap.keySet()) {
//                String iri = individual.toString();
//                System.out.print(iri + "\t: ");
//                Expr[] exprs = new Expr[domains.length];
//                exprs[0] = context.mkConst(iri, domains[0]);
//                for (Constant constant : dataRoleMap.get(individual)) {
////                    int data = (int)(Float.parseFloat(constant.getDataValue().toString()) * 1000000);
////                    exprs[1] = context.mkReal(data, 1000000);
//                    exprs[1] = context.mkReal(constant.getDataValue().toString());
//                    System.out.print(constant + "  ");
//                }
//                System.out.println();
//                BoolExpr boolExpr = context.mkEq(context.mkApp(funcDecl, exprs), context.mkTrue());
//                solver.add(boolExpr);
//                count++;
//                System.out.println(boolExpr);
//                System.out.println();
//            }
//            System.out.println();
//        }

//        System.out.println(solver.getAssertions().length);

//        for(Expr e : solver.getAssertions()) {
//            System.out.println(e);
//        }

        FuncDecl funcDecl = map.get("<http://www.semanticweb.org/traffic-ontology#hasPosition>");
        Sort[] domains = funcDecl.getDomain();
        Expr[] exprs = new Expr[domains.length];
        exprs[0] = context.mkConst("<http://www.semanticweb.org/traffic-ontology#福中路>", domains[0]);
        exprs[1] = context.mkConst("<http://www.semanticweb.org/traffic-ontology#福中路p4>", domains[1]);

        solver.add(context.mkEq(context.mkApp(funcDecl, exprs), context.mkFalse()));

//        if (solver.check() == Status.SATISFIABLE) {
//            System.out.println(solver.getModel());
//        } else {
//            System.out.println(Status.UNSATISFIABLE);
//        }

        res = null;
        for (BoolExpr boolExpr : solver.getAssertions()) {
            if (res == null) {
                res = boolExpr;
            } else {
                res = context.mkAnd(res, boolExpr);
            }
        }


        return res;
    }


    public static void main(String[] args) {
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
