package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.jettyServer.jetty.RoadData;
import com.bupt.poirot.knowledgeBase.fusekiLibrary.FetchModelClient;
import com.bupt.poirot.data.mongodb.MongoTool;
import com.bupt.poirot.knowledgeBase.incidents.Incident;
import com.bupt.poirot.knowledgeBase.incidents.TrafficIncident;
import com.bupt.poirot.knowledgeBase.schemaManage.Knowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Position;
import com.bupt.poirot.knowledgeBase.schemaManage.ScopeManage;
import com.bupt.poirot.knowledgeBase.schemaManage.TargetKnowledge;
import com.bupt.poirot.utils.Config;
import com.bupt.poirot.z3.parseAndDeduceOWL.OWLToZ3;
import com.bupt.poirot.z3.parseAndDeduceOWL.QuantifierGenerate;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import org.bson.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Deducer {
    private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public Context context;
    public Solver knowledgeDeduceSolver;

    public Map<String, List<Solver>> solverMap;

    public RequestContext requestContext;
    long current;
    LinkedList<Incident> bufferQueue;
    ScopeManage scopeManage;

    public Deducer(Context context, RequestContext requestContext) {
        this.context = context;
        this.knowledgeDeduceSolver = context.mkSolver(); //
        this.requestContext = requestContext;
        init();
    }

    public void init() {
        solverMap = new HashMap<>();
        bufferQueue = new LinkedList<>();
        try {
            current = formater.parse("2011/04/25 15:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        knowledgeDeduceSolver = context.mkSolver();
        loadKnowledge();


        scopeManage = new ScopeManage();
        scopeManage.addTarget(requestContext.scope, requestContext.topic); // 加入一个scope，用TargetKnowledge保存其IRI, topic is also domain

        parseTarget(); // responsible for init the solverMap
    }

    private void loadKnowledge() {
        // add to the knowledgeDeduceSolver
        OWLToZ3 owlToZ3 = new OWLToZ3();
        File file = new File(Config.getString("traffic_domain")); // only load knowledge for specific domain
        try {
            BoolExpr knoweledgeInFormOfZ3 = owlToZ3.parseFromStream(context, new FileInputStream(file));
            knowledgeDeduceSolver.add(knoweledgeInFormOfZ3);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // responsible for init the solverMap
    private void parseTarget() {

        String scope = requestContext.scope;
        List<Solver> list = new ArrayList<>();

        int min = Integer.valueOf(requestContext.minCars);
        int sereve = Integer.valueOf(requestContext.severe);
        int medium = Integer.valueOf(requestContext.conjection);
        int slight = Integer.valueOf(requestContext.slightConjection);

        ArithExpr a = context.mkIntConst("valid");
        ArithExpr b = context.mkIntConst("carsInRoad");
        // target 严重拥堵
        BoolExpr targetExpr = context.mkAnd(context.mkGe(context.mkDiv(a, b), context.mkReal(sereve, 100)),
                context.mkGe(b, context.mkInt(min)));
        Solver solverOfSevere = context.mkSolver();
        solverOfSevere.add(targetExpr);
        list.add(solverOfSevere);

        // 拥堵
        BoolExpr targetExpr2 = context.mkAnd(context.mkGe(context.mkDiv(a, b), context.mkReal(medium, 100)),
                context.mkGe(b, context.mkInt(min)));
        Solver solverOfMedium = context.mkSolver();
        solverOfMedium.add(targetExpr2);
        list.add(solverOfMedium);

        // 轻微拥堵
        BoolExpr targetExpr3 = context.mkAnd(context.mkGe(context.mkDiv(a, b), context.mkReal(slight, 100)),
                context.mkGe(b, context.mkInt(min)));
        Solver solverOfSlight = context.mkSolver();
        solverOfSlight.add(targetExpr3);
        list.add(solverOfSlight);

        solverMap.put(scope, list);
    }

    public void deduce(Knowledge knowledge, Incident incident) {
        if (knowledge != null && knowledge instanceof Position && incident instanceof TrafficIncident) {
            Position position = (Position) knowledge;
            bufferQueue.addLast(incident);
            System.out.println(position.getIRI());

            // judge is or not in the scope
            String scope = null;
            boolean mark = false;
            for (String s : solverMap.keySet()) {
                // TODO
                TargetKnowledge targetKnowledge = (TargetKnowledge) scopeManage.getKnowledge(s);
                BoolExpr t = mkBoolExpr(targetKnowledge.getIRI(), position);

                knowledgeDeduceSolver.push();
                knowledgeDeduceSolver.add(context.mkNot(t));

                if (knowledgeDeduceSolver.check() == Status.UNSATISFIABLE) { // 判断position是否在我们要推理的scope内
                    mark = true;
                    scope = s;
                    knowledgeDeduceSolver.pop();
                    break;
                }
                knowledgeDeduceSolver.pop();
            }
            if (!mark) { // do not deduce, not in the scope
                return;
            }


            List<Solver> solverList = solverMap.get(scope); // get the solver responsible for the scope
            System.out.println("solverList.size() : " + solverList.size());

            long time = ((TrafficIncident)bufferQueue.peekLast()).time;
            if (time - current > 600 * 1000) { // 积累十分钟的时间
                System.out.println(new Date(current));
                System.out.println("size : " + bufferQueue.size());
                // 推理一次
                Document document = new Document();
                document.append("id", Integer.valueOf(requestContext.id));
                document.append("time", String.valueOf(current));

                boolean sat = false;
                for (int i = 0; i < solverList.size(); i++) {
                    Solver solver = solverList.get(i);
                    solver.push();
                    ArithExpr a = context.mkIntConst("valid");
                    ArithExpr b = context.mkIntConst("carsInRoad");
                    int valid = 0;
                    int carsInRoad = bufferQueue.size();
                    for (Incident incident1 : bufferQueue) {
                        TrafficIncident trafficIncident1 = (TrafficIncident) incident1;
                        float s = trafficIncident1.speed;
                        if (s < 10) {
                            valid++;
                        }
                    }
                    solver.add(context.mkEq(a, context.mkInt(valid)));
                    solver.add(context.mkEq(b, context.mkInt(carsInRoad)));


                    System.out.println(valid + " " + carsInRoad + " " + ((float) valid) / carsInRoad);
                    if (solver.check() == Status.UNSATISFIABLE) {
                        document.append("value", ResultManager.get(i));
                        solver.pop();
                        sat = true;
                        break;
                    }
                    solver.pop();
                }

                if (!sat) {
                    document.append("value", ResultManager.get(-1));
                }

                System.out.println(document);
                MongoTool.flushToDatabase(document);

                while (!bufferQueue.isEmpty() ) { // 把最前5分钟的数据remove掉
                    TrafficIncident trafficIncident = (TrafficIncident)bufferQueue.peekFirst();
                    if (trafficIncident.time - current < 300 * 1000) {
                        bufferQueue.removeFirst();
                    } else {
                        break;
                    }
                }
                current += 300 * 1000;// 向后移动5分钟
            }
        }
    }

    private BoolExpr mkBoolExpr(String iri, Position position) {
        iri = iri.split("#")[0] + "#hasPosition>";
        FuncDecl funcDecl = QuantifierGenerate.stringToFuncMap.get(iri);
//        System.out.println(QuantifierGenerate.stringToFuncMap.size());
//        for (String key : QuantifierGenerate.stringToFuncMap.keySet()) {
//            System.out.println(key  + "  : " + QuantifierGenerate.stringToFuncMap.get(key));
//        }
        if (funcDecl == null) {
            System.out.println("don't found funcDecl for iri : " + iri);
            throw new RuntimeException("don't found funcDecl in Deducer.mkBoolExpr()");
        }

        Sort[] domains = funcDecl.getDomain();
        Expr[] exprs = new Expr[domains.length];
        exprs[0] = context.mkConst(iri, domains[0]);
        exprs[1] = context.mkConst(position.getIRI(), domains[1]);

        BoolExpr res = context.mkEq(context.mkApp(funcDecl, exprs), context.mkTrue());
        return res;
    }

    public void getRoadData(String roadName) {
        String query = "SELECT ?subject ?predicate ?object\n" +
                "WHERE {\n" +
                "  <http://www.co-ode.org/ontologies/ont.owl#福中路起点> ?predicate ?object  \t\n" +
                "}\n" +
                "LIMIT 25";

        FetchModelClient fetchModelClient = new FetchModelClient();
        InputStream inputStream = fetchModelClient.fetch("http://localhost:3030", "traffic", query);

    }

    // 根据gps坐标推理判断属于哪条路
    public static boolean isInTheRoad(double x, double y) {
        // TODO
        // get roadData
        RoadData roadData = null;
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

    public static void main(String[] args) {
        try {
            long beginTime = formater.parse("2011/04/25 15:00:00").getTime();
            System.out.println(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

}
