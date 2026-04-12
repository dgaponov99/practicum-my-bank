package com.github.dgaponov99.practicum.mybank.cash.service;

import com.github.dgaponov99.practicum.mybank.cash.gateway.AccountsGateway;
import com.github.dgaponov99.practicum.mybank.cash.gateway.NotificationsGateway;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashService {

    private final AccountsGateway accountsGateway;
    private final NotificationsGateway notificationsGateway;
    private final MeterRegistry meterRegistry;

    public void withdraw(String username, int amount) {
        try {
            accountsGateway.debit(username, amount);
            notificationsGateway.sendNotification(username, "Успешно снята сумма %d руб.".formatted(amount));
            meterRegistry.counter("cash_withdraw").increment();
        } catch (RuntimeException e) {
            meterRegistry.counter("cash_withdraw_failed", "username", username).increment();
            throw e;
        }
    }

    public void deposit(String username, int amount) {
        try {
            accountsGateway.credit(username, amount);
            notificationsGateway.sendNotification(username, "Успешно внесена сумма %d руб.".formatted(amount));
            meterRegistry.counter("cash_deposit").increment();
        } catch (RuntimeException e) {
            meterRegistry.counter("cash_deposit_failed", "username", username).increment();
            throw e;
        }
    }

}
