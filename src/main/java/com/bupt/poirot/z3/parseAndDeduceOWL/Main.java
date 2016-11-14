package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.util.Set;

import org.semanticweb.HermiT.model.DLClause;

public class Main {

	public static void main(String[] args) {
		System.out.println("begin");
//		File file = new File("data/ontologies/warnSchemaTest0.xml");
        File file = new File("data/schema.owl");
		Set<DLClause> set = ParseOWL.parseOwl(file);
		System.out.println(set.size());


		for (DLClause dlClause: set) {
            String dlClauseString = dlClause.toString();
			System.out.println(dlClauseString);
		}
	}

}
