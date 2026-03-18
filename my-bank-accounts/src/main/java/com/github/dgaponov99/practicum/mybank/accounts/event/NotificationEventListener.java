package com.github.dgaponov99.practicum.mybank.accounts.event;

import com.github.dgaponov99.practicum.mybank.accounts.client.NotificationsClient;
import com.github.dgaponov99.practicum.mybank.accounts.client.dto.NotificationDto;
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

    private final NotificationsClient notificationsClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEditComplete(EditAccountEvent editAccountEvent) {
        try {
            notificationsClient.sendNotification(new NotificationDto(editAccountEvent.username(), "Счет пользователя успешно отредактирован"));
        } catch (Exception e) {
            log.warn("Ошибка отправки уведомления: {}", e.getMessage(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onErrorEditComplete(EditAccountEvent editAccountEvent) {
        try {
            notificationsClient.sendNotification(new NotificationDto(editAccountEvent.username(), "Ошибка редактирования счета пользователя"));
        } catch (Exception e) {
            log.warn("Ошибка отправки уведомления: {}", e.getMessage(), e);
        }
    }

}
