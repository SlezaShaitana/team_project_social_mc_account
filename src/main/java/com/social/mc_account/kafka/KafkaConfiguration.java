package com.social.mc_account.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.mc_account.model.KafkaAccount;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.kafkaMessageGroupId}")
    private String kafkaMessageGroupId;

    @Bean
    public NewTopic updateTopic(){
        return new NewTopic("updateTopic", 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, KafkaAccount> kafkaAccountProducerFactory(ObjectMapper objectMapper){
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, KafkaAccount> kafkaTemplate(ProducerFactory<String, KafkaAccount> kafkaAccountProducerFactory){
        return new KafkaTemplate<>(kafkaAccountProducerFactory);
    }

    @Bean
    public ConsumerFactory<String, KafkaAccount> kafkaAccountConsumerFactory(ObjectMapper objectMapper){
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaMessageGroupId);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(objectMapper));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaAccount> kafkaAccountConcurrentKafkaListenerContainerFactory(
            ConsumerFactory<String, KafkaAccount> kafkaAccountConsumerFactory
    ){
        ConcurrentKafkaListenerContainerFactory<String, KafkaAccount> factory= new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaAccountConsumerFactory);

        return factory;
    }
}
