package com.kafka.lib.config;

import com.maersk.shared.kafka.configuration.KafkaReceiverBaseConfiguration;
import com.telikos.booking.Booking;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka consumer class to extend the properties from library class
 */
@Configuration
public class KafkaConsumerConfig extends KafkaReceiverBaseConfiguration<String, Booking> {
}
