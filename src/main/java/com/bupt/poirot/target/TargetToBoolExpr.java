package com.bupt.poirot.target;

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

    public List<Solver> parseTargetToBoolExpr(Context ctx, TargetInfo targetInfo) {

        List<Solver> list = new ArrayList<>();
        int min = Integer.valueOf(targetInfo.minCars);
        int sereve = Integer.valueOf(targetInfo.severe);
        int medium = Integer.valueOf(targetInfo.conjection);
        int slight = Integer.valueOf(targetInfo.slightConjection);

        ArithExpr a = ctx.mkIntConst("valid");
        ArithExpr b = ctx.mkIntConst("carsInRoad");
        // target 严重拥堵
        BoolExpr targetExpr = ctx.mkAnd(ctx.mkGe(ctx.mkDiv(a, b), ctx.mkReal(sereve, 100)),
                ctx.mkGe(b, ctx.mkInt(min)));
        System.out.println(targetExpr);
        Solver solverOfSevere = ctx.mkSolver();
        solverOfSevere.add(targetExpr);
        list.add(solverOfSevere);

        // 拥堵
        BoolExpr targetExpr2 = ctx.mkAnd(ctx.mkGe(ctx.mkDiv(a, b), ctx.mkReal(medium, 100)),
                ctx.mkGe(b, ctx.mkInt(min)));
        System.out.println(targetExpr2);
        Solver solverOfMedium = ctx.mkSolver();
        solverOfMedium.add(targetExpr2);
        list.add(solverOfMedium);

        // 轻微拥堵
        BoolExpr targetExpr3 = ctx.mkAnd(ctx.mkGe(ctx.mkDiv(a, b), ctx.mkReal(slight, 100)),
                ctx.mkGe(b, ctx.mkInt(min)));
        System.out.println(targetExpr3);
        Solver solverOfSlight = ctx.mkSolver();
        solverOfSlight.add(targetExpr3);
        list.add(solverOfSlight);

        return list;
    }
}
