package com.social.mc_account.kafka;

import com.social.mc_account.dto.RegistrationDto;
import com.social.mc_account.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AccountServiceImpl service;


    @KafkaListener(topics = "registerTopic", groupId = "${spring.kafka.kafkaMessageGroupId}", containerFactory = "kafkaAccountConcurrentKafkaListenerContainerFactory")
    public void listen(RegistrationDto accountDtoRequest) {
        try {
            log.info("Received data: " + accountDtoRequest);
            service.createAccount(accountDtoRequest);
        } catch (Exception e) {
            log.error("Error processing Kafka message", e);
            throw e;
        }
    }
}
