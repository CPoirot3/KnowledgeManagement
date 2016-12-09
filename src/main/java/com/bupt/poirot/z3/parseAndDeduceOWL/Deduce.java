package com.bupt.poirot.z3.parseAndDeduceOWL;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Log;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Symbol;
import com.microsoft.z3.Version;
import com.microsoft.z3.Z3Exception;

import java.util.HashMap;

/**
 * Created by hui.chen on 11/29/16.
 */
public class Deduce {
    // / A basic example of how to use quantifiers.
    public static void quantifierExampleByHui(Context ctx) throws Z3Exception {
        System.out.println("QuantifierExampleByHui");
        Log.append("QuantifierExampleByHui");

        Sort[] types = new Sort[2];
        Symbol[] names = new Symbol[2];
        IntExpr[] vars = new IntExpr[2];
        IntExpr[] xs = new IntExpr[2];


        for (int j = 0; j < 2; j++) {
            types[j] = ctx.getIntSort();
            names[j] = ctx.mkSymbol("x_" + Integer.toString(j));
            vars[j] = (IntExpr) ctx.mkBound(1, types[j]); // <-- vars
            // reversed!
            xs[j] = (IntExpr) ctx.mkConst(names[j], types[j]);
        }
        System.out.println();
        for (IntExpr intExpr : vars) {
            System.out.println(intExpr);
        }
//			Expr body = ctx.mkAnd(ctx.mkGe(ctx.mkMul(vars[0], vars[1]), ctx.mkInt(0)),
//					ctx.mkLe(ctx.mkAdd(vars[0], vars[1]), ctx.mkInt(0)));
        Expr body = ctx.mkLe(ctx.mkAdd(vars[0], vars[1]), ctx.mkInt(10));

        Expr x = ctx.mkForall(types, names, body, 1, null, null, ctx.mkSymbol("Q1"), ctx.mkSymbol("skid1"));
        System.out.println("Quantifier X: " + x.toString());

        Solver solver = ctx.mkSimpleSolver();
        solver.add((BoolExpr) x);
        System.out.println(solver.check());
        if (solver.check() == Status.SATISFIABLE) {
            System.out.println(solver.getModel());
        }
        solver.reset();
        System.out.println();

        body = ctx.mkGe(xs[0], ctx.mkInt(100));
        Expr y = ctx.mkForall(xs, body, 1, null, null, ctx.mkSymbol("Q2"), ctx.mkSymbol("skid2"));
        System.out.println(y);
        solver.add((BoolExpr) y);
        System.out.println(solver.check());
        if (solver.check() == Status.SATISFIABLE) {
            System.out.println(solver.getModel());
        }
        solver.reset();
    }

    public static void existQuantifier(Context ctx) {
        System.out.println("QuantifierExampleByHui");
        Log.append("QuantifierExampleByHui");

        Sort[] types = new Sort[1];
        Symbol[] names = new Symbol[1];
        IntExpr[] xs = new IntExpr[1];

        for (int j = 0; j < 1; j++) {
            types[j] = ctx.getIntSort();
            names[j] = ctx.mkSymbol("x_" + Integer.toString(j));
            xs[j] = (IntExpr) ctx.mkConst(names[j], types[j]);
        }

        Solver solver = ctx.mkSimpleSolver();
        Expr body = ctx.mkGe(xs[0], ctx.mkInt(100));
        Expr y = ctx.mkForall(xs, body, 1, null, null, ctx.mkSymbol("Q2"), ctx.mkSymbol("skid2"));
        System.out.println(y);

        solver.push();
        solver.add((BoolExpr) y);
        System.out.println(solver.check());
        solver.pop();
    }

    public static void type(Context ctx) {
        Sort type = ctx.mkStringSort();

        Sort[] domain = new Sort[2];
        domain[0] = type;
        domain[1] = type;
        FuncDecl subtype = ctx.mkFuncDecl("subtype", domain, ctx.getBoolSort());
        FuncDecl array_of = ctx.mkFuncDecl("array-of", type, type);

        Expr[] exprs = new Expr[3];
        exprs[0] = ctx.mkConst("x", type);
        exprs[1] = ctx.mkConst("y", type);
        exprs[2] = ctx.mkConst("z", type);
        Expr body = ctx.mkApp(subtype, exprs[0], exprs[0]);
        Quantifier quantifier = ctx.mkForall(exprs, body, 1, null, null, null, null);

        Solver solver = ctx.mkSimpleSolver();
        solver.add(quantifier);



        System.out.println(solver.check());
        if (solver.check() == Status.SATISFIABLE) {
            System.out.println(solver.getModel());
        }


    }
    public static void main(String[] args) {
        try {

            Log.open("test.log");
            System.out.println(Version.getString());

            { // These examples need model generation turned on.
                HashMap<String, String> cfg = new HashMap<String, String>();
                cfg.put("model", "true");
                Context ctx = new Context(cfg);
//                type(ctx);
            }

            { // These examples need proof generation turned on and
                HashMap<String, String> cfg = new HashMap<>();
                cfg.put("proof", "true");
                Context ctx = new Context(cfg);
                existQuantifier(ctx);
            }
            Log.close();
            if (Log.isOpen())
                System.out.println("Log is still open!");
        } catch (Z3Exception ex) {
            System.out.println("Z3 Managed Exception: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
        } catch (Exception ex) {
            System.out.println("Unknown Exception: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
        }
    }

}

