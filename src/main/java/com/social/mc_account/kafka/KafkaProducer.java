package com.social.mc_account.kafka;

import com.social.mc_account.dto.KafkaAccountDtoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.kafkaMessageTopic}")
    private String kafkaMessageTopic;

    public void sendMessage(KafkaAccountDtoRequest data) {
        kafkaTemplate.send(kafkaMessageTopic, data);
    }
}