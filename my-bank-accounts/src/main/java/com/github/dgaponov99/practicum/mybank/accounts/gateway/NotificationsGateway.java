package com.github.dgaponov99.practicum.mybank.accounts.gateway;

import com.github.dgaponov99.practicum.mybank.dto.NotificationDto;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.NotificationOutbox;
import com.github.dgaponov99.practicum.mybank.accounts.persistence.repository.NotificationOutboxRepository;
import com.github.dgaponov99.practicum.mybank.accounts.properties.OutboxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationsGateway {

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final OutboxProperties outboxProperties;
    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    public void sendNotification(String username, String message) {
        var notificationDto = new NotificationDto(username, message);
        kafkaTemplate.send("notifications", notificationDto.username(), notificationDto)
                .whenComplete((notification, throwable) -> {
                    if (throwable != null) {
                        saveOutbox(notificationDto);
                    }
                });
    }

    @Scheduled(fixedDelay = 5000)
    protected void outboxSchedule() {

        var notifications = notificationOutboxRepository.findReady(Instant.now(), Pageable.ofSize(outboxProperties.getBatchSize()));
        if (notifications.isEmpty()) {
            return;
        }

        notifications.forEach(notification -> {
            var notificationDto = notification.getPayload();
            kafkaTemplate.send("notifications", notificationDto.username(), notificationDto)
                    .whenComplete((result, e) -> {
                        if (e != null) {
                            notification.setRetryCount(notification.getRetryCount() + 1);
                            notification.setNextRetryAt(getNextRetryAt(notification.getRetryCount()));
                            notificationOutboxRepository.save(notification);
                        }
                    });
        });
    }


    private void saveOutbox(NotificationDto notificationDto) {
        var outbox = new NotificationOutbox();
        outbox.setPayload(notificationDto);
        outbox.setRetryCount(0);
        outbox.setNextRetryAt(getNextRetryAt(outbox.getRetryCount()));
        notificationOutboxRepository.save(outbox);
    }

    private Instant getNextRetryAt(int retryCount) {
        var delay = (long) Math.pow(2, retryCount + 1) * outboxProperties.getBaseDelay();
        delay = Math.min(delay, outboxProperties.getMaxDelay());
        return Instant.now().plusSeconds(delay);
    }


}
