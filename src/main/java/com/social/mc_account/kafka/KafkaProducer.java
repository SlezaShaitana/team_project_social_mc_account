package com.social.mc_account.kafka;

import com.social.mc_account.dto.NotificationDTO;
import com.social.mc_account.dto.RegistrationDto;
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

    public void sendMessageForAuth(RegistrationDto data) {
        kafkaTemplate.send(kafkaMessageTopicForAccount, data);
    }

    public void sendMessageForNotification(NotificationDTO data) {
        kafkaTemplate.send(kafkaMessageTopicForNotification, data);
    }
}