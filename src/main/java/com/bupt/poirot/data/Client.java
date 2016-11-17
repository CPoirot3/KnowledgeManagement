package com.bupt.poirot.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Map;

import com.bupt.poirot.data.modelLibrary.FetchModelClient;
import com.bupt.poirot.z3.parseAndDeduceOWL.OWLToZ3;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.apache.jena.atlas.json.JsonObject;

import com.bupt.poirot.main.Config;
public class Client {
	public Context context;
	public Solver solver;

	public DealData dealData;
	public LinkedList<JsonObject> resultQueue;
	public LinkedList<String> data;
	public String target;
	public String domain;
	public String topic;
	InputStream inputStream;

	public Client(Map<String, String[]> paramsMap) {
		context = new Context();
		solver = context.mkSolver();
		Params params = context.mkParams();
		params.add("mbqi", false);
		solver.setParameters(params);

		target = paramsMap.get("target")[0];
		topic = paramsMap.get("topic")[0];
		domain = paramsMap.get("topic")[0];

		System.out.println("target : " + target + " topic : " + topic + " domain : " + domain);
//		this.dealData = new DealData(sections);

		if (topic == null) {
			System.out.println("need topic info");
			throw new RuntimeException("need topic info");
		}
		this.resultQueue = new LinkedList<>();
		this.data = new LinkedList<>();
		workflow();
	}

	public void workflow() {
		fetchModel(domain);
		System.out.println("Fetch Model done");
		BoolExpr preAxiom = OWLToZ3.parseFromStream(context, inputStream);
		// 预先定义的公理 --- z3模式
		System.out.println("preAxiom : " + preAxiom);

		BoolExpr t = parseTarget(target);
		deduce(preAxiom, t);

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


	private BoolExpr parseTarget(String target) {
		BoolExpr res = null;
		String[] strs = target.split("&");
		for (String str : strs) {
			// TODO
		}
		return res;
	}

	private void deduce(BoolExpr preAxiom, BoolExpr t) {
		// TODO 推理后的结果存入resultQueue中
		solver.add(preAxiom);
		if (t != null) {
			solver.add(context.mkNot(t));
		}
		boolean res;
		if (solver.check() == Status.UNSATISFIABLE) {
			res = true;
		} else {
			res = false;
		}

		JsonObject jsonObject = new JsonObject();
		if (res) {
			jsonObject.put("result", "yes");
		} else {
			jsonObject.put("result", "no");
		}
		resultQueue.addLast(jsonObject);
	}


	public JsonObject getResult() {
//		return dealData.gerOne();
		return resultQueue.removeFirst();
	}

	
	public void accept() {
		File file = new File(Config.getValue("mac"));
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
            String line;
            int count = 0;
			while ((line = reader.readLine()) != null) {
				data.addLast(line);
                count++;
                if (count > 100000) {
                	return;
                }
                if (data.size() >= 10000) {
                	deduceWithOneSection();
					data.clear();
                	
                }
            }
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private void deduceWithOneSection() {
		for (String message : data) {
			dealData.deal(message);
		}
		
		dealData.deduce();
	}

	public static void main(String[] args) {
//		System.out.println("begin accept");
		
//		HashMap<String, String> map = new HashMap<>();
//		String values = "10";
//		map.put("sections", values);
//		new Client(map);
		
	}
	
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
}
