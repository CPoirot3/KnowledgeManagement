package com.bupt.poirot.z3.deduce;

import java.util.HashMap;
import java.util.Map;

public class ResultManager {
    public static Map<Integer, Integer> map;
    static {
        map = new HashMap<>();
        map.put(0, 70);
        map.put(1, 50);
        map.put(2, 30);

        map.put(-1, 10);
    }

    public static int get(int i) {
        return map.get(i);
    }
}
