package controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import kafka.message.Message;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import models.ApacheLog;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import play.Logger;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;

import com.fasterxml.jackson.databind.JsonNode;

public class SchemaHelper extends Controller {
	
	private static String SCHEMA_URL = "http://localhost:2876/schema-repo/";
	public final static String broker = "10.0.14.52:9092,10.0.14.51:9092";
	public final static String topic = "avroT";
	
	public static JsonNode  getSchemaById(String group, String id) {
	    final Promise<JsonNode> resultPromise = WS.url(SCHEMA_URL+group+"/id/"+ id).get().map(
	            new Function<WSResponse, JsonNode>() {
	                public JsonNode apply(WSResponse response) {	                	
	                	return response.asJson();
	                }
	            }
	    );
	    JsonNode res = resultPromise.get(5,TimeUnit.SECONDS);
	    Logger.debug("Schema repo returned " + res.toString());
	    
	    Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(res.toString());
        ApacheLog al = new ApacheLog();
        al.setDatetime(Long.toString(System.currentTimeMillis()));        
        al.setHost("maxServer");
        al.setLog("something good happened");
        al.setReferer("a valid referer class");
        al.setRequest("a valid request method");
        al.setResponseTime("a valir RT");
        al.setSession("a valid session");
        al.setSize("a valid log size");
        al.setStatus("an error status");
        al.setUser("a valid user -dm");
        al.setUserAgent("guy whi picked it up");
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<ApacheLog> writer = new SpecificDatumWriter<ApacheLog>(ApacheLog.class);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        try {
			writer.write(al, encoder);
			encoder.flush();
		    out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
      
        
        Logger.debug("Event Created");
        
        Properties props = new Properties();
        props.put("bootstrap.servers", broker);

        Producer producer = new KafkaProducer(props);
        Logger.debug("will log to topic");
        try {
			producer.send(new ProducerRecord(topic, out.toByteArray())).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        producer.close();
        
	    return res;
	}
	
	 
	
	
	
}
