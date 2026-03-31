package com.github.dgaponov99.practicum.mybank.accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/actuator/**").permitAll();
            auth.anyRequest().authenticated();
        });

        http.csrf(CsrfConfigurer::disable);

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractRealmRoles);
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        var authorities = new ArrayList<GrantedAuthority>();

        // собираем роли
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            var rolesObj = realmAccess.get("roles");

            // Если провайдер вдруг вернёт не коллекцию (например, строку или null),
            // мы не упадём с ClassCastException,
            // а просто считаем, что ролей нет.
            if (rolesObj instanceof Collection<?> rawRoles) {
                rawRoles.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .forEach(authorities::add);
            }
        }

        // собираем все роли клиентов как права доступа
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().forEach(client -> {
                if (client instanceof Map<?, ?> clientData) {
                    var roles = clientData.get("roles");
                    if (roles instanceof Collection<?> rolesData) {
                        rolesData
                                .stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .map(SimpleGrantedAuthority::new)
                                .forEach(authorities::add);
                    }
                }
            });
        }

        return authorities;
    }

}
