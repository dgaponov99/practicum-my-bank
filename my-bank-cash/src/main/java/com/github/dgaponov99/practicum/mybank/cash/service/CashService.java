package com.github.dgaponov99.practicum.mybank.cash.service;

import com.github.dgaponov99.practicum.mybank.cash.gateway.AccountsGateway;
import com.github.dgaponov99.practicum.mybank.cash.gateway.NotificationsGateway;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashService {

    private final AccountsGateway accountsGateway;
    private final NotificationsGateway notificationsGateway;
    private final MeterRegistry meterRegistry;

    public void withdraw(String username, int amount) {
        try {
            log.debug("Снятие наличных со счета {} ", username);
            accountsGateway.debit(username, amount);
            notificationsGateway.sendNotification(username, "Успешно снята сумма %d руб.".formatted(amount));
            meterRegistry.counter("cash_withdraw").increment();
            log.info("Снятие наличных со счета {} успешно", username);
        } catch (RuntimeException e) {
            log.warn("Снятие наличных со счета {} неуспешно", username);
            meterRegistry.counter("cash_withdraw_failed").increment();
            throw e;
        }
    }

    public void deposit(String username, int amount) {
        try {
            log.debug("Пополнение наличными счета {} ", username);
            accountsGateway.credit(username, amount);
            notificationsGateway.sendNotification(username, "Успешно внесена сумма %d руб.".formatted(amount));
            meterRegistry.counter("cash_deposit").increment();
            log.info("Пополнение наличными счета успешно {} ", username);
        } catch (RuntimeException e) {
            log.warn("Пополнение наличными счета {} неуспешно", username);
            meterRegistry.counter("cash_deposit_failed").increment();
            throw e;
        }
    }

}
