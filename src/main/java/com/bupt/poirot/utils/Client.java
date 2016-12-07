package com.bupt.poirot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.bupt.poirot.main.jetty.RoadData;
import com.bupt.poirot.main.jetty.TimeData;
import com.bupt.poirot.z3.Deduce.Deducer;
import com.microsoft.z3.Context;
import org.apache.jena.atlas.RuntimeIOException;

public class Client {

	private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static HashMap<String, RoadData> roadNameToGPSData = new HashMap<>();
	public static HashMap<String, String> roadNameToOWLSyntax = new HashMap<>();
	static {
		roadNameToOWLSyntax.put("翠竹路",  "<http://www.co-ode.org/ontologies/ont.owl#翠竹路起点>");
		roadNameToOWLSyntax.put("红岭中路",  "<http://www.co-ode.org/ontologies/ont.owl#红岭中路起点>");
		roadNameToOWLSyntax.put("福中路",  "<http://www.co-ode.org/ontologies/ont.owl#福中路起点>");
		roadNameToOWLSyntax.put("金田路",  "<http://www.co-ode.org/ontologies/ont.owl#金田路起点>");

		// temp solution
		roadNameToGPSData.put("翠竹路", new RoadData(114.134266, 22.582957, 114.134606, 22.580431));
		roadNameToGPSData.put("红岭中路", new RoadData(114.110848, 22.568226, 114.110848, 22.561985));
		roadNameToGPSData.put("福中路", new RoadData(114.056446, 22.548233, 114.059447, 22.548283));
		roadNameToGPSData.put("金田路", new RoadData(114.069633, 22.553857, 114.069562, 22.54835));
	}

	public Context context;
	public RequestContext requestContext;
	public Deducer deducer;

	public Client(Map<String, String[]> paramsMap) {
		int id = paramsMap.containsKey("id") ? Integer.valueOf(paramsMap.get("id")[0]) : 1;

		String topic = paramsMap.get("topic")[0];
		String roadName = paramsMap.get("road")[0];
		int minCars = Integer.valueOf(paramsMap.get("min")[0]);

		this.requestContext = new RequestContext(id, topic, roadName, minCars);
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

		String latestTime = "";
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
				String[] strs = line.split(",");
				String time = strs[1]; // 时间
				float x, y, speed;
				latestTime = time;
				long t;
				try {
					t = formater.parse(time).getTime();
					x = Float.parseFloat(strs[2]); // 经度
					y = Float.parseFloat(strs[3]); // 纬度
					speed = Float.parseFloat(strs[5]); // 速度
				} catch (ParseException e1) {
					continue;
				}
				if (y <= 10 || y >= 40 || x <= 100 || x >= 140) {
					continue;
				}
				if (!Deducer.isInTimeSection(t)) {
					continue;
				}
				if (!Deducer.isInTheRoad(x, y)) {
					continue;
				}
//				System.out.println("got one x : " + x + "  y : " + y + " speed : " + speed + " time : " + time + "  " + t);
				deducer.deduce(x, y, t, speed, latestTime);
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
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
