package com.bupt.poirot.target;

import com.bupt.poirot.knowledgeBase.schemaManage.ScopeManage;
import com.bupt.poirot.pubAndSub.subscribe.PubSub;
import com.bupt.poirot.z3.deduce.TargetInfo;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;

import java.util.List;
import java.util.Map;

/**
 * Created by hui.chen on 2017/1/6.
 */
public class TargetParser {

    TargetInfo targetInfo;
    public TargetParser(TargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public void parse(Context context, Solver knowledgeDeduceSolver, Map<String, List<Solver>> solverMap, ScopeManage scopeManage) {
        PubSub pubsub = new PubSub();
        pubsub.subscribe(targetInfo.topic);

        scopeManage.addTarget(targetInfo.scope, targetInfo.topic); // 加入一个scope，用TargetKnowledge保存其IRI, topic is also domain
        LoadTargetKnowledge loadTargetKnowledge = new LoadTargetKnowledge();
        loadTargetKnowledge.load(context, knowledgeDeduceSolver);

        TargetToBoolExpr targetToBoolExpr = new TargetToBoolExpr();
        List<Solver> list = targetToBoolExpr.parseTarget(context, targetInfo, targetInfo.scope); // responsible for init the solverMap
        solverMap.put(targetInfo.scope, list);
    }
}
