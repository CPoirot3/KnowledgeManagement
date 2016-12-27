package com.bupt.poirot.z3.parseAndDeduceOWL;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Sort;
import org.semanticweb.HermiT.model.DLClause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuantifierGenerate {
    public static Map<String, FuncDecl> stringToFuncMap = new HashMap<>();

    public Pattern pattern = Pattern.compile("(.+)\\((.+?)\\)");


    public String[] findDomainFormulaString(String dlClauseString) {
//        System.out.println(dlClauseString);

        String[] strs = dlClauseString.split(" :- ");

        String domain, formula;
        if (strs[0].contains("atLeast") || strs[0].contains("atMost") || strs[0].contains(" v ")) {
            domain = strs[1];
            formula = strs[0];
        } else if (strs[1].contains("atLeast") || strs[1].contains("atMost") || strs[1].contains(" v ")) {
            domain = strs[0];
            formula = strs[1];
        } else {
            if (strs[0].contains(",")) {
                domain = strs[1];
                formula = strs[0];
            } else {
                domain = strs[0];
                formula = strs[1];
            }
        }
        return new String[] { domain, formula};
    }

    public Quantifier mkQuantifier(Context ctx, DLClause dlClause) {

        String[] stringsOfDomainAndFormula = findDomainFormulaString(dlClause.toString());
        String domain = stringsOfDomainAndFormula[0];
        String formula = stringsOfDomainAndFormula[1];

        List<Expr> exprList = new ArrayList<>();
        Set<Expr> exprSet = new HashSet<>();
        Map<String, Expr> varToExpr = new HashMap<>();

        BoolExpr preExpr = null;
        String[] strings = domain.split(", ");
        for (String str : strings) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String funcString = matcher.group(1);
                FuncDecl funcDecl = stringToFuncMap.get(funcString);

                String string = matcher.group(2);

                String[] variables = string.split(",");
                Sort[] funcDomainsSort;
                Sort funcRangeSort;
                if (funcDecl != null) {
                    funcDomainsSort = funcDecl.getDomain();
                    funcRangeSort = funcDecl.getRange();
                } else {
                    // need to mkQuantifier FuncDecl

                    String specificName = funcString.substring(funcString.indexOf("#") + 1, funcString.length() - 1);
                    if (variables.length == 1) {
                        funcDomainsSort = new Sort[1];
                        // specificName is form like Point, Road, Car
                        funcDomainsSort[0] = ctx.mkUninterpretedSort(specificName);
                        funcRangeSort = ctx.getBoolSort();
                    } else {
                        funcDomainsSort = new Sort[2];
                        // specificName is form like hasSpeed, hasBeginPoint, hasEndPoint, hasLongitude, hasLatitude
                        if (specificName.endsWith("X1")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Position");
                            funcDomainsSort[1] = ctx.getRealSort();
                        } else if (specificName.endsWith("X2")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Position");
                            funcDomainsSort[1] = ctx.getRealSort();

                        } else if (specificName.endsWith("Y1")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Position");
                            funcDomainsSort[1] = ctx.getRealSort();

                        } else if (specificName.endsWith("Y2")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Position");
                            funcDomainsSort[1] = ctx.getRealSort();

                        } else if (specificName.endsWith("osition")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Road");
                            funcDomainsSort[1] = ctx.mkUninterpretedSort("Position");

                        } else if (Character.isDigit(specificName.indexOf(specificName.length() - 1))) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Positon");
                            funcDomainsSort[1] = ctx.getRealSort();
                        }
                        funcRangeSort = ctx.getBoolSort();
                    }

                    funcDecl = ctx.mkFuncDecl(funcString, funcDomainsSort, funcRangeSort);
                }


                stringToFuncMap.put(funcString, funcDecl);

                Expr[] funcDomainExprs = new Expr[funcDomainsSort.length];
                for (int i = 0; i < funcDomainExprs.length; i++) {
                    funcDomainExprs[i] = ctx.mkConst(variables[i], funcDomainsSort[i]);
                    exprSet.add(funcDomainExprs[i]);
                    varToExpr.put(variables[i], funcDomainExprs[i]);
                }

                Expr funcRangeExpr;
                if (variables.length == 1) {
                    funcRangeExpr = ctx.mkTrue();
                } else {
                    String name = variables[variables.length - 1];
                    funcRangeExpr = ctx.mkConst(name, funcRangeSort);
                    varToExpr.put(name, funcRangeExpr);
                }
                BoolExpr boolExpr = ctx.mkEq(ctx.mkApp(funcDecl, funcDomainExprs), funcRangeExpr);
                if (preExpr == null) {
                    preExpr = boolExpr;
                } else {
                    preExpr = ctx.mkAnd(preExpr, boolExpr);
                }
            }
        }
