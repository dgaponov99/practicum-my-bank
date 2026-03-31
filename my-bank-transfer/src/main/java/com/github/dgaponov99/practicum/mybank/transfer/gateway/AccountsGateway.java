package com.github.dgaponov99.practicum.mybank.transfer.gateway;

import com.github.dgaponov99.practicum.mybank.transfer.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.transfer.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import com.github.dgaponov99.practicum.mybank.transfer.exception.ExternalMultipleException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountsGateway {

    private final AccountServiceClient accountServiceClient;
    private final CircuitBreakerFactory<?, ?> breakerFactory;

    public List<AccountDto> getAllAccounts() {
        return getCircuitBreaker()
                .run(accountServiceClient::getAllAccounts, e -> Collections.emptyList());
    }

    public void transfer(TransferDto transferDto) {
        getCircuitBreaker()
                .run(() -> {
                            accountServiceClient.transfer(transferDto);
                            return null;
                        },
                        e -> {
                            throw new ExternalMultipleException("Сервис временно не доступен");
                        });
    }

    private CircuitBreaker getCircuitBreaker() {
        return breakerFactory.create("accounts-service");
    }

}
