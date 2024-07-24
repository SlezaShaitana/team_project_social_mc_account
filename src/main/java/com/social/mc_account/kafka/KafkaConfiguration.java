package com.social.mc_account.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.hibernate.query.criteria.JpaConflictUpdateAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic updateTopic(){
        return new NewTopic("updateTopic", 1, (short) 1);
    }
}
