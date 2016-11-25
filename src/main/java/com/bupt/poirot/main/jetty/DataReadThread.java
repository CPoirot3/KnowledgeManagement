package com.bupt.poirot.main.jetty;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by hui.chen on 11/25/16.
 */
public class DataReadThread implements Runnable {
    public BlockingQueue<String> blockingQueue;

    public BufferedReader bufferedReader;

    public DataReadThread(BlockingQueue<String> blockingQueue, BufferedReader bufferedReader) {
        this.blockingQueue = blockingQueue;
        this.bufferedReader = bufferedReader;
    }


    @Override
    public void run() {
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                blockingQueue.put(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataDealThread.finish = true;
    }
}
