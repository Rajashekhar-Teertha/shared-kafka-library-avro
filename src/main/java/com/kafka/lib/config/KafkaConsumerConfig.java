package com.kafka.lib.config;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.maersk.shared.kafka.configuration.KafkaReceiverBaseConfiguration;
import com.telikos.booking.Booking;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.Map;

/**
 * Kafka consumer class to extend the properties from library class
 */
@Configuration
public class KafkaConsumerConfig extends KafkaReceiverBaseConfiguration<String, Booking> {

    @Override
    protected Map<String, Object> kafkaConsumerProperties() {
        Map<String, Object> properties = super.kafkaConsumerProperties();
        //properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DecompressionConfig.class);
       // properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class);
        return properties;
            }
    }
