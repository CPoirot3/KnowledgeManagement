package com.bupt.poirot.knowledgeBase.schemaManage;

import com.bupt.poirot.jettyServer.utils.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hui.chen on 2016/12/25.
 */
public class PositionMap {
    public Map<String, Road> stringRoadMap;
    public Map<Position, String> positionStringMap;
    public PositionMap() {
        stringRoadMap = new HashMap<>();
        positionStringMap = new HashMap<>();
        load();
    }

    public void load() {
        File roadSchemaDir = new File(Config.getString("road_schema_dir"));
        for (File file : roadSchemaDir.listFiles()) {
            parse(file);
        }
    }

    private void parse(File file) {
        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(jsonString.toString());
        JSONArray positions = jsonObject.getJSONArray("positions");
        for (int i = 0; i < positions.length(); i++) {
            JSONObject position = positions.getJSONObject(i);
            double x1 = position.getDouble("x1");
            double y1 = position.getDouble("y1");
            double x2 = position.getDouble("x2");
            double y2 = position.getDouble("y2");
            System.out.println(x1 + " " + y1 + " " + x2 + " " + y2);
        }
    }

    public static void main(String[] args) {
        PositionMap positionMap = new PositionMap();

    }
}
