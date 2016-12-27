package com.bupt.poirot.z3.deduce;

import java.util.HashMap;
import java.util.Map;

public class ResultManager {
    public static Map<Integer, Result> map;
    static {
        map = new HashMap<>();
        map.put(0, new Result().append("result", 70));
        map.put(1, new Result().append("result", 50));
        map.put(2, new Result().append("result", 30));

        map.put(-1, new Result().append("result", 10));
    }

    public static Result get(int i) {
        return map.get(i);
    }
}
