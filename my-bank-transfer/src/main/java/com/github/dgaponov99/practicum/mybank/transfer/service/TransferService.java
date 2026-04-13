package com.github.dgaponov99.practicum.mybank.transfer.service;

import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferAccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import com.github.dgaponov99.practicum.mybank.transfer.gateway.AccountsGateway;
import com.github.dgaponov99.practicum.mybank.transfer.gateway.NotificationsGateway;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountsGateway accountsGateway;
    private final NotificationsGateway notificationsGateway;
    private final MeterRegistry meterRegistry;

    public List<TransferAccountDto> getTransferAccounts(String username) {
        log.debug("Запрос списка пользователей для перевода от пользователя {}", username);
        return accountsGateway.getAllAccounts().stream()
                .filter(account -> !username.equals(account.username()))
                .map(account -> new TransferAccountDto(account.username(), account.name()))
                .toList();
    }

    public void transfer(TransferDto transferDto) {
        try {
            log.debug("Запрос перевода от пользователя {} пользователю {}", transferDto.fromUsername(), transferDto.toUsername());
            accountsGateway.transfer(transferDto);
            notificationsGateway.sendNotification(transferDto.fromUsername(), "Успешно выполнен исходящий перевод на сумму %d руб.".formatted(transferDto.amount()));
            notificationsGateway.sendNotification(transferDto.toUsername(), "Успешно выполнен входящий перевод на сумму %d руб.".formatted(transferDto.amount()));
            meterRegistry.counter("transfer").increment();
            log.info("Запрос перевода от пользователя {} пользователю {} прошел успешно", transferDto.fromUsername(), transferDto.toUsername());
        } catch (RuntimeException e) {
            log.warn("Запрос перевода от пользователя {} пользователю {} завершился с ошибкой", transferDto.fromUsername(), transferDto.toUsername());
            meterRegistry.counter("transfer_failed").increment();
            throw e;
        }
    }

}
