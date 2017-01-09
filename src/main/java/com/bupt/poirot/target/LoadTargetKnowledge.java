package com.bupt.poirot.target;

import com.bupt.poirot.knowledgeBase.fusekiLibrary.FetchModelClient;
import com.bupt.poirot.utils.Config;
import com.bupt.poirot.z3.parseAndDeduceOWL.OWLToZ3;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Solver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class LoadTargetKnowledge {

    public LoadTargetKnowledge() {

    }

    public void load(Context context, Solver knowledgeDeduceSolver, Map<String, FuncDecl> funcDeclMap) {
        // add to the scopeDeduceSolver
        OWLToZ3 owlToZ3 = new OWLToZ3();
        File file = new File(Config.getString("traffic_domain")); // only load knowledge for specific domain
        try {
            BoolExpr knoweledgeInFormOfZ3 = owlToZ3.parseFromStream(context, new FileInputStream(file), funcDeclMap);
            knowledgeDeduceSolver.add(knoweledgeInFormOfZ3);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

}
