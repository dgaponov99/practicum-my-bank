package com.github.dgaponov99.practicum.mybank.transfer.contracts.transfer;

import com.github.dgaponov99.practicum.mybank.transfer.contracts.ContractTestClientConfig;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferAccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import com.github.dgaponov99.practicum.mybank.transfer.exception.ExternalMultipleException;
import com.github.dgaponov99.practicum.mybank.transfer.service.TransferService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({"contract-test", "test"})
@Import(ContractTestClientConfig.class)
public class BaseContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected TransferService transferService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        when(transferService.getTransferAccounts("user1"))
                .thenReturn(List.of(new TransferAccountDto("user2", "Петров Петр")));

        doNothing().when(transferService).transfer(new TransferDto("user1", "user2", 100));

        doThrow(new ExternalMultipleException("На счету не достаточно средств для списания"))
                .when(transferService).transfer(new TransferDto("user1", "user2", 10000));
    }

}

