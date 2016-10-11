/**
 * 2016年1月19日
 * Poirot
 * 下午5:45:11
 * KnowledgeManagement
 */
package jenaModule;

import java.io.File;
import java.io.FileWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.VCARD;

/**
 * @author Poirot
 *
 */
public class GenerateOWLFile {
	public void generate(String outputFile) { 
		// 使用 FileManager 查找文件
//		InputStream in = FileManager.get().open(fileName);
//		if (in == null) {
//			throw new IllegalArgumentException("File: " + fileName + " not found");
//		}
//		// 读取RDF/XML 文件
//		model.read(in, null);

		String personURI    = "http://somewhere/JohnSmith";
		String givenName    = "John";
		String familyName   = "Smith";
		String fullName     = givenName + " " + familyName;

		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		// create the resource
		//   and add the properties cascading style
		Resource johnSmith
		  = model.createResource(personURI)
		         .addProperty(VCARD.FN, fullName)
		         .addProperty(VCARD.N,
		                      model.createResource()
		                           .addProperty(VCARD.Given, givenName)
		                           .addProperty(VCARD.Family, familyName));
		
		File file = new File(outputFile);
		try (FileWriter fileWriter = new FileWriter(file)) {
			model.write(fileWriter);
		} catch (Exception e) {
			// TODO: handle exception
		}
 
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
		GenerateOWLFile generateOWLFile = new GenerateOWLFile();
		generateOWLFile.generate("outputModel.owl");
	}

}
