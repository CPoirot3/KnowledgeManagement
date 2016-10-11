package jenaModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Poirot
 *
 */
public class RunZeTest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("ph"); 
		Process process = Runtime.getRuntime().exec("z3 -smt2 output.smt2");
//		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String buff = null;
		while ((buff = reader.readLine()) != null) {
			System.out.println(buff);
		}
	}

}
