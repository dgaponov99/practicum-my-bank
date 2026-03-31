package com.github.dgaponov99.practicum.mybank.cash.integration.contracts.cash;

import com.github.dgaponov99.practicum.mybank.cash.integration.PostreSQLTestcontainer;
import com.github.dgaponov99.practicum.mybank.cash.integration.contracts.ContractTestClientConfig;
import com.github.dgaponov99.practicum.mybank.cash.exception.ExternalMultipleException;
import com.github.dgaponov99.practicum.mybank.cash.service.CashService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({"contract-test", "test"})
@Import(ContractTestClientConfig.class)
@Testcontainers
@ImportTestcontainers(PostreSQLTestcontainer.class)
public class BaseContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected CashService cashService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        doNothing().when(cashService).deposit("user1", 100);

        doNothing().when(cashService).withdraw("user1", 100);

        doThrow(new ExternalMultipleException("На счету не достаточно средств для списания")).when(cashService).withdraw("user1", 10000);

    }

}

