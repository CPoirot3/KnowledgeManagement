/**
 * Poirot
 * 2016年10月11日上午11:00:30
 * KnowledgeManagement
 */
package com.bupt.poirot.knowledgeBase.fusekiLibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Params;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import com.bupt.poirot.z3.parseAndDeduceOWL.OWLToZ3;

/**
 * @author Poirot
 *
 */
public class FetchModelClient {
	HttpClient httpClient;
	HttpGet httpGet;
	HttpPost httpPost;
	
	public FetchModelClient() {
		httpClient = HttpClients.createDefault();
		httpGet = new HttpGet("");
		httpPost = new HttpPost();
	}

	private InputStream fetchModel(String domain, String host, String query) {
		FetchModelClient fetchModelClient = new FetchModelClient();
		if (host == null) {
			host = "http://localhost:3030/";
		}
		if (query == null) {
			query = ""; // TODO
		}
		InputStream inputStream = fetchModelClient.fetch(host, domain, query);
		return inputStream;
	}

	private InputStream fetchModel(String domain) {
		return fetchModel(domain, null, null);
	}

	public boolean modelExist(String urlString, String queryString, String mark) throws UnsupportedEncodingException {
		 
		StringBuilder stringBuilder = new StringBuilder();
		try {
			URI uri = new URI(urlString + URLEncoder.encode(queryString, "utf-8"));
			httpGet.setURI(uri);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (URISyntaxException | UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}
		if (parse(stringBuilder.toString(), mark)) {
			return true;
		} else {
			return false;
		}
	}

	// mark stands for domain
	private boolean parse(String response, String mark) {
		return response.contains(mark);
	}
	
	public InputStream fetch(String host, String domain, String sparqlQuery) {
		InputStream inputStream = null;
		try {
			URI uri = new URI(host + "/" + domain + "?query=" + URLEncoder.encode(sparqlQuery, "utf-8"));
			httpGet.setURI(uri);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			
		} catch (URISyntaxException | UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
	
	public static void main(String[] args) {
		FetchModelClient fetchModel = new FetchModelClient();
		String host = "http://localhost:3030";
		String domain = "trafficWithInstance";
		//		String query = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object } LIMIT 25";
		String query = "";
		InputStream inputStream = fetchModel.fetch(host, domain, query);

		Context context = new Context();
		Solver solver = context.mkSolver();
		Params params = context.mkParams();
		params.add("mbqi", true);
		solver.setParameters(params);

		OWLToZ3 owlToZ3 = new OWLToZ3();
		BoolExpr preAsumptions = owlToZ3.parseFromStream(context, inputStream);
		solver.push();
		solver.add(preAsumptions);

        System.out.println();

        // mkQuantifier for test

		Sort x = context.mkUninterpretedSort("X");
		Sort y = context.mkUninterpretedSort("Y");
		FuncDecl funcDecl = context.mkFuncDecl("<http://www.semanticweb.org/traffic-ontology#Conjestion>", x,
				context.getBoolSort());
		BoolExpr p = (BoolExpr)funcDecl.apply(new Expr[]{context.mkConst("X0", x)});

		Sort[] domains = new Sort[2];
		domains[0] = x;
		domains[1] = y;
		FuncDecl funcDecl1 = context.mkFuncDecl("<http://www.semanticweb.org/traffic-ontology#hasRoad>", domains
				, context.mkBoolSort());
//		funcDecl1 = OWLToZ3.stringToFuncMap.get("<http://www.semanticweb.org/traffic-ontology#hasRoad>");
		BoolExpr m = (BoolExpr)context.mkApp(funcDecl1, new Expr[]{context.mkConst("X0", x), context.mkConst("Y1", y)});

		FuncDecl funcDecl2 = context.mkFuncDecl("<http://www.semanticweb.org/traffic-ontology#Road>", y,
				context.getBoolSort());
		BoolExpr n = (BoolExpr)funcDecl2.apply(new Expr[]{context.mkConst("Y1", y)});

		BoolExpr q = context.mkAnd(m, n);

		BoolExpr targetExpr = context.mkAnd(p, context.mkNot(q));
		System.out.println(targetExpr);

		solver.add(targetExpr);

		if (solver.check() == Status.SATISFIABLE) {
			System.out.println("Yes");
		} else {
			System.out.println("No");
		}

		solver.pop();
		solver.push();

		System.out.println(preAsumptions.equals(context.mkImplies(p, q)));

//		Expr e1 = context.mkApp(funcDecl1, context.mkConst("m", x), context.mkConst("n", y));
//		Expr e2 = context.mkApp(funcDecl1, context.mkConst("m", x), context.mkConst("n", y));
//		System.out.println(e1.equals(e2));
//
//		BoolExpr p1 = (BoolExpr)context.mkApp(funcDecl, context.mkConst("X0", x));
//		BoolExpr m1 = (BoolExpr)context.mkApp(funcDecl1, new Expr[]{context.mkConst("X0", x), context.mkConst("Y1", y)});
//		BoolExpr n1 = (BoolExpr)funcDecl2.apply(new Expr[]{context.mkConst("Y1", y)});
//		BoolExpr target2 = context.mkImplies(p1, context.mkAnd(m1, n1));
//		System.out.println(targetExpr.equals(target2));

//		Expr exprs = new Expr[] {context.mkApp(funcDecl, context.mkConst(""))}

		Expr a = context.mkConst("X0", x);
		Expr b = context.mkConst("Y1", y);

		Expr body = context.mkImplies((BoolExpr)context.mkApp(funcDecl, a),
				context.mkAnd((BoolExpr)context.mkApp(funcDecl1, new Expr[]{a, b}), (BoolExpr)context.mkApp(funcDecl2, b)));
		Expr[] bound = new Expr[]{a, b};
		Expr quantifier = context.mkForall(bound , body, 1, null, null, context.mkSymbol("q"), context.mkSymbol("sk"));
		System.out.println(quantifier);
		solver.reset();
		solver.add((BoolExpr)quantifier);
		if (solver.check() == Status.UNSATISFIABLE) {
			System.out.println("unsat");
		} else {
			System.out.println("sat");
		}

		Quantifier quantifier1 = context.mkForall(new Expr[]{a}, context.mkApp(funcDecl, a), 1, null, null,
				context.mkSymbol("q"), context.mkSymbol("sk"));

		Quantifier quantifier2 = context.mkExists(new Expr[]{b}, context.mkAnd((BoolExpr)context.mkApp(funcDecl1, new Expr[]{a, b}), (BoolExpr)context.mkApp(funcDecl2, b)),
				1, null, null, context.mkSymbol("q"), context.mkSymbol("sk"));

		BoolExpr quan = context.mkImplies((BoolExpr)quantifier1, (BoolExpr)quantifier2);

		System.out.println(quan);
		solver.reset();
		solver.add(quan);
		if (solver.check() == Status.UNSATISFIABLE) {
			System.out.println("unsat");
		} else {
			System.out.println("sat");
		}

		String str = "atLeast(1 <http://www.semanticweb.org/traffic-ontology#hasRoad> <http://www.semanticweb.org/traffic-ontology#Road>)";
		str = str.trim();
		Pattern pattern = Pattern.compile("(.+)\\((.+?)\\)");
		if (str.indexOf("atLeast") > -1 || str.indexOf("atMost") > -1) {
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				String conditionName = matcher.group(1);
				String[] strs = matcher.group(2).split(" ");
				String funcName = conditionName + "@" + strs[1] + "@" + strs[2];
				FuncDecl funcdecl = context.mkFuncDecl(funcName, x, context.getIntSort());
				Expr expr = context.mkGe((ArithExpr)funcdecl.apply(context.mkConst("X0", x)), context.mkInt(strs[0]));
				System.out.println(expr);
			} else {
				throw new RuntimeException();
			}
		}
	}

}
