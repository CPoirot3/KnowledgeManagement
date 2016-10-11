package com.bupt.poirot.z3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class Z3WB3 {
	Context ctx = new Context();

	public boolean prove(Context ctx, BoolExpr f, boolean useMBQI, BoolExpr... assumptions) throws Z3Exception {
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
		} else {
			return false;
		}
	}

	public BoolExpr parseString(String str) {
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
		} else {
			return null;
		}
	}

	public String getProveResultByOneExpresion(String condition) throws Z3Exception {
		String[] strings = condition.split(",");
		String str = strings[0];
		String toProve = strings[1];
		Solver solver = ctx.mkSolver();
		Params p = ctx.mkParams();
		p.add("mbqi", false);
		solver.setParameters(p);
		System.out.println();
		solver.add(parseString(str));
		for (int i = 0; i < solver.getAssertions().length; i++) {
			System.out.println(solver.getAssertions()[i]);
		}
		if (prove(ctx, parseString(toProve), false, solver.getAssertions())) {
			return "proved";
		} else {
			return "can't proved";
		}
	}

	public static void main(String[] args) throws Z3Exception {
		// TODO Auto-generated method stub
		
		System.out.println(System.getProperty("java.library.path"));
		
		Z3WB3 testZ3wb3 = new Z3WB3();
		System.out.println(testZ3wb3.getProveResultByOneExpresion("a>=11,a>100"));
		System.out.println(testZ3wb3.getProveResultByOneExpresion("a>=21,a>100"));
		System.out.println(testZ3wb3.getProveResultByOneExpresion("a>=33,a>100"));
		System.out.println(testZ3wb3.getProveResultByOneExpresion("a>=34,a>100"));
		System.out.println(testZ3wb3.getProveResultByOneExpresion("a>=193,a>100"));
	}

}
