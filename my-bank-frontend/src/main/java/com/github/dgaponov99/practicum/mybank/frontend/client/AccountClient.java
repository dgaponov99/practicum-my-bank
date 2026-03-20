package com.github.dgaponov99.practicum.mybank.frontend.client;

import com.github.dgaponov99.practicum.mybank.frontend.client.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountClient {

    private final RestClient gatewayRestClient;

    public AccountDto getAccount(String username) {
        return gatewayRestClient.get()
                .uri("/accounts-service/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(AccountDto.class)
                .getBody();
    }

    public int getAccountBalance(String username) {
        return gatewayRestClient.get()
                .uri("/accounts-service/{username}/balance", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(AccountBalanceDto.class)
                .getBody()
                .balance();
    }

    public AccountDto editAccount(String username, AccountDataDto accountDataDto) {
        return gatewayRestClient.put()
                .uri("/accounts-service/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountDataDto)
                .retrieve()
                .toEntity(AccountDto.class)
                .getBody();
    }

    public void withdrawCash(String username, int amount) {
        gatewayRestClient.post()
                .uri("/cash-service/withdraw")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CashAmountDto(username, amount))
                .retrieve()
                .toBodilessEntity();
    }

    public void depositCash(String username, int amount) {
        gatewayRestClient.post()
                .uri("/cash-service/deposit")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CashAmountDto(username, amount))
                .retrieve()
                .toBodilessEntity();
    }

    public List<TransferAccountDto> getTransferAccounts(String username) {
        return gatewayRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/transfer-service/accounts")
                        .queryParam("username", username)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<TransferAccountDto>>() {
                })
                .getBody();
    }

    public void transfer(String fromUsername, String toUsername, int amount) {
        gatewayRestClient.post()
                .uri("/transfer-service")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TransferDto(fromUsername, toUsername, amount))
                .retrieve()
                .toBodilessEntity();
    }

}
