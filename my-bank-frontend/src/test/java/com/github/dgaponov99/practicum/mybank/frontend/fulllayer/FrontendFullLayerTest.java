package com.github.dgaponov99.practicum.mybank.frontend.fulllayer;

import com.github.dgaponov99.practicum.mybank.frontend.client.AccountClient;
import com.github.dgaponov99.practicum.mybank.frontend.client.dto.AccountDataDto;
import com.github.dgaponov99.practicum.mybank.frontend.client.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.frontend.client.dto.TransferAccountDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FrontendFullLayerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    AccountClient accountClient;

    @Test
    @WithMockUser(username = "user1")
    public void account_ok() throws Exception {
        when(accountClient.getAccount(anyString())).thenReturn(new AccountDto("user1", "Иванов Иван", LocalDate.parse("2000-01-01")));
        when(accountClient.getAccountBalance(anyString())).thenReturn(100);
        when(accountClient.getTransferAccounts(anyString())).thenReturn(List.of(
                new TransferAccountDto("user2", "Петров Петр"),
                new TransferAccountDto("user3", "Васильев Василий")
        ));

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

        verify(accountClient, times(1)).getAccount("user1");
        verify(accountClient, times(1)).getAccountBalance("user1");
        verify(accountClient, times(1)).getTransferAccounts("user1");
    }

    @Test
    @WithMockUser(username = "user1")
    public void account_edit_ok() throws Exception {
        when(accountClient.editAccount(anyString(), any())).thenReturn(new AccountDto("user1", "Иванов Иван1", LocalDate.parse("2000-02-01")));

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Иванов Иван1")
                        .param("birthdate", "2000-02-01")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(accountClient, times(1)).editAccount("user1", new AccountDataDto("Иванов Иван1", LocalDate.parse("2000-02-01")));
    }

    @Test
    @WithMockUser(username = "user1")
    public void cash_put_ok() throws Exception {
        doNothing().when(accountClient).depositCash(anyString(), anyInt());

        mockMvc.perform(post("/cash")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("value", "100")
                        .param("action", "PUT")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(accountClient, times(1)).depositCash("user1", 100);
        verifyNoMoreInteractions(accountClient);
    }

    @Test
    @WithMockUser(username = "user1")
    public void cash_get_ok() throws Exception {
        doNothing().when(accountClient).withdrawCash(anyString(), anyInt());

        mockMvc.perform(post("/cash")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("value", "100")
                        .param("action", "GET")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(accountClient, times(1)).withdrawCash("user1", 100);
        verifyNoMoreInteractions(accountClient);
    }

    @Test
    @WithMockUser(username = "user1")
    public void transfer_ok() throws Exception {
        doNothing().when(accountClient).transfer(anyString(), anyString(), anyInt());

        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("value", "100")
                        .param("login", "user2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(accountClient, times(1)).transfer("user1", "user2", 100);
        verifyNoMoreInteractions(accountClient);
    }

}
