package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.jettyServer.jetty.TimeData;
import com.bupt.poirot.knowledgeBase.incidents.Incident;
import com.bupt.poirot.knowledgeBase.incidents.IncidentFactory;
import com.bupt.poirot.knowledgeBase.schemaManage.IncidentToKnowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Knowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.TrafficKnowdedge;
import com.bupt.poirot.knowledgeBase.incidents.TrafficIncident;
import com.bupt.poirot.utils.Config;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.apache.jena.atlas.RuntimeIOException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Client {

	public static String IRI = "http://www.semanticweb.org/traffic-ontology#";
	private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public Context context;
	public Deducer deducer;
	IncidentToKnowledge incidentToKnowledge;
	static int count = 0;

	public LinkedList<String> buffer;
	public LinkedList<Incident> incidentBuffer;

	public long lastDeduce = -1;
	public Solver timeSolver;

	public Client(TargetInfo targetInfo) {
		System.out.println("construct client");
		this.context = new Context();
		System.out.println("construct client mid");

		this.deducer = new Deducer(context, targetInfo);

		timeSolver = context.mkSolver();
		buffer = new LinkedList<>();
		incidentBuffer = new LinkedList<>();
		System.out.println("construct client end");
	}

	public void workflow() {
		System.out.println("begin workflow");
		init();
		acceptData();
	}

	public void init() {
		incidentToKnowledge = new IncidentToKnowledge();
		incidentToKnowledge.load();
	}

	public void acceptData() { // 数据

		System.out.println("begin accept data : ");
		File file = new File(Config.getString("data_file"));
		System.out.println(file.getAbsoluteFile());
		int count = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
			String line;
			long begin = new Date().getTime();
			while ((line = reader.readLine()) != null) {
				deal(line, "traffic");
				count++;
				if (count == 400000) {
					long diff = (new Date().getTime() - begin);
					System.out.println("time for 400000 message : " + diff);
					System.out.println((400000 - incidentBuffer.size()) * 1000 / diff);
//					break;
				}
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private void deal(String message, String domain) throws InterruptedException {
		buffer.add(message);
		if (buffer.size() >= 5000) { // 每秒钟发送1000
//			long x = new Date().getTime();
			while (!buffer.isEmpty()) {
				deduceInSection(buffer.removeFirst(), domain);
			}
//			Thread.sleep(1000);
		}
	}

	public void deduceInSection(String message, String domain) {

		IncidentFactory incidentFactory = new IncidentFactory();
		Incident incident = incidentFactory.converIncident(domain, message);

		if (incidentBuffer.isEmpty()) {
			incidentBuffer.addLast(incident);
		} else {
			incidentBuffer.addLast(incident);
			TrafficIncident trafficIncident = (TrafficIncident) incident;

			TrafficIncident firstTrafficIncident = (TrafficIncident)incidentBuffer.peekFirst();

			if (trafficIncident.time - firstTrafficIncident.time >= 1200 * 1000) {
				System.out.println("deduce once, the deduce queue size is : " + incidentBuffer.size());
				deduce(incidentBuffer); // 遍历但不删除

				// 删除一段时间的数据
				long last = ((TrafficIncident)incidentBuffer.peekFirst()).time;
				while (!incidentBuffer.isEmpty() && ((TrafficIncident)incidentBuffer.peekFirst()).time - last < 300 * 1000) {
					incidentBuffer.removeFirst();
				}
			}
		}
	}

	private void deduce(LinkedList<Incident> incidentBuffer) {
		LinkedList<Knowledge> knowledges = new LinkedList<>();
		for (Incident incident : incidentBuffer) {
			knowledges.add(getKnowledge(incident));
		}

		deducer.deduce(incidentBuffer, knowledges);
	}

	public void deduce(Incident incident) {

		Knowledge knowledge = null;
		if (incident != null) {
			knowledge = getKnowledge(incident);// todo 根据事件对象映射成位置（知识库中已有的知识)
		}
		if (knowledge != null) {
			System.out.println(knowledge.getIRI());
			deducer.deduce(knowledge, incident);
		} else {
			incident = null;
			count++;
			if (count % 1000000 == 0) {
				System.gc();
			}
		}
	}
	public void deduce(String message, String domain) {

		IncidentFactory incidentFactory = new IncidentFactory();
		Incident incident = incidentFactory.converIncident(domain, message);
		Knowledge knowledge = null;
		if (incident != null) {
			knowledge = getKnowledge(incident);// todo 根据事件对象映射成位置（知识库中已有的知识)
		}
		deducer.deduce(knowledge, incident);

//		if (knowledge != null) {
//			System.out.println(knowledge.getIRI());
//			deducer.deduce(knowledge, incident);
//		} else {
//			incident = null;
//			count++;
//			if (count % 1000000 == 0) {
//				System.gc();
//			}
//		}
	}
	private Knowledge getKnowledge(Incident incident) {
		TrafficKnowdedge trafficKnowdedge = null;
		if (incident instanceof TrafficIncident) {
			TrafficIncident trafficIncident = (TrafficIncident) incident;
			for (TrafficKnowdedge p : incidentToKnowledge.positionStringMap.keySet()) {
				if (trafficIncident.x >= p.x1 && trafficIncident.x <= p.x2 && trafficIncident.y >= p.y2 && trafficIncident.y <= p.y1) {
					trafficKnowdedge = new TrafficKnowdedge(p.getIRI(), p.domain, p.name, p.x1, p.y1, p.x2, p.y2);
					break;
				}
			}
		}
		return trafficKnowdedge;
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
