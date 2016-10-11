package jenaModule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

public class GenerateSTM2 {

	public static void main(String[] args) {

		Model model = ModelFactory.createDefaultModel();

		InputStream in = FileManager.get().open("newOwl2.owl");
		if (in == null) {
			throw new IllegalArgumentException("File not found");
		}

		model.read(in, null);

		model.write(System.out, "N-TRIPLES");
		System.out.println("=============================================================");
		String result = new GenerateSTM2().getZ3(model);

		System.out.println(result);

		try {
			File outFile = new File("output.smt2");

			FileWriter fileWriter = new FileWriter(outFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(result);

			bufferedWriter.close();
			fileWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}

	public String getZ3(Model model) {

		StringBuffer sbf = new StringBuffer(1000);
		Set<String> funcSet = new HashSet<>();
		Map<String, String> classMap = new HashMap<>();
		Set<String> predSet = getPredicateSet();

		sbf.append("(declare-sort CLASS)\n");

		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			Resource subject = statement.getSubject();
			Property predicate = statement.getPredicate();
			RDFNode object = statement.getObject();

			if (!classMap.containsKey(subject.toString())) {
				classMap.put(subject.toString(), getSubjectValue(subject.toString()));
				sbf.append("(declare-const " + classMap.get(subject.toString()) + " CLASS)\n");
			}
			if (!object.isLiteral()) {
				if (!classMap.containsKey(object.toString())) {
					classMap.put(object.toString(), getSubjectValue(object.toString()));
					sbf.append("(declare-const " + classMap.get(object.toString()) + " CLASS)\n");
				}
			}
			String pred = getSubjectValue(predicate.toString());
			if (predSet.contains(pred)) {// ν�ʿ����и�ν��
				if (object.isLiteral()) {
					if (!funcSet.contains(pred)) {
						funcSet.add(pred);
						Integer i;
//						if(object.canAs((Double)2.3))){
//							
//						}
						sbf.append("(declare-fun " + pred + " (CLASS) String)\n");
					}
					sbf.append("(assert (= (" + pred + " " + classMap.get(subject.toString()) + ") \""
							+ object.toString() + "\"))\n");
				} else {
					if (!funcSet.contains(pred)) {
						funcSet.add(pred);
						sbf.append("(declare-fun " + pred + " (CLASS) CLASS)\n");
					}
					sbf.append("(assert (= (" + pred + " " + classMap.get(subject.toString()) + ") "
							+ classMap.get(object.toString()) + "))\n");

				}

			} else {// ν�ʿ���û�и�ν��,unknow
				System.out.println("***����δ֪ν�ʣ�" + pred);

				if (object.isLiteral()) {

					if (!funcSet.contains(pred)) {
						funcSet.add(pred);
						sbf.append("(declare-fun " + pred + " (CLASS) String)\n");
					}
					sbf.append("(assert (= (" + pred + " " + classMap.get(subject.toString()) + ") \""
							+ object.toString() + "\"))\n");
				} else {
					if (!funcSet.contains(pred)) {
						funcSet.add(pred);
						sbf.append("(declare-fun " + pred + " (CLASS) CLASS)\n");
					}
					sbf.append("(assert (= (" + pred + " " + classMap.get(subject.toString()) + ") "
							+ classMap.get(object.toString()) + "))\n");

				}
			}

		}

		sbf.append("(check-sat)\n");
		sbf.append("(get-model)\n");

		return sbf.toString();
	}

	private String getSubjectValue(String subject) {

		String result = "";
		for (int i = subject.length() - 1; i >= 0; i--) {
			if (!Character.isAlphabetic(subject.charAt(i))) {
				result = subject.substring(i + 1);
				break;
			}
		}
		if (result.length() > 0) {
			return result;
		}
		System.out.println("***�޷�������" + subject);
		return subject;
	}

	private HashSet<String> getPredicateSet() {
		HashSet<String> set = new HashSet<>();
		String pathname = "predicate.txt";
		File filename = new File(pathname);

		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				set.add(line.trim());

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return set;
	}

}
