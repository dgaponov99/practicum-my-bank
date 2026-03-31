package com.github.dgaponov99.practicum.mybank.frontend.controller;

import com.github.dgaponov99.practicum.mybank.frontend.service.AccountService;
import com.github.dgaponov99.practicum.mybank.frontend.view.AlertView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Контроллер main.html.
 * <p>
 * Используемая модель для main.html:
 * model.addAttribute("name", name);
 * model.addAttribute("birthdate", birthdate.format(DateTimeFormatter.ISO_DATE));
 * model.addAttribute("sum", sum);
 * model.addAttribute("accounts", accounts);
 * model.addAttribute("errors", errors);
 * model.addAttribute("info", info);
 * <p>
 * Поля модели:
 * name - Фамилия Имя текущего пользователя, String (обязательное)
 * birthdate - дата рождения текущего пользователя, String в формате 'YYYY-MM-DD' (обязательное)
 * sum - сумма на счету текущего пользователя, Integer (обязательное)
 * accounts - список аккаунтов, которым можно перевести деньги, List<AccountDto> (обязательное)
 * errors - список ошибок после выполнения действий, List<String> (не обязательное)
 * info - строка успешности после выполнения действия, String (не обязательное)
 * <p>
 * С примерами использования можно ознакомиться в тестовом классе заглушке AccountStub
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    private final AccountService accountService;

    /**
     * GET /.
     * Редирект на GET /account
     */
    @GetMapping
    public String index() {
        return "redirect:/account";
    }

    /**
     * GET /account.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для получения данных аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     */
    @GetMapping("/account")
    public String getAccount(Model model) {
        model.addAttribute("account", accountService.getAccountView());
        model.addAttribute("transferAccounts", accountService.getTransferAccountViews());

        return "main";
    }

    /**
     * POST /account.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для изменения данных текущего пользователя по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Изменяемые данные:
     * 1. name - Фамилия Имя
     * 2. birthdate - дата рождения в формате YYYY-DD-MM
     */
    @PostMapping("/account")
    public String editAccount(
            @RequestParam("name") String name,
            @RequestParam("birthdate") LocalDate birthdate,
            RedirectAttributes redirectAttributes) {
        var alert = accountService.editAccount(name, birthdate);

        redirectAttributes.addFlashAttribute("alert", alert);
        return "redirect:/account";
    }

    /**
     * POST /cash.
     * Что нужно сделать:
     * 1. Сходить в сервис cash через Gateway API для снятия/пополнения счета текущего аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Параметры:
     * 1. value - сумма списания
     * 2. action - GET (снять), PUT (пополнить)
     */
    @PostMapping("/cash")
    public String editCash(
            @RequestParam("value") int value,
            @RequestParam("action") CashAction action,
            RedirectAttributes redirectAttributes) {
        AlertView alert;
        if (action == CashAction.PUT) {
            alert = accountService.depositCash(value);
        } else {
            alert = accountService.withdrawCash(value);
        }

        redirectAttributes.addFlashAttribute("alert", alert);
        return "redirect:/account";
    }

    /**
     * POST /transfer.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для перевода со счета текущего аккаунта на счет другого аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Параметры:
     * 1. value - сумма списания
     * 2. username - логин пользователя получателя
     */
    @PostMapping("/transfer")
    public String transfer(
            @RequestParam("value") int value,
            @RequestParam("login") String login,
            RedirectAttributes redirectAttributes) {
        var alert = accountService.transfer(login, value);

        redirectAttributes.addFlashAttribute("alert", alert);
        return "redirect:/account";
    }

    public enum CashAction {
        PUT, GET
    }
}
