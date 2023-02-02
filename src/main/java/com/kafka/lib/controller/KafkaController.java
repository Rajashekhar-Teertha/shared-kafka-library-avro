package com.kafka.lib.controller;


import com.telikos.booking.Booking;
import java.time.Duration;
import java.util.HashMap;
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
    String topicName1="test-topic-1";
    String topicName2="test-topic-2";
    Booking booking1= new Booking("3","product3");
    Booking booking2= new Booking("4","product4");

    /**
     * @param booking
     */
    @PostMapping ("/publish")
    public void publish(@RequestBody Booking booking){
         kafkaSender.send(publishMsg(topicName,booking))
            .doOnNext(result -> log.info("Sender result for booking {}: {}", booking.getBookingId(), result))
            .doOnError(ex -> log.warn("Failed producing record to Kafka, booking ID: {}", booking.getBookingId(), ex))
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
            .doOnError(ex -> log.error("Failed producing record to Kafka after all retries, booking ID: {}", booking.getBookingId(), ex))
             .subscribe();
    }

    @PostMapping ("/publish/multipleTopicWithMultiplePayload")
    public void publishToMultipleTopicsWithMultiplePayload(){
        HashMap<String, Booking> prices = new HashMap<>();
        prices.put(topicName1,booking1);
        prices.put(topicName2, booking2);
//        prices.put("topic3", "payload3");

        prices.forEach((key, value) -> {
//            System.out.println(key + "=" + value + " ");
            kafkaSender.send(publishMsg(key,value))
                .doOnNext(result -> log.info("Sender result for booking {}: {}", value.getBookingId(), result))
                .doOnError(ex -> log.warn("Failed producing record to Kafka, booking ID: {}", value.getBookingId(), ex))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
                .doOnError(ex -> log.error("Failed producing record to Kafka after all retries, booking ID: {}", value.getBookingId(), ex))
                .subscribe();
        });
    }
    /**
     * @param booking
     * @return
     * @param <T>
     */
    private <T> Mono<SenderRecord<String, Booking, T>> publishMsg(String topicName, Booking booking) {
        return Mono.fromSupplier(() -> SenderRecord.create(new ProducerRecord<>(topicName, booking.getBookingId(),booking), null));
    }

}
