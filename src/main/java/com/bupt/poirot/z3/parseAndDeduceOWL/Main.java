package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import org.semanticweb.HermiT.model.DLClause;

public class Main {

    public static Pattern pattern = Pattern.compile("(.+)\\((.+?)\\)");

    public static Quantifier mkQuantifier(Context ctx, String boundString, String quantifierString, Set<String> sortSet) {
        Map<String, Sort> map = new HashMap<>();
        String[] strings = quantifierString.split(",");
        for (String str : strings) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {

                String string = matcher.group(2);
                System.out.println(string);

                String[] varibles = string.split(",");
                for (String var : varibles) {
                    if (!map.containsKey(var)) {
                        Sort sort = ctx.mkUninterpretedSort(var);
                        map.put(var, sort);
                    }
                }

                Sort[] domains = new Sort[varibles.length];
                for (int i = 0; i < domains.length; i++) {
                    domains[i] = map.get(varibles[i]);
                }
                String funcName = matcher.group(1);


                FuncDecl funcDecl = ctx.mkFuncDecl(funcName, domains, ctx.mkBoolSort());

                Expr[] exprs = new Expr[varibles.length];
                for (int i = 0; i < exprs.length; i++) {
                    exprs[i] = ctx.mk
                }
                BoolExpr boolExpr = ctx.mkApp(funcdecl
            }
        }

        strings = boundString.split(",");
        for (String str : strings) {

        }
        Quantifier quantifier = null;
        return quantifier;
    }

	public static void main(String[] args) {
		System.out.println("begin");
//		File file = new File("data/ontologies/warnSchemaTest0.xml");
        File file = new File("data/schema.owl");
		Set<DLClause> set = ParseOWL.parseOwl(file);
		System.out.println(set.size());


        Set<String> quantifiersSet = new HashSet<>();
        Set<String> boundStringSet = new HashSet<>();
        Set<String> sortSet = new HashSet<>();

        Context context = new Context();
        Solver solver = context.mkSolver();

		for (DLClause dlClause: set) {
            String dlClauseString = dlClause.toString();

			String[] strings = dlClauseString.split(":-");

			System.out.println(dlClauseString);
            String quantifier = strings[1];
            quantifiersSet.add(quantifier);
            String boundString = strings[0];
            boundStringSet.add(boundString);
            mkQuantifier(context, boundString, quantifier, sortSet);
		}

		if (solver.check() == Status.SATISFIABLE) {
            System.out.println(Status.SATISFIABLE);
            System.out.println(solver.getModel());
        } else {
            System.out.println(Status.UNSATISFIABLE);
        }

	}

}
