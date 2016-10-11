package com.bupt.poirot.z3;
 
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
   
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr; 
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
 
public class Z3WB2 {
	
	@SuppressWarnings("serial")
	class TestFailedException extends Exception {
		public TestFailedException() {
			super("Check FAILED");
		}
	};

	private static HashMap<String, String> cfg;
	private static Context ctx;
	private static Solver solver;

	public Z3WB2() {
		// TODO Auto-generated constructor stub
	    cfg = new HashMap<String, String>();
		cfg.put("proof", "true");
		ctx = new Context(cfg);
		solver = ctx.mkSolver();
		Params p = ctx.mkParams();
		p.add("mbqi", false);
		solver.setParameters(p);
	}

	public boolean prove(Context ctx, BoolExpr f, boolean useMBQI,
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
//		switch (q) {
//		case UNKNOWN:
//			System.out.println("Unknown because: " + s.getReasonUnknown());
//			break;
//		case SATISFIABLE:
//			throw new TestFailedException();
//		case UNSATISFIABLE:
//			System.out.println("OK, proof: " + s.getProof());
//			break;
//		}
	}
	
	public BoolExpr parseString(String str){
		String temp = str;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum0, isNum1;
		
		if (temp.indexOf(">=") != -1) {
			String[] subStrings = temp.split(">=");
			if (subStrings.length > 2) { // 如果不是x>=y的模式，
				System.out.println("incorrect formula");
				return null;
			}
			isNum0 = pattern.matcher(subStrings[0]);
			isNum1 = pattern.matcher(subStrings[1]);
			if (!isNum0.matches() && !isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
				IntExpr temp1 = ctx.mkIntConst(subStrings[1]);
				BoolExpr tempBoolExpr = ctx.mkGe(temp0, temp1);
				return tempBoolExpr;
			} else if (!isNum0.matches() && isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				// intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkGe(temp0, temp1);
				return tempBoolExpr;
			} else {
				return null;
			}
		} else if (temp.indexOf("<=") != -1) {
			String[] subStrings = temp.split("<=");
			if (subStrings.length > 2) { // 如果不是 x<=y 的模式，
				System.out.println("incorrect formula");
				return null;
			}
			isNum0 = pattern.matcher(subStrings[0]);
			isNum1 = pattern.matcher(subStrings[1]);
			if (!isNum0.matches() && !isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkIntConst(subStrings[1]);

				BoolExpr tempBoolExpr = ctx.mkLe(temp0, temp1);
				return tempBoolExpr;
			} else if (!isNum0.matches() && isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				// intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkLe(temp0, temp1);
				return tempBoolExpr;
			} else {
				return null;
			}
		} else if (temp.indexOf("<") != -1) {
			String[] subStrings = temp.split("<");
			if (subStrings.length > 2) { // 如果不是 x<=y 的模式，
				System.out.println("incorrect formula");
				return null;
			}
			isNum0 = pattern.matcher(subStrings[0]);
			isNum1 = pattern.matcher(subStrings[1]);
			if (!isNum0.matches() && !isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkIntConst(subStrings[1]);

				BoolExpr tempBoolExpr = ctx.mkLt(temp0, temp1);
				return tempBoolExpr;
			} else if (!isNum0.matches() && isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				// intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkLt(temp0, temp1);
				return tempBoolExpr;
			} else {
				return null;
			}
		} else if (temp.indexOf(">") != -1) {
			String[] subStrings = temp.split(">");
			if (subStrings.length > 2) { // 如果不是 x<=y 的模式，
				System.out.println("incorrect formula");
				return null;
			}
			isNum0 = pattern.matcher(subStrings[0]);
			isNum1 = pattern.matcher(subStrings[1]);
			if (!isNum0.matches() && !isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkIntConst(subStrings[1]);

				BoolExpr tempBoolExpr = ctx.mkGt(temp0, temp1);
				return tempBoolExpr;
			} else if (!isNum0.matches() && isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				// intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkGt(temp0, temp1);
				return tempBoolExpr;
			} else {
				return null;
			}
		} else if (temp.indexOf("=") != -1) {
			String[] subStrings = temp.split("=");
			if (subStrings.length > 2) { // 如果不是 x=y 的模式，
				System.out.println("incorrect formula");
				return null;
			}
			isNum0 = pattern.matcher(subStrings[0]);
			isNum1 = pattern.matcher(subStrings[1]);
			if (!isNum0.matches() && !isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkIntConst(subStrings[1]);

				BoolExpr tempBoolExpr = ctx.mkEq(temp0, temp1);
				return tempBoolExpr;
			} else if (!isNum0.matches() && isNum1.matches()) {
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);

				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				// intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkEq(temp0, temp1);
				return tempBoolExpr;
			} else {
				return null;
			}
		}else {
			return null;
		}
	}
	
	public String getProveResultByAddOneExpresion(String str, String toProve,
			boolean reset) throws Z3Exception, TestFailedException {
		if (reset) {
			solver.reset();
		}
		System.out.println();
		solver.add(parseString(str));
		for (int i = 0; i < solver.getAssertions().length; i++) {
			System.out.println(solver.getAssertions()[i]);
		}
		if (prove(ctx, parseString(toProve), false, solver.getAssertions())){
			return "proved";
		}else {
			return "can't proved";
		}
	}
	
	public static void  main(String[] args) throws Z3Exception, TestFailedException {
		Z3WB2 testZ3wb2 = new Z3WB2();
		System.out.println(testZ3wb2.getProveResultByAddOneExpresion("a>=11", "a>100", true));
		System.out.println(testZ3wb2.getProveResultByAddOneExpresion("a>=21", "a>100", false));
		System.out.println(testZ3wb2.getProveResultByAddOneExpresion("a>=33", "a>100", false));
		System.out.println(testZ3wb2.getProveResultByAddOneExpresion("a>=34", "a>100", false));
		System.out.println(testZ3wb2.getProveResultByAddOneExpresion("a>=193", "a>100", false));
	}

}
