package com.aston.homework.service.impl;

import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.dto.UserDtoOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, EventDto> kafkaTemplate;
    private static final String USER_EVENTS_TOPIC = "user_events_topic";

    public KafkaProducerService(KafkaTemplate<String, EventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(EventName eventName, UserDtoOut userDtoOut) {
        logger.info("sending event to kafka");
        EventDto event = new EventDto(eventName, userDtoOut.getEmail());
        CompletableFuture<SendResult<String, EventDto>> future = kafkaTemplate.send(USER_EVENTS_TOPIC, event);
        future.whenComplete((stringEventDtoSendResult, throwable) ->{
                    if (throwable == null) {
                        logger.info("event successfully sent to kafka");
                    } else {
                        logger.info("event not delivered to kafka");
                    }
                });
    }
}
