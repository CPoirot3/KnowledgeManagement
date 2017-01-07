package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.jettyServer.jetty.RoadData;
import com.bupt.poirot.data.mongodb.MongoTool;
import com.bupt.poirot.knowledgeBase.incidents.Incident;
import com.bupt.poirot.knowledgeBase.incidents.TrafficIncident;
import com.bupt.poirot.knowledgeBase.schemaManage.Knowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.Position;
import com.bupt.poirot.knowledgeBase.schemaManage.ScopeManager;
import com.bupt.poirot.knowledgeBase.schemaManage.TargetKnowledge;
import com.bupt.poirot.target.TargetParser;
import com.bupt.poirot.z3.library.Z3Factory;
import com.bupt.poirot.z3.parseAndDeduceOWL.FuncDeclGenerate;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Deducer {
    private static DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public Context context;
    public TargetInfo targetInfo;

    public Solver knowledgeDeduceSolver;
    public Map<String, List<Solver>> solverMap;
    LinkedList<Incident> bufferQueue;
    ScopeManager scopeManager;
    long current;

    public Deducer(Context context, TargetInfo targetInfo) {
        this.context = context;
        this.targetInfo = targetInfo;
        this.knowledgeDeduceSolver = context.mkSolver();
        solverMap = new HashMap<>();
        bufferQueue = new LinkedList<>();
        scopeManager = new ScopeManager();
        init();
    }

    public void init() {
        try {
            current = formater.parse("2011/04/25 15:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TargetParser targetParser = new TargetParser(targetInfo);
        targetParser.parse(context, knowledgeDeduceSolver, solverMap, scopeManager);
    }


    public void deduce(Knowledge knowledge, Incident incident) {
        if (knowledge != null && knowledge instanceof Position && incident instanceof TrafficIncident) { // 存在映射
            Position position = (Position) knowledge;
            bufferQueue.addLast(incident);
//            System.out.println(position.getIRI());

            // judge is or not in the scope
            String scope = null;
            boolean mark = false;
            for (String s : solverMap.keySet()) {
                // TODO
                TargetKnowledge targetKnowledge = (TargetKnowledge) scopeManager.getKnowledge(s);
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
                document.append("id", Integer.valueOf(targetInfo.id));
                document.append("time", String.valueOf(current));

                boolean sat = false;
                for (int i = 0; i < solverList.size(); i++) {
                    Solver solver = solverList.get(i);
                    solver.push();
                    ArithExpr valid = context.mkIntConst("valid");
                    ArithExpr total = context.mkIntConst("carsInRoad");
                    ArithExpr totalReal = context.mkReal(0,1);
                    ArithExpr validReal =  context.mkReal(0,1);

                    Z3Factory z3Factory = new Z3Factory();

                    for (Incident incident1 : bufferQueue) {
                        TrafficIncident trafficIncident1 = (TrafficIncident) incident1;
                        totalReal = z3Factory.plus(context, totalReal);

                        float s = trafficIncident1.speed;
                        if (s < 10) {
                            validReal = z3Factory.plus(context, validReal);
                        }
                    }

                    solver.add(context.mkEq(valid, validReal));
                    solver.add(context.mkEq(total, totalReal));
//                    System.out.println(valid + "\n" + total);

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
                MongoTool mongoTool = new MongoTool();
                mongoTool.flushToDatabase(document);

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
        FuncDecl funcDecl = FuncDeclGenerate.stringToFuncMap.get(iri);
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



    // 根据gps坐标推理判断属于哪条路
    public static boolean isInTheRoad(double x, double y) {
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
