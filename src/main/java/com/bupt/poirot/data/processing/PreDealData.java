package com.bupt.poirot.data.processing;

import com.bupt.poirot.utils.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by hui.chen on 11/24/16.
 */
public class PreDealData {

    public static boolean transformFileEncoding(File file, String sourEncoding, String destEncoding) {
//        System.out.println(file.getName());

        String name = file.getName().split("\\.")[0];
        String suffix = file.getName().split("\\.")[1];
        File outputFile = new File(file.getParent() + File.pathSeparator + name + "_bak" + "\\." + suffix);

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), sourEncoding));
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            PrintStream printStream = new PrintStream(new FileOutputStream(outputFile), true, destEncoding);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                printStream.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        file.delete();
        outputFile.renameTo(file);
        return true;
    }



    public class SingleLine {
        long time;
        String value;

        public SingleLine(long time, String value) {
            this.time = time;
            this.value = value;
        }
    }

    public void sortFile(File file) {
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                sortFile(file1);
            }
        } else {
            try {
                File temp = new File("temp");
                PrintStream ps = new PrintStream(new FileOutputStream(temp), true, "utf-8");

                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                List<SingleLine> list = new ArrayList<>();
                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String timeString = line.split(",")[1];
                    long time = formatter.parse(timeString).getTime();
                    list.add(new SingleLine(time, line));
                }

                Collections.sort(list, new Comparator<SingleLine>() {
                    @Override
                    public int compare(SingleLine o1, SingleLine o2) {
                        return (int)(o1.time - o2.time);
                    }
                });

                for (SingleLine singleLine : list) {
                    ps.println(singleLine.value);
                }

                file.delete();
                temp.renameTo(file);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void splitFileFromDayIntoHours() {
        Date begin = new Date();
        System.out.println("begin at :" + begin);
        File file = new File("data/stream-data/20110425.txt");
        String parent = file.getParent();
        String fileName = file.getName();
        String name = fileName.split("\\.")[0];
        String suffix = fileName.split("\\.")[1];
        String[] outputFileNames = new String[24];
        for (int i = 0; i < outputFileNames.length; i++) {
            outputFileNames[i] = parent + File.separator + name + "-" + i + "." + suffix;
        }

        try {
            File[] outputFiles = new File[outputFileNames.length];
            PrintStream[] printStreams = new PrintStream[outputFileNames.length];
            for (int i = 0; i < outputFileNames.length; i++) {
                outputFiles[i] = new File(outputFileNames[i]);
                if (!outputFiles[i].exists()) {
                    outputFiles[i].createNewFile();
                }
                printStreams[i] = new PrintStream(new FileOutputStream(outputFiles[i]), true, "utf-8");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

            String line;
            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            long beginTime = Long.MIN_VALUE;
            try {
                beginTime = formatter.parse("2011/04/25 00:00:00").getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(beginTime);
            while ((line = bufferedReader.readLine()) != null) {
                String timeString = line.split(",")[1];
                long time = formatter.parse(timeString).getTime();
                int secIndex = (int)((time - beginTime) / (3600 * 1000));
                printStreams[secIndex].println(line);
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        System.out.println("cost " + (end.getTime() - begin.getTime()) / 1000 + "s");
    }


    public void splitFile() {  // 单线程读写
        Date begin = new Date();
        System.out.println("begin at :" + begin);
//        File file = new File("./data/all_data_gbk.txt");
//        for (File f : file.listFiles()) {
//            transformFileEncoding(f, "gbk", "utf-8");
//        }

        File file = new File(Config.getString("data_file"));
        System.out.println(file.exists());

        String[] outputFileNames = new String[]{"20110418.txt", "20110419.txt", "20110420.txt", "20110421.txt","20110422.txt",
                "20110423.txt", "20110424.txt", "20110425.txt", "20110426.txt"};

        String[] marks = new String[]{"2011/04/18", "2011/04/19", "2011/04/20.txt", "2011/04/21","2011/04/22",
                "2011/04/23", "2011/04/24", "2011/04/25", "2011/04/26"};

        try {
            File[] outputFiles = new File[outputFileNames.length];
            PrintStream[] printStreams = new PrintStream[outputFileNames.length];
            for (int i = 0; i < outputFileNames.length; i++) {
                outputFiles[i] = new File(file.getParent() + File.separator + outputFileNames[i]);
                System.out.println(outputFiles[i].getAbsoluteFile());

                System.out.println(marks[i]);
                if (!outputFiles[i].exists()) {
                    outputFiles[i].createNewFile();
                }
                printStreams[i] = new PrintStream(new FileOutputStream(outputFiles[i]), true, "utf-8");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                for (int i = 0; i < marks.length; i++) {
                    if (line.contains(marks[i])) {
                        printStreams[i].println(line);
                        break;
                    }
                }
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        System.out.println("cost " + (end.getTime() - begin.getTime()) / 1000 + "s");
    }

    public static void main(String[] args) {

        File file = new File("data/stream-data/one_day_20110425");


        PreDealData preDealData = new PreDealData();
//        preDealData.splitFileFromDayIntoHours();

        preDealData.sortFile(file);

//        Date begin = new Date();
//        System.out.println("begin at :" + begin);
//        File file = new File(Config.getString("data_file"));
//
//        String[] outputFileNames = new String[]{"20110418.txt", "20110419.txt", "20110420.txt", "20110421.txt","20110422.txt",
//                "20110423.txt", "20110424.txt", "20110425.txt", "20110426.txt"};
//
//        String[] marks = new String[]{"2011/04/18", "2011/04/19", "2011/04/20.txt", "2011/04/21","2011/04/22",
//                "2011/04/23", "2011/04/24", "2011/04/25", "2011/04/26"};
//
//        try {
//            File[] outputFiles = new File[outputFileNames.length];
//            PrintStream[] printStreams = new PrintStream[outputFileNames.length];
//            for (int i = 0; i < outputFileNames.length; i++) {
//                outputFiles[i] = new File(file.getParent() + File.separator + outputFileNames[i]);
//                if (!outputFiles[i].exists()) {
//                    outputFiles[i].createNewFile();
//                }
//                printStreams[i] = new PrintStream(new FileOutputStream(outputFiles[i]), true, "utf-8");
//            }
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
//
//            BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1000000);
//
//            DataDealThread[] dataDealThreads = new DataDealThread[4];
//            for (int i = 0; i < dataDealThreads.length; i++) {
//                dataDealThreads[i] = new DataDealThread(blockingQueue, printStreams, marks);
//                new Thread(dataDealThreads[i]).start();
//            }
//
//            DataReadThread dataReadThread = new DataReadThread(blockingQueue, bufferedReader);
//            new Thread(dataReadThread).start();
//        } catch (FileNotFoundException exception) {
//            exception.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Date end = new Date();
//        System.out.println("cost " + (end.getTime() - begin.getTime()) / 1000 + "s");
    }


}
