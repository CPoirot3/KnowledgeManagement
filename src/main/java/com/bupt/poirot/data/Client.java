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
import org.apache.jena.atlas.json.JsonObject;

import com.bupt.poirot.main.Config;
public class Client {
	public Context context;
	public Solver solver;

	public DealData dealData;
	public LinkedList<String> resultQueue; 
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
		domain = paramsMap.get("domain")[0];
//		this.dealData = new DealData(sections);
		this.resultQueue = new LinkedList<>();

		fetchModel(domain);

		accept();
	}

	private void fetchModel(String domain) {
		FetchModelClient fetchModelClient = new FetchModelClient();
		String host = "http://localhost:3030/";
		String query = ""; // TODO
		inputStream = fetchModelClient.fetch(host, domain, query);

		BoolExpr preAxiom = OWLToZ3.parseFromStream(context, solver, inputStream);
		// 预先定义的公理 --- z3模式
		System.out.println(preAxiom);


		BoolExpr t = parseTarget(target);

		deduce(preAxiom, t);
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

	}


	public JsonObject getResult() {
		return dealData.gerOne();
	}
	
	public void accept() {

		File file = new File(Config.getValue("mac"));
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
            String line = null;
            int count = 0;
			while ((line = reader.readLine()) != null) {
                resultQueue.addLast(line);
                count++;
                if (count > 100000) {
                	return;
                }
                if (resultQueue.size() >= 10000) {
//                	System.out.println("count " + count);
                	deduceWithOneSection();
                	resultQueue.clear();
                	
                }
//                Thread.sleep(1);
            }
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	private void deduceWithOneSection() {
//		System.out.println("list size : " + linkedList.size());
//		System.out.println("before deal  dataSortByTime size : " + dealData.dataSortByTime.size());
		for (String message : resultQueue) {
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
