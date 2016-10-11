/**
 * 2015年11月18日
 * Poirot
 */
package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.util.HashMap;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
 

/**
 * @author Poirot
 *
 */
public class SortDeduction {
	@SuppressWarnings("serial")
	class TestFailedException extends Exception {
		public TestFailedException() {
			super("Check FAILED");
		}
	};
	void prove(Context ctx, BoolExpr f, boolean useMBQI) throws Z3Exception, TestFailedException {
		BoolExpr[] assumptions = new BoolExpr[0];
		prove(ctx, f, useMBQI, assumptions);
	}

	void prove(Context ctx, BoolExpr f, boolean useMBQI, BoolExpr... assumptions)
			throws Z3Exception, TestFailedException {
		System.out.println("Proving: " + f);
		Solver s = ctx.mkSolver();
		Params p = ctx.mkParams();
		p.add("mbqi", useMBQI);
		s.setParameters(p);
		for (BoolExpr a : assumptions)
			s.add(a);
		s.add(ctx.mkNot(f));
		Status q = s.check();

		switch (q) {
		case UNKNOWN:
			System.out.println("Unknown because: " + s.getReasonUnknown());
			break;
		case SATISFIABLE:
			throw new TestFailedException();
		case UNSATISFIABLE:
			System.out.println("OK, proof: " + s.getProof());
			break;
		}
	}

	public void sortReasoning(Context ctx) throws Z3Exception, TestFailedException {
		// /* declare function f */
		// Sort I = ctx.mkBoolSort();
		// FuncDecl fTemperature = ctx.mkFuncDecl("fTemperature", new Sort[] {
		// I}, I);
		//
		// /* create x, y, v, w, fxy, fwv */
		// Expr x = ctx.mkIntConst("x");
		// Expr y = ctx.mkIntConst("y");
		// Expr v = ctx.mkIntConst("v");
		// Expr w = ctx.mkIntConst("w");
		// Expr fx = ctx.mkApp(fTemperature, x);
		//

		/* create uninterpreted type. */
		Sort U = ctx.mkUninterpretedSort(ctx.mkSymbol("U"));

		/* declare function g */
		FuncDecl g = ctx.mkFuncDecl("g", U, U);

		/* create x and y */
		Expr x = ctx.mkConst("x", U);
		Expr y = ctx.mkConst("y", U);
		/* create g(x), g(y) */
		Expr gx = g.apply(x);
		Expr gy = g.apply(y);

		/* assert x = y */
		BoolExpr eq = ctx.mkEq(x, y);

		/* prove g(x) = g(y) */
		BoolExpr f = ctx.mkEq(gx, gy);
		System.out.println("prove: x = y implies g(x) = g(y)");
		prove(ctx, ctx.mkImplies(eq, f), false);

		BoolExpr temperatureSensor = ctx.mkBoolConst("temperatureSensor");
		BoolExpr resoure = ctx.mkBoolConst("resoure");
		BoolExpr concept = ctx.mkBoolConst("concept"); 

		BoolExpr temperatureSensorImplyResoure = ctx.mkImplies(temperatureSensor, resoure);
		BoolExpr resoureImplyConcept = ctx.mkImplies(resoure, concept); 

		BoolExpr postulate = ctx.mkAnd(temperatureSensorImplyResoure, resoureImplyConcept);

		BoolExpr temperatureSensorImplyConcept = ctx.mkImplies(temperatureSensor, concept);

		System.out.println();
		prove(ctx, ctx.mkImplies(postulate, temperatureSensorImplyConcept), false);

	}
	/**
	 * @param args
	 * @throws TestFailedException 
	 * @throws Z3Exception 
	 */
	public static void main(String[] args) throws Z3Exception, TestFailedException {
		// TODO Auto-generated method stub
		 
		SortDeduction sortDeduction = new SortDeduction();
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("proof", "true");
		Context ctx = new Context(cfg);
		// p.proveExample1(ctx);
		sortDeduction.sortReasoning(ctx);
	}

}
