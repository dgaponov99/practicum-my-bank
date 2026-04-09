package com.github.dgaponov99.practicum.mybank.accounts.integration.service;

import com.github.dgaponov99.practicum.mybank.accounts.exception.AccountNotFoundException;
import com.github.dgaponov99.practicum.mybank.accounts.exception.InsufficientFundsException;
import com.github.dgaponov99.practicum.mybank.accounts.gateway.NotificationsGateway;
import com.github.dgaponov99.practicum.mybank.accounts.integration.ClearSchemaIT;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.Account;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.repository.AccountRepository;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.repository.NotificationOutboxRepository;
import com.github.dgaponov99.practicum.mybank.accounts.service.AccountsService;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.AccountDataDto;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.TransferDto;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceIT extends ClearSchemaIT {

    @Autowired
    AccountsService accountsService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    NotificationOutboxRepository notificationOutboxRepository;
    @MockitoBean
    NotificationsGateway notificationsGateway;

    @BeforeEach
    void init() {
        var account1 = new Account();
        account1.setUsername("user1");
        account1.setName("Иванов Иван");
        account1.setBirthDate(LocalDate.parse("2000-01-01"));
        account1.setBalance(0);
        accountRepository.save(account1);

        var account2 = new Account();
        account2.setUsername("user2");
        account2.setName("Петров Петр");
        account2.setBirthDate(LocalDate.parse("2000-01-02"));
        account2.setBalance(100);
        accountRepository.save(account2);

        var account3 = new Account();
        account3.setUsername("user3");
        account3.setName("Васильев Василий");
        account3.setBirthDate(LocalDate.parse("2000-01-03"));
        account3.setBalance(100);
        accountRepository.save(account3);
    }

    @Test
    public void getAllAccounts_success() {
        var allAccounts = accountsService.getAllAccounts();

        assertNotNull(allAccounts);
        assertEquals(3, allAccounts.size());
    }

    @Test
    public void getAccountDto_success() {
        var accountDto = accountsService.getAccountDto("user1");

        assertNotNull(accountDto);
        assertAll(() -> {
            assertEquals("user1", accountDto.username());
            assertEquals("Иванов Иван", accountDto.name());
            assertEquals(LocalDate.parse("2000-01-01"), accountDto.birthDate());
        });
    }

    @Test
    public void getAccountDto_notFound() {
        assertThrows(AccountNotFoundException.class, () -> accountsService.getAccountDto("user100500"));
    }

    @Test
    public void editAccount_success() {
        doNothing().when(notificationsGateway).sendNotification(anyString(), anyString());

        var account = accountsService.editAccount("user1", new AccountDataDto("Иванов Иван1", LocalDate.parse("2000-02-01")));
        assertNotNull(account);

        var actualAccount = accountRepository.findById("user1").orElseThrow();
        assertEquals("Иванов Иван1", actualAccount.getName());
        assertEquals(LocalDate.parse("2000-02-01"), actualAccount.getBirthDate());

        Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() ->
                        verify(notificationsGateway, times(1)).sendNotification(eq("user1"), anyString())
                );

    }

    @Test
    public void getAccountBalance_success() {
        var balance = accountsService.getAccountBalance("user2");

        assertEquals(100, balance);
    }

    @Test
    public void getAccountBalance_notFound() {
        assertThrows(AccountNotFoundException.class, () -> accountsService.getAccountBalance("user100500"));
    }

    @Test
    public void creditAccount_success() {
        var account = accountsService.creditAccount("user1", 100);
        assertNotNull(account);

        var actualAccount = accountRepository.findById("user1").orElseThrow();
        assertEquals(100, actualAccount.getBalance());
    }

    @Test
    public void debitAccount_success() throws InsufficientFundsException {
        var account = accountsService.debitAccount("user2", 100);
        assertNotNull(account);

        var actualAccount = accountRepository.findById("user2").orElseThrow();
        assertEquals(0, actualAccount.getBalance());
    }

    @Test
    public void debitAccount_insufficientFunds() {
        assertThrows(InsufficientFundsException.class, () -> accountsService.debitAccount("user2", 10000));

        var actualAccount = accountRepository.findById("user2").orElseThrow();
        assertEquals(100, actualAccount.getBalance());
    }

    @Test
    public void transfer_success() throws InsufficientFundsException {
        accountsService.transfer(new TransferDto("user2", "user1", 50));

        var actualUser1Account = accountRepository.findById("user1").orElseThrow();
        assertEquals(50, actualUser1Account.getBalance());
        var actualUser2Account = accountRepository.findById("user2").orElseThrow();
        assertEquals(50, actualUser2Account.getBalance());
    }

    @Test
    public void transfer_notFound() {
        assertThrows(AccountNotFoundException.class, () -> accountsService.transfer(new TransferDto("user2", "user100500", 100)));
    }

    @Test
    public void transfer_insufficientFunds() {
        assertThrows(InsufficientFundsException.class, () -> accountsService.transfer(new TransferDto("user2", "user1", 10000)));
    }

}
