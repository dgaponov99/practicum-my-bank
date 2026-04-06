package com.github.dgaponov99.practicum.mybank.notifications.config;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Value("${kafka.backoff.interval:1000}")
    private long backOffInterval;
    @Value("${kafka.backoff.maxAttempts:3}")
    private int backOffMaxAttempts;

    @Bean
    public CommonErrorHandler errorHandler() {
        var defaultHandler = new DefaultErrorHandler(new FixedBackOff(backOffInterval, backOffMaxAttempts));
        defaultHandler.addNotRetryableExceptions(ConstraintViolationException.class);
        return defaultHandler;
    }

}
