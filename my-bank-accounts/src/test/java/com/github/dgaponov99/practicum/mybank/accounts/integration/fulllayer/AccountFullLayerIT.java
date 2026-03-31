package com.github.dgaponov99.practicum.mybank.accounts.integration.fulllayer;

import com.github.dgaponov99.practicum.mybank.accounts.client.NotificationsClient;
import com.github.dgaponov99.practicum.mybank.accounts.integration.ClearSchemaIT;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.Account;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountFullLayerIT extends ClearSchemaIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @MockitoBean
    NotificationsClient notificationsClient;

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
    @WithMockUser(authorities = {"ROLE_SERVICE", "accounts.read"})
    public void getAllAccounts_success() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_USER", "accounts.read"})
    public void getAccount_success() throws Exception {
        mockMvc.perform(get("/{username}", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.name").value("Иванов Иван"))
                .andExpect(jsonPath("$.birthDate").value("2000-01-01"));
    }

    @Test
    @WithMockUser(username = "user100500", authorities = {"ROLE_USER", "accounts.read"})
    public void getAccount_notFound() throws Exception {
        mockMvc.perform(get("/{username}", "user100500"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user2", authorities = {"ROLE_USER", "accounts.read"})
    public void getAccountBalance_success() throws Exception {
        mockMvc.perform(get("/{username}/balance", "user2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value("100"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_SERVICE", "accounts.write"})
    public void credit_success() throws Exception {
        mockMvc.perform(post("/{username}/credit", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_SERVICE", "accounts.write"})
    public void debit_success() throws Exception {
        mockMvc.perform(post("/{username}/debit", "user2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("user2"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_SERVICE", "accounts.write"})
    public void debit_conflict() throws Exception {
        mockMvc.perform(post("/{username}/debit", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 100
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_SERVICE", "accounts.write"})
    public void transfer_success() throws Exception {
        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromUsername": "user2",
                                  "toUsername": "user1",
                                  "amount": 50
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_SERVICE", "accounts.write"})
    public void transfer_conflict() throws Exception {
        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromUsername": "user1",
                                  "toUsername": "user2",
                                  "amount": 50
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
