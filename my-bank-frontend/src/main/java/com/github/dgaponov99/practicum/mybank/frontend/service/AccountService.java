package com.github.dgaponov99.practicum.mybank.frontend.service;

import com.github.dgaponov99.practicum.mybank.frontend.client.AccountClient;
import com.github.dgaponov99.practicum.mybank.frontend.client.dto.AccountDataDto;
import com.github.dgaponov99.practicum.mybank.frontend.exception.BusinessMultipleException;
import com.github.dgaponov99.practicum.mybank.frontend.view.AccountView;
import com.github.dgaponov99.practicum.mybank.frontend.view.AlertView;
import com.github.dgaponov99.practicum.mybank.frontend.view.TransferAccountView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
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
        log.debug("Получение данных для отображения пользователей для перевода");
        var username = extractUsername();
        return accountClient.getTransferAccounts(username).stream()
                .map(account -> new TransferAccountView(account.username(), account.name()))
                .toList();
    }

    public AlertView editAccount(String name, LocalDate birthDate) {
        var username = extractUsername();
        log.debug("Редактирование пользователя {}", username);
        try {
            accountClient.editAccount(username, new AccountDataDto(name, birthDate));
            log.info("Данные пользователя {} успешно изменены", username);
            return new AlertView("Данные успешно изменены", null);
        } catch (BusinessMultipleException e) {
            log.warn("Данные пользователя {} не изменены: {}", username, e.getMessage());
            return new AlertView(null, e.getMessages());
        }
    }

    public AlertView withdrawCash(int amount) {
        var username = extractUsername();
        log.debug("Снятие наличных со счета пользователя {}", username);
        try {
            accountClient.withdrawCash(username, amount);
            log.info("Снятие наличных со счета пользователя {} произведено успешно", username);
            return new AlertView("Снято %d руб.".formatted(amount), null);
        } catch (BusinessMultipleException e) {
            log.warn("Снятие наличных со счета пользователя {} завершено с ошибкой: {}", username, e.getMessage());
            return new AlertView(null, e.getMessages());
        }
    }

    public AlertView depositCash(int amount) {
        var username = extractUsername();
        log.debug("Пополнение счета пользователя {}", username);
        try {
            accountClient.depositCash(username, amount);
            log.info("Пополнение счета пользователя {} произведено успешно", username);
            return new AlertView("Пополнение %d руб.".formatted(amount), null);
        } catch (BusinessMultipleException e) {
            log.warn("Пополнение счета пользователя {} завершено с ошибкой: {}", username, e.getMessage());
            return new AlertView(null, e.getMessages());
        }
    }

    public AlertView transfer(String toUsername, int amount) {
        var username = extractUsername();
        log.debug("Перевод между пользователями {} и {}", username, toUsername);
        try {
            accountClient.transfer(username, toUsername, amount);
            log.debug("Перевод между пользователями {} и {} произведен успешно", username, toUsername);
            return new AlertView("Успешно переведено %d руб.".formatted(amount), null);
        } catch (BusinessMultipleException e) {
            log.warn("Перевод между пользователями {} и {} завершился с ошибкой", username, toUsername);
            return new AlertView(null, e.getMessages());
        }
    }

    private String extractUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
