package com.github.dgaponov99.practicum.mybank.accounts.service;

import com.github.dgaponov99.practicum.mybank.accounts.event.EditAccountEvent;
import com.github.dgaponov99.practicum.mybank.accounts.exception.AccountNotFoundException;
import com.github.dgaponov99.practicum.mybank.accounts.exception.InsufficientFundsException;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.Account;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.repository.AccountRepository;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.AccountDataDto;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountsService {

    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        log.debug("Получение всех счетов пользователей");
        return accountRepository.findAll().stream()
                .map(this::toAccountDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountDto(String username) {
        log.debug("Получение счета пользователя {}", username);
        return toAccountDto(getAccount(username));
    }

    @Transactional
    public AccountDto editAccount(String username, AccountDataDto accountDataDto) {
        log.debug("Редактирование счета пользователя {}", username);
        var account = getAccount(username);
        account.setName(accountDataDto.name());
        account.setBirthDate(accountDataDto.birthDate());

        publisher.publishEvent(new EditAccountEvent(username));
        accountRepository.save(account);
        log.info("Счет пользователя {} успешно отредактирован", username);
        return toAccountDto(account);
    }

    @Transactional(readOnly = true)
    public int getAccountBalance(String username) {
        log.debug("Получение баланса пользователя {}", username);
        return getAccount(username).getBalance();
    }

    @Transactional
    public AccountDto creditAccount(String username, int amount) {
        log.debug("Пополнение счета пользователя {}", username);
        var account = getAccount(username);
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        log.info("Счет пользователя {} успешно пополнен", username);
        return toAccountDto(account);
    }

    @Transactional(rollbackFor = InsufficientFundsException.class)
    public AccountDto debitAccount(String username, int amount) throws InsufficientFundsException {
        log.debug("Снятие со счета пользователя {}", username);
        var account = getAccount(username);
        if (account.getBalance() < amount) {
            log.info("На счету пользователя {} не достаточно средств для списания", username);
            throw new InsufficientFundsException("На счету не достаточно средств для списания");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        log.info("Счет пользователя {} успешно уменьшился", username);
        return toAccountDto(account);
    }

    @Transactional(rollbackFor = InsufficientFundsException.class)
    public void transfer(TransferDto transferDto) throws InsufficientFundsException {
        log.debug("Перевод от пользователя {} пользователю {}", transferDto.fromUsername(), transferDto.toUsername());
        var fromAccount = getAccount(transferDto.fromUsername());
        Account toAccount;
        try {
            toAccount = getAccount(transferDto.toUsername());
        } catch (AccountNotFoundException e) {
            log.warn("Счет пользователя получателя {} не найден", transferDto.toUsername());
            throw new AccountNotFoundException("Счет пользователя получателя %s не найден".formatted(transferDto.toUsername()));
        }

        if (fromAccount.getBalance() < transferDto.amount()) {
            log.info("На счету пользователя {} не достаточно средств для списания", transferDto.fromUsername());
            throw new InsufficientFundsException("На счету не достаточно средств для списания");
        }
        fromAccount.setBalance(fromAccount.getBalance() - transferDto.amount());
        toAccount.setBalance(toAccount.getBalance() + transferDto.amount());
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        log.info("Перевод от пользователя {} пользователю {} успешен", transferDto.fromUsername(), transferDto.toUsername());
    }

    private Account getAccount(String username) {
        return accountRepository.findById(username)
                .orElseThrow(() -> new AccountNotFoundException("Счет пользователя %s не найден".formatted(username)));
    }

    private AccountDto toAccountDto(Account account) {
        return new AccountDto(account.getUsername(), account.getName(), account.getBirthDate());
    }

}
