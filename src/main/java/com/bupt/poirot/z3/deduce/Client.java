package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.jettyServer.jetty.RequestInfo;
import com.bupt.poirot.jettyServer.jetty.TimeData;
import com.bupt.poirot.knowledgeBase.incidents.Incident;
import com.bupt.poirot.knowledgeBase.incidents.IncidentFactory;
import com.bupt.poirot.knowledgeBase.schemaManage.IncidentToKnowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Knowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Position;
import com.bupt.poirot.knowledgeBase.incidents.TrafficIncident;
import com.bupt.poirot.knowledgeBase.schemaManage.ScopeManage;
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

public class Client {

	public static String IRI = "http://www.semanticweb.org/traffic-ontology#";
	private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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

	public void workflow() {
		init();
		acceptData();
	}

	public void init() {
        // TODO
		incidentToKnowledge = new IncidentToKnowledge();
		incidentToKnowledge.load();


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
