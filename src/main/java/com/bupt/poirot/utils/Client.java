package com.bupt.poirot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bupt.poirot.data.modelLibrary.FetchModelClient;
import com.bupt.poirot.main.jetty.RoadData;
import com.bupt.poirot.main.jetty.TimeData;
import com.bupt.poirot.z3.parseAndDeduceOWL.OWLToZ3;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.apache.jena.atlas.RuntimeIOException;
import org.apache.jena.atlas.json.JsonObject;

public class Client {

	public Context context;
	private List<Solver> solverList;

	private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//	private DealData dealData;
	private LinkedList<JsonObject> resultQueue;
	public LinkedList<String> data;
	private String target;
	public String domain;
	private String topic;
	private String roadName;
	private RoadData roadData;
	private InputStream inputStream;

	private HashMap<String, RoadData> stringRoadDataHashMap = new HashMap<>();

	private TimeData timeData;
	private int min;

	public Client(Map<String, String[]> paramsMap) {
		stringRoadDataHashMap.put("翠竹路", new RoadData(114.134266, 22.582957, 114.134606, 22.580431));
		stringRoadDataHashMap.put("红岭中路", new RoadData(114.110848, 22.568226, 114.110848, 22.561985));
		stringRoadDataHashMap.put("福中路", new RoadData(114.056446, 22.548233, 114.059447, 22.548283));
		stringRoadDataHashMap.put("金田路", new RoadData(114.069633, 22.553857, 114.069562, 22.54835));

		solverList = new ArrayList<>();

		target = paramsMap.get("target")[0];
		topic = paramsMap.get("topic")[0];
		domain = paramsMap.get("topic")[0];
		roadName = paramsMap.get("road")[0];
		System.out.println("target : " + target + " topic : " + topic + " domain : " + domain + " road : " + roadName);

		context = new Context();

		if (topic == null) {
			System.out.println("need topic info");
			throw new RuntimeException("need topic info");
		}


		roadData = stringRoadDataHashMap.get(roadName);
		min = Integer.valueOf(paramsMap.get("min")[0]);
		System.out.println("min : " + min);
		System.out.println(roadData.x1 + "  " + roadData.y1 + "  " + roadData.x2 + "  " + roadData.y2);

		timeData = parseTimeSection(paramsMap.get("time")[0]);
		this.resultQueue = new LinkedList<>();
		this.data = new LinkedList<>();
		workflow();
	}

	public void workflow() {
		System.out.println("Fetch Model begin");
		fetchModel(domain);
		System.out.println("Fetch Model done");
		OWLToZ3 owlToZ3 = new OWLToZ3();
		BoolExpr preAxiom = owlToZ3.parseFromStream(context, inputStream);
		// 预先定义的公理 --- z3模式
		System.out.println("preAxiom : " + preAxiom);

		parseTarget(target);
		deduce(preAxiom, roadData);
	}

