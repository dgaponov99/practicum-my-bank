package com.github.dgaponov99.practicum.mybank.transfer.service;

import com.github.dgaponov99.practicum.mybank.transfer.client.NotificationsClient;
import com.github.dgaponov99.practicum.mybank.transfer.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationsClient notificationsClient;

    public void sendNotification(String username, String message) {
        try {
            notificationsClient.sendNotification(new NotificationDto(username, message));
        } catch (Exception e) {
            log.warn("Невозможно отправить уведомление: {}", e.getMessage(), e);
        }
    }

}
