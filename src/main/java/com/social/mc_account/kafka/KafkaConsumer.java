package com.social.mc_account.kafka;

import com.social.mc_account.dto.RegistrationDto;
import com.social.mc_account.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AccountServiceImpl service;


    @KafkaListener(topics = "registerTopic", groupId = "${spring.kafka.kafkaMessageGroupId}", containerFactory = "kafkaAccountConcurrentKafkaListenerContainerFactory")
    public void listen(RegistrationDto accountDtoRequest, Acknowledgment ack) {
        log.info("Received data: " + accountDtoRequest);
        service.createAccount(accountDtoRequest);
        ack.acknowledge();
    }
}