package com.bupt.poirot.z3.baocun;
import java.util.ArrayList;
import java.util.HashMap;
import com.microsoft.z3.*;

public class TestZ3ByCH {

	@SuppressWarnings("serial")
	class TestFailedException extends Exception
	{
		public TestFailedException()
		{
			super("Check FAILED");
		}
	};
	
	
	public void bitvectorExample1(Context ctx) throws Z3Exception,
	TestFailedException {
		System.out.println("BitvectorExample1");
		Log.append("BitvectorExample1");
		Sort bv_type = ctx.mkBitVecSort(32);
		BitVecExpr x = (BitVecExpr) ctx.mkConst("x", bv_type);
		BitVecNum zero = (BitVecNum) ctx.mkNumeral("0", bv_type);
		BitVecNum ten = ctx.mkBV(10, 32);
		BitVecExpr x_minus_ten = ctx.mkBVSub(x, ten);

		/* bvsle is signed less than or equal to */
		BoolExpr c1 = ctx.mkBVSLT(x, ten);// x <= 10
		BoolExpr c2 = ctx.mkBVSGE(x_minus_ten, zero);// x-10 >= 0
		Solver s = ctx.mkSolver();
		s.add(c1);
		s.add(c2);
		if (s.check() == Status.SATISFIABLE) {
			Model m = s.getModel();
			System.out.println(m);
		}
		
		
//		ctx.mkBound(arg0, arg1);
//		ctx.mkBV2Int(arg0, arg1);
//		ctx.mkZeroExt(arg0, arg1);
//		ctx.mkUninterpretedSort(arg0);
//		ctx.mk
//		BoolExpr thm = ctx.mkIff(c1, c2); //
//		System.out.println("disprove: x - 10 <= 0 IFF x <= 10 for (32-bit) machine integers");
//		disprove(ctx, thm, false);
	}

	// / Find x and y such that: x ^ y - 103 == x * y

	public void bitvectorExample2(Context ctx) throws Z3Exception,
	TestFailedException {
		System.out.println("BitvectorExample2");
		Log.append("BitvectorExample2");

		/* construct x ^ y - 103 == x * y */
		Sort bv_type = ctx.mkBitVecSort(32);
		BitVecExpr x = ctx.mkBVConst("x", 32);
		BitVecExpr y = ctx.mkBVConst("y", 32);
		BitVecExpr x_xor_y = ctx.mkBVXOR(x, y);
		BitVecExpr c103 = (BitVecNum) ctx.mkNumeral("103", bv_type);
		BitVecExpr lhs = ctx.mkBVSub(x_xor_y, c103);
		BitVecExpr rhs = ctx.mkBVMul(x, y);
		BoolExpr ctr = ctx.mkEq(lhs, rhs);

		System.out.println("find values of x and y, such that x ^ y - 103 == x * y");

		/* find a model (i.e., values for x an y that satisfy the constraint */
		Solver s = ctx.mkSolver();
		s.push();
		s.add(ctr);
		if (s.check() == Status.SATISFIABLE) {
			Model m = s.getModel();
			System.out.println(m);
		}
		
		IntExpr a = ctx.mkIntConst("a");
		IntExpr b = ctx.mkIntConst("b");
		ArithExpr a_or_b = ctx.mkMul(a,b);
		BoolExpr e= ctx.mkEq(a_or_b, ctx.mkInt(100));
		
		s.pop();
		s.add(e);
		if (s.check() == Status.SATISFIABLE) {
			System.out.println();
			System.out.println(s.getModel());
		}
//		int a = 0x2c330437;
//		int b = 0x656fe436;
//		if ((a * b) == ((a ^ b) - 103))
//			System.out.println("right");
	}
	void arrayExample1(Context ctx) throws Z3Exception, TestFailedException {
		System.out.println("ArrayExample1");
		Log.append("ArrayExample1");

		Goal g = ctx.mkGoal(true, false, false);
		ArraySort asort = ctx.mkArraySort(ctx.getIntSort(),
				ctx.mkBitVecSort(32));
		ArrayExpr aex = (ArrayExpr) ctx.mkConst(ctx.mkSymbol("MyArray"), asort);

		Expr sel = ctx.mkSelect(aex, ctx.mkInt(0));// aex[0]
		g.add(ctx.mkEq(sel, ctx.mkBV(42, 32)));

		Symbol xs = ctx.mkSymbol("x");
		IntExpr xc = (IntExpr) ctx.mkConst(xs, ctx.getIntSort());

		Symbol fname = ctx.mkSymbol("f");
		Sort[] domain = { ctx.getIntSort() };
		FuncDecl fd = ctx.mkFuncDecl(fname, domain, ctx.getIntSort());

		Expr[] fargs = { ctx.mkConst(xs, ctx.getIntSort()) };// x
		IntExpr fapp = (IntExpr) ctx.mkApp(fd, fargs);//以x为参数

		g.add(ctx.mkEq(ctx.mkAdd(xc, fapp), ctx.mkInt(123)));// (= (+ x (f x))
		// 123)

		Solver s = ctx.mkSolver();
		System.out.println(g.getFormulas().length);
		for (BoolExpr a : g.getFormulas())
			s.add(a);
		System.out.println("Solver: " + s);

		Status q = s.check();
		System.out.println("Status: " + q);
		System.out.println();

		if (q != Status.SATISFIABLE)
			throw new TestFailedException();

		System.out.println("Model = " + s.getModel());
		System.out.println();

		System.out.println("Interpretation of MyArray:\n"
				+ s.getModel().getFuncInterp(aex.getFuncDecl()));
		System.out.println("Interpretation of x:\n"
				+ s.getModel().getConstInterp(xc));
		System.out.println("Interpretation of f:\n"
				+ s.getModel().getFuncInterp(fd));
	}

