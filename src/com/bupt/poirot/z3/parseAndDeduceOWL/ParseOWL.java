/**
 * 2015年11月9日
 * Poirot
 */
package com.bupt.poirot.z3.parseAndDeduceOWL;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FiniteDomainSort;

/**
 * @author Poirot
 *
 */
public class ParseOWL {
	public void parseOwl() throws ParserConfigurationException, SAXException, IOException {

		File file = new File("heatExchangeStation.owl");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(file);

		Element root = doc.getDocumentElement();

		// System.out.println(root.getNodeName());
		// System.out.println(root.getTagName());

		NodeList children = root.getChildNodes();
		// System.out.println(children.getLength());

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				String elementName = ((Element) child).getTagName();

				if (elementName.contains("NamedIndividual")) {
					String attribute = ((Element) child).getAttribute("rdf:about");
					attribute = attribute.substring(0, attribute.length() - 1);
					float low = 0, high = 1000, value = 0;
					if (attribute.endsWith("Sensor")) {
						// System.out.println(elementName + " :");
						NodeList list = ((Element) child).getChildNodes();
						for (int j = 0; j < list.getLength(); j++) {
							Node childNode = list.item(j);
							if (childNode instanceof Element) {
								Element element = (Element) childNode;

								if (element.getTagName().endsWith("temperatureLow")) {
									// System.out.println(element.getTextContent());
									low = Float.parseFloat(element.getTextContent());

								}

								if (element.getTagName().endsWith("temperatureHigh")) {
									// System.out.println(element.getTextContent());
									high = Float.parseFloat(element.getTextContent());

								}
								if (element.getTagName().endsWith("temperature")) {
									// System.out.println(element.getTextContent());
									value = Float.parseFloat(element.getTextContent());
									System.out.println(guoLuTemperatureTest(low, high, value));
								}

							}
						}
					}

				}
				// System.out.println(((Element) child).getTextContent());
			}

		}

	}

	public String guoLuTemperatureTest(float low, float high, float value) {

		System.out.println(low + "  " + high + " " + value);
		Context ctx = new Context();
		// System.out.println("FiniteDomainExample");
		FiniteDomainSort temperature = ctx.mkFiniteDomainSort("T", (long) high);
		// FiniteDomainSort pressure = ctx.mkFiniteDomainSort("P", 10000);

		try {
			Expr t1 = ctx.mkNumeral(String.valueOf(value), temperature);
			System.out.println(t1);
		} catch (Exception e) {
			// System.out.println("alarm : temperature exceed");
			return "alarm : temperature exceed";
		}

		// try {
		// Expr p1 = ctx.mkNumeral(10001, pressure);
		// } catch (Exception e) {
		// // TODO: handle exception
		// System.out.println("alarm : pressure exceed");
		// return "alarm : pressure exceed";
		// }

		return "Normal";

	}

	/**
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		ParseOWL parseOWL = new ParseOWL();
		parseOWL.parseOwl();
	}

}
