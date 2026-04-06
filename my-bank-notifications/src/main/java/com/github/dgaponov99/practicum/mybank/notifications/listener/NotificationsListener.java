package com.github.dgaponov99.practicum.mybank.notifications.listener;

import com.github.dgaponov99.practicum.mybank.notifications.dto.NotificationDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
public class NotificationsListener {

    @KafkaListener(topics = "notifications")
    public void listen(@Valid NotificationDto notificationDto, Acknowledgment ack) {
        log.info("Notification for {}: {}", notificationDto.username(), notificationDto.message());
        ack.acknowledge();
    }

}
