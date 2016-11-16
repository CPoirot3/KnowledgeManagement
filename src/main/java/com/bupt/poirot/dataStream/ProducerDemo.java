package com.bupt.poirot.dataStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerDemo {

    public static void main(String[] args) {
        System.out.println("begin produce");
        connectionKafka();
        System.out.println("finish produce");
    }

    public static void connectionKafka() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.109.253.74:9092");
//        props.put("bootstrap.servers", "localhost:9092");             
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
        try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("traffic_data")));
			String message = "";
			int messageNumber = 1;
			while ((message = reader.readLine()) != null) {
				Thread.sleep(1000);
//				System.out.println(message);
				producer.send(new ProducerRecord<String, String>("traffic-data", Integer.toString(messageNumber++), message));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        producer.close();
    }

}