package com.social.mc_account.kafka;

import com.social.mc_account.model.KafkaAccount;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, HashMap<String, Object>> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, HashMap<String, Object>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(HashMap<String, Object> data){
        kafkaTemplate.send("${app.kafka.kafkaMessageTopic}", data);
    }
}
