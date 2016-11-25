package com.bupt.poirot.data.dataStream;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerDemo {

	public static void main(String[] args) {
		System.out.println("begin consumer");
		connectionKafka();
		System.out.println("finish consumer");
	}

	@SuppressWarnings("resource")
	public static void connectionKafka() {

		Properties props = new Properties();
		props.put("bootstrap.servers", "10.109.253.74:9092");
//		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "testConsumer");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "10000");
		props.put("max.poll.records", "3");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("traffic-data"));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1);
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			for (ConsumerRecord<String, String> record : records) {
				System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
				System.out.println();
			}
		}
	}
}
