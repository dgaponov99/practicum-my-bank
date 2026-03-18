package com.github.dgaponov99.practicum.mybank.accounts.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

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
                .defaultHeaders(addAccessTokenHeader(authorizedClientManager))
                .build();
    }

    private Consumer<HttpHeaders> addAccessTokenHeader(OAuth2AuthorizedClientManager authorizedClientManager) {
        return httpHeaders -> {
            var fakePrincipal = new UsernamePasswordAuthenticationToken("service", "N/A");
            var authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("accounts-service")
                    .principal(fakePrincipal)
                    .build();

            var client = authorizedClientManager.authorize(authorizeRequest);
            httpHeaders.setBearerAuth(client.getAccessToken().getTokenValue());
        };
    }


}
