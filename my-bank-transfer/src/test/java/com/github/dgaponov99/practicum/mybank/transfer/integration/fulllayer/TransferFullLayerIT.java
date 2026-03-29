package com.github.dgaponov99.practicum.mybank.transfer.integration.fulllayer;

import com.github.dgaponov99.practicum.mybank.transfer.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.transfer.client.NotificationsClient;
import com.github.dgaponov99.practicum.mybank.transfer.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import com.github.dgaponov99.practicum.mybank.transfer.exception.ExternalMultipleException;
import com.github.dgaponov99.practicum.mybank.transfer.integration.PostreSQLTestcontainer;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@ImportTestcontainers(PostreSQLTestcontainer.class)
public class TransferFullLayerIT {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    NotificationsClient notificationsClient;
    @MockitoBean
    AccountServiceClient accountServiceClient;

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "transfer.read"}, username = "user1")
    public void accounts_success() throws Exception {
        when(accountServiceClient.getAllAccounts()).thenReturn(List.of(
                new AccountDto("user2", "Петров Петр", LocalDate.parse("2000-01-02")),
                new AccountDto("user3", "Васильев Василий", LocalDate.parse("2000-01-03"))
        ));

        mockMvc.perform(get("/accounts")
                        .queryParam("username", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2));

        verify(accountServiceClient, times(1)).getAllAccounts();
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "transfer.write"}, username = "user1")
    public void transfer_success() throws Exception {
        doNothing().when(notificationsClient).sendNotification(any());
        doNothing().when(accountServiceClient).transfer(any());

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromUsername": "user1",
                                  "toUsername": "user2",
                                  "amount": 100
                                }
                                """))
                .andExpect(status().isOk());

        verify(accountServiceClient, times(1)).transfer(new TransferDto("user1", "user2", 100));
        verify(notificationsClient, times(2)).sendNotification(any());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "transfer.write"}, username = "user1")
    public void transfer_badRequest() throws Exception {
        doNothing().when(notificationsClient).sendNotification(any());
        doThrow(new ExternalMultipleException("На счету не достаточно средств для списания")).when(accountServiceClient).transfer(any());

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromUsername": "user1",
                                  "toUsername": "user2",
                                  "amount": 10000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isNotEmpty());

        verify(accountServiceClient, times(1)).transfer(new TransferDto("user1", "user2", 10000));
    }

}
