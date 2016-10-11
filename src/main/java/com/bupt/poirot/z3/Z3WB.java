package com.bupt.poirot.z3;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService; 

import com.microsoft.z3.*;

@WebService 
public class Z3WB {
	private static HashMap<String, String> cfg ;	
	private static Context ctx ;
	private static Solver solver ;
	public Z3WB() {
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
			System.out.println(solver.getProof());
			return "UNSATISFIABLE";
		}
	
	}
	
	public String getSatisfiableByAddOneExpresion(String str,boolean reset){
		
		cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		ctx = new Context(cfg);
		solver = ctx.mkSolver();
		if (reset) {
			solver.reset();
		}
		System.out.println();
		System.out.println("begin add:");
		
		String temp = str;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum0,isNum1;
		
		if (temp.indexOf(">=")!=-1) {
			String[] subStrings = temp.split(">=");
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
				solver.add(tempBoolExpr);
			}else if(!isNum0.matches() && isNum1.matches()){
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
				 
				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				//intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkGe(temp0, temp1);
				solver.add(tempBoolExpr);
			}else {
				return "wrong expression";
			}
		}else if (temp.indexOf("<=")!=-1) {
			String[] subStrings = temp.split("<=");
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
				solver.add(tempBoolExpr);
			} else if(!isNum0.matches() && isNum1.matches()){
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
				 
				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				//intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkLe(temp0, temp1);
				solver.add(tempBoolExpr);
			}else {
				return "wrong expression";
			}		
		}else if (temp.indexOf("<")!=-1) {
			String[] subStrings = temp.split("<");
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
				solver.add(tempBoolExpr);
			}else if(!isNum0.matches() && isNum1.matches()){
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
				 
				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				//intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkLt(temp0, temp1);
				solver.add(tempBoolExpr);
			}else {
				return "wrong expression";
			}
		}else if (temp.indexOf(">")!=-1) {
			String[] subStrings = temp.split(">");
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
				solver.add(tempBoolExpr);
			}else if(!isNum0.matches() && isNum1.matches()){
				IntExpr temp0 = ctx.mkIntConst(subStrings[0]);
				 
				IntExpr temp1 = ctx.mkInt(subStrings[1]);
				//intExprSet.add(temp1);
				BoolExpr tempBoolExpr = ctx.mkGt(temp0, temp1);
				solver.add(tempBoolExpr);
			}else {
				return "wrong expression";
			}
		}else {
			return "wrong expression";
		}
		System.out.println(solver.getNumAssertions());
		System.out.println(solver.getNumScopes());
		for (int i = 0; i < solver.getAssertions().length; i++) {
			System.out.println(solver.getAssertions()[i]);
		}
		if (solver.check() == Status.SATISFIABLE) {
			Model m = solver.getModel();
			System.out.println(m.toString());
			return "SATISFIABLE";
		}else {
			return "UNSATISFIABLE";
		}
	}
	
	
	public String test(String ix, String iy, int constraint,
			int xLowBound, int xHighBound) throws Z3Exception {
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");

		Context ctx = new Context(cfg);
		IntExpr x = ctx.mkIntConst(ix);
		IntExpr y = ctx.mkIntConst(iy);
		IntExpr diff = ctx.mkInt(constraint);
		IntExpr xlow = ctx.mkInt(xLowBound);
		IntExpr xhigh = ctx.mkInt(xHighBound);
		ArithExpr y_plus_diff = ctx.mkAdd(y, diff);

		BoolExpr c1 = ctx.mkLt(x, y_plus_diff);
		BoolExpr c2 = ctx.mkGt(x, xlow);
		BoolExpr c3 = ctx.mkLt(x, xhigh);
		BoolExpr q = ctx.mkAnd(c1, c2, c3);
		String resultString = "";
		
		resultString = resultString+"model for:\nx < y + " + constraint + "\nx > "+ xLowBound + "\nx < " + xHighBound+"\n";
		Solver s = ctx.mkSolver();
		s.add(q);
		
		resultString = resultString+s.check();
		if (s.check() == Status.SATISFIABLE) {

			Model model = s.getModel();
			
			resultString = resultString+"the Model is : x = " + model.evaluate(x, false)+ ", y =" + model.evaluate(y, false)+"\n";

			resultString = resultString+model.toString();
		}
		return resultString;	
	}

	
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));
//		Endpoint.publish("http://10.108.165.195:8088/ws/Z3WB", new Z3WB());  
		
		Z3WB testZ3WB = new Z3WB();
		System.out.println(testZ3WB.getSatisfiableByAddOneExpresion("x>y",true));
		
 		System.out.println( testZ3WB.getSatisfiableByAddOneExpresion("z>x",false));
		System.out.println( testZ3WB.getSatisfiableByAddOneExpresion("x<100",false));
		System.out.println( testZ3WB.getSatisfiableByAddOneExpresion("z>90",false));
		System.out.println( testZ3WB.getSatisfiableByAddOneExpresion("y<93",false));
		System.out.println( testZ3WB.getSatisfiableByAddOneExpresion("x>z",false));
	}
}
