package com.bupt.poirot.knowledgeBase.datasets;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

import java.io.File;

public class DatasetFactory {

    DatasetMap datasetMap;
    public SparqlQuery sparqlQuery;

    public DatasetFactory() {
        this.datasetMap = new DatasetMap();
        File dir = new File("./resource");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (File file : dir.listFiles()) {
            Dataset dataset = TDBFactory.createDataset(file.getAbsolutePath());
            datasetMap.put(file.getName(), dataset);
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

    public boolean removeDatasetByName(String datasetName) {
        Dataset dataset = datasetMap.remove(datasetName);
        TDBFactory.release(dataset);
        System.out.println("./resource" + File.separator + datasetName);
        File dir = new File("./resource" + File.separator + datasetName);
        System.out.println(dir.exists());
        deleteFile(dir);
        return true;
    }

    public void deleteFile(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFile(f);
            }
        }
        file.delete();
    }

    public static void main(String[] args) {
        
        DatasetFactory datasetFactory = new DatasetFactory();
        datasetFactory.createDatasetByName("test2");
        datasetFactory.removeDatasetByName("test1");

        for (Dataset dataset : datasetFactory.datasetMap.values()) {
            System.out.println(dataset.getContext());
        }
    }

}
