package com.github.dgaponov99.practicum.mybank.cach.service;

import com.github.dgaponov99.practicum.mybank.cach.client.AccountServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CachService {

    private final AccountServiceClient accountServiceClient;
    private final NotificationService notificationService;

    public void withdraw(String username, int amount) {
        accountServiceClient.debit(username, amount);
        notificationService.sendNotification(username, "Успешно снята сумма %d руб.".formatted(amount));
    }

    public void debit(String username, int amount) {
        accountServiceClient.debit(username, amount);
        notificationService.sendNotification(username, "Успешно внесена сумма %d руб.".formatted(amount));
    }

}
