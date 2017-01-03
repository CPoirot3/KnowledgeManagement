package com.bupt.poirot.z3.library;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
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


    public static ArithExpr plus(Context context, ArithExpr t) {
        return context.mkAdd(t, context.mkReal(1, 1));
    }

    public static ArithExpr subtraction(Context context, ArithExpr t) {
        return context.mkSub(t, context.mkReal(1, 1));
    }

    public static void get() {
        Context ctx = new Context();
        IntExpr valid = ctx.mkIntConst("valid");
        IntExpr total = ctx.mkIntConst("total");

        BoolExpr target = ctx.mkGe(ctx.mkDiv(valid, total), ctx.mkReal(80, 100));

        Solver solver = ctx.mkSolver();
        solver.add(ctx.mkNot(target));
        solver.push();

        boolean mark = true;
        ArithExpr totalExpr = ctx.mkReal(0,1);
        ArithExpr validExpr =  ctx.mkReal(0,1);

        totalExpr = plus(ctx, totalExpr);


//		if (mark) {
//			validExpr = plus(ctx, validExpr);
//		}
        System.out.println(totalExpr);
        System.out.println(validExpr);

//		Sort domain = ctx.mkIntSort();
//		Sort range = ctx.mkIntSort();
//		FuncDecl funcDecl = ctx.mkFuncDecl("plus", domain, range);
//		BoolExpr vPlusPlus = ctx.mkBoolConst("vPlusPlus");
//		BoolExpr tPlusPlus = ctx.mkBoolConst("tPlusPlus");
//		BoolExpr truth1 = ctx.mkImplies(ctx.mkEq(vPlusPlus, ctx.mkTrue()), ctx.mkEq(valid, ctx.mkAdd(validExpr, ctx.mkReal(1, 1))));
//		BoolExpr truth2 = ctx.mkImplies(ctx.mkEq(tPlusPlus, ctx.mkTrue()), ctx.mkEq(valid, ctx.mkAdd(totalExpr, ctx.mkReal(1, 1))));
//		System.out.println(truth1);
//		System.out.println(truth2);

        solver.add(ctx.mkEq(valid, validExpr));
        solver.add(ctx.mkEq(total, totalExpr));


        totalExpr = plus(ctx, totalExpr);
        totalExpr = plus(ctx, totalExpr);
        totalExpr = plus(ctx, totalExpr);

        validExpr = plus(ctx, validExpr);
        validExpr = plus(ctx, validExpr);
        validExpr = plus(ctx, validExpr);
        validExpr = plus(ctx, validExpr);
        if (solver.check() == Status.UNSATISFIABLE) {
            System.out.println("proved");
        } else {
            System.out.println("not proved");
            System.out.println(solver.getModel());
        }
    }


    public static void main(String[] args) {
        Context context = new Context();
        Solver solver = context.mkSolver();
        Z3Factory z3Factory = new Z3Factory();
        z3Factory.get();
//        solver.add(z3Factory.mkGE(context, "a", 10, 4));
//        if (solver.check() == Status.SATISFIABLE) {
//            System.out.println(solver.getModel());
//        }

//        RatNum ratNum = context.mkReal(121987125, 10000);
//        System.out.println(ratNum.getNumerator() + " " + ratNum.getDenominator());

    }
}
