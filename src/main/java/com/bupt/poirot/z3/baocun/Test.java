package com.bupt.poirot.z3.baocun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class Test {
	private static HashMap<String, String> cfg ;	
	private static Context ctx ;
	private static Solver solver;
	public Test() {
		// TODO Auto-generated constructor stub
		 cfg = new HashMap<String, String>();	
		 ctx = new Context(cfg);
		 solver = ctx.mkSolver();
	}
	public static String getSatifiable(String string) throws Z3Exception {

		String[] exprStrings = string.split("\\^");
		
		for (int i = 0; i < exprStrings.length; i++) {
			System.out.println(exprStrings[i]);
		}
		
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum0,isNum1;
		String[] subStrings;
		List<BoolExpr> checkExprs = new ArrayList<BoolExpr>();
		for (int i = 0; i < exprStrings.length; i++) {
			//checkExprs[i] =  
			String temp = exprStrings[i];//为每一个子表达式建立BoolExpr,如  x<y
			if (temp.indexOf(">=")!=-1) {
				subStrings = temp.split(">=");
				if (subStrings.length>2) { //如果不是x>=y的模式，
					System.out.println("incorrect formula");
					return "wrong expression";
				}
				isNum0 = pattern.matcher(subStrings[0]);
				isNum1 = pattern.matcher(subStrings[1]);
				if(!isNum0.matches() && !isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkIntConst(subStrings[1]);
					
					BoolExpr tempBoolExpr = ctx.mkGe(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}else if(!isNum0.matches() && isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkInt(subStrings[1]);
					//intExprSet.add(temp1);
					BoolExpr tempBoolExpr = ctx.mkGe(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}else {
					return "wrong expression";
				}
			}else if (temp.indexOf("<=")!=-1) {
				subStrings = temp.split("<=");
				if (subStrings.length>2) { //如果不是 x<=y 的模式，
					System.out.println("incorrect formula");
					return "wrong expression";
				}
				isNum0 = pattern.matcher(subStrings[0]);
				isNum1 = pattern.matcher(subStrings[1]);
				if(!isNum0.matches() && !isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkIntConst(subStrings[1]);
					
					BoolExpr tempBoolExpr = ctx.mkLe(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}
				else if(!isNum0.matches() && isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkInt(subStrings[1]);
					//intExprSet.add(temp1);
					BoolExpr tempBoolExpr = ctx.mkLe(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}else {
					return "wrong expression";
				}
					
			}else if (temp.indexOf("<")!=-1) {
				subStrings = temp.split("<");
				if (subStrings.length>2) { //如果不是 x<=y 的模式，
					System.out.println("incorrect formula");
					return "wrong expression";
				}
				isNum0 = pattern.matcher(subStrings[0]);
				isNum1 = pattern.matcher(subStrings[1]);
				if(!isNum0.matches() && !isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkIntConst(subStrings[1]);
				
					BoolExpr tempBoolExpr = ctx.mkLt(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}else if(!isNum0.matches() && isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkInt(subStrings[1]);
					
					BoolExpr tempBoolExpr = ctx.mkLt(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}else {
					return "wrong expression";
				}
			}else if (temp.indexOf(">")!=-1) {
				subStrings = temp.split(">");
				if (subStrings.length>2) { //如果不是 x<=y 的模式，
					System.out.println("incorrect formula");
					return "wrong expression";
				}
				isNum0 = pattern.matcher(subStrings[0]);
				isNum1 = pattern.matcher(subStrings[1]);
				if(!isNum0.matches() && !isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkIntConst(subStrings[1]);
					
					BoolExpr tempBoolExpr = ctx.mkGt(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}else if(!isNum0.matches() && isNum1.matches()){
					IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
					
					IntExpr temp1 = ctx.mkInt(subStrings[1]);
					//intExprSet.add(temp1);
					BoolExpr tempBoolExpr = ctx.mkGt(temp0, temp1);
					checkExprs.add(tempBoolExpr);
				}else {
					return "wrong expression";
				}
			}else {
				return "wrong expression";
			}	
		}
		solver.reset();
		solver.push();
		for (int i = 0; i < checkExprs.size(); i++) {
			solver.add(checkExprs.get(i));
		}
		if (solver.check() == Status.SATISFIABLE) {
			Model m = solver.getModel();
			System.out.println(m.toString());
			return "SATISFIABLE";
		}else {
			return "UNSATISFIABLE";
		}
	
	}
	
	public static void main(String[] args){
		
	}

}
