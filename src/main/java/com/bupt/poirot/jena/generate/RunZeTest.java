package com.bupt.poirot.jena.generate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunZeTest {

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("ph"); 
		Process process = Runtime.getRuntime().exec("z3 -smt2 output.smt2");

//		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String buff;
		while ((buff = reader.readLine()) != null) {
			System.out.println(buff);
		}
	}
}
