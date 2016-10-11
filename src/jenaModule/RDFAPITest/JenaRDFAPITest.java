/**
 * 2016年1月18日
 * Poirot
 * 下午4:08:21
 * KnowledgeManagement
 */
package jenaModule.RDFAPITest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.VCARD;

/**
 * @author Poirot
 *
 */
public class JenaRDFAPITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// some definitions
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
		
		// list the statements in the Model
		StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object

		    System.out.print(subject.toString());
		    System.out.print("   " + predicate.toString() + "   ");
		    if (object instanceof Resource) {
		       System.out.print(object.toString());
		    } else {
		        // object is a literal
		        System.out.print(" \"" + object.toString() + "\"");
		    }

		    System.out.println("   .");
		}
		
		 System.out.println();
		 System.out.println();
		 System.out.println();
		 
		// now write the model in XML form to a file 
		File file = new File("test.rdf");
		try (
				FileOutputStream fileInputStream = new FileOutputStream(file)){
			model.write(fileInputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		 
		System.out.println("RDF/XML format : ");
		model.write(System.out);
		System.out.println();
		System.out.println("RDF/XML-ABBREV format : ");
		model.write(System.out, "RDF/XML-ABBREV");
		System.out.println();
		System.out.println("N-TRIPLES format : ");
		model.write(System.out, "N-TRIPLES");
		System.out.println();
		System.out.println("N3 format : ");
		model.write(System.out, "N3");
		System.out.println();
		System.out.println("TURTLE format : ");
		model.write(System.out, "TURTLE");
	}
}
