package com.kafka.lib.config;

import com.maersk.shared.kafka.configuration.KafkaSenderBaseConfiguration;
import com.telikos.booking.Booking;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka producer class to extend the properties from library class
 */
@Configuration
public class KafkaProducerConfig extends KafkaSenderBaseConfiguration<String, Booking> {
}