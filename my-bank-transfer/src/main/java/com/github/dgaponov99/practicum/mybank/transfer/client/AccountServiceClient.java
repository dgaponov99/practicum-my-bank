package com.github.dgaponov99.practicum.mybank.transfer.client;

import com.github.dgaponov99.practicum.mybank.transfer.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {

    private final RestClient serviceRestClient;

    @Value("${accounts.service.url:http://localhost:8081}")
    private String baseUrl;

    public List<AccountDto> getAllAccounts() {
        return serviceRestClient.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void transfer(TransferDto transferDto) {
        serviceRestClient.post()
                .uri(baseUrl + "/transfer")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(transferDto)
                .retrieve()
                .toBodilessEntity();
    }

}
