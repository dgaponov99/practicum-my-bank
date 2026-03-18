package com.github.dgaponov99.practicum.mybank.cash.service;

import com.github.dgaponov99.practicum.mybank.cash.client.AccountServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashService {

    private final AccountServiceClient accountServiceClient;
    private final NotificationService notificationService;

    public void withdraw(String username, int amount) {
        accountServiceClient.debit(username, amount);
        notificationService.sendNotification(username, "Успешно снята сумма %d руб.".formatted(amount));
    }

    public void deposit(String username, int amount) {
        accountServiceClient.credit(username, amount);
        notificationService.sendNotification(username, "Успешно внесена сумма %d руб.".formatted(amount));
    }

}