	private void fetchModel(String domain) {
		FetchModelClient fetchModelClient = new FetchModelClient();
		String host = "http://localhost:3030/";
		String query = ""; // TODO
		inputStream = fetchModelClient.fetch(host, domain, query);
		if (inputStream == null) {
			throw new RuntimeException("fetch model failed");
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
			System.out.println("begin :" + begin);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(begin + "  " + end);
		TimeData timeData = new TimeData(begin, end);
		return timeData;
	}

	private void parseTarget(String target) {
		int solverSize = target.split("&&").length < 4 ? 4 : target.split("&&").length;
		for(int i = 0; i < solverSize; i++) {
			// make sure at least one solver
			Solver solver = context.mkSolver();
			Params params = context.mkParams();
			params.add("mbqi", false);
			solver.setParameters(params);
			solverList.add(solver);
		}

		System.out.println("solverList size : " + solverList.size());
		ArithExpr a = context.mkIntConst("valid");
		ArithExpr b = context.mkIntConst("carsInRoad");

		// target 严重拥堵
		BoolExpr targetExpr = context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(80), b)),
				context.mkGe(b, context.mkInt(min)));
		System.out.println(targetExpr);
		Solver solver = solverList.get(0);
		solver.add(context.mkNot(targetExpr));

		// 拥堵
		BoolExpr targetExpr2 = context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(60), b)),
				context.mkGe(b, context.mkInt(min)));
		System.out.println(targetExpr2);
		Solver solver2 = solverList.get(1);
		solver2.add(context.mkNot(targetExpr2));

		// 轻微拥堵
		BoolExpr targetExpr3 = context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(40), b)),
				context.mkGe(b, context.mkInt(min)));
		System.out.println(targetExpr3);
		Solver solver3 = solverList.get(2);
		solver3.add(context.mkNot(targetExpr3));

		// 畅通
		BoolExpr targetExpr4 = context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(20), b)),
				context.mkGe(b, context.mkInt(min)));
		System.out.println(targetExpr4);
		Solver solver4 = solverList.get(3);
		solver4.add(context.mkNot(targetExpr4));

		for (Solver sol : solverList) {
			System.out.println(sol.check());
		}
		System.out.println("parse target done");
	}

	private void deduce(BoolExpr preAxiom, RoadData roadData) {
		// TODO 推理后的结果存入resultQueue中

		List<Boolean> resultList = new ArrayList<>();

		dealWithData(preAxiom, roadData, resultList);
		System.out.println("deduce done");
		JsonObject jsonObject = new JsonObject();
		int index = 3;
		for (int i = 0; i < resultList.size(); i++) {
			if (resultList.get(i)) {
				index = i;
				break;
			}
		}

		System.out.println(resultList.size());
		for (boolean b : resultList) {
			System.out.print(b + "  ");
		}
		System.out.println();

		System.out.println("index  : " + index);
		switch (index) {
			case 0:
				jsonObject.put("result", "严重拥堵");
				break;
			case 1:
				jsonObject.put("result", "拥堵");
				break;
			case 2:
				jsonObject.put("result", "轻微拥堵");
				break;
			default:
				jsonObject.put("result", "畅通");
		}
		resultQueue.addLast(jsonObject);
	}

	boolean isInTheRoad(double x, double y, RoadData roadData) {
		double m = (roadData.x1 - x) * (roadData.y2 - y);
		double n = (roadData.x2 - x) * (roadData.y1 - y);
		double result = m * n;
		if (Math.abs(result) > 0.0000000001) {
			return false;
		}

		if ((roadData.x1 - x) * (roadData.x2 - x) + (roadData.y1 - y) * (roadData.y2 - y) > 0) {
			return false;
		}

		double ax = x - roadData.x1;
		double ay = y - roadData.y1;

		double bx = roadData.x2 - roadData.x1;
		double by = roadData.y2 - roadData.x2;

		double aLength = Math.sqrt(ax * ax + ay * ay);
		double bLength = Math.sqrt(bx * bx + by * by);

		double cos = aLength * bLength / (ax * bx + ay * by);
		double sin = Math.sqrt(1.0 - cos * cos);

		double distane = aLength * sin;

		if (distane > 0.0000001) {
			return false;
		}

		return true;
	}

	private void dealWithData(BoolExpr preAxiom, RoadData roadData, List<Boolean> resultList) {
//		System.out.println(preAxiom);

		File file = new File(Config.getString("data_file"));
		int length = solverList.size();
		int[] validCars = new int[length];
		for (int i = 0; i < length; i++) {
			resultList.add(false);
		}
		int carsInRoad = 0;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count++;
//				if (count > 50000000) {
//					break;
//				}
				if (count % 1000000 == 0) {
					System.out.println("dealt lines : " + count);
				}

				if (line.length() == 0) {
					continue;
				}
				String[] strs = line.split(",");
				if (strs.length < 5) {
					continue;
				}
				String time = strs[1]; // 时间
				long t;
				try {
					t = formater.parse(time).getTime();
				} catch (ParseException e1) {
					continue;
				}

				if (!isInTimeSection(t, timeData)) {
					continue;
				}

//				String carName = strs[0]; // 车名
//				boolean states = strs[4].equals("1") ? true : false; // 状态
				float x, y, speed;
//				byte direction;
				try {
					x = Float.parseFloat(strs[2]); // 经度
					y = Float.parseFloat(strs[3]); // 纬度
					speed = Float.parseFloat(strs[5]); // 速度
//					direction = Byte.parseByte(strs[6]); // 方向
				} catch (Exception e) {
					continue;
				}
				if (y <= 10 || y >= 40 || x <= 100 || x >= 140) {
					continue;
				}
				if (!isInTheRoad(x, y, roadData)) {
					continue;
				}

				System.out.println("got one x : " + x + "  y : " + y + " speed : " + speed + " time : " + time + "  " + t);

				carsInRoad++;
				if (speed < 5) {
					for (int i = 0; i < validCars.length; i++) {
						validCars[i]++;
					}
//				} else if (speed < 10) {
//					for (int i = 1; i < validCars.length; i++) {
//						validCars[i]++;
//					}
//				} else if (speed < 15) {
//					for (int i = 2; i < validCars.length; i++) {
//						validCars[i]++;
//					}
				}

				ArithExpr a = context.mkIntConst("valid");
				ArithExpr b = context.mkIntConst("carsInRoad");


				for (int i = 0; i < solverList.size(); i++) {
					Solver solver = solverList.get(i);
					// push
					solver.push();
					solver.add(context.mkEq(a, context.mkInt(validCars[i])));
					solver.add(context.mkEq(b, context.mkInt(carsInRoad)));
					System.out.println("carsInRoad : " + carsInRoad + "  valid : " + validCars[i]);
					if (solver.check() == Status.UNSATISFIABLE) {
						resultList.set(i, true);
						System.out.println("proved ");
					} else {
						System.out.println("not proved ");
					}
					// pop
					solver.pop();
				}
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private boolean isInTimeSection(long t, TimeData timeData) {
		if (t < timeData.begin || t > timeData.end) {
			return false;
		}
		return true;
	}


	public JsonObject getResult() {
		return resultQueue.removeFirst();
	}
	
//	public void accept() {
//		File file = new File(Config.getValue("mac"));
//		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
//            String line;
//            int count = 0;
//			while ((line = reader.readLine()) != null) {
//				data.addLast(line);
//                count++;
//                if (count > 100000) {
//                	return;
//                }
//                if (data.size() >= 10000) {
//                	deduceWithOneSection();
//					data.clear();
//
//                }
//            }
//		} catch (Exception e ) {
//			e.printStackTrace();
//		}
//	}
	
	public void acceptSocket() {
//		try (Socket socket = new Socket("127.0.0.1", 30000)) {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			String message = null;
//			while ((message = reader.readLine()) != null) {
////				System.out.println(message);
//				dealData.deal(message);
//				Thread.sleep(1000);
//			}
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
	}


	public static void main(String[] args) {
		try {
			System.out.println(formater.parse("2011/04/18 12:04:22").getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
