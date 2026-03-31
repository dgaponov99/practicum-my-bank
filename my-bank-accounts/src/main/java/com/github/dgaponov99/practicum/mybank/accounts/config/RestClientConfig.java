package com.github.dgaponov99.practicum.mybank.accounts.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

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
                .build();
    }

    @Bean
    public RestClient keycloakRestClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        return RestClient.builder()
                .requestInterceptor(addAccessTokenHeader(authorizedClientManager))
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


}