//        System.out.println("preExpr : ");
//        System.out.println(preExpr);

        BoolExpr formulaExpr = null;
        for (String s : formula.split(" v ")) {
            BoolExpr subExpr = null;
            strings = s.split(", ");
            for (String str : strings) {
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String funcString = matcher.group(1);
                    FuncDecl funcDecl = stringToFuncMap.get(funcString);

                    String string = matcher.group(2);
                    String[] variables = string.split(",");

                    BoolExpr appFuncExpr = null;
                    if (!isNumeralFunc(funcString) && !hasAtLeastOrAtMost(funcString) && !hasDataName(funcString) && !hasEquals(funcString)) {
                        Sort[] funcDomainsSort;
                        Sort funcRangeSort = null;
                        if (funcDecl != null) {
                            funcDomainsSort = funcDecl.getDomain();
                            funcRangeSort = funcDecl.getRange();
                        } else {
                            // need to make FuncDecl
                            String specificName = funcString.substring(funcString.indexOf("#") + 1, funcString.length() - 1);
                            if (variables.length == 1) {
                                funcDomainsSort = new Sort[1];

                                // specificName is form like Point, Road, Car
                                funcDomainsSort[0] = ctx.mkUninterpretedSort(specificName);
                                funcRangeSort = ctx.getBoolSort();
                            } else {
                                funcDomainsSort = new Sort[2];
                                funcDomainsSort[1] = ctx.getRealSort();
                                funcRangeSort = ctx.getBoolSort();

                                // specificName is form like hasSpeed, hasBeginPoint, hasEndPoint, hasLongitude, hasLatitude
                                if (specificName.endsWith("X1") || specificName.endsWith("X2") || specificName.endsWith("Y1") || specificName.endsWith("Y2")) {
                                    funcDomainsSort[0] = ctx.mkUninterpretedSort("Position");
                                } else if (specificName.endsWith("osition")) {
                                    funcDomainsSort[0] = ctx.mkUninterpretedSort("Road");
                                    funcDomainsSort[1] = ctx.mkUninterpretedSort("Position");
                                } else if (Character.isDigit(specificName.indexOf(specificName.length() - 1))) {
                                    funcDomainsSort[0] = ctx.mkUninterpretedSort("Positon");
                                }
                            }
                        }

                        funcDecl = ctx.mkFuncDecl(funcString, funcDomainsSort, funcRangeSort);

                        stringToFuncMap.put(funcString, funcDecl);
                        Expr[] funcDomainExprs = new Expr[funcDomainsSort.length];
                        for (int i = 0; i < funcDomainExprs.length; i++) {
                            funcDomainExprs[i] = ctx.mkConst(variables[i], funcDomainsSort[i]);
                        }

                        Expr funcRangeExpr;
                        if (variables.length == 1) {
                            funcRangeExpr = ctx.mkTrue();
                        } else {
                            String name = variables[variables.length - 1];
                            funcRangeExpr = ctx.mkConst(name, funcRangeSort);
                        }
                        appFuncExpr = ctx.mkEq(ctx.mkApp(funcDecl, funcDomainExprs), funcRangeExpr);
                    }
                    if (appFuncExpr == null) {
                        continue;
                    }
                    if (subExpr == null) {
                        subExpr = appFuncExpr;
                    } else {
                        subExpr = ctx.mkAnd(subExpr, appFuncExpr);
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

//        System.out.println();
//        System.out.println("formulaExpr : ");
//        System.out.println(formulaExpr);

        if (preExpr == null || formulaExpr == null) {
            return null;
        }
        BoolExpr body = ctx.mkImplies(preExpr, formulaExpr);

//        System.out.println(body);
        Expr[] boundVariables = new Expr[varToExpr.size()];
        exprList.addAll(varToExpr.values());
        for (int i = 0; i < exprList.size(); i++) {
            boundVariables[i] = exprList.get(i);
        }

//        System.out.println(boundVariables.length);

        Quantifier res = ctx.mkForall(boundVariables, body, 1, null, null, ctx.mkSymbol("a"), ctx.mkSymbol("b"));
//        System.out.println(res);
        return res;
    }

    private boolean isNumeralFunc(String funcString) {
        return funcString.contains("xsd:");
    }

    private boolean hasEquals(String funcString) {
        return funcString.contains(" == ");
    }

    private boolean hasDataName(String funcString) {
        return funcString.contains("xsd:double") || funcString.contains("xsd:float") || funcString.contains("xsd:int");
    }

    private boolean hasAtLeastOrAtMost(String funcString) {
        return funcString.indexOf("atLeast") > -1 || funcString.indexOf("atMost") > -1;
    }


}
