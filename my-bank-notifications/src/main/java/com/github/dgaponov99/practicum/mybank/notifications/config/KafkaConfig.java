package com.github.dgaponov99.practicum.mybank.notifications.config;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.lang.reflect.UndeclaredThrowableException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final MeterRegistry meterRegistry;

    @Value("${kafka.backoff.interval:1000}")
    private long backOffInterval;
    @Value("${kafka.backoff.maxAttempts:3}")
    private int backOffMaxAttempts;

    @Bean
    public CommonErrorHandler errorHandler() {
        var recoverer = (ConsumerRecordRecoverer) (record, e) -> {
            if (record.topic().equals("notifications")) {
                meterRegistry.counter("notification_failed",
                                "username", (String) record.key())
                        .increment();
            }
            log.error("Error occurred while processing record {}", record, e);
        };
        var defaultHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(backOffInterval, backOffMaxAttempts));
        defaultHandler.addNotRetryableExceptions(ConstraintViolationException.class);
        return defaultHandler;
    }

}
