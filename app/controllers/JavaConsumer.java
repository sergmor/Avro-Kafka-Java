package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import models.ApacheLog;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import play.Logger;


public class JavaConsumer extends Thread{

	public static void main(String[] args) {
		JavaConsumer jc = new JavaConsumer("dAvroTest1");
		jc.run();
	}
	
	private final ConsumerConnector consumer;
	private final String topic;
	public boolean consume = true;

	public JavaConsumer(String topic)
	{
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
				createConsumerConfig());
		this.topic = topic;
	}

	private static ConsumerConfig createConsumerConfig()
	{
		Properties props = new Properties();
		props.put("zookeeper.connect", "10.0.14.52:2181");
		props.put("group.id", "avro.test");
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");

		return new ConsumerConfig(props);

	}

	public void run() {
		Logger.debug("in RUN");
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream =  consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		
		
		DatumReader<ApacheLog> reader = new SpecificDatumReader<ApacheLog>(ApacheLog.getClassSchema());
		
		
		while(it.hasNext() && consume){
			Decoder decoder = DecoderFactory.get().binaryDecoder(it.next().message(), null);
			try {
				ApacheLog log = reader.read(null, decoder);
				Logger.debug(log.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			
	}
}
