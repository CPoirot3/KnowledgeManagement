package com.bupt.poirot.z3.Deduce;

import com.bupt.poirot.data.mongodb.FetchData;
import com.bupt.poirot.main.jetty.RoadData;
import com.bupt.poirot.main.jetty.TimeData;
import com.bupt.poirot.utils.Config;
import com.bupt.poirot.utils.RequestContext;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Deducer {

    public Context context;
    public Solver solver;
    public RequestContext requestContext;
    long current;
    BoolExpr targetExpr;
    LinkedList<Float> speeds;

    public Deducer(Context context, Solver solver,  RequestContext requestContext) {
        this.context = context;
        this.solver = solver;
        this.requestContext = requestContext;
        init();
    }

    public void init() {
        speeds = new LinkedList<>();
        current = requestContext.timeData.begin;
        targetExpr = parseTarget(requestContext.target);
        solver.add(targetExpr);
        solver.push();
    }

    public void deduce(float x, float y, long t, float speed, String latestTime) {
        int carsInRoad = 0, validCar = 0;
        if (t - current > 3600 * 1000) { // 积累一个小时的时间
            // 推理一次

            while (speeds.peekFirst() - current < 600 * 1000) { // 把最前十分钟的数据remove掉
                speeds.removeFirst();
            }
            current += 600 * 1000;// 向后移动10分钟
        } else {
            speeds.addLast(speed);
        }

        carsInRoad++;
        if (speed < 10) {
             validCar++;
        }

        ArithExpr a = context.mkIntConst("valid");
        ArithExpr b = context.mkIntConst("carsInRoad");

        // push
        solver.push();
        solver.add(context.mkEq(a, context.mkInt(validCar)));
        solver.add(context.mkEq(b, context.mkInt(carsInRoad)));
//					System.out.println("carsInRoad : " + carsInRoad + "  valid : " + validCars[i]);
        if (solver.check() == Status.UNSATISFIABLE) {
						System.out.println("proved " );
        } else {
						System.out.println("not proved ");
        }
        // pop
        solver.pop();
    }



    // TODO
    private BoolExpr parseTarget(String target) {
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

        return context.mkAnd(context.mkGe(context.mkMul(a, context.mkInt(100)) , context.mkMul(context.mkInt(80), b)),
                context.mkGe(b, context.mkInt(min)));
    }

    public static boolean isInTheRoad(double x, double y) { // 根据gps坐标推理判断属于哪条路
        // TODO
        RoadData roadData = null;
        // get roadData

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

    public static boolean isInTimeSection(long t) {
        // TODO
        return true;
    }

}
