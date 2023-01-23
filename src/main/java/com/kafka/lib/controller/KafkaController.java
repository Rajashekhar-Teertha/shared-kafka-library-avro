package com.kafka.lib.controller;



import com.telikos.booking.Booking;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.util.retry.Retry;

/**
 * Controller to publish the message to kafka topic
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {


    @Autowired
    private final KafkaSender<String, Booking> kafkaSender;

    String topicName="test-topic-1";

    /**
     * @param booking
     */
    @PostMapping ("/publish")
    public void publish(@RequestBody Booking booking){
         kafkaSender.send(publishMsg(booking))
            .doOnNext(result -> log.info("Sender result for booking {}: {}", booking.getBookingId(), result))
            .doOnError(ex -> log.warn("Failed producing record to Kafka, booking ID: {}", booking.getBookingId(), ex))
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
            .doOnError(ex -> log.error("Failed producing record to Kafka after all retries, booking ID: {}", booking.getBookingId(), ex))
             .subscribe();
        log.info("message published {}",booking);
    }

    /**
     * @param booking
     * @return
     * @param <T>
     */
    private <T> Mono<SenderRecord<String, Booking, T>> publishMsg(Booking booking) {
        long timestamp=System.currentTimeMillis();
        return Mono.fromSupplier(() -> SenderRecord.create(new ProducerRecord<>(topicName, booking.getBookingId(),booking), null));
    }

}
