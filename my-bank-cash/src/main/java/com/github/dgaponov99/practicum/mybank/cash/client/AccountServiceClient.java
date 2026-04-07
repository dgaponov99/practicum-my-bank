package com.github.dgaponov99.practicum.mybank.cash.client;

import com.github.dgaponov99.practicum.mybank.cash.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.cash.dto.AmountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {

    private final RestClient serviceRestClient;

    @Value("${accounts.service.url:http://accounts-service:8082}")
    private String baseUrl;

    public AccountDto credit(String username, int amount) {
        return serviceRestClient.post()
                .uri(baseUrl + "/{username}/credit", username)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AmountDto(amount))
                .retrieve()
                .body(AccountDto.class);
    }

    public AccountDto debit(String username, int amount) {
        return serviceRestClient.post()
                .uri(baseUrl + "/{username}/debit", username)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AmountDto(amount))
                .retrieve()
                .body(AccountDto.class);
    }

}
