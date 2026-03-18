package com.github.dgaponov99.practicum.mybank.frontend.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertView {

    private String info;
    private List<String> errors;

}
