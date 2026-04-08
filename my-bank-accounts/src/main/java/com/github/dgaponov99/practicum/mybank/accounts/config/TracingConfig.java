package com.github.dgaponov99.practicum.mybank.accounts.config;

import io.micrometer.observation.ObservationPredicate;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

import javax.sql.DataSource;

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

    @Bean
    @ConditionalOnBooleanProperty(name = "management.tracing.enabled", matchIfMissing = true)
    static BeanPostProcessor dataSourcePostProcessor(ObjectProvider<OpenTelemetry> otelProvider) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof DataSource) {
                    return JdbcTelemetry.create(otelProvider.getIfAvailable()).wrap((DataSource) bean);
                }
                return bean;
            }
        };
    }

}