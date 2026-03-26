package com.github.dgaponov99.practicum.mybank.cash.contracts.accounts;

import com.github.dgaponov99.practicum.mybank.cash.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.cash.contracts.ContractTestClientConfig;
import com.github.dgaponov99.practicum.mybank.cash.exception.ExternalMultipleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "contract-test"})
@AutoConfigureStubRunner(
        ids = "com.github.dgaponov99.practicum:my-bank-accounts:+:stubs:8382",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@Import(ContractTestClientConfig.class)
public class AccountContractTest {

    @Autowired
    AccountServiceClient accountServiceClient;

    @Test
    public void credit_success() {
        var accounts = accountServiceClient.credit("user1", 100);

        assertEquals("user1", accounts.username());
    }

    @Test
    public void debit_success() {
        var accounts = accountServiceClient.debit("user1", 100);

        assertEquals("user1", accounts.username());
    }

    @Test
    public void debit_error() {
        assertThrows(ExternalMultipleException.class, () -> accountServiceClient.debit("user1", 10000));
    }

}
