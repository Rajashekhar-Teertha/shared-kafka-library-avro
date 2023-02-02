package com.kafka.lib.config;

import com.maersk.shared.kafka.configuration.KafkaSenderBaseConfiguration;
import com.telikos.booking.Booking;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Kafka producer class to extend the properties from library class
 */
@Configuration
public class KafkaProducerConfig extends KafkaSenderBaseConfiguration<String, Booking> {

    @Override
    protected Map<String, Object> kafkaProducerProperties() {
        Map<String, Object> properties = super.kafkaProducerProperties();
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CompressionConfig.class);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 8000000);
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 8000000);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

}