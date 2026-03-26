package com.github.dgaponov99.practicum.mybank.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyBankAccountsApplication {

    public static void main(String[] args) {
        var app = new SpringApplication(MyBankAccountsApplication.class);
        app.setAdditionalProfiles("accounts-init");

        app.run(args);
    }

}
