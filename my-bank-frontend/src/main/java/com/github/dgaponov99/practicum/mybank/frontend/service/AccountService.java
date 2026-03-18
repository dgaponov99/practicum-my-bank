package com.github.dgaponov99.practicum.mybank.frontend.service;

import com.github.dgaponov99.practicum.mybank.frontend.client.AccountClient;
import com.github.dgaponov99.practicum.mybank.frontend.client.dto.AccountDataDto;
import com.github.dgaponov99.practicum.mybank.frontend.exception.BusinessMultipleException;
import com.github.dgaponov99.practicum.mybank.frontend.view.AccountView;
import com.github.dgaponov99.practicum.mybank.frontend.view.AlertView;
import com.github.dgaponov99.practicum.mybank.frontend.view.TransferAccountView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountClient accountClient;

    public AccountView getAccountView() {
        var username = extractUsername();
        var accountDto = accountClient.getAccount(username);
        var accountBalance = accountClient.getAccountBalance(username);
        return new AccountView(username, accountDto.name(), accountDto.birthDate(), accountBalance);
    }

    public List<TransferAccountView> getTransferAccountViews() {
        var username = extractUsername();
        return accountClient.getTransferAccounts(username).stream()
                .map(account -> new TransferAccountView(account.username(), account.name()))
                .toList();
    }

    public AlertView editAccount(String name, LocalDate birthDate) {
        var username = extractUsername();
        try {
            accountClient.editAccount(username, new AccountDataDto(name, birthDate));
            return new AlertView("Данные успешно изменены", null);
        } catch (BusinessMultipleException e) {
            return new AlertView(null, e.getMessages());
        }
    }

    public AlertView withdrawCash(int amount) {
        var username = extractUsername();
        try {
            accountClient.withdrawCash(username, amount);
            return new AlertView("Снято %d руб.".formatted(amount), null);
        } catch (BusinessMultipleException e) {
            return new AlertView(null, e.getMessages());
        }
    }

    public AlertView depositCash(int amount) {
        var username = extractUsername();
        try {
            accountClient.depositCash(username, amount);
            return new AlertView("Пополнение %d руб.".formatted(amount), null);
        } catch (BusinessMultipleException e) {
            return new AlertView(null, e.getMessages());
        }
    }

    public AlertView transfer(String toUsername, int amount) {
        var username = extractUsername();
        try {
            accountClient.transfer(username, toUsername, amount);
            return new AlertView("Успешно переведено %d руб.".formatted(amount), null);
        } catch (BusinessMultipleException e) {
            return new AlertView(null, e.getMessages());
        }
    }

    private String extractUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
