package com.github.dgaponov99.practicum.mybank.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Блок настройки авторизации запросов
                .authorizeHttpRequests(auth -> auth
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().hasRole("USER")
                )
                // Включаем аутентификацию через OAuth2 Login
                // Неавторизованный пользователь будет перенаправлен на страницу логина провайдера
                .oauth2Login(Customizer.withDefaults())
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

}
