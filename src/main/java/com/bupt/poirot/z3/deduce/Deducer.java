package com.bupt.poirot.z3.deduce;

import com.bupt.poirot.data.mongodb.MongoTool;
import com.bupt.poirot.knowledgeBase.incidents.Incident;
import com.bupt.poirot.knowledgeBase.incidents.TrafficIncident;
import com.bupt.poirot.knowledgeBase.schemaManage.TrafficKnowdedge;
import com.bupt.poirot.knowledgeBase.schemaManage.Knowledge;
import com.bupt.poirot.knowledgeBase.schemaManage.ScopeManager;
import com.bupt.poirot.knowledgeBase.schemaManage.TargetKnowledge;
import com.bupt.poirot.target.TargetParser;
import com.bupt.poirot.z3.library.Z3Factory;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
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

    public Map<String, FuncDecl> funcDeclMap;

    public Solver scopeDeduceSolver;
    public Map<String, List<Solver>> solverMap;
    LinkedList<Incident> bufferQueue;
    ScopeManager scopeManager;
    long current;

    public Deducer(Context context, TargetInfo targetInfo) {
        System.out.println("Construct Deducer :");
        this.context = context;
        this.targetInfo = targetInfo;
        this.scopeDeduceSolver = context.mkSolver();
        solverMap = new HashMap<>();
        bufferQueue = new LinkedList<>();
        scopeManager = new ScopeManager();
        funcDeclMap = new HashMap<>();
        init();
    }

    public void init() {
        try {
            current = formater.parse("2011/04/25 15:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TargetParser targetParser = new TargetParser(targetInfo);
        targetParser.parse(context, scopeDeduceSolver, solverMap, scopeManager, funcDeclMap);
    }


    public void deduce(Knowledge knowledge, Incident incident) {
        bufferQueue.addLast(incident);

        if (knowledge != null && knowledge instanceof TrafficKnowdedge && incident instanceof TrafficIncident) { // 存在映射
            TrafficKnowdedge trafficKnowdedge = (TrafficKnowdedge) knowledge;
            bufferQueue.addLast(incident);
//            System.out.println(trafficKnowdedge.getIRI());
            // judge is or not in the scope
            String scope = null;
            boolean mark = false;
            for (String s : solverMap.keySet()) {
                TargetKnowledge targetKnowledge = (TargetKnowledge) scopeManager.getKnowledge(s);
                BoolExpr t = mkBoolExpr(targetKnowledge.getIRI(), trafficKnowdedge);
                System.out.println("范围表达式 :" + t);

                scopeDeduceSolver.push();
                scopeDeduceSolver.add(context.mkNot(t));

                if (scopeDeduceSolver.check() == Status.UNSATISFIABLE) { // 判断position是否在我们要推理的scope内
                    mark = true;
                    scope = s;
                    scopeDeduceSolver.pop();
                    System.out.println("在推理范围内");
                    break;
                } else {
                    System.out.println("不在此目标推理范围");
                }
                scopeDeduceSolver.pop();
            }
            System.out.println();
            if (!mark) { // do not deduce, not in the scope
                return;
            }

            List<Solver> solverList = solverMap.get(scope); // get the solver responsible for the scope
            System.out.println("solverList.size() : " + solverList.size());

            long time = ((TrafficIncident)bufferQueue.peekLast()).time;
            Solver timeSolver = context.mkSolver();
            IntExpr t = context.mkInt(time);
            timeSolver.add(context.mkNot(context.mkGe(t, context.mkInt(current))));
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
                        for (BoolExpr boolExpr : solver.getAssertions()) {
                            System.out.println(boolExpr);
                        }
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

    private BoolExpr mkBoolExpr(String iri, TrafficKnowdedge trafficKnowdedge) {
        String funcName = iri.split("#")[0] + "#hasPosition>";
        FuncDecl funcDecl = funcDeclMap.get(funcName);
        if (funcDecl == null) {
            System.out.println("don't found funcDecl for iri : " + funcName);
            throw new RuntimeException("don't found funcDecl in Deducer.mkBoolExpr()");
        }
        Sort[] domains = funcDecl.getDomain();
        Expr[] exprs = new Expr[domains.length];
        exprs[0] = context.mkConst(iri, domains[0]);
        exprs[1] = context.mkConst(trafficKnowdedge.getIRI(), domains[1]);

        BoolExpr res = context.mkEq(context.mkApp(funcDecl, exprs), context.mkTrue());
        return res;
    }


//    // 根据gps坐标推理判断属于哪条路
//    public static boolean isInTheRoad(double x, double y) {
//        // get roadData
//        RoadData roadData = null;
//        double m = (roadData.x1 - x) * (roadData.y2 - y);
//        double n = (roadData.x2 - x) * (roadData.y1 - y);
//        double result = m * n;
//        if (Math.abs(result) > 0.00000000001) {
//            return false;
//        }
//
//        if ((roadData.x1 - x) * (roadData.x2 - x) + (roadData.y1 - y) * (roadData.y2 - y) > 0) {
//            return false;
//        }
//
//        double ax = x - roadData.x1;
//        double ay = y - roadData.y1;
//
//        double bx = roadData.x2 - roadData.x1;
//        double by = roadData.y2 - roadData.x2;
//
//        double aLength = Math.sqrt(ax * ax + ay * ay);
//        double bLength = Math.sqrt(bx * bx + by * by);
//
//        double cos = aLength * bLength / (ax * bx + ay * by);
//        double sin = Math.sqrt(1.0 - cos * cos);
//
//        double distane = aLength * sin;
//
//        if (distane > 0.00000001) {
//            return false;
//        }
//
//        return true;
//    }

    public static void main(String[] args) {
        try {
            long beginTime = formater.parse("2011/04/25 15:00:00").getTime();
            System.out.println(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
