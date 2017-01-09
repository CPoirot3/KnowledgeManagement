package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;

import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.HermiT.model.Term;

public class OWLToZ3 {

    public BoolExpr parseFromStream(Context context, InputStream inputStream, Map<String, FuncDecl> funcDeclMap) {
        ParseOWLToDLOntology parseOWLToDLOntology = new ParseOWLToDLOntology();

        DLOntology dlOntology = parseOWLToDLOntology.parse(inputStream);

        ParseOWLToDLClauses parseOWLToDLClauses = new ParseOWLToDLClauses();
        Set<DLClause> set = parseOWLToDLClauses.owlToDLClsuses(dlOntology);
        System.out.println("DLClause number : " + set.size());
        for (DLClause dlClause : set) {
            System.out.println(dlClause.toString());
        }

        BoolExpr res = null;
        FuncDeclGenerate funcDeclGenerate = new FuncDeclGenerate();

        System.out.println("mark");
        for (DLClause dlClause : set) {
            Quantifier quantifier = funcDeclGenerate.getFuncDecl(context, dlClause);
            if (quantifier != null) {
                if (res != null) {
                    res = context.mkAnd(res, quantifier);
                } else {
                    res = quantifier;
                }
            }
        }

        Map<String, FuncDecl> map = funcDeclGenerate.stringToFuncMap;

        System.out.println("Z3函数如下 :");
        for (String str : map.keySet()) {
            System.out.println(str + " " + map.get(str));
            funcDeclMap.put(str, map.get(str));
        }
        Solver solver = context.mkSolver();

        Set<Atom> positiveFacts = dlOntology.getPositiveFacts();
        System.out.println("PositiveFacts set size : " + positiveFacts.size());
        System.out.println();
        for (Atom atom : positiveFacts) {
            System.out.println("Atom : " + atom);
            String dlPredicateString = atom.getDLPredicate().toString();
            FuncDecl funcDecl = map.get(dlPredicateString);
            Sort[] domains = funcDecl.getDomain();
            Expr[] exprs = new Expr[domains.length];
            for (int i = 0; i < atom.getArity(); i++) {
                Term term = atom.getArgument(i);
                String termString = term.toString();
                if (termString.endsWith("xsd:decimal")) {
                    String decimalString = termString.substring(1, termString.indexOf("^^") - 1);
                    exprs[i] = context.mkReal(decimalString);
                } else {
                    exprs[i] = context.mkConst(termString, domains[i]);
                }
            }
            BoolExpr boolExpr = context.mkEq(context.mkApp(funcDecl, exprs), context.mkTrue());
            solver.add(boolExpr);
            System.out.println("BoolExpr : " + boolExpr);
            System.out.println();
        }
        System.out.println();

//        FuncDecl funcDecl = map.get("<http://www.semanticweb.org/traffic-ontology#hasPosition>");
//        Sort[] domains = funcDecl.getDomain();
//        Expr[] exprs = new Expr[domains.length];
//        exprs[0] = context.mkConst("<http://www.semanticweb.org/traffic-ontology#福中路>", domains[0]);
//        exprs[1] = context.mkConst("<http://www.semanticweb.org/traffic-ontology#福中路p4>", domains[1]);
//
//        solver.add(context.mkEq(context.mkApp(funcDecl, exprs), context.mkFalse()));

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

        System.out.println("Solver length : " + solver.getAssertions().length);
        return res;
    }


    public static void main(String[] args) {
        File schemaFile = new File("./data/models/new_model.owl");

        try {
            InputStream inputStream = new FileInputStream(schemaFile);

            Map<String, FuncDecl> funcDeclMap = new HashMap<>();
            OWLToZ3 owlToZ3 = new OWLToZ3();
            owlToZ3.parseFromStream(new Context(), inputStream, funcDeclMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
