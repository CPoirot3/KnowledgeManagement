package com.bupt.poirot.z3.library;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.RatNum;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Z3Factory {

    BoolExpr mkGT(Context context, String variable, int numerator, int denominator) {
        BoolExpr result = context.mkGt(context.mkIntConst(variable), context.mkReal(numerator, denominator));
        return result;
    }
    BoolExpr mkGE(Context context, String variable, int numerator, int denominator) {
        BoolExpr result = context.mkGe(context.mkIntConst(variable), context.mkReal(numerator, denominator));
        return result;
    }


    BoolExpr mkEqual(Context context, String variable, int numerator, int denominator) {
        BoolExpr result = context.mkEq(context.mkIntConst(variable), context.mkReal(numerator, denominator));
        return result;
    }

    BoolExpr mkLE(Context context, String variable, int numerator, int denominator) {
        BoolExpr result = context.mkLe(context.mkIntConst(variable), context.mkReal(numerator, denominator));
        return  result;
    }

    BoolExpr mkLT(Context context, String variable, int numerator, int denominator) {
        BoolExpr result = context.mkLt(context.mkIntConst(variable), context.mkReal(numerator, denominator));
        return  result;
    }

    public static void main(String[] args) {
        Context context = new Context();
        Solver solver = context.mkSolver();
        Z3Factory z3Factory = new Z3Factory();
        solver.add(z3Factory.mkGE(context, "a", 10, 4));
        if (solver.check() == Status.SATISFIABLE) {
            System.out.println(solver.getModel());
        }

//        RatNum ratNum = context.mkReal(121987125, 10000);
//        System.out.println(ratNum.getNumerator() + " " + ratNum.getDenominator());

    }
}
