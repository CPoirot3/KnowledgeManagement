/**
 * 2016年5月5日
 * Poirot
 * 下午4:00:27
 * KnowledgeManagement
 */
package jenaModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Symbol;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;

/**
 * @author Poirot
 *
 */
public class ParseOWLAndDeduce {
	Set<String> funcSet;
	Map<String, String> classMap;

	public ParseOWLAndDeduce() {
		this.classMap = new HashMap<>();
		this.funcSet = new HashSet<>();
	}

	public void parseSingleStatement(Context ctx, Solver s, Statement statement) {
		if (statement == null)
			return;
		String subject = statement.getSubject().toString();
		String object = statement.getObject().toString();
		String predicate = statement.getPredicate().toString();
		predicate = Character.toUpperCase(predicate.charAt(0)) + predicate.substring(1);
		if (statement.getObject().isLiteral()) {
			
		}
		Symbol funcSymbol = ctx.mkSymbol("has" + predicate);
		Sort sort = ctx.mkUninterpretedSort(subject);
		Sort sort2 = ctx.mkUninterpretedSort(object);
		 
//		ctx.mkDatatypeSort(arg0, arg1)
		FuncDecl funcDecl = ctx.mkFuncDecl("has" + predicate, sort2, sort2);
		
//		ctx.mkImplies(t1, t2);
//		FuncDecl isSubject = ctx.mkFuncDecl() 
	}

	public String parsePredicateStatement(Statement statement) {
		if (statement == null) {
			return "";
		}
		StringBuilder resOfParsestatement = new StringBuilder();
		String subject = statement.getSubject().toString();
		String predicate = statement.getPredicate().toString();
		String object = statement.getObject().toString();
		
		return resOfParsestatement.toString();
	}

	public void generate(String fileName) {
		Model model = ModelFactory.createDefaultModel();
		// 使用 FileManager 查找文件
		InputStream in = FileManager.get().open(fileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + fileName + " not found");
		}
		// 读取RDF/XML 文件
		model.read(in, null);

		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			System.out.println(statement.getSubject() + "  " + statement.getPredicate() + "  " + statement.getObject());
			// bufferedWriter.write(parseSingleStatement(statement));
		}

		// System.out.println();
		// System.out.println(model.getNsURIPrefix("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
		// System.out.println(model.getNsPrefixURI("rdf"));
		// System.out.println();
		// Map<String, String> nsMap = model.getNsPrefixMap();
		// for (String key : nsMap.keySet()) {
		//
		// System.out.println("prefix is : " + key + " uri is :" +
		// nsMap.get(key));
		// }
		//
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub 
		ParseOWLAndDeduce t = new ParseOWLAndDeduce();

		t.generate("data\\traffic1.owl");

	}

}
