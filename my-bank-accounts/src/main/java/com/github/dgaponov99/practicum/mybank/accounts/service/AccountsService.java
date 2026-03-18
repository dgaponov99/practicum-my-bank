package com.github.dgaponov99.practicum.mybank.accounts.service;

import com.github.dgaponov99.practicum.mybank.accounts.exception.AccountNotFoundException;
import com.github.dgaponov99.practicum.mybank.accounts.exception.InsufficientFundsException;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.Account;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.repository.AccountRepository;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.AccountDataDto;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountsService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::toAccountDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountDto(String username) {
        return toAccountDto(getAccount(username));
    }

    @Transactional
    public AccountDto editAccount(String username, AccountDataDto accountDataDto) {
        var account = getAccount(username);
        account.setName(accountDataDto.name());
        account.setBirthDate(accountDataDto.birthDate());
        accountRepository.save(account);
        return toAccountDto(account);
    }

    @Transactional(readOnly = true)
    public int getAccountBalance(String username) {
        return getAccount(username).getBalance();
    }

    @Transactional
    public AccountDto creditAccount(String username, int amount) {
        var account = getAccount(username);
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        return toAccountDto(account);
    }

    @Transactional(rollbackFor = InsufficientFundsException.class)
    public AccountDto debitAccount(String username, int amount) throws InsufficientFundsException {
        var account = getAccount(username);
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("На счету не достаточно средств для списания");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        return toAccountDto(account);
    }

    @Transactional(rollbackFor = InsufficientFundsException.class)
    public void transfer(TransferDto transferDto) throws InsufficientFundsException {
        var fromAccount = getAccount(transferDto.fromUsername());
        Account toAccount;
        try {
            toAccount = getAccount(transferDto.toUsername());
        } catch (AccountNotFoundException e) {
            throw new AccountNotFoundException("Счет пользователя получателя %s не найден".formatted(transferDto.toUsername()));
        }

        if (fromAccount.getBalance() < transferDto.amount()) {
            throw new InsufficientFundsException("На счету не достаточно средств для списания");
        }
        fromAccount.setBalance(fromAccount.getBalance() - transferDto.amount());
        toAccount.setBalance(toAccount.getBalance() + transferDto.amount());
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    private Account getAccount(String username) {
        return accountRepository.findById(username)
                .orElseThrow(() -> new AccountNotFoundException("Счет пользователя %s не найден".formatted(username)));
    }

    private AccountDto toAccountDto(Account account) {
        return new AccountDto(account.getUsername(), account.getName(), account.getBirthDate());
    }

}
