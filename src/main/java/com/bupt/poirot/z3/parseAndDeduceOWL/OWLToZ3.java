package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import org.semanticweb.HermiT.model.DLClause;

public class OWLToZ3 {

	public Set<FuncDecl>  funcDeclSet = new HashSet<>();;

    public Map<String, FuncDecl> stringToFuncMap = new HashMap<>();

    public Pattern pattern = Pattern.compile("(.+)\\((.+?)\\)");
    public int begin = 0;

//    public static BoolExpr mkQuantifier(Context ctx, String string1, String string2, Set<String> sortSet, Set<String> quantifierSet) {
    public BoolExpr mkQuantifier(Context ctx, String string1, String string2) {
        Map<String, String> variableNameToExprName = new HashMap<>();
		Map<String, Sort> variableNameToSort = new HashMap<>();
		Set<Sort> sets = new HashSet<>();

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

        BoolExpr preExpr = null;
        String[] strings = domain.split(", ");
        for (String str : strings) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String funcString = matcher.group(1);
                FuncDecl funcDecl = stringToFuncMap.get(funcString);

                String string = matcher.group(2);
                String[] variables = string.split(",");
                Sort[] domains = new Sort[variables.length];

                if (funcDecl != null) {
                    domains = funcDecl.getDomain();
                } else {
                    // need to mk FuncDecl
                    for (int i = 0; i < domains.length; i++) {
						domains[i] = variableNameToSort.containsKey(variables[i]) ? variableNameToSort.get(variables[i]) : 	ctx.mkUninterpretedSort(variables[i]);
						variableNameToSort.put(variables[i], domains[i]);
                    }
                    funcDecl = ctx.mkFuncDecl(funcString, domains, ctx.getBoolSort());
                }
				stringToFuncMap.put(funcString, funcDecl);

				funcDeclSet.add(funcDecl);
                Expr[] exprs = new Expr[variables.length];
                for (int i = 0; i < exprs.length; i++) {
                    String variableName = variableNameToExprName.containsKey(variables[i]) ? variableNameToExprName.get(variables[i]) : variables[i] + (begin++);
                    variableNameToExprName.put(variables[i], variableName);
                    exprs[i] = ctx.mkConst(variableName, domains[i]);
                }
                if (preExpr == null) {
                    preExpr = (BoolExpr)ctx.mkApp(funcDecl, exprs);
                } else {
                    preExpr = ctx.mkAnd(preExpr, (BoolExpr)ctx.mkApp(funcDecl, exprs));
                }
            }
        }

        BoolExpr formulaExpr = null;
        for (String s : formua.split(" v ")) {
            BoolExpr subExpr = null;
            strings = s.split(", ");
            for (String str : strings) {
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String funcString = matcher.group(1);
                    String funcName = matcher.group(1);
                    FuncDecl funcDecl = stringToFuncMap.get(funcString);
                    int limit = Integer.MIN_VALUE;
                    if (funcName.indexOf("atLeast") > -1 || funcName.indexOf("atMost") > -1) {
                        Matcher matcherOfAtLeastAndAtMost = pattern.matcher(funcName);
                        if (matcherOfAtLeastAndAtMost.find()) {
                            String conditionName = matcherOfAtLeastAndAtMost.group(1);
                            String[] strs = matcherOfAtLeastAndAtMost.group(2).split(" ");
                            limit = Integer.valueOf(strs[0]);
                            funcName = conditionName + "@" + strs[1] + "@" + strs[2];
                        } else {
                            System.out.println("mark");
                            throw new RuntimeException();
                        }
                    }

                    String string = matcher.group(2);
                    String[] variables = string.split(",");
                    Sort[] domains = new Sort[variables.length];

                    if (funcDecl != null) {
                        domains = funcDecl.getDomain();
                    } else {
                        for (int i = 0; i < domains.length; i++) {
							domains[i] = variableNameToSort.containsKey(variables[i]) ? variableNameToSort.get(variables[i]) : 	ctx.mkUninterpretedSort(variables[i]);
							variableNameToSort.put(variables[i], domains[i]);
                        }
                        if (funcName.contains("atLeast") || funcName.contains("atMost")) {
                            funcDecl = ctx.mkFuncDecl(funcName, domains, ctx.getIntSort());
                        } else {
                            funcDecl = ctx.mkFuncDecl(funcName, domains, ctx.getBoolSort());
                        }
						System.out.println("");
                    }
					stringToFuncMap.put(funcString, funcDecl);
					funcDeclSet.add(funcDecl);
                    Expr[] exprs = new Expr[variables.length];
                    for (int i = 0; i < exprs.length; i++) {
                        String variableName = variableNameToExprName.containsKey(variables[i]) ? variableNameToExprName.get(variables[i]) : variables[i] + (begin++);
                        variableNameToExprName.put(variables[i], variableName);
                        exprs[i] = ctx.mkConst(variableName, domains[i]);
                    }
                    Expr expr = ctx.mkApp(funcDecl, exprs);
                    if (funcName.contains("atLeast")) {
                        expr = ctx.mkGe((ArithExpr)expr, ctx.mkInt(limit));
                    } else if (funcName.contains("atMost")) {
                        expr = ctx.mkLe((ArithExpr)expr, ctx.mkInt(limit));
                    }

                    if (subExpr == null) {
                        subExpr = (BoolExpr)expr;
                    } else {
                        subExpr = ctx.mkAnd(subExpr, (BoolExpr)expr);
                    }

                }
            }
            if (subExpr == null) {
                continue;
            }
            if (formulaExpr == null) {
                formulaExpr = subExpr;
            } else {
                formulaExpr = ctx.mkOr(formulaExpr, subExpr);
            }
        }
//        System.out.println("expression : " + preExpr);
//        System.out.println("expression2 : " + formulaExpr);

        if (preExpr == null || formulaExpr == null) {
            return null;
        }
        BoolExpr finalExpr = ctx.mkImplies(preExpr, formulaExpr);
        System.out.println("finalExpr : " + finalExpr);
        System.out.println();

//        Quantifier quantifier = null;
//        return quantifier;

        return finalExpr;
    }

    public BoolExpr parseFromStream(Context context, InputStream inputStream) {
    	Set<DLClause> set = ParseOWL.owlToDLClsuses(inputStream);
		System.out.println("DLClause number : " + set.size());

        Set<String> quantifiersSet = new HashSet<>();
        Set<String> sortSet = new HashSet<>();

        BoolExpr res = null;
        int count = 1;
		for (DLClause dlClause: set) {
            String dlClauseString = dlClause.toString();
			String[] strings = dlClauseString.split(" :- ");

			System.out.println("dlClauseString " + (count++) + " : " + dlClauseString);

            BoolExpr boolExpr = mkQuantifier(context, strings[0], strings[1]);

            if (boolExpr != null) {
                if (res != null) {
                    res = context.mkAnd(res, boolExpr);
                } else {
                    res = boolExpr;
                }
            }
		}
        return res;
    }
    
	public static void main(String[] args) {
        File schemaFile = new File("data/schema.owl");
        try {
            InputStream inputStream = new FileInputStream(schemaFile);
            OWLToZ3 owlToZ3 = new OWLToZ3();
            BoolExpr boolExpr = owlToZ3.parseFromStream(new Context(), inputStream);
//            System.out.println(boolExpr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
