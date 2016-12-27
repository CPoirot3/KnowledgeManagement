package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.jettyServer.jetty.RequestInfo;
import com.bupt.poirot.jettyServer.jetty.RoadData;
import com.bupt.poirot.jettyServer.jetty.TimeData;
import com.bupt.poirot.knowledgeBase.incidents.Incident;
import com.bupt.poirot.knowledgeBase.incidents.IncidentFactory;
import com.bupt.poirot.knowledgeBase.schemaManage.IncidentToKnowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Knowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Position;
import com.bupt.poirot.knowledgeBase.incidents.TrafficIncident;
import com.bupt.poirot.utils.Config;
import com.microsoft.z3.Context;
import org.apache.jena.atlas.RuntimeIOException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class Client {

	public static String IRI = "http://www.semanticweb.org/traffic-ontology#";
	private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static HashMap<String, RoadData> roadNameToGPSData = new HashMap<>();
	public static HashMap<String, String> roadNameToOWLSyntax = new HashMap<>();
	static {
		roadNameToOWLSyntax.put("翠竹路",  "<" + IRI + "翠竹路" + ">");
		roadNameToOWLSyntax.put("红岭中路",  "<" + IRI + "红岭中路" + ">");
		roadNameToOWLSyntax.put("福中路",  "<" + IRI + "福中路" + ">");
		roadNameToOWLSyntax.put("金田路",  "<" + IRI + "金田路" + ">");

		// temp solution
		roadNameToGPSData.put("翠竹路", new RoadData(114.134266, 22.582957, 114.134606, 22.580431));
		roadNameToGPSData.put("红岭中路", new RoadData(114.110848, 22.568226, 114.110848, 22.561985));
		roadNameToGPSData.put("福中路", new RoadData(114.056446, 22.548233, 114.059447, 22.548283));
		roadNameToGPSData.put("金田路", new RoadData(114.069633, 22.553857, 114.069562, 22.54835));
	}

	public Context context;
	public RequestContext requestContext;
	public Deducer deducer;
	IncidentToKnowledge incidentToKnowledge;

	public Client(RequestInfo requestInfo) {
		int id = Integer.valueOf(requestInfo.infos.get("id"));

		String scope = requestInfo.infos.get("scope");

		String topic = requestInfo.infos.get("topic");
		String minCars = requestInfo.infos.get("min");
		String a = requestInfo.infos.get("severe");
		String b = requestInfo.infos.get("conjection");
		String c = requestInfo.infos.get("slightConjection");
		String speed = requestInfo.infos.get("speed");

		this.requestContext = new RequestContext(id, topic, scope, minCars, a, b, c, speed);
		this.context = new Context();
		this.deducer = new Deducer(context, requestContext);
	}

	private void getRoadOntology(String roadName) {
		String owlSyntax = roadNameToOWLSyntax.get(roadName);
		String query = "SELECT ?subject ?predicate ?object\n" +
				"WHERE {\n" +
				"  \t\n" +
				owlSyntax + " ?predicate ?object  \t\n" +
				"}\n" +
				"LIMIT 25";

//		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fetchModel(domain, null, singleFilterQuery)))) {
//			String line;
//			while ((line = bufferedReader.readLine()) != null) {
//				System.out.println(line);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void workflow() {
		init();
		acceptData();
	}

	public void init() {
        // TODO
		incidentToKnowledge = new IncidentToKnowledge();
	}

	public void acceptData() { // 数据

		File file = new File(Config.getString("data_file"));
		System.out.println(file.getAbsoluteFile());
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count++;
				if (count % 1000000 == 0) {
					System.out.println("dealt lines : " + count);
				}
				deal(line, "traffic");
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private void deal(String message, String domain) {
		Knowledge knowledge = null;
		IncidentFactory incidentFactory = new IncidentFactory();
		Incident incident = incidentFactory.converIncident(domain, message);
		if (incident != null) {
			knowledge = getKnowledge(incident);// todo 根据事件对象映射成位置（知识库中已有的知识)
		}
		deducer.deduce(knowledge, incident);
	}


	private Knowledge getKnowledge(Incident incident) {
		Position position = null;
		if (incident instanceof TrafficIncident) {
			TrafficIncident trafficIncident = (TrafficIncident) incident;
			for (Position p : incidentToKnowledge.positionStringMap.keySet()) {
				if (trafficIncident.x >= p.x1 && trafficIncident.x <= p.x2 && trafficIncident.y >= p.y1 && trafficIncident.y <= p.y2) {
					position = p;
					break;
				}
			}
		}
		return position;
	}

	private TimeData parseTimeSection(String timeSection) {
		System.out.println("Time section : " + timeSection);
		String[] times = timeSection.split(" - ");
		if (times.length < 2) {
			throw new RuntimeIOException();
		}
		long begin = 0;
		long end = begin;
		try {
			begin = formater.parse(times[0]).getTime();
			end = formater.parse(times[1]).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		TimeData timeData = new TimeData(begin, end);
		return timeData;
	}


	public static void main(String[] args) {

	}
}
