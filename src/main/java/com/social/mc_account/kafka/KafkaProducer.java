package com.social.mc_account.kafka;

import com.social.mc_account.dto.AccountDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void sendMessage(AccountDataDTO data){
        kafkaTemplate.send("${spring.kafka.kafkaMessageTopic}", data);
    }
}
