package com.github.dgaponov99.practicum.mybank.frontend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dgaponov99.practicum.mybank.frontend.client.dto.ErrorDto;
import com.github.dgaponov99.practicum.mybank.frontend.exception.BusinessMultipleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final ObjectMapper objectMapper;

    @Value("${gateway.service.url:http://localhost:8080}")
    private String gatewayServiceUrl;

    @Bean
    public RestClient getewayRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(gatewayServiceUrl)
                .requestInterceptor(addAccessTokenHeader())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, businessErrorHandler())
                .build();
    }


    /**
     * Фильтр для передачи JWT-токена пользователя в Gateway API
     * Извлекает Access Token из OAuth2AuthorizedClient и добавляет его в заголовок Authorization
     * Access Token содержит информацию о пользователе, ролях и правах, необходимую для Resource Server
     */
    private ClientHttpRequestInterceptor addAccessTokenHeader() {
        return (httpRequest, body, execution) -> {
            // Достаём текущую аутентификацию из SecurityContext
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            // Проверяем, что пользователь залогинен через OAuth2 (OAuth2AuthenticationToken)
            if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                var authorizeRequest = OAuth2AuthorizeRequest
                        .withClientRegistrationId(oauth2Token.getAuthorizedClientRegistrationId())
                        .principal(authentication)
                        .build();

                var authorizedClient = authorizedClientManager.authorize(authorizeRequest);

                if (authorizedClient != null) {
                    var accessToken = authorizedClient.getAccessToken();
                    httpRequest.getHeaders().setBearerAuth(accessToken.getTokenValue());
                }
            }
            return execution.execute(httpRequest, body);
        };
    }

    private RestClient.ResponseSpec.ErrorHandler businessErrorHandler() {
        return (req, res) -> {
            var body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
            var errorDTO = objectMapper.readValue(body, ErrorDto.class);
            throw new BusinessMultipleException(errorDTO.errors());
        };
    }

}
