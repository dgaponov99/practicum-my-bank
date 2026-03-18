package com.github.dgaponov99.practicum.mybank.accounts;

import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.Account;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountsInitComponent {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    private final AccountRepository accountRepository;
    private final RestClient serviceRestClient;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    @EventListener(ApplicationReadyEvent.class)
    public void initAccounts() {
        var keycloakUrl = keycloakIssuerUri.split("/realms/")[0];
        var realm = keycloakIssuerUri.split("/realms/")[1];

        var users = serviceRestClient.get()
                .uri(keycloakUrl + "/admin/realms/{realm}/users", realm)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
        for (var user : users) {
            var username = user.get("username").toString();
            var firstName = user.get("firstName").toString();
            var lastName = user.get("lastName").toString();
            @SuppressWarnings("unchecked")
            var attributes = (Map<String, Object>) user.get("attributes");
            var birthDate = LocalDate.parse(attributes.get("birthDate").toString().substring(1).split("]")[0], formatter);

            if (!accountRepository.existsById(username)) {
                var account = new Account();
                account.setUsername(username);
                account.setName(lastName + " " + firstName);
                account.setBirthDate(birthDate);
                accountRepository.save(account);
                log.info("Account {} created", username);
            }
        }
    }

}
