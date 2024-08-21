package com.social.mc_account.kafka;

import com.social.mc_account.dto.AccountMeDTO;
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

    @KafkaListener(topics = "registerTopic", groupId = "${spring.kafka.kafkaMessageGroupId}", containerFactory = "kafkaMessageConcurrentKafkaListenerContainerFactory")
    public void listen(RegistrationDto accountDtoRequest) {
        log.info("Received data: " + accountDtoRequest);
        try {
            AccountMeDTO createdAccount = service.createAccount(accountDtoRequest);
            log.info("Account created successfully: " + createdAccount);
        } catch (Exception e) {
            log.error("Failed to create account from Kafka message", e);
        }
    }
}