package com.social.mc_account.kafka;

import com.social.mc_account.dto.KafkaAccountDtoRequest;
import com.social.mc_account.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AccountServiceImpl service;

    @KafkaListener(topics = "registerTopic", groupId = "${spring.kafka.kafkaMessageGroupId}")
    public void listen(KafkaAccountDtoRequest kafkaAccountDtoRequest) {
        log.info("Received data: " + kafkaAccountDtoRequest);
        service.createAccount(kafkaAccountDtoRequest);
    }
}