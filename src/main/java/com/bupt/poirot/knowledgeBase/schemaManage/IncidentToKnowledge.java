package com.bupt.poirot.knowledgeBase.schemaManage;

import com.bupt.poirot.jettyServer.utils.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IncidentToKnowledge {
    public Map<String, String> map;
    public Map<Position, String> positionStringMap;
    public IRIManage iriManage;
    public IncidentToKnowledge() {
        map = new HashMap<>();
        positionStringMap = new HashMap<>();
        iriManage = new IRIManage();
        load();
    }

    public void load() {
        File roadSchemaDir = new File(Config.getString("road_schema_dir"));
        for (File file : roadSchemaDir.listFiles()) {
            parse(file);
        }
    }

    private void parse(File file) {
//        Road road = new Road("traffic", file.getName().split("\\.")[0]);
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
        String roadName = jsonObject.getString("name");
        JSONArray positions = jsonObject.getJSONArray("positions");
        for (int i = 0; i < positions.length(); i++) {
            JSONObject positionObject = positions.getJSONObject(i);
            String positionName = positionObject.getString("name");
            JSONObject data = positionObject.getJSONObject("data");
            double x1 = data.getDouble("x1");
            double y1 = data.getDouble("y1");
            double x2 = data.getDouble("x2");
            double y2 = data.getDouble("y2");
            String name = roadName + "#" + positionName;

            String iri = iriManage.knowledgeNameWithSeparator(name);
            Position p = new Position(iri,"traffic", name, x1, y1, x2, y2);
            positionStringMap.put(p, p.name);
            System.out.println(positionStringMap.get(p));
            System.out.println(p.getIRI() + " " + p.name + " " + x1 + " " + y1 + " " + x2 + " " + y2);
        }
    }

    public static String getOneKey(Set<String> set) {
        String res = null;
        for (String string : set) {
            res = string;
            break;
        }
        return res;
    }

    public static void main(String[] args) {
        IncidentToKnowledge positionMap = new IncidentToKnowledge();

    }
}
