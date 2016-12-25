package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.jettyServer.jetty.RequestInfo;
import com.bupt.poirot.jettyServer.jetty.RoadData;
import com.bupt.poirot.jettyServer.jetty.TimeData;
import com.bupt.poirot.knowledgeBase.fusekiLibrary.FetchModelClient;
import com.bupt.poirot.knowledgeBase.schemaManage.Knowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Position;
import com.bupt.poirot.knowledgeBase.schemaManage.TrafficIncident;
import com.bupt.poirot.utils.Config;
import com.microsoft.z3.Context;
import org.apache.jena.atlas.RuntimeIOException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class Client {

	private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static HashMap<String, RoadData> roadNameToGPSData = new HashMap<>();
	public static HashMap<String, String> roadNameToOWLSyntax = new HashMap<>();
	static {
		roadNameToOWLSyntax.put("翠竹路",  "<http://www.co-ode.org/ontologies/ont.owl#翠竹路>");
		roadNameToOWLSyntax.put("红岭中路",  "<http://www.co-ode.org/ontologies/ont.owl#红岭中路>");
		roadNameToOWLSyntax.put("福中路",  "<http://www.co-ode.org/ontologies/ont.owl#福中路>");
		roadNameToOWLSyntax.put("金田路",  "<http://www.co-ode.org/ontologies/ont.owl#金田路>");

		// temp solution
		roadNameToGPSData.put("翠竹路", new RoadData(114.134266, 22.582957, 114.134606, 22.580431));
		roadNameToGPSData.put("红岭中路", new RoadData(114.110848, 22.568226, 114.110848, 22.561985));
		roadNameToGPSData.put("福中路", new RoadData(114.056446, 22.548233, 114.059447, 22.548283));
		roadNameToGPSData.put("金田路", new RoadData(114.069633, 22.553857, 114.069562, 22.54835));
	}

	public Context context;
	public RequestContext requestContext;
	public Deducer deducer;

	public Client(RequestInfo requestInfo) {
		int id = Integer.valueOf(requestInfo.infos.get("id"));
		String topic = requestInfo.infos.get("topic");
		String roadName = requestInfo.infos.get("road");
		String minCars = requestInfo.infos.get("min");
		String a = requestInfo.infos.get("severe");
		String b = requestInfo.infos.get("conjection");
		String c = requestInfo.infos.get("slightConjection");
		String speed = requestInfo.infos.get("speed");

//		System.out.println(topic);
//		System.out.println(roadName);
//		System.out.println(minCars);
//		System.out.println(a);
//		System.out.println(b);
//		System.out.println(c);
//		System.out.println(speed);

		this.requestContext = new RequestContext(id, topic, roadName, minCars, a, b, c, speed);
		this.context = new Context();
		this.deducer = new Deducer(context, context.mkSolver(), requestContext);
	}

	private void getRoadOntology(String roadName) {
		String owlSyntax = roadNameToOWLSyntax.get(roadName);
		String query = "SELECT ?subject ?predicate ?object\n" +
				"WHERE {\n" +
				"  \t\n" +
				owlSyntax + " ?predicate ?object  \t\n" +
				"}\n" +
				"LIMIT 25";

//		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fetchModel(domain, null, query)))) {
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
				deal(line);
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private void deal(String message) {
		String domain = "traffic";


		Knowledge knowledge = getKnowledge(message, domain);



//		if (y <= 10 || y >= 40 || x <= 100 || x >= 140) {
//			return;
//		}
//		if (!Deducer.isInTimeSection(t)) {
//			return;
//		}
//		if (!Deducer.isInTheRoad(x, y)) {
//			return;
//		}
//				System.out.println("got one x : " + x + "  y : " + y + " speed : " + speed + " time : " + time + "  " + t);

		// todo
//		DeduceData deduceData = new DeduceData(x, y, t, speed, latestTime);
//		deducer.deduce(deduceData);
		deducer.deduce(null);
	}

	private Knowledge getKnowledge(String line, String domain) {
		String[] strs = line.split(",");
//		String carName = strs[0];
//		String time = strs[1]; // 时间
//		String longitude = strs[2];
//		String latitude = strs[3];
//		String states = strs[4];
//		String speed = strs[5];
//		String direction = strs[6];

		float x, y, speed;
		boolean status;
		long time;
		byte direction;
		try {
			time = formater.parse(strs[1]).getTime();
			x = Float.parseFloat(strs[2]); // 经度
			y = Float.parseFloat(strs[3]); // 纬度
			status = Boolean.parseBoolean(strs[4]);
			speed = Float.parseFloat(strs[5]); // 速度
			direction = Byte.parseByte(strs[6]);
		} catch (ParseException e1) {
			return null;
		}

		TrafficIncident trafficIncident = new TrafficIncident(domain, strs[0], time, x, y, status, speed, direction);
		// todo 根据事件对象映射成位置（知识库中已有的知识） domain
		Knowledge res = getKnowledge(trafficIncident);

		return res;
	}

	private Knowledge getKnowledge(TrafficIncident trafficIncident) {
		FetchModelClient fetchModelClient = new FetchModelClient();
		InputStream inputStream = fetchModelClient.fetch(trafficIncident.domain);

		return null;
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

}
