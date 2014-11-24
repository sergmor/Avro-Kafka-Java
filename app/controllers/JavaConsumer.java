package controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import models.ApacheLog;

import org.apache.avro.Schema;
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
		
		Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(SchemaHelper.getSchemaById("logMessage", "2").toString());
		DatumReader<ApacheLog> reader = new SpecificDatumReader<ApacheLog>(schema);
		//DatumReader<ApacheLog> reader = new SpecificDatumReader<ApacheLog>(ApacheLog.getClassSchema());
		
		while(it.hasNext() && consume){
			MessageAndMetadata<byte[],byte[]> mes = it.next();
			//if(mes.valueDecoder() != null) Logger.debug("decoder " + mes.valueDecoder());
			//Logger.debug(mes.valueDecoder().toString());
			//byte[] key = mes.key();
			//Logger.debug("key " + key + " as string " + new String(key));
			byte[] bytes = mes.message();
			byte[] byteCopy = Arrays.copyOf(bytes, bytes.length);
			Logger.debug("got bytes " + byteCopy.length + "original "+ bytes.length+ " with initial byte " + new String(new byte[] {byteCopy[0]}) + " and " + byteCopy[1]);
			Logger.debug("got bytes " + new String(byteCopy));
			Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
			try {
				ApacheLog log = reader.read(null, decoder);
				Logger.debug("consumer got " + log.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			
	}
}
