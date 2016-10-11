package com.bupt.poirot.z3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bupt.poirot.z3.exceptions.TestFailedException;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class ProveResult {
	public BoolExpr targetExpr;
	public List<BoolExpr> list;
	
	private HashMap<String, String> cfg;
	private Context ctx;
	private Solver solver;
	
//	public ProveResult() {
//		this.list = new ArrayList<>();
//		cfg = new HashMap<String, String>();
//		cfg.put("proof", "true");
//		ctx = new Context(cfg);
//		solver = ctx.mkSolver();
//		Params p = ctx.mkParams();
//		p.add("mbqi", false);
//		solver.setParameters(p);
//	}
	
	public ProveResult(BoolExpr targetExpr, HashMap<String, String> cfg, Context ctx, Solver solver) {
		this.targetExpr = targetExpr;
		this.list = new ArrayList<>();
		this.cfg = cfg;
		this.ctx = ctx;
		this.solver = solver;
	}
	
	public ProveResult(BoolExpr targetExpr, List<BoolExpr> list, HashMap<String, String> cfg, Context ctx, Solver solver) {
		this.targetExpr = targetExpr;
		this.list = list;
		
		this.cfg = cfg;
		this.ctx = ctx;
		this.solver = solver;
	}
	
	public void add(BoolExpr boolExpr) {
		list.add(boolExpr);
	}
	
	public boolean proveTarget() throws Z3Exception, TestFailedException {
		System.out.println("Proving target  " + targetExpr);
		solver.reset();
		Params p = ctx.mkParams();
		solver.setParameters(p);
		
		for (BoolExpr a : list) {
			solver.add(a);
		}
		
		solver.add(ctx.mkNot(targetExpr));
		Status q = solver.check();
		if (q == Status.UNSATISFIABLE) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean proveTarget(boolean useMBQI) throws Z3Exception, TestFailedException {
		System.out.println("Proving target");
		solver.reset();
		Params p = ctx.mkParams();
		p.add("mbqi", useMBQI);
		solver.setParameters(p);
		for (BoolExpr a : list)
			solver.add(a);
		solver.add(ctx.mkNot(targetExpr));
		Status q = solver.check();
		if (q == Status.UNSATISFIABLE) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean prove(BoolExpr f, boolean useMBQI,
			BoolExpr... assumptions) throws Z3Exception, TestFailedException {
		System.out.println("Proving: " + f);
		Solver s = ctx.mkSolver();
		Params p = ctx.mkParams();
		p.add("mbqi", useMBQI);
		s.setParameters(p);
		for (BoolExpr a : assumptions)
			s.add(a);
		s.add(ctx.mkNot(f));
		Status q = s.check();
		if (q == Status.UNSATISFIABLE) {
			return true;
		}else {
			return false;
		}
	}
}