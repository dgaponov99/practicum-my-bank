package com.github.dgaponov99.practicum.mybank.cash.integration.contracts;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@TestConfiguration
@Profile("contract-test")
public class ContractTestClientConfig {

    @Bean
    @Primary
    public ClientHttpRequestInterceptor accessTokenRequestInterceptor() {
        return (httpRequest, body, execution) -> {
            httpRequest.getHeaders().setBearerAuth("test-token");

            return execution.execute(httpRequest, body);
        };
    }

}
