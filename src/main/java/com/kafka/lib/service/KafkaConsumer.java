package com.kafka.lib.service;

import com.telikos.booking.Booking;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class KafkaConsumer {

  @Autowired
  private KafkaReceiver<String, Booking> kafkaReceiver;

  /**
   * Kafka listener to consume the messages
   * @return
   */
  @EventListener(ApplicationStartedEvent.class)
  public Disposable startKafkaConsumer() {
    log.info("inside consumer class ...");
    return kafkaReceiver
        .receive()
        .doOnNext(booking -> log.info("consumed successfully test-topic-1 key - {} value - {} ",booking.key(), booking.value()))
        .doOnError(error -> log.error("Error receiving booking event, will retry", error))
        .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofMinutes(1)))
        .subscribe(receiverRecord -> receiverRecord.receiverOffset().acknowledge());
  }
}