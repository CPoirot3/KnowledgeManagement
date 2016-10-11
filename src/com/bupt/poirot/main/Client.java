package com.bupt.poirot.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;

/**
 * Created by Poirot on 2016/6/15.
 */
public class Client {
	public static void stringToOwl(String message) {
		if (message == null || message.length() == 0) {
			return;
		}
		String[] strs = message.split(",");
		
		String carName = strs[0];
		System.out.println(carName);
		String time = strs[1];
		String x = strs[2];
		String y = strs[3];
		String states = strs[4];
		String speed = strs[5];
		String direction = strs[6];

		for (String string : strs) {
			System.out.print(string + " ");
		}
		System.out.println();

		
		String carURI    = "http://bupt/wangfu/" + carName;
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		// create the resource
		// and add the properties cascading style
//		Resource johnSmith = model.createResource(personURI).addProperty(VCARD.FN, fullName).addProperty(VCARD.N,
//				model.createResource().addProperty(VCARD.Given, givenName).addProperty(VCARD.Family, familyName));
		Property stateProperty = model.createProperty(carURI, "STATE");
		Property speedProperty = model.createProperty(carURI, "SPEED");
		Property directionProperty = model.createProperty(carURI, "DIRECTION");
		Resource car = model.createResource(carURI).addProperty(stateProperty, states).addProperty(speedProperty, speed).
				addProperty(directionProperty, direction);

		System.out.println("RDF/XML format : ");
		model.write(System.out);
		System.out.println();
	}

	public static void main(String[] args) {
		try (Socket socket = new Socket("127.0.0.1", 30000)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "gbk"));
			String message = null;
			while ((message = reader.readLine()) != null) {
				stringToOwl(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
