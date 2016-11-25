package com.bupt.poirot.main.jetty;

import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by hui.chen on 11/25/16.
 */
public class DataDealThread implements Runnable {

    public static boolean finish = false;
    public BlockingQueue<String> blockingQueue;

    public PrintStream[] printStreams;
    public String[] marks;

    public DataDealThread(BlockingQueue<String> blockingQueue, PrintStream[] printStreams, String[] marks) {
        this.blockingQueue = blockingQueue;
        this.printStreams = printStreams;
        this.marks = marks;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (finish == true) {
                    break;
                }
                String line = blockingQueue.take();
                for (int i = 0; i < marks.length; i++) {
                    if (line.contains(marks[i])) {
                        printStreams[i].println(line);
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " finish at " + new Date());
    }
}