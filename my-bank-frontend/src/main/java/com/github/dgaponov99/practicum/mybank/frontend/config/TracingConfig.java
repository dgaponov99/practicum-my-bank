package com.github.dgaponov99.practicum.mybank.frontend.config;

import io.micrometer.observation.ObservationPredicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

@Slf4j
@Configuration
public class TracingConfig {

    @Bean
    ObservationPredicate skipActuator() {
        return (name, context) -> {
            if (context instanceof ServerRequestObservationContext serverContext) {
                var request = serverContext.getCarrier();
                if (request != null) {
                    String uri = request.getRequestURI();
                    return uri == null || !uri.startsWith("/actuator");
                }
            }
            return true;
        };
    }

}