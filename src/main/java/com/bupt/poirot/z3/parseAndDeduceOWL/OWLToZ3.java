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

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import org.semanticweb.HermiT.model.DLClause;

public class OWLToZ3 {

    public Set<FuncDecl> funcDeclSet = new HashSet<>();

    public Map<String, FuncDecl> stringToFuncMap = new HashMap<>();

    public Pattern pattern = Pattern.compile("(.+)\\((.+?)\\)");


    public Quantifier mk(Context ctx, String string1, String string2) {

        List<Expr> exprList = new ArrayList<>();
        Set<Expr> exprSet = new HashSet<>();
        Map<String, Expr> varToExpr = new HashMap<>();

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


        System.out.println(domain);
        System.out.println(formua);


        BoolExpr preExpr = null;
        String[] strings = domain.split(", ");
        for (String str : strings) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String funcString = matcher.group(1);
                FuncDecl funcDecl = stringToFuncMap.get(funcString);

                String string = matcher.group(2);
                String[] variables = string.split(",");
                Sort[] funcDomainsSort = null;
                Sort funcRangeSort = null;
                if (funcDecl != null) {
                    funcDomainsSort = funcDecl.getDomain();
                    funcRangeSort = funcDecl.getRange();
                } else {
                    // need to mk FuncDecl
                    funcDomainsSort = new Sort[1];
                    String specificName = funcString.substring(funcString.indexOf("#") + 1, funcString.length() - 1);
                    if (variables.length == 1) {
                        // specificName is form like Point, Road, Car
                        funcDomainsSort[0] = ctx.mkUninterpretedSort(specificName);
                        funcRangeSort = ctx.getBoolSort();
                    } else {
                        // specificName is form like hasSpeed, hasBeginPoint, hasEndPoint, hasLongitude, hasLatitude
                        // specificName is form like hasSpeed, hasBeginPoint, hasEndPoint, hasLongitude, hasLatitude
                        if (specificName.endsWith("Speed")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Car");
//                            funcRangeSort = ctx.mkUninterpretedSort("Speed");
                            funcRangeSort = ctx.getRealSort();

                        } else if (specificName.endsWith("Point")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Road");
                            funcRangeSort = ctx.mkUninterpretedSort("Point");

                        } else if (specificName.endsWith("tude")) {
                            funcDomainsSort[0] = ctx.mkUninterpretedSort("Point");
                            funcRangeSort = ctx.getRealSort();

                        }
                    }
                    funcDecl = ctx.mkFuncDecl(funcString, funcDomainsSort[0], funcRangeSort);
                }
                stringToFuncMap.put(funcString, funcDecl);

                Expr[] funcDomainExprs = new Expr[funcDomainsSort.length];
                for (int i = 0; i < funcDomainExprs.length; i++) {
                    funcDomainExprs[i] = ctx.mkConst(variables[i], funcDomainsSort[i]);
                    exprSet.add(funcDomainExprs[i]);
                    varToExpr.put(variables[i], funcDomainExprs[i]);
                }

                Expr funcRangeExpr = null;
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


        System.out.println();
        System.out.println("preExpr : ");
        System.out.println(preExpr);


        BoolExpr formulaExpr = null;
        for (String s : formua.split(" v ")) {
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
                    if (isNumeralFunc(funcString)) {
                        Expr expr = varToExpr.get(variables[0]);
                        Sort sort = expr.getSort();
                        Expr e = ctx.mkConst("temp", sort);
                        Expr tempBody = ctx.mkNot(ctx.mkEq(e, expr)); //
                        appFuncExpr = ctx.mkForall(new Expr[]{e}, tempBody, 1, null, null, ctx.mkSymbol("Q2"), ctx.mkSymbol("skid2"));
                    } else if (hasAtLeastOrAtMost(funcString)) {

                        if (funcString.startsWith("at")) {
                            // startsWith form like :  atMost/atLeast

                            int limit = Integer.MIN_VALUE;
                            Matcher matcherOfAtLeastAndAtMost = pattern.matcher(funcString);
                            if (matcherOfAtLeastAndAtMost.find()) {
                                String conditionName = matcherOfAtLeastAndAtMost.group(1); //
                                String[] strs = matcherOfAtLeastAndAtMost.group(2).split(" ");

                                limit = Integer.valueOf(strs[0]);
                                FuncDecl a = stringToFuncMap.get(strs[1]);


                                Expr[] exprs = new Expr[limit];
                                for (int i = 0; i < limit; i++) {
                                    exprs[i] = ctx.mkConst("x_" + i, a.getRange());
                                }

//                                String name = str.substring(funcString.lastIndexOf("(") + 1, funcString.length() - 1);
                                String name = variables[0];
                                Expr expr = varToExpr.get(name);

                                Expr restrctExpr = null;
                                for (int i = 0; i < limit; i++) {
                                    if (restrctExpr == null) {
                                        restrctExpr = ctx.mkEq(a.apply(expr), exprs[i]);
                                    } else {
                                        restrctExpr = ctx.mkAnd((BoolExpr) restrctExpr, ctx.mkEq(a.apply(expr), exprs[i]));
                                    }
                                }
                                appFuncExpr = ctx.mkExists(exprs, restrctExpr, 1, null, null, ctx.mkSymbol("Q2"), ctx.mkSymbol("skid2"));

                            } else {
                                throw new RuntimeException();
                            }
                        } else {

                            // startsWith form like :  [Y1 == Y2]atMost
                            // the info after @ is just a
                            funcString = funcString.substring(1, funcString.indexOf("@") - 1);
                            String[] names = funcString.split(" == ");
                            Expr a = varToExpr.get(names[0]);
                            Expr b = varToExpr.get(names[1]);
                            appFuncExpr = ctx.mkEq(a, b);
                        }
                    } else if (hasDataName(funcString)) {  // deal with the data like int , float, double
                        System.out.println("hasDataName ");
                        System.out.println(funcString);
                        Sort[] funcDomainsSort = null;
                        Sort funcRangeSort = null;
                        if (funcDecl != null) {
                            funcDomainsSort = funcDecl.getDomain();
                            funcRangeSort = funcDecl.getRange();
                        } else {

                        }
                        stringToFuncMap.put(funcString, funcDecl);
                        String varName = variables[0];

                        Expr expr = varToExpr.get(varName);
                        if (funcString.contains("double")) {
                            appFuncExpr = ctx.mkIsInteger((RealExpr) expr);
                        } else if (funcString.contains("float")) {
                            appFuncExpr = ctx.mkIsInteger((RealExpr) expr);
                        } else if (funcString.contains("int")) {
                            appFuncExpr = ctx.mkIsInteger((RealExpr) expr);
                        }

                    } else if (hasEquals(funcString)) {
                        String[] names = funcString.split(" == ");
                        Expr a = varToExpr.get(names[0]);
                        Expr b = varToExpr.get(names[1]);
                        appFuncExpr = ctx.mkEq(a, b);
                    } else {

                        Sort[] funcDomainsSort;
                        Sort funcRangeSort = null;
                        if (funcDecl != null) {
                            funcDomainsSort = funcDecl.getDomain();
                            funcRangeSort = funcDecl.getRange();
                        } else {

                            // need to make FuncDecl
                            funcDomainsSort = new Sort[1];
                            String specificName = funcString.substring(funcString.indexOf("#") + 1, funcString.length() - 1);
                            if (variables.length == 1) {
                                // specificName is form like Point, Road, Car
                                funcDomainsSort[0] = ctx.mkUninterpretedSort(specificName);
                                funcRangeSort = ctx.getBoolSort();
                            } else {
                                // specificName is form like hasSpeed, hasBeginPoint, hasEndPoint, hasLongitude, hasLatitude
                                if (specificName.endsWith("Speed")) {
                                    funcDomainsSort[0] = ctx.mkUninterpretedSort("Car");
//                                    funcRangeSort = ctx.mkUninterpretedSort("Speed");
                                    funcRangeSort = ctx.getRealSort();

                                } else if (specificName.endsWith("Point")) {
                                    funcDomainsSort[0] = ctx.mkUninterpretedSort("Road");
                                    funcRangeSort = ctx.mkUninterpretedSort("Point");

                                } else if (specificName.endsWith("tude")) {
                                    funcDomainsSort[0] = ctx.mkUninterpretedSort("Point");
                                    funcRangeSort = ctx.getRealSort();
                                }
                            }
                            funcDecl = ctx.mkFuncDecl(funcString, funcDomainsSort[0], funcRangeSort);
                        }

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

        System.out.println();
        System.out.println("formulaExpr : ");
        System.out.println(formulaExpr);

        if (preExpr == null || formulaExpr == null) {
            return null;
        }
        BoolExpr body = ctx.mkImplies(preExpr, formulaExpr);

        System.out.println("body : " + body);
        Expr[] boundVariables = new Expr[varToExpr.size()];
        exprList.addAll(varToExpr.values());
        for (int i = 0; i < exprList.size(); i++) {
            boundVariables[i] = exprList.get(i);
        }

        System.out.println(boundVariables.length);

        Quantifier res = ctx.mkForall(boundVariables, body, 1, null, null, ctx.mkSymbol("a"), ctx.mkSymbol("b"));
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


    //    public static BoolExpr mkQuantifier(Context ctx, String string1, String string2, Set<String> sortSet, Set<String> quantifierSet) {
//    public BoolExpr mkQuantifier(Context ctx, String string1, String string2) {
//        Map<String, String> variableNameToExprName = new HashMap<>();
//        Map<String, Sort> variableNameToSort = new HashMap<>();
//
//        String domain, formua;
//        if (string1.contains("atLeast") || string1.contains("atMost") || string1.contains(" v ")) {
//            domain = string2;
//            formua = string1;
//        } else if (string2.contains("atLeast") || string2.contains("atMost") || string2.contains(" v ")) {
//            domain = string1;
//            formua = string2;
//        } else {
//            if (string1.contains(",")) {
//                domain = string2;
//                formua = string1;
//            } else {
//                domain = string1;
//                formua = string2;
//            }
//        }
//
//        BoolExpr preExpr = null;
//        String[] strings = domain.split(", ");
//        for (String str : strings) {
//            Matcher matcher = pattern.matcher(str);
//            if (matcher.find()) {
//                String funcString = matcher.group(1);
//                FuncDecl funcDecl = stringToFuncMap.get(funcString);
//
//                String string = matcher.group(2);
//                String[] variables = string.split(",");
//                Sort[] domains = new Sort[variables.length];
//
//                if (funcDecl != null) {
//                    domains = funcDecl.getDomain();
//                } else {
//                    // need to mk FuncDecl
//                    for (int i = 0; i < domains.length; i++) {
//                        domains[i] = variableNameToSort.containsKey(variables[i]) ? variableNameToSort.get(variables[i]) : ctx.mkUninterpretedSort(variables[i]);
//                        variableNameToSort.put(variables[i], domains[i]);
//                    }
//                    funcDecl = ctx.mkFuncDecl(funcString, domains, ctx.getBoolSort());
//                }
//                stringToFuncMap.put(funcString, funcDecl);
//
//                funcDeclSet.add(funcDecl);
//                Expr[] exprs = new Expr[variables.length];
//                for (int i = 0; i < exprs.length; i++) {
//                    String variableName = variableNameToExprName.containsKey(variables[i]) ? variableNameToExprName.get(variables[i]) : variables[i] + (begin++);
//                    variableNameToExprName.put(variables[i], variableName);
//                    exprs[i] = ctx.mkConst(variableName, domains[i]);
//                }
//                if (preExpr == null) {
//                    preExpr = (BoolExpr) ctx.mkApp(funcDecl, exprs);
//                } else {
//                    preExpr = ctx.mkAnd(preExpr, (BoolExpr) ctx.mkApp(funcDecl, exprs));
//                }
//            }
//        }
//
//        BoolExpr formulaExpr = null;
//        for (String s : formua.split(" v ")) {
//            BoolExpr subExpr = null;
//            strings = s.split(", ");
//            for (String str : strings) {
//                Matcher matcher = pattern.matcher(str);
//                if (matcher.find()) {
//                    String funcString = matcher.group(1);
//                    String funcName = matcher.group(1);
//                    FuncDecl funcDecl = stringToFuncMap.get(funcString);
//                    int limit = Integer.MIN_VALUE;
//                    if (funcName.indexOf("atLeast") > -1 || funcName.indexOf("atMost") > -1) {
//                        Matcher matcherOfAtLeastAndAtMost = pattern.matcher(funcName);
//                        if (matcherOfAtLeastAndAtMost.find()) {
//                            String conditionName = matcherOfAtLeastAndAtMost.group(1);
//                            String[] strs = matcherOfAtLeastAndAtMost.group(2).split(" ");
//                            limit = Integer.valueOf(strs[0]);
//                            funcName = conditionName + "@" + strs[1] + "@" + strs[2];
//                        } else {
//                            System.out.println("mark");
//                            throw new RuntimeException();
//                        }
//                    }
//
//                    String string = matcher.group(2);
//                    String[] variables = string.split(",");
//                    Sort[] domains = new Sort[variables.length];
//
//                    if (funcDecl != null) {
//                        domains = funcDecl.getDomain();
//                    } else {
//                        for (int i = 0; i < domains.length; i++) {
//                            domains[i] = variableNameToSort.containsKey(variables[i]) ? variableNameToSort.get(variables[i]) : ctx.mkUninterpretedSort(variables[i]);
//                            variableNameToSort.put(variables[i], domains[i]);
//                        }
//                        if (funcName.contains("atLeast") || funcName.contains("atMost")) {
//                            funcDecl = ctx.mkFuncDecl(funcName, domains, ctx.getIntSort());
//                        } else {
//                            funcDecl = ctx.mkFuncDecl(funcName, domains, ctx.getBoolSort());
//                        }
//                        System.out.println("");
//                    }
//                    stringToFuncMap.put(funcString, funcDecl);
//                    funcDeclSet.add(funcDecl);
//                    Expr[] exprs = new Expr[variables.length];
//                    for (int i = 0; i < exprs.length; i++) {
//                        String variableName = variableNameToExprName.containsKey(variables[i]) ? variableNameToExprName.get(variables[i]) : variables[i] + (begin++);
//                        variableNameToExprName.put(variables[i], variableName);
//                        exprs[i] = ctx.mkConst(variableName, domains[i]);
//                    }
//                    Expr expr = ctx.mkApp(funcDecl, exprs);
//                    if (funcName.contains("atLeast")) {
//                        expr = ctx.mkGe((ArithExpr) expr, ctx.mkInt(limit));
//                    } else if (funcName.contains("atMost")) {
//                        expr = ctx.mkLe((ArithExpr) expr, ctx.mkInt(limit));
//                    }
//
//                    if (subExpr == null) {
//                        subExpr = (BoolExpr) expr;
//                    } else {
//                        subExpr = ctx.mkAnd(subExpr, (BoolExpr) expr);
//                    }
//
//                }
//            }
//            if (subExpr == null) {
//                continue;
//            }
//            if (formulaExpr == null) {
//                formulaExpr = subExpr;
//            } else {
//                formulaExpr = ctx.mkOr(formulaExpr, subExpr);
//            }
//        }
//
//        if (preExpr == null || formulaExpr == null) {
//            return null;
//        }
//        BoolExpr finalExpr = ctx.mkImplies(preExpr, formulaExpr);
////        System.out.println("finalExpr : " + finalExpr);
//        System.out.println();
//        return finalExpr;
//    }

    public BoolExpr parseFromStream(Context context, InputStream inputStream) {
        Set<DLClause> set = ParseOWL.owlToDLClsuses(inputStream);
        System.out.println("DLClause number : " + set.size());

        Set<String> quantifiersSet = new HashSet<>();
        Set<String> sortSet = new HashSet<>();

        BoolExpr res = null;
        int count = 1;
        for (DLClause dlClause : set) {
            String dlClauseString = dlClause.toString();
            String[] strings = dlClauseString.split(" :- ");
            System.out.println("dlClauseString " + (count++) + " : " + dlClauseString);
            Quantifier quantifier = mk(context, strings[0], strings[1]);

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
//            System.out.println(boolExpr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
