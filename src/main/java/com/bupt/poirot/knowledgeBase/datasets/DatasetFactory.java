package com.bupt.poirot.knowledgeBase.datasets;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

import java.io.File;

public class DatasetFactory {

    DatasetMap datasetMap;
    public SparqlQuery sparqlQuery;

    public DatasetFactory(DatasetMap datasetMap) {
        this.datasetMap = datasetMap;
        File dir = new File("./resource");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (File file : dir.listFiles()) {
            createDatasetByName(file.getName());
        }
    }

    public void createDatasetByName(String datasetName) {
        if (datasetMap.containsKey(datasetName)) {
            return;
        }
        File file = new File("./resource" + File.separator + datasetName);
        if (file.exists()) {
            return;
        }
        file.mkdir();
        Dataset dataset = TDBFactory.createDataset(file.getAbsolutePath());
        datasetMap.put(datasetName, dataset);
    }

    public Dataset getDatasetByName(String datasetName) {
        if (!datasetMap.containsKey(datasetName)) {
            createDatasetByName(datasetName);
        }
        return datasetMap.get(datasetName);
    }

    public Dataset removeDatasetByName(String datasetName) {
        Dataset dataset = datasetMap.remove(datasetName);
        return dataset;
    }

    public void backupDatasetByName(String datasetName) {
        Dataset dataset = datasetMap.get(datasetName);
        if (dataset != null) {

        }
    }

    public static void main(String[] args) {
        DatasetFactory datasetFactory = new DatasetFactory(new DatasetMap());
        datasetFactory.createDatasetByName("test");

        for (Dataset dataset : datasetFactory.datasetMap.values()) {
            System.out.println(dataset.getDefaultModel());
        }
    }

}
