package com.github.dgaponov99.practicum.mybank.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Блок настройки авторизации запросов
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                // Включаем аутентификацию через OAuth2 Login
                // Неавторизованный пользователь будет перенаправлен на страницу логина провайдера
                .oauth2Login(oauth -> oauth.userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService())))
                // Блок настройки выхода из системы
                .logout(logout -> logout
                        // После успешного выхода перенаправляем пользователя на главную страницу
                        .logoutSuccessUrl("/")
                        // Разрешаем всем вызывать эндпоинт выхода
                        .permitAll()
                );

        // Строим и возвращаем цепочку фильтров безопасности
        return http.build();
    }

    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        var delegate = new OidcUserService();
        return userRequest -> {

            OidcUser oidcUser = delegate.loadUser(userRequest);

            return new DefaultOidcUser(
                    oidcUser.getAuthorities(),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    "preferred_username"   // ← теперь authentication.getName() будет username
            );
        };
    }

}
