package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.knowledgeBase.fusekiLibrary.FetchModelClient;
import com.bupt.poirot.data.mongodb.MongoTool;
import com.bupt.poirot.jettyServer.jetty.RoadData;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Deducer {
    private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public Context context;
    public Solver solver;
    public RequestContext requestContext;
    long current;
    public static RoadData roadData;
    BoolExpr targetExpr;
    LinkedList<BufferData> bufferQueue;
    List<BoolExpr> targets;

    public Deducer(Context context, Solver solver,  RequestContext requestContext) {
        this.context = context;
        this.solver = solver;
        this.requestContext = requestContext;
        init();
    }

    public void init() {
        targets = new ArrayList<>();
        bufferQueue = new LinkedList<>();
        roadData = Client.roadNameToGPSData.get(requestContext.roadName);
        try {
            current = formater.parse("2011/04/25 15:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        parseTarget();
    }

    public void deduce(DeduceData deduceData) {
        float x = deduceData.x;
        float y = deduceData.y;
        long t = deduceData.t;
        float speed  = deduceData.speed;
        String latestTime = deduceData.latestTime;

        if (t - current > 600 * 1000) { // 积累十分钟的时间
            System.out.println(x + "  " + y + "  " + latestTime);
            System.out.println(new Date(current));
            System.out.println("size : " + bufferQueue.size());
            // 推理一次
            Document document = new Document();
            document.append("id", Integer.valueOf(requestContext.id));
            solver.push();
            ArithExpr a = context.mkIntConst("valid");
            ArithExpr b = context.mkIntConst("carsInRoad");
            int valid = 0;
            int carsInRoad = 0;
            for (BufferData bufferData : bufferQueue) {
                float s = bufferData.speed;
                carsInRoad++;
                if (s < 10) {
                    valid++;
                }
            }
            solver.add(context.mkEq(a, context.mkInt(valid)));
            solver.add(context.mkEq(b, context.mkInt(carsInRoad)));

            System.out.println(valid + " " + carsInRoad + " " + ((float)valid)/carsInRoad);
            int index = -1;
            for (int i = 0; i < targets.size(); i++) {
                solver.push();
                solver.add(context.mkNot(targets.get(i)));
                if (solver.check() == Status.UNSATISFIABLE) {
                    index = i;
                    solver.pop();
                    break;
                }
                solver.pop();
            }
            solver.pop();

            switch (index) {
                case 0:
                    document.put("value", 70);
                    break;
                case 1:
                    document.put("value", 50);
                    break;
                case 2:
                    document.put("value", 30);
                    break;
                default:
                    document.put("value", 10);
                    break;
            }

            document.append("time", String.valueOf(current));
            MongoTool.flushToDatabase(document);
            while (!bufferQueue.isEmpty() && bufferQueue.peekFirst().time - current < 300 * 1000) { // 把最前5分钟的数据remove掉
                bufferQueue.removeFirst();
            }
            current += 300 * 1000;// 向后移动5分钟
        } else {
            bufferQueue.addLast(new BufferData(t, speed));
        }
    }

    // TODO
    private BoolExpr parseTargetBackup(String target) {
        int min = 0;
        List<Solver> solverList = new ArrayList<>();
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

        System.out.println("parse target done");

        targets.add(targetExpr);
        targets.add(targetExpr2);
        targets.add(targetExpr3);
        targets.add(targetExpr4);

        return context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(80), b)),
                context.mkGe(b, context.mkInt(min)));
    }

    private void parseTarget() {
        int min = Integer.valueOf(requestContext.minCars);
        int sereve = Integer.valueOf(requestContext.severe);
        int conjection = Integer.valueOf(requestContext.conjection);
        int slightConjection = Integer.valueOf(requestContext.slightConjection);

        ArithExpr a = context.mkIntConst("valid");
        ArithExpr b = context.mkIntConst("carsInRoad");
        // target 严重拥堵
        BoolExpr targetExpr = context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(sereve), b)),
                context.mkGe(b, context.mkInt(min)));

        // 拥堵
        BoolExpr targetExpr2 = context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(conjection), b)),
                context.mkGe(b, context.mkInt(min)));

        // 轻微拥堵
        BoolExpr targetExpr3 = context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(slightConjection), b)),
                context.mkGe(b, context.mkInt(min)));

        targets.add(targetExpr);
        targets.add(targetExpr2);
        targets.add(targetExpr3);
    }


    public void getRoadData(String roadName) {
        String owlSyntax = Client.roadNameToOWLSyntax.get(roadName);
        System.out.println(owlSyntax);

        String query = "SELECT ?subject ?predicate ?object\n" +
                "WHERE {\n" +
                "  <http://www.co-ode.org/ontologies/ont.owl#福中路起点> ?predicate ?object  \t\n" +
                "}\n" +
                "LIMIT 25";

        FetchModelClient fetchModelClient = new FetchModelClient();
        InputStream inputStream = fetchModelClient.fetch("http://localhost:3030", "traffic", query);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 根据gps坐标推理判断属于哪条路
    public static boolean isInTheRoad(double x, double y) {
        // TODO
        // get roadData
        double m = (roadData.x1 - x) * (roadData.y2 - y);
        double n = (roadData.x2 - x) * (roadData.y1 - y);
        double result = m * n;
        if (Math.abs(result) > 0.00000000001) {
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

        if (distane > 0.00000001) {
            return false;
        }

        return true;
    }

    public static boolean isInTimeSection(long t) {
        // TODO
        return true;
    }

    public static void main(String[] args) {
        try {
            long beginTime = formater.parse("2011/04/25 15:00:00").getTime();
            System.out.println(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
