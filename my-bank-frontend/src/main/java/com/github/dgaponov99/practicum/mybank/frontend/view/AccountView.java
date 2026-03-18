package com.github.dgaponov99.practicum.mybank.frontend.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountView {

    private String username;
    private String name;
    private LocalDate birthDate;
    private int balance;

}
