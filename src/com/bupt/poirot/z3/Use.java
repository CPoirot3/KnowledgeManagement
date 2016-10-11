package com.bupt.poirot.z3;

import java.util.HashMap;

import org.apache.jena.base.Sys;

import com.bupt.poirot.z3.exceptions.TestFailedException;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Z3Exception;

public class Use {
	public ProveResult proveResult;
	public BoolExpr target;	
	private Parser parser;

	public Use(String targetString, HashMap<String, String> cfg, Context ctx, Solver solver) {
		this.parser = new Parser(cfg, ctx, solver);
		this.target = parser.parseString(targetString);
		System.out.println(target);
		this.proveResult = new ProveResult(target, cfg, ctx, solver);	 
	}
	
	public void dealData(String string) {
		BoolExpr newExpr = parser.parseString(string);
		proveResult.add(newExpr);
		boolean res;
		try {
			res = proveResult.proveTarget();
			System.out.println(res);
		} catch (Z3Exception | TestFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		HashMap<String, String> cfg = new HashMap<>();
		cfg.put("proof", "true");
		Context ctx = new Context(cfg);
		Solver solver = ctx.mkSolver();
		Params p = ctx.mkParams();
		p.add("mbqi", false);
		solver.setParameters(p);
		
		Use use = new Use("a>100", cfg, ctx, solver);
		use.dealData("a>=11");
		use.dealData("a>=21");
		use.dealData("a>=33");
		use.dealData("a>=34");
		use.dealData("a>=193");
	}
}
