package com.github.dgaponov99.practicum.mybank.cach.exception;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ExternalMultipleException extends RuntimeException {
    @Getter
    final List<String> messages;

    /**
     * Создает исключение с множественными ошибками.
     *
     * @param messages сообщения об ошибке
     */
    public ExternalMultipleException(String... messages) {
        super(String.join(", ", messages));
        this.messages = Arrays.asList(messages);
    }

    /**
     * Создает исключение с множественными ошибками.
     *
     * @param messages сообщения об ошибке
     */
    public ExternalMultipleException(Collection<String> messages) {
        super(String.join(", ", messages));
        this.messages = List.copyOf(messages);
    }
}
