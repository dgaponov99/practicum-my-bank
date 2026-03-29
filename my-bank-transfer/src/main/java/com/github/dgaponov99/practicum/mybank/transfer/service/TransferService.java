package com.github.dgaponov99.practicum.mybank.transfer.service;

import com.github.dgaponov99.practicum.mybank.transfer.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferAccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import com.github.dgaponov99.practicum.mybank.transfer.gateway.NotificationsGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountServiceClient accountServiceClient;
    private final NotificationsGateway notificationsGateway;

    public List<TransferAccountDto> getTransferAccounts(String username) {
        return accountServiceClient.getAllAccounts().stream()
                .filter(account -> !username.equals(account.username()))
                .map(account -> new TransferAccountDto(account.username(), account.name()))
                .toList();
    }

    public void transfer(TransferDto transferDto) {
        accountServiceClient.transfer(transferDto);
        notificationsGateway.sendNotification(transferDto.fromUsername(), "Успешно выполнен исходящий перевод на сумму %d руб.".formatted(transferDto.amount()));
        notificationsGateway.sendNotification(transferDto.toUsername(), "Успешно выполнен входящий перевод на сумму %d руб.".formatted(transferDto.amount()));
    }

}
