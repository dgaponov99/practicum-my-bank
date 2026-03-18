package com.github.dgaponov99.practicum.mybank.frontend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dgaponov99.practicum.mybank.frontend.client.dto.ErrorDto;
import com.github.dgaponov99.practicum.mybank.frontend.exception.BusinessMultipleException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ObjectMapper objectMapper;

    @Value("${gateway.service.url:http://localhost:8080}")
    private String gatewayServiceUrl;

    @Bean
    public RestClient getewayRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(gatewayServiceUrl)
                .defaultHeaders(addAccessTokenHeader())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, businessErrorHandler())
                .build();
    }


    /**
     * Фильтр для передачи JWT-токена пользователя в Gateway API
     * Извлекает Access Token из OAuth2AuthorizedClient и добавляет его в заголовок Authorization
     * Access Token содержит информацию о пользователе, ролях и правах, необходимую для Resource Server
     */
    private Consumer<HttpHeaders> addAccessTokenHeader() {
        return httpHeaders -> {
            // Достаём текущую аутентификацию из SecurityContext
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            // Проверяем, что пользователь залогинен через OAuth2 (OAuth2AuthenticationToken)
            if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                // Из токена берём clientRegistrationId (имя клиента в настройках security)
                // и имя пользователя (principal), чтобы найти его authorized client
                var authorizedClient = authorizedClientService.loadAuthorizedClient(
                        oauth2Token.getAuthorizedClientRegistrationId(),
                        oauth2Token.getName());

                // Если для этого пользователя и клиента нашли authorized client —
                // пробуем достать из него access token (JWT)
                var accessToken = authorizedClient != null ? authorizedClient.getAccessToken() : null;

                // Если access token есть, добавляем его в заголовок Authorization
                if (accessToken != null) {
                    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                }
            }
        };
    }

    private RestClient.ResponseSpec.ErrorHandler businessErrorHandler() {
        return (req, res) -> {
            var errorDTO = objectMapper.readValue(res.getBody(), ErrorDto.class);
            throw new BusinessMultipleException(errorDTO.errors());
        };
    }

}
