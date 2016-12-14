package com.bupt.poirot.jena.datasets;

import org.apache.jena.query.Dataset;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DatasetMap {
    public Map<String, Dataset> datasetMap;
    Config config;
    public DatasetMap() {
        config = new Config();
    }

    public void init() {

    }

    public boolean containsKey(String datasetName) {
        return datasetMap.containsKey(datasetName);
    }

    public void put(String datasetName, Dataset dataset) {
        datasetMap.put(datasetName, dataset);
    }

    public Dataset get(String datasetName) {
        return datasetMap.get(datasetName);
    }

    public Dataset remove(String datasetName) {
        return datasetMap.remove(datasetName);
    }

    public Collection<Dataset> values() {
        return datasetMap.values();
    }
}
