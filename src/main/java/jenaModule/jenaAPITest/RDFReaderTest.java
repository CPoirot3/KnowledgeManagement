/**
 * 2016年1月18日
 * Poirot
 * 下午4:49:10
 * KnowledgeManagement
 */
package jenaModule.jenaAPITest;

import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

/**
 * @author Poirot
 *
 */
public class RDFReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// create an empty model
		 Model model = ModelFactory.createDefaultModel();
		 String inputFileName = "test.rdf";
		 // use the FileManager to find the input file
		 InputStream in = FileManager.get().open( inputFileName );
		if (in == null) {
		    throw new 
		    IllegalArgumentException("File: " + inputFileName + " not found");
		}

		// read the RDF/XML file
		model.read(in, null);
		
		// write it to standard out
		model.write(System.out);
	}

}
