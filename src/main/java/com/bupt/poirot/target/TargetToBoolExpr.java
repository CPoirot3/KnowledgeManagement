package com.bupt.poirot.target;

import com.bupt.poirot.jettyServer.jetty.ParamsParse;
import com.bupt.poirot.z3.deduce.TargetInfo;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;

import java.util.ArrayList;
import java.util.List;

public class TargetToBoolExpr {
    public TargetToBoolExpr() {

    }

    // responsible for init the solverMap
    public List<Solver> parseTarget(Context context, TargetInfo targetInfo, String scope) {

        List<Solver> list = new ArrayList<>();
        int min = Integer.valueOf(targetInfo.minCars);
        int sereve = Integer.valueOf(targetInfo.severe);
        int medium = Integer.valueOf(targetInfo.conjection);
        int slight = Integer.valueOf(targetInfo.slightConjection);

        ArithExpr a = context.mkIntConst("valid");
        ArithExpr b = context.mkIntConst("carsInRoad");
        // target 严重拥堵
        BoolExpr targetExpr = context.mkAnd(context.mkGe(context.mkDiv(a, b), context.mkReal(sereve, 100)),
                context.mkGe(b, context.mkInt(min)));
        Solver solverOfSevere = context.mkSolver();
        solverOfSevere.add(targetExpr);
        list.add(solverOfSevere);

        // 拥堵
        BoolExpr targetExpr2 = context.mkAnd(context.mkGe(context.mkDiv(a, b), context.mkReal(medium, 100)),
                context.mkGe(b, context.mkInt(min)));
        Solver solverOfMedium = context.mkSolver();
        solverOfMedium.add(targetExpr2);
        list.add(solverOfMedium);

        // 轻微拥堵
        BoolExpr targetExpr3 = context.mkAnd(context.mkGe(context.mkDiv(a, b), context.mkReal(slight, 100)),
                context.mkGe(b, context.mkInt(min)));
        Solver solverOfSlight = context.mkSolver();
        solverOfSlight.add(targetExpr3);
        list.add(solverOfSlight);

        return list;
    }
}
