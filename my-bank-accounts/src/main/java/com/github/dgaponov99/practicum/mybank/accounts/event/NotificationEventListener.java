package com.github.dgaponov99.practicum.mybank.accounts.event;

import com.github.dgaponov99.practicum.mybank.accounts.gateway.NotificationsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationsGateway notificationsGateway;

    @Async("asyncTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEditComplete(EditAccountEvent editAccountEvent) {
        try {
            log.debug("Отправка уведомления пользователю [успешное редактирование]: {}", editAccountEvent.username());
            notificationsGateway.sendNotification(editAccountEvent.username(), "Счет пользователя успешно отредактирован");
        } catch (Exception e) {
            log.warn("Ошибка отправки уведомления: {}", e.getMessage(), e);
        }
    }

    @Async("asyncTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onErrorEditComplete(EditAccountEvent editAccountEvent) {
        try {
            log.debug("Отправка уведомления пользователю [неуспешное редактирование]: {}", editAccountEvent.username());
            notificationsGateway.sendNotification(editAccountEvent.username(), "Ошибка редактирования счета пользователя");
        } catch (Exception e) {
            log.warn("Ошибка отправки уведомления: {}", e.getMessage(), e);
        }
    }

}
