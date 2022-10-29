import com.sun.corba.se.impl.util.Utility.printStackTrace
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer, OffsetAndMetadata, OffsetCommitCallback}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.TopicPartition

import java.util
import java.util.Properties
import scala.jdk.CollectionConverters.{IterableHasAsJava, IterableHasAsScala}

/*
Assignment:
1. Create a java/scala project with kafka-clients library as a dependency
2. Write a method to consume events from a kafka topic
3. Write a method to produce events into a kafka topic
4. Understand what happens to offsets when an exception occurs.
5. How do we commit offsets after a message is consumed from a topic? Understand what are the pros and cons of auto commit offset.
6. How do we check the lag for a consumer for a topic
7. How do we parallelize reads from a topic?
 */

object KafkaAssignment {

  val topic = "text_topic1"

  def kafkaConsumer = {
    val props:Properties = new Properties()
    props.put("group.id", "test")
    props.put("bootstrap.servers","localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    props.put("enable.auto.commit", "false")
    props.put("auto.commit.interval.ms", "1000")
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(List(topic).asJavaCollection)
    println(s"consuming $topic")

    while (true) {
      val records: ConsumerRecords[String, String] =  consumer.poll(10)
      //    val records = consumer.poll(10)
      for (record <- records.asScala) {
        println("record ---" + record)
        println("before commitAsync() call", consumer);
        consumer.commitAsync(new OffsetCommitCallback() {
          override def onComplete(map: util.Map[TopicPartition, OffsetAndMetadata], e: Exception): Unit = {
            println("callbackFun "+ record.offset())
          }
        });
        println("after commitAsync() call", consumer);
      }
    }
    println("after consumer loop", consumer);
  }

  def kafkaProducer = {
    val props:Properties = new Properties()
    props.put("bootstrap.servers","localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    try {
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 1"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 2"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 3"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 4"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 5"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 6"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 7"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 8"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 9"))
      producer.send(new ProducerRecord[String, String](topic, "1", "Message 10"))
      producer.close()
    }finally {
      producer.close()
    }

  }

  def main(args: Array[String]) {
//    KafkaAssignment.kafkaProducer
    KafkaAssignment.kafkaConsumer
  }

}
