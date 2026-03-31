package com.github.dgaponov99.practicum.mybank.accounts.integration.contracts;

import com.github.dgaponov99.practicum.mybank.accounts.exception.InsufficientFundsException;
import com.github.dgaponov99.practicum.mybank.accounts.integration.ClearSchemaIT;
import com.github.dgaponov99.practicum.mybank.accounts.service.AccountsService;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.AccountDataDto;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.AccountDto;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.TransferDto;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("contract-test")
public class BaseContractIT extends ClearSchemaIT {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected AccountsService accountsService;

    @BeforeEach
    public void setup() throws InsufficientFundsException {
        RestAssuredMockMvc.mockMvc(mockMvc);

        when(accountsService.getAccountDto("user1"))
                .thenReturn(new AccountDto(
                        "user1",
                        "Иванов Иван",
                        LocalDate.parse("2000-01-01"))
                );

        when(accountsService.getAllAccounts())
                .thenReturn(List.of(
                        new AccountDto(
                                "user1",
                                "Иванов Иван",
                                LocalDate.parse("2000-01-01")),
                        new AccountDto(
                                "user2",
                                "Петров Петр",
                                LocalDate.parse("2000-01-02"))
                ));

        when(accountsService.editAccount("user1", new AccountDataDto("Иванов Иван", LocalDate.parse("2000-01-01"))))
                .thenReturn(new AccountDto(
                        "user1",
                        "Иванов Иван",
                        LocalDate.parse("2000-01-01")));

        when(accountsService.getAccountBalance("user1"))
                .thenReturn(100);

        when(accountsService.creditAccount("user1", 100))
                .thenReturn(new AccountDto(
                        "user1",
                        "Иванов Иван",
                        LocalDate.parse("2000-01-01")));

        when(accountsService.debitAccount("user1", 100))
                .thenReturn(new AccountDto(
                        "user1",
                        "Иванов Иван",
                        LocalDate.parse("2000-01-01")));

        when(accountsService.debitAccount("user1", 10000))
                .thenThrow(new InsufficientFundsException("На счету не достаточно средств для списания"));

        doNothing().when(accountsService).transfer(new TransferDto("user1", "user2", 100));

        doThrow(new InsufficientFundsException("На счету не достаточно средств для списания"))
                .when(accountsService).transfer(new TransferDto("user1", "user2", 10000));
    }

}

