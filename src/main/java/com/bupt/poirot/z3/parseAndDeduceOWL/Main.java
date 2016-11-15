package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import org.semanticweb.HermiT.model.DLClause;

public class Main {

    public static Map<String, FuncDecl> stringToFuncMap = new HashMap<>();

    public static Pattern pattern = Pattern.compile("(.+)\\((.+?)\\)");
    public static int begin = 0;

    public static BoolExpr mkQuantifier(Context ctx, String string1, String string2, Set<String> sortSet, Set<String> quantifierSet) {
        Map<String, String> varaibleNameToExprName = new HashMap<>();



        String domain, formua;
        if (string1.contains("atLeast") || string1.contains("atMost") || string1.contains(" v ")) {
            domain = string2;
            formua = string1;
        } else if (string2.contains("atLeast") || string2.contains("atMost") || string2.contains(" v ")) {
            domain = string1;
            formua = string2;
        } else {
            if (string1.contains(",")) {
                domain = string2;
                formua = string1;
            } else {
                domain = string1;
                formua = string2;
            }
        }

        BoolExpr expression = null;
        String[] strings = domain.split(", ");
        for (String str : strings) {
            if (str.equals("<http://www.semanticweb.org/traffic-ontology#hasLatitude>(X,Y")) {
                System.out.println("mark : " + expression);
            }

            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String funcString = matcher.group(1);
                FuncDecl funcDecl = null;
                if (stringToFuncMap.containsKey(funcString)) {
                    funcDecl = stringToFuncMap.get(funcString);
                }

                String string = matcher.group(2);
                String[] variables = string.split(",");
                Sort[] domains = new Sort[variables.length];

                if (funcDecl != null) {
                    domains = funcDecl.getDomain();
                } else {
                    // need to mk FuncDecl
                    for (int i = 0; i < domains.length; i++) {
                        domains[i] = ctx.mkUninterpretedSort(variables[i]);
                    }
                    funcDecl = ctx.mkFuncDecl(funcString, domains, ctx.getBoolSort());
                    stringToFuncMap.put(funcString, funcDecl);
                }
                Expr[] exprs = new Expr[variables.length];
                for (int i = 0; i < exprs.length; i++) {
                    String variableName = varaibleNameToExprName.containsKey(variables[i]) ? varaibleNameToExprName.get(variables[i]) :variables[i] + (begin++);
                    varaibleNameToExprName.put(variables[i], variableName);

                    exprs[i] = ctx.mkConst(variables[i], domains[i]);
                }
                if (expression == null) {
                    expression = (BoolExpr)ctx.mkApp(funcDecl, exprs);
                } else {
                    expression = ctx.mkAnd(expression, (BoolExpr)ctx.mkApp(funcDecl, exprs));
                }
            }
        }

        BoolExpr formuaExpr = null;
        for (String s : formua.split(" v ")) {
            BoolExpr expression2 = null;
            strings = s.split(", ");
            for (String str : strings) {
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String funcString = matcher.group(1);
                    FuncDecl funcDecl = null;
                    if (stringToFuncMap.containsKey(funcString)) {
                        funcDecl = stringToFuncMap.get(funcString);
                    }

                    String string = matcher.group(2);
                    String[] variables = string.split(",");
                    Sort[] domains = new Sort[variables.length];

                    if (funcDecl != null) {
                        domains = funcDecl.getDomain();
                    } else {
                        // need to mk FuncDecl
                        for (int i = 0; i < domains.length; i++) {
                            domains[i] = ctx.mkUninterpretedSort(variables[i]);
                        }
                        funcDecl = ctx.mkFuncDecl(funcString, domains, ctx.getBoolSort());
                        stringToFuncMap.put(funcString, funcDecl);
                    }
                    Expr[] exprs = new Expr[variables.length];
                    for (int i = 0; i < exprs.length; i++) {
                        String variableName = varaibleNameToExprName.containsKey(variables[i]) ? varaibleNameToExprName.get(variables[i]) : variables[i] + (begin++);
                        varaibleNameToExprName.put(variables[i], variableName);
                        System.out.println(variableName);
                        exprs[i] = ctx.mkConst(variables[i], domains[i]);
                    }

                    if (expression2 == null) {
                        expression2 = (BoolExpr)ctx.mkApp(funcDecl, exprs);
                    } else {
                        expression2 = ctx.mkAnd(expression2, (BoolExpr)ctx.mkApp(funcDecl, exprs));
                    }

                }
            }
            if (expression2 == null) {
                continue;
            }
            if (formuaExpr == null) {
                formuaExpr = expression2;
            } else {
                formuaExpr = ctx.mkOr(formuaExpr, expression2);
            }
        }



        System.out.println("expression : " + expression);
        System.out.println("expression2 : " + formuaExpr);

        if (expression == null || formuaExpr == null) {
            return null;
        }
        BoolExpr finalExpr = ctx.mkImplies(expression, formuaExpr);
        System.out.println(finalExpr);
        System.out.println();

//        Quantifier quantifier = null;
//        return quantifier;

        return finalExpr;
    }

	public static void main(String[] args) {
		System.out.println("begin");
//		File file = new File("data/ontologies/warnSchemaTest0.xml");
        File file = new File("data/schema.owl");
		Set<DLClause> set = ParseOWL.parseOwl(file);
		System.out.println(set.size());


        Set<String> quantifiersSet = new HashSet<>();
        Set<String> sortSet = new HashSet<>();

        Context context = new Context();
        Solver solver = context.mkSolver();

		for (DLClause dlClause: set) {
            String dlClauseString = dlClause.toString();
			String[] strings = dlClauseString.split(":-");

			System.out.println(dlClauseString);

            BoolExpr boolExpr = mkQuantifier(context, strings[0], strings[1], sortSet, quantifiersSet);

            if (boolExpr != null) {
                solver.add(boolExpr);
            }
		}

		if (solver.check() == Status.SATISFIABLE) {
            System.out.println(Status.SATISFIABLE);
//            System.out.println(solver.getModel());
        } else {
            System.out.println(Status.UNSATISFIABLE);
        }
	}
}
