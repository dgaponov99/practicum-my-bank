package com.github.dgaponov99.practicum.mybank.accounts.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
public class Account {

    @Id
    private String username;
    @NotBlank(message = "Поле \"Имя\" не может быть пустым")
    @Size(max = 150, message = "Поле \"Имя\" не может быть длиннее {max} символов")
    @Column(length = 150)
    private String name;
    @NotNull(message = "Поле \"Дата рождения\" не может быть пустым")
    @PastOrPresent(message = "Поле \"Дата рождения\" не может быть в будущем")
    @Column
    private LocalDate birthDate;
    @PositiveOrZero(message = "Баланс не может быть отрицательным")
    @Column
    private int balance;

}
