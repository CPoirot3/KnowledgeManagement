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
import com.microsoft.z3.Sort;
import org.semanticweb.HermiT.model.DLClause;

public class OWLToZ3 {



    public BoolExpr parseFromStream(Context context, InputStream inputStream) {
        ParseOWLToDLClauses parseOWLToDLClauses = new ParseOWLToDLClauses();
        Set<DLClause> set = parseOWLToDLClauses.owlToDLClsuses(inputStream);
        System.out.println("DLClause number : " + set.size());
        BoolExpr res = null;

        QuantifierGenerate quantifierGenerate = new QuantifierGenerate();
        for (DLClause dlClause : set) {
            Quantifier quantifier = quantifierGenerate.mkQuantifier(context, dlClause);
            if (quantifier != null) {
                if (res != null) {
                    res = context.mkAnd(res, quantifier);
                } else {
                    res = quantifier;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        File schemaFile = new File("data/models/model_rdf.owl");
        try {
            InputStream inputStream = new FileInputStream(schemaFile);
            OWLToZ3 owlToZ3 = new OWLToZ3();
            BoolExpr boolExpr = owlToZ3.parseFromStream(new Context(), inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
