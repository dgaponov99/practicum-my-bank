package com.github.dgaponov99.practicum.mybank.cash.gateway;

import com.github.dgaponov99.practicum.mybank.cash.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.cash.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.cash.exception.ExternalMultipleException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountsGateway {

    private final AccountServiceClient accountServiceClient;
    private final CircuitBreakerFactory<?, ?> breakerFactory;

    public AccountDto credit(String username, int amount) {
        return getCircuitBreaker()
                .run(() -> accountServiceClient.credit(username, amount),
                        e -> {
                            throw new ExternalMultipleException("Сервис временно не доступен");
                        });
    }

    public AccountDto debit(String username, int amount) {
        return getCircuitBreaker()
                .run(() -> accountServiceClient.debit(username, amount),
                        e -> {
                            throw new ExternalMultipleException("Сервис временно не доступен");
                        });
    }

    private CircuitBreaker getCircuitBreaker() {
        return breakerFactory.create("accounts-service");
    }

}
