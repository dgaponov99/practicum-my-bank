package com.github.dgaponov99.practicum.mybank.notifications.listener;

import com.github.dgaponov99.practicum.mybank.dto.NotificationDto;
import com.github.dgaponov99.practicum.mybank.notifications.service.NotificationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class NotificationsListener {

    private final NotificationsService notificationsService;

    @KafkaListener(topics = "notifications")
    public void listen(@Valid NotificationDto notificationDto, Acknowledgment ack) {
        log.debug("Новое сообщение из очереди {}", "notifications");
        notificationsService.sendNotification(notificationDto);
        log.debug("Сообщение успешно обработано, подтверждаем");
        ack.acknowledge();
    }

}
