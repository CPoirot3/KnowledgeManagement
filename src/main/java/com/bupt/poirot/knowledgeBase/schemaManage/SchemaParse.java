package com.bupt.poirot.knowledgeBase.schemaManage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SchemaParse {
    public static Schema parse(File file) {
        Schema schema = new Schema();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            System.out.println(stringBuilder);


            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            String domain = jsonObject.getString("domain");
            Map<String, String> map = new HashMap<>();
            JSONObject fields = jsonObject.getJSONObject("fields");
            for (String key : fields.keySet()) {
                map.put(key, fields.getString(key));
            }
            schema.domain = domain;
            schema.fields = map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schema;
    }

    public static void main(String[] args) {
        Schema schema = parse(new File("./data/schema/schema.json"));
        System.out.println(schema.domain);
        for (String key : schema.fields.keySet()) {
            System.out.println(key + "  :  " + schema.getFiled(key));
        }
    }
}
