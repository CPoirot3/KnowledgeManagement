/**
 * Dec 7, 2015
 * Poirot
 * 8:38:03 AM
 * JenaTest
 */
package jenaModule;

import java.io.BufferedWriter;
import java.io.File; 
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

/**
 * @author Poirot
 *
 */
public class GenerateSMT2WithJena {
	Set<String> funcSet;
	Map<String, String> classMap;

	public GenerateSMT2WithJena() {
		this.classMap = new HashMap<>();
		this.funcSet = new HashSet<>();
	}

	public String parseSingleStatement(Statement statement) {

		if (statement == null)
			return null;
		StringBuilder res = new StringBuilder();
		String subject = statement.getSubject().toString();
		String object = statement.getObject().toString();

		boolean isValid = true;
		if (!classMap.containsKey(subject)) {
			int begin = subject.length() - 1;
			while (Character.isAlphabetic(subject.charAt(begin))) {
				begin--;
			}
			String subjectToSMT2 = subject.substring(begin + 1);
			if (subjectToSMT2 != null && subjectToSMT2.length() != 0) {
				res.append("(declare-const " + subjectToSMT2 + " CLASS)\n");
				classMap.put(subject, subjectToSMT2);
			} else {
				isValid = false;
				System.out.println("the expression can't parse :  " + subject);
			}
		}

		if (!classMap.containsKey(object)) {
			int begin = object.length() - 1;
			while (Character.isAlphabetic(object.charAt(begin))) {
				begin--;
			}
			String objectToSMT2 = object.substring(begin + 1);
			if (objectToSMT2 != null && objectToSMT2.length() != 0) {
				res.append("(declare-const " + objectToSMT2 + " CLASS)\n");
				classMap.put(object, objectToSMT2);
			} else {
				isValid = false;
				System.out.println("the expression can't parse :  " + object);
			}
		}
		if (isValid) {
			res.append(parsePredicateStatement(statement));
		}
		return res.toString();
	}

