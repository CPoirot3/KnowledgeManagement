package com.bupt.poirot.z3.deduce;

import java.util.HashMap;
import java.util.Map;

public class ResultManager {
    public static Map<Integer, Integer> map;
    public static Map<Integer, String> nameMap;
    static {
        map = new HashMap<>();
        map.put(0, 70);
        map.put(1, 50);
        map.put(2, 30);
        map.put(-1, 10);

        nameMap = new HashMap<>();
        nameMap.put(0, "严重拥堵");
        nameMap.put(1, "拥堵");
        nameMap.put(2, "轻微拥堵");
        nameMap.put(-1, "畅通");
    }

    public static int get(int i) {
        return map.get(i);
    }
}
