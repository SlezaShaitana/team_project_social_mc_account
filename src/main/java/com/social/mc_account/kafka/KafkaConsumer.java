package com.social.mc_account.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

@Service
@Slf4j
public class KafkaConsumer {
    @KafkaListener(topics = "registerTopic", groupId = "account_consumer")
    public void listen(HashMap<String, Object> data){
        System.out.println("Received data: " + data);
        log.info("Received data: " + data);
    }
}
