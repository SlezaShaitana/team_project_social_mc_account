package com.social.mc_account.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Bean
    public Counter failedAuthCounter(MeterRegistry meterRegistry) {
        return Counter.builder("failed_auth_count")
                .description("Количество неудачных авторизаций")
                .register(meterRegistry);
    }
}