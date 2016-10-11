package com.bupt.poirot.z3;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;

public class Parser {
	private HashMap<String, String> cfg;
	private Context ctx;
	private Solver solver;
	
	public Parser(HashMap<String, String> cfg, Context ctx, Solver solver) {
		this.cfg = cfg;
		this.ctx = ctx;
		this.solver = solver;
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
}
