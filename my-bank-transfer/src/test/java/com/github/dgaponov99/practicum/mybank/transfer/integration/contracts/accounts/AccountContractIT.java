package com.github.dgaponov99.practicum.mybank.transfer.integration.contracts.accounts;

import com.github.dgaponov99.practicum.mybank.transfer.client.AccountServiceClient;
import com.github.dgaponov99.practicum.mybank.transfer.integration.PostreSQLTestcontainer;
import com.github.dgaponov99.practicum.mybank.transfer.integration.contracts.ContractTestClientConfig;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "contract-test"})
@AutoConfigureStubRunner(
        ids = "com.github.dgaponov99.practicum:my-bank-accounts:+:stubs:8382",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@Import(ContractTestClientConfig.class)
@Testcontainers
@ImportTestcontainers(PostreSQLTestcontainer.class)
public class AccountContractIT {

    @Autowired
    AccountServiceClient accountServiceClient;

    @Test
    public void getAllAccounts_success() {
        var accounts = accountServiceClient.getAllAccounts();

        assertEquals(2, accounts.size());
        assertEquals("user1", accounts.get(0).username());
        assertEquals("user2", accounts.get(1).username());
    }

    @Test
    public void transfer_success() {
        accountServiceClient.transfer(new TransferDto("user1", "user2", 100));
    }

}
