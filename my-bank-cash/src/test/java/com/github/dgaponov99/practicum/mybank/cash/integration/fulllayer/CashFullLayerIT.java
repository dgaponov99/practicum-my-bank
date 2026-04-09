package com.github.dgaponov99.practicum.mybank.cash.integration.fulllayer;

import com.github.dgaponov99.practicum.mybank.cash.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.cash.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.cash.exception.ExternalMultipleException;
import com.github.dgaponov99.practicum.mybank.cash.gateway.NotificationsGateway;
import com.github.dgaponov99.practicum.mybank.cash.integration.PostreSQLTestcontainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@ImportTestcontainers(PostreSQLTestcontainer.class)
public class CashFullLayerIT {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    NotificationsGateway notificationsGateway;
    @MockitoBean
    AccountServiceClient accountServiceClient;

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "cash.write"}, username = "user1")
    public void withdraw_success() throws Exception {
        doNothing().when(notificationsGateway).sendNotification(anyString(), anyString());
        when(accountServiceClient.debit(anyString(), anyInt())).thenReturn(new AccountDto("user1", "Иванов Иван", LocalDate.parse("2000-01-01")));

        mockMvc.perform(post("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "user1",
                                  "amount": 100
                                }
                                """))
                .andExpect(status().isOk());

        verify(accountServiceClient, times(1)).debit("user1", 100);
        verify(notificationsGateway, times(1)).sendNotification(eq("user1"), anyString());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "cash.write"}, username = "user1")
    public void withdraw_badRequest() throws Exception {
        doNothing().when(notificationsGateway).sendNotification(anyString(), anyString());
        when(accountServiceClient.debit(anyString(), anyInt())).thenThrow(new ExternalMultipleException("На счету не достаточно средств для списания"));

        mockMvc.perform(post("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "user1",
                                  "amount": 10000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isNotEmpty());

        verify(accountServiceClient, times(1)).debit("user1", 10000);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "cash.write"}, username = "user1")
    public void deposit_success() throws Exception {
        doNothing().when(notificationsGateway).sendNotification(anyString(), anyString());
        when(accountServiceClient.debit(anyString(), anyInt())).thenReturn(new AccountDto("user1", "Иванов Иван", LocalDate.parse("2000-01-01")));

        mockMvc.perform(post("/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "user1",
                                  "amount": 100
                                }
                                """))
                .andExpect(status().isOk());

        verify(accountServiceClient, times(1)).credit("user1", 100);
        verify(notificationsGateway, times(1)).sendNotification(eq("user1"), anyString());
    }

}
