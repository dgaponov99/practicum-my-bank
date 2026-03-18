package com.github.dgaponov99.practicum.mybank.cach.service;

import com.github.dgaponov99.practicum.mybank.cach.client.AccountServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CachService {

    private final AccountServiceClient accountServiceClient;

    public void withdraw(String username, int amount) {
        accountServiceClient.debit(username, amount);
    }

    public void debit(String username, int amount) {
        accountServiceClient.debit(username, amount);
    }

}
