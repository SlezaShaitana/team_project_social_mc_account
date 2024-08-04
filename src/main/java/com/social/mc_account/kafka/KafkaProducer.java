package com.social.mc_account.kafka;

import com.social.mc_account.dto.BirthdayDTO;
import com.social.mc_account.dto.AccountDtoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.kafkaMessageTopicForAccount}")
    private String kafkaMessageTopicForAccount;

    @Value("${spring.kafka.kafkaMessageTopicForNotification}")
    private String kafkaMessageTopicForNotification;

    public void sendMessageForAuth(AccountDtoRequest data) {
        kafkaTemplate.send(kafkaMessageTopicForAccount, data);
    }

    public void sendMessageForNotification(BirthdayDTO data) {
        kafkaTemplate.send(kafkaMessageTopicForNotification, data);
    }
}