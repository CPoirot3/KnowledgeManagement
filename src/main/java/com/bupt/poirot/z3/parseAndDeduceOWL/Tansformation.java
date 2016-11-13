package com.bupt.poirot.z3.parseAndDeduceOWL;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import org.semanticweb.HermiT.model.DLClause;

import java.util.Set;

/**
 * Created by poirot3 on 16-11-12.
 */
public class Tansformation {

    public Context tranfor(Set<DLClause> dlClauseSet) {
        Context context = new Context();
        Solver solver = context.mkSolver();
        for (DLClause dlClause : dlClauseSet) {

        }
        return context;
    }

    public Expr parSingleDLCLause(Context context, DLClause dlClause) {
        // TODO solve 
        Expr expr = context.mkInt(10);
        return expr;
    }

}
