package com.github.dgaponov99.practicum.mybank.accounts.integration;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.integration.spring.SpringResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@ImportTestcontainers(PostreSQLTestcontainer.class)
public abstract class ClearSchemaIT {

    @Autowired
    SpringLiquibase springLiquibase;

    @AfterEach
    void clean() throws Exception {
        try (var liquibase = createLiquibase()) {
            liquibase.dropAll();
            liquibase.update();
        }
    }

    protected Liquibase createLiquibase() throws Exception {
        var connection = springLiquibase.getDataSource().getConnection();
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        return new Liquibase(springLiquibase.getChangeLog(),
                new SpringResourceAccessor(springLiquibase.getResourceLoader()),
                database);
    }

}
