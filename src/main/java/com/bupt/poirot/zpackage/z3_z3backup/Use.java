package com.bupt.poirot.zpackage.z3_z3backup;

import java.util.HashMap;

import com.bupt.poirot.z3.exceptions.TestFailedException;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Version;
import com.microsoft.z3.Z3Exception;

public class Use {
	public ProveResult proveResult;
	public BoolExpr target;	
	private Parser parser;

	public Use(String targetString) {
		HashMap<String, String> cfg = new HashMap<>();
		cfg.put("proof", "true");
		Context ctx = new Context(cfg);
		Solver solver = ctx.mkSolver();
		Params p = ctx.mkParams();
		p.add("mbqi", false);
		solver.setParameters(p);
		
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
//		System.out.println(System.getProperty("DYLD_LIBRARY_PATH"));
//		System.out.println(System.getProperty("LD_LIBRARY_PATH"));
		System.out.println(System.getProperty("java.library.path"));
		System.out.println(Version.getString());
		
		Use use = new Use("a>100");
		use.dealData("a>=11");
		use.dealData("a>=21");
		use.dealData("a>=33");
		use.dealData("a>=34");
		use.dealData("a>=193");
	}
}
