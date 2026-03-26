package com.github.dgaponov99.practicum.mybank.cash.contracts.cash;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("contract-test")
public class JwtTestConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> {
            var now = Instant.now();
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .subject("contract-test")
                    .claim("preferred_username", "contract-test")
                    .claim("realm_access", Map.of(
                            "roles", List.of("SERVICE")
                    ))
                    .claim("resource_access", Map.of(
                            "test-service", Map.of(
                                    "roles", List.of("cash.write")
                            )
                    ))
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(3600))
                    .build();
        };
    }

}
