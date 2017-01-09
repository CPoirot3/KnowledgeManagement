package com.bupt.poirot.target;

import com.bupt.poirot.knowledgeBase.schemaManage.ScopeManager;
import com.bupt.poirot.pubAndSub.subscribe.PubSub;
import com.bupt.poirot.z3.deduce.TargetInfo;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Solver;

import java.util.List;
import java.util.Map;

public class TargetParser {

    TargetInfo targetInfo;
    public TargetParser(TargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public void parse(Context context, Solver knowledgeDeduceSolver, Map<String, List<Solver>> solverMap, ScopeManager scopeManager, Map<String, FuncDecl> funcDeclMap) {
        PubSub pubsub = new PubSub();
        pubsub.subscribe(targetInfo.topic);

        scopeManager.addTarget(targetInfo.scope, targetInfo.topic); // 加入一个scope，用TargetKnowledge保存其IRI, topic is also domain
        LoadTargetKnowledge loadTargetKnowledge = new LoadTargetKnowledge();
        loadTargetKnowledge.load(context, knowledgeDeduceSolver, funcDeclMap);
        System.out.println("mark");

        TargetToBoolExpr targetToBoolExpr = new TargetToBoolExpr();
        List<Solver> list = targetToBoolExpr.parseTargetToBoolExpr(context, targetInfo); // responsible for init the solverMap
        solverMap.put(targetInfo.scope, list);
    }
}