	public void arrayExample2(Context ctx) throws Z3Exception,
	TestFailedException {
		System.out.println("ArrayExample2");
		Log.append("ArrayExample2");

		Sort int_type = ctx.getIntSort();
		Sort array_type = ctx.mkArraySort(int_type, int_type);

		ArrayExpr a1 = (ArrayExpr) ctx.mkConst("a1", array_type);
		ArrayExpr a2 = ctx.mkArrayConst("a2", int_type, int_type);
		Expr i1 = ctx.mkConst("i1", int_type);
		Expr i2 = ctx.mkConst("i2", int_type);
		Expr i3 = ctx.mkConst("i3", int_type);
		Expr v1 = ctx.mkConst("v1", int_type);
		Expr v2 = ctx.mkConst("v2", int_type);

		Expr st1 = ctx.mkStore(a1, i1, v1);
		Expr st2 = ctx.mkStore(a2, i2, v2);

		Expr sel1 = ctx.mkSelect(a1, i3);
		Expr sel2 = ctx.mkSelect(a2, i3);

		/* create antecedent */
		BoolExpr antecedent = ctx.mkEq(st1, st2);

		/*
		 * create consequent: i1 = i3 or i2 = i3 or select(a1, i3) = select(a2,
		 * i3)
		 */
		BoolExpr consequent = ctx.mkOr(ctx.mkEq(i1, i3), ctx.mkEq(i2, i3),
				ctx.mkEq(sel1, sel2));

		/*
		 * prove store(a1, i1, v1) = store(a2, i2, v2) implies (i1 = i3 or i2 =
		 * i3 or select(a1, i3) = select(a2, i3))
		 */
		BoolExpr thm = ctx.mkImplies(antecedent, consequent);
		System.out.println("prove: store(a1, i1, v1) = store(a2, i2, v2) implies (i1 = i3 or i2 = i3 or select(a1, i3) = select(a2, i3))");
		System.out.println("thm:");
		System.out.println(thm);
		Solver s = ctx.mkSolver();
		s.add(thm);
		if(s.check() == Status.SATISFIABLE){
			System.out.println(s.check());
			System.out.println(s.getModel());
		}
	}

	public void arrayExample3(Context ctx) throws Z3Exception,
	TestFailedException {
		System.out.println("ArrayExample3");
		Log.append("ArrayExample3");

		for (int n = 2; n <= 5; n++) {
			System.out.println("n = " + n);

			Sort bool_type = ctx.mkBoolSort();
			Sort array_type = ctx.mkArraySort(bool_type, bool_type);
			Expr[] a = new Expr[n];

			/* create arrays */
			for (int i = 0; i < n; i++) {
				a[i] = ctx.mkConst("array_" + Integer.toString(i), array_type);
			}

			/* assert distinct(a[0], ..., a[n]) */
			BoolExpr d = ctx.mkDistinct(a);
			System.out.println(d);
			System.out.println("the model is:  ");
			/* context is satisfiable if n < 5 */
			Solver s = ctx.mkSolver();
			s.add(d);
			//Model model = check(ctx, d, n < 5 ? Status.SATISFIABLE:Status.UNSATISFIABLE);
			System.out.println(s.check());
			if (s.check() == Status.SATISFIABLE) {
				Model model = s.getModel();
				System.out.println(model);
			}

			System.out.println();
		}
	}
	
	public static void test(Integer... t){
		for(int i: t){
			System.out.println(i);
		}
	}
	public static void main(String[] args) {
		ArrayList<Integer> t = new ArrayList<Integer>();
		t.add(1);
		t.add(1);
		t.add(1);
		int size = t.size();
		Integer[] t1 = (Integer[])t.toArray(new Integer[size]);
		test(t1);
		
		TestZ3ByCH p = new TestZ3ByCH();
		try {

			Log.open("test.log");

			{ // These examples need model generation turned on.
				HashMap<String, String> cfg = new HashMap<String, String>();
				cfg.put("model", "true");
				Context ctx = new Context(cfg);
				
				//p.bitvectorExample1(ctx);
				p.bitvectorExample2(ctx);

				//p.arrayExample1(ctx);
				//p.arrayExample3(ctx);
				

			}

			{ // These examples need proof generation turned on.
				HashMap<String, String> cfg = new HashMap<String, String>();
				cfg.put("proof", "true");
				Context ctx = new Context(cfg);		
				p.arrayExample2(ctx);
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
