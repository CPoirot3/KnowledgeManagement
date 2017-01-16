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
	public LinkedList<String> messageBuffer;

	public long lastDeduce = -1;
	public Solver timeSolver;

	public Client(TargetInfo targetInfo) {
		System.out.println("construct client");
		this.context = new Context();
		this.deducer = new Deducer(context, targetInfo);

		timeSolver = context.mkSolver();
		buffer = new LinkedList<>();
		messageBuffer = new LinkedList<>();
	}

	public void workflow() {
		System.out.println("begin workflow");
		init();
		acceptData();
	}

	public void init() {
		System.out.println("init begin : ");
		incidentToKnowledge = new IncidentToKnowledge();
		incidentToKnowledge.load();
		System.out.println("init done : ");

	}

	public void acceptData() { // 数据

		System.out.println("begin accept data : ");
		File file = new File(Config.getString("data_file"));
		System.out.println(file.getAbsoluteFile());
		Date init = new Date();
		Date begin = init;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count++;
//				Thread.sleep(1);
				if (count % 300000 == 0) {
//					Thread.sleep(1000 + begin.getTime() - (new Date().getTime()));
//					System.out.println("dealt lines : " + count);
//					System.out.println("current :" + new Date());
//					Date cur = new Date();
//					System.out.println(cur.getTime() - begin.getTime());
//					begin = cur;
				}

				deal(line, "traffic");

//				if (count > 2000000) {
//					break;
//				}
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
		Date end = new Date();
		System.out.println(((double)end.getTime() - init.getTime()) * 1000 / count);
		System.out.println("dealt done");
	}

	private void deal(String message, String domain) throws InterruptedException {

		if (buffer.size() >= 50000) { // 每秒钟发送1000
			long x = new Date().getTime();
			int size = buffer.size();
			System.out.println("buffer size : " + size);
			while (!buffer.isEmpty()) {
				deduce(buffer.removeFirst(), domain);
			}
			Thread.sleep(980);
			System.out.println(("20000条处理时间 : " + (new Date().getTime() - x)));
		} else {
			buffer.add(message);
		}
	}

	long start = 0;

	public void deduceInSection(String message, String domain) {

		if (timeSolver.getAssertions().length == 0) {
			start = new Date().getTime();
			IntExpr cur = context.mkIntConst("cur");
			timeSolver.push();
			BoolExpr boolExpr = context.mkGe(cur, context.mkInt(start + 20 * 1000));
			System.out.println(boolExpr);
			timeSolver.add(context.mkNot(boolExpr));
			messageBuffer.add(message);
			System.out.println(timeSolver.getAssertions().length);
		} else {
			BoolExpr boolExpr = context.mkGe(context.mkIntConst("cur"), context.mkInt(new Date().getTime()));
//			System.out.println(boolExpr);
			timeSolver.add(boolExpr);

			if (timeSolver.check() == Status.UNSATISFIABLE) {

				while (!messageBuffer.isEmpty()) {
					deduce(messageBuffer.removeFirst(), domain);
				}
				double average = ((double)(new Date().getTime() - start)) / timeSolver.getAssertions().length;
				System.out.println("Time Solver : " + timeSolver.getAssertions().length + "  average : " + average);
				System.out.println();
				timeSolver.pop();
			} else {
				messageBuffer.add(message);
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
			TrafficIncident trafficIncident = new TrafficIncident("traffic");
			trafficIncident = (TrafficIncident) incident;
			for (TrafficKnowdedge p : incidentToKnowledge.positionStringMap.keySet()) {
				if (trafficIncident.x >= p.x1 && trafficIncident.x <= p.x2 && trafficIncident.y >= p.y2 && trafficIncident.y <= p.y1) {
					trafficKnowdedge = p;
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
