package com.github.dgaponov99.practicum.mybank.cash.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dgaponov99.practicum.mybank.cash.dto.ErrorDto;
import com.github.dgaponov99.practicum.mybank.cash.exception.ExternalMultipleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * Настраиваем OAuth2AuthorizedClientManager —
     * компонент, который умеет:
     * - создавать сервисный access token по Client Credentials Flow,
     * - обновлять его при истечении,
     * - хранить его в OAuth2AuthorizedClientService.
     * <p>
     * Здесь мы указываем, что transfer-service использует Client Credentials Flow,
     * поэтому включаем только clientCredentials().
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        // Провайдер, который знает, как получать client_credentials токены
        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        // Manager, который связывает client registrations и storage токенов
        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService);

        // Говорим менеджеру: для используем client_credentials
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }

    @Bean
    public RestClient serviceRestClient(RestClient.Builder builder,
                                        OAuth2AuthorizedClientManager authorizedClientManager) {
        return builder
                .requestInterceptor(addAccessTokenHeader(authorizedClientManager))
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, externalErrorHandler())
                .build();
    }

    private ClientHttpRequestInterceptor addAccessTokenHeader(OAuth2AuthorizedClientManager authorizedClientManager) {
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

    private RestClient.ResponseSpec.ErrorHandler externalErrorHandler() {
        return (req, res) -> {
            try (var bodyIs = res.getBody()) {
                var errorDto = objectMapper.readValue(bodyIs, ErrorDto.class);
                throw new ExternalMultipleException(errorDto.errors());
            }
        };
    }


}
