package com.redhat.examples;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {
    private static int timeout = 60000;
    private static Logger LOG = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) throws InterruptedException, TimeoutException {
        /*
         * Configure the logger
         */
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        /*
         * Consumer configuration
         */
        Map<String, Object> props = new HashMap();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, System.getenv("KAFKA_GROUP_ID"));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        /*
         * Consume messages
         */
        consumer.subscribe(Pattern.compile(System.getenv("TOPIC_PATTERN")));

        int messageNo = 0;

        while (true)
        {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeout));

            if(records.isEmpty()) {
                LOG.info("No message in topic for {} seconds. Finishing ...", timeout/1000);
                break;
            }

            for (ConsumerRecord<String, String> record : records)
            {
                LOG.info("Received message no. {}: {} / {} (from topic {}, partition {}, offset {})", ++messageNo, record.key(), record.value(), record.topic(), record.partition(), record.offset());
            }

            consumer.commitSync();
        }

        consumer.close();

    }
}
