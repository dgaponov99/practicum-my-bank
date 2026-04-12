package com.github.dgaponov99.practicum.mybank.cash.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dgaponov99.practicum.mybank.cash.dto.ErrorDto;
import com.github.dgaponov99.practicum.mybank.cash.exception.ExternalMultipleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientInterceptorConfig {

    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    @ConditionalOnProperty(
            name = "app.oauth.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public ClientHttpRequestInterceptor accessTokenRequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return (httpRequest, body, execution) -> {
            var fakePrincipal = new UsernamePasswordAuthenticationToken("service", "N/A");
            var authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(serviceName)
                    .principal(fakePrincipal)
                    .build();

            var client = authorizedClientManager.authorize(authorizeRequest);
            httpRequest.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());

            return execution.execute(httpRequest, body);
        };
    }

    @Bean
    public RestClient.ResponseSpec.ErrorHandler externalErrorHandler() {
        return (req, res) -> {
            try (var bodyIS = res.getBody()) {
                var errorDto = objectMapper.readValue(bodyIS, ErrorDto.class);
                throw new ExternalMultipleException(errorDto.errors());
            }
        };
    }

}