	public String parsePredicateStatement(Statement statement) {
		if (statement == null) {
			return "";
		}
		StringBuilder resOfParsestatement = new StringBuilder();
		String subject = statement.getSubject().toString();
		String predicate = statement.getPredicate().toString();
		String object = statement.getObject().toString();
		if (predicate.endsWith("type")) {
			// resOfParsestatement.append("(declare-fun " + resOfParsestatement
			// + " (A) A )");
			if (!funcSet.contains("type")) {
				resOfParsestatement.append("(declare-fun type (CLASS) CLASS)\n");
				funcSet.add("type");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {
				resOfParsestatement
						.append("(assert (= (type " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("subClassOf")) {
			if (!funcSet.contains("subClassOf")) {
				resOfParsestatement.append("(declare-fun subClassOf (CLASS) CLASS)\n");

				funcSet.add("subClassOf");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (subClassOf " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}

		} else if (predicate.endsWith("subPropertyOf")) {
			if (!funcSet.contains("subPropertyOf")) {
				resOfParsestatement.append("(declare-fun subPropertyOf (CLASS) CLASS )\n");

				funcSet.add("subPropertyOf");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (subPropertyOf " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("domain")) {
			if (!funcSet.contains("domain")) {
				resOfParsestatement.append("(declare-fun domain (CLASS) CLASS )\n");

				funcSet.add("domain");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {
				resOfParsestatement
						.append("(assert (= (domain " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("range")) {
			if (!funcSet.contains("range")) {
				resOfParsestatement.append("(declare-fun range (CLASS) CLASS )\n");

				funcSet.add("range");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement
						.append("(assert (= (range " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("intersectionOf")) {
			if (!funcSet.contains("intersectionOf")) {
				resOfParsestatement.append("(declare-fun intersectionOf (CLASS) CLASS )\n");

				funcSet.add("intersectionOf");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (intersectionOf " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("unionOf")) {
			if (!funcSet.contains("unionOf")) {
				resOfParsestatement.append("(declare-fun unionOf (CLASS) CLASS )\n");

				funcSet.add("unionOf");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement
						.append("(assert (= (unionOf " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("equivalentClass")) {
			if (!funcSet.contains("equivalentClass")) {
				resOfParsestatement.append("(declare-fun equivalentClass (CLASS) CLASS )\n");

				funcSet.add("equivalentClass");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (equivalentClass " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("disjointWith")) {
			if (!funcSet.contains("disjointWith")) {
				resOfParsestatement.append("(declare-fun disjointWith (CLASS) CLASS )\n");

				funcSet.add("disjointWith");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (disjointWith " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("sameAs")) {
			if (!funcSet.contains("sameAs")) {
				resOfParsestatement.append("(declare-fun sameAs (CLASS) CLASS )\n");

				funcSet.add("sameAs");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement
						.append("(assert (= (sameAs " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("differentFrom")) {
			if (!funcSet.contains("differentFrom")) {
				resOfParsestatement.append("(declare-fun differentFrom (CLASS) CLASS )\n");

				funcSet.add("differentFrom");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (differentFrom " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("distinctMembers")) {
			if (!funcSet.contains("distinctMembers")) {
				resOfParsestatement.append("(declare-fun distinctMembers (CLASS) CLASS )\n");

				funcSet.add("distinctMembers");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (distinctMembers " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("Thing")) {
			if (!funcSet.contains("Thing")) {
				resOfParsestatement.append("(declare-fun Thing (CLASS) CLASS )\n");

				funcSet.add("Thing");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement
						.append("(assert (= (Thing " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("equivalentProperty")) {
			if (!funcSet.contains("equivalentProperty")) {
				resOfParsestatement.append("(declare-fun equivalentProperty (CLASS) CLASS )\n");

				funcSet.add("equivalentProperty");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append("(assert (= (equivalentProperty " + classMap.get(subject) + ") "
						+ classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("inverseOf")) {
			if (!funcSet.contains("inverseOf")) {
				resOfParsestatement.append("(declare-fun inverseOf (CLASS) CLASS )\n");

				funcSet.add("inverseOf");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (inverseOf " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("FunctionalProperty")) {
			if (!funcSet.contains("FunctionalProperty")) {
				resOfParsestatement.append("(declare-fun FunctionalProperty (CLASS) CLASS )\n");

				funcSet.add("FunctionalProperty");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append("(assert (= (FunctionalProperty " + classMap.get(subject) + ") "
						+ classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("InverseFunctionalProperty")) {
			if (!funcSet.contains("InverseFunctionalProperty")) {
				resOfParsestatement.append("(declare-fun InverseFunctionalProperty (CLASS) CLASS )\n");

				funcSet.add("InverseFunctionalProperty");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append("(assert (= (InverseFunctionalProperty " + classMap.get(subject) + ") "
						+ classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("SymmeticProperty")) {
			if (!funcSet.contains("SymmeticProperty")) {
				resOfParsestatement.append("(declare-fun SymmeticProperty (CLASS) CLASS )\n");

				funcSet.add("SymmeticProperty");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (SymmeticProperty " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("TransitiveProperty")) {
			if (!funcSet.contains("TransitiveProperty")) {
				resOfParsestatement.append("(declare-fun TransitiveProperty (CLASS) CLASS )\n");

				funcSet.add("TransitiveProperty");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append("(assert (= (TransitiveProperty " + classMap.get(subject) + ") "
						+ classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("someValuesFrom")) {
			if (!funcSet.contains("someValuesFrom")) {
				resOfParsestatement.append("(declare-fun someValuesFrom (CLASS) CLASS )\n");

				funcSet.add("someValuesFrom");
			}

			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (someValuesFrom " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("allValuesFrom")) {
			if (!funcSet.contains("allValuesFrom")) {
				resOfParsestatement.append("(declare-fun allValuesFrom (CLASS) CLASS )\n");

				funcSet.add("allValuesFrom");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (allValuesFrom " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("minCardinality")) {
			if (!funcSet.contains("minCardinality")) {
				resOfParsestatement.append("(declare-fun minCardinality (CLASS) CLASS )\n");

				funcSet.add("minCardinality");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (minCardinality " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("maxCardinality")) {
			if (!funcSet.contains("maxCardinality")) {
				resOfParsestatement.append("(declare-fun maxCardinality (CLASS) CLASS )\n");

				funcSet.add("maxCardinality");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (maxCardinality " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		} else if (predicate.endsWith("cardinality")) {
			if (!funcSet.contains("cardinality")) {
				resOfParsestatement.append("(declare-fun cardinality (CLASS) CLASS )\n");

				funcSet.add("cardinality");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement.append(
						"(assert (= (cardinality " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}

		} else {
			if (!funcSet.contains("unknown")) {
				resOfParsestatement.append("(declare-fun unknown (CLASS) CLASS )\n");

				funcSet.add("unknown");
			}
			if (classMap.get(object) != null && classMap.get(subject) != null) {

				resOfParsestatement
						.append("(assert (= (unknown " + classMap.get(subject) + ") " + classMap.get(object) + "))\n");
			}
		}

		return resOfParsestatement.toString();
	}

	public void generate(String fileName, String outputFile) {
		Model model = ModelFactory.createDefaultModel();
		// 使用 FileManager 查找文件
		InputStream in = FileManager.get().open(fileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + fileName + " not found");
		}
		// 读取RDF/XML 文件
		model.read(in, null);

		File file = new File(outputFile);
		try (FileWriter fileWriter = new FileWriter(file);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

			// model.write(System.out);
			bufferedWriter.write("(declare-sort CLASS)\n");
			StmtIterator stmtIterator = model.listStatements();
			while (stmtIterator.hasNext()) {
				Statement statement = stmtIterator.next();
				// System.out.println(statement);
				bufferedWriter.write(parseSingleStatement(statement));
			}
			bufferedWriter.write("(check-sat)\n");
			bufferedWriter.write("(get-model)\n");
		} catch (Exception e) {
			// TODO: handle exception
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
		System.out.println();
		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			System.out.println(statement.getPredicate());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenerateSMT2WithJena t = new GenerateSMT2WithJena();
		t.generate("data/owl/owlDemoSchema.owl", "output.smt2");
		
	}

}
