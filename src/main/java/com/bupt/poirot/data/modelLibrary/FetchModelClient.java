/**
 * Poirot
 * 2016年10月11日上午11:00:30
 * KnowledgeManagement
 */
package com.bupt.poirot.data.modelLibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.UninterpretedSort;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
	
	public boolean modelExist(String urlString, String queryString, String mark) throws UnsupportedEncodingException {
		 
		StringBuilder stringBuilder = new StringBuilder();
		try {
			URI uri = new URI(urlString + URLEncoder.encode(queryString, "utf-8"));
			httpGet.setURI(uri);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line = null;
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
			URI uri = new URI(host + "/" + domain + "?" + URLEncoder.encode(sparqlQuery, "utf-8"));
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
		String domain = "traffic-data";
		//		String query = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object } LIMIT 25";
		String query = "";
		InputStream inputStream = fetchModel.fetch(host, domain, query);

		Context context = new Context();
		Solver solver = context.mkSolver();
		Params params = context.mkParams();
		params.add("mbqi", false);
		solver.setParameters(params);

		BoolExpr boolExpr = OWLToZ3.parseFromStream(context, inputStream);
		boolExpr = context.mkEq(boolExpr, context.mkTrue());
		System.out.println(boolExpr);


		solver.add(boolExpr);
		if (solver.check() == Status.SATISFIABLE) {
			System.out.println("Yes");
		} else {
			System.out.println("No");
		}


		Sort x = context.mkUninterpretedSort("X");
		Sort y = context.mkUninterpretedSort("Y");
		String targetString = "<http://www.semanticweb.org/traffic-ontology#Conjestion>(X)";
		FuncDecl funcDecl = context.mkFuncDecl("<http://www.semanticweb.org/traffic-ontology#Conjestion>", x,
				context.getBoolSort());
		BoolExpr p = (BoolExpr)funcDecl.apply(new Expr[]{context.mkConst("X", context.mkUninterpretedSort("X"))});


		targetString = "<http://www.semanticweb.org/traffic-ontology#hasRoad>(X,Y)";
		FuncDecl funcDecl1 = context.mkFuncDecl("<http://www.semanticweb.org/traffic-ontology#hasRoad>", new Sort[] {x, y}
				, context.mkBoolSort());
		BoolExpr m = (BoolExpr)context.mkApp(funcDecl1, new Expr[]{context.mkConst("X", x), context.mkConst("Y", y)});

		targetString = "<http://www.semanticweb.org/traffic-ontology#Road>(Y)";
		FuncDecl funcDecl2 = context.mkFuncDecl("<http://www.semanticweb.org/traffic-ontology#Road>", y,
				context.getBoolSort());
		BoolExpr n = (BoolExpr)funcDecl2.apply(new Expr[]{context.mkConst("Y", y)});
		BoolExpr q = context.mkAnd(m, n);


		BoolExpr targetExpr = context.mkImplies(p, q);
		targetExpr = context.mkEq(targetExpr, context.mkFalse());
		System.out.println(targetExpr);

//		targetExpr = context.mkNot(targetExpr);
//		BoolExpr targetExpr = context.mkApp(context.mkConst("X", context.mkUninterpretedSort("X")));
//		solver.add(context.mkNot(targetExpr));
		solver.add(targetExpr);
		if (solver.check() == Status.SATISFIABLE) {
			System.out.println("Yes");
			System.out.println(solver.getModel());
		} else {
			System.out.println("No");
		}

	}

}
