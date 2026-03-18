package com.github.dgaponov99.practicum.mybank.transfer.service;

import com.github.dgaponov99.practicum.mybank.transfer.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferAccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountServiceClient accountServiceClient;

    public List<TransferAccountDto> getTransferAccounts(String username) {
        return accountServiceClient.getAllAccounts().stream()
                .filter(account -> !username.equals(account.username()))
                .map(account -> new TransferAccountDto(account.username(), account.name()))
                .toList();
    }

    public void transfer(TransferDto transferDto) {
        accountServiceClient.transfer(transferDto);
    }

}
