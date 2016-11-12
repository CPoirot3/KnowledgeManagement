package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.util.Set;

import org.semanticweb.HermiT.model.DLClause;

public class Main {

	public static void main(String[] args) {
		System.out.println("begin");
		File file = new File("src/main/java/com/bupt/poirot/z3/parseAndDeduceOWL/examples/ontologies/warnSchemaTest0.xml");
		Set<DLClause> set = ParseOWL.parseOwl(file);
		System.out.println(set.size());
		for (DLClause dlClause: set) {
			System.out.println(dlClause);
		}
	}

}
