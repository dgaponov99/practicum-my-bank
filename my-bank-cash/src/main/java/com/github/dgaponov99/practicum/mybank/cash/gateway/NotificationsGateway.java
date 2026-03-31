package com.github.dgaponov99.practicum.mybank.cash.gateway;

import com.github.dgaponov99.practicum.mybank.cash.client.NotificationsClient;
import com.github.dgaponov99.practicum.mybank.cash.dto.NotificationDto;
import com.github.dgaponov99.practicum.mybank.cash.persistence.entity.NotificationOutbox;
import com.github.dgaponov99.practicum.mybank.cash.persistence.repository.NotificationOutboxRepository;
import com.github.dgaponov99.practicum.mybank.cash.properties.OutboxProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class NotificationsGateway {

    private final NotificationsClient notificationsClient;
    private final NotificationOutboxRepository notificationOutboxRepository;
    private final OutboxProperties outboxProperties;
    private final CircuitBreakerFactory<?, ?> breakerFactory;
    private final CircuitBreakerRegistry registry;

    public void sendNotification(String username, String message) {
        var notificationDto = new NotificationDto(username, message);
        getCircuitBreaker()
                .run(() -> {
                    notificationsClient.sendNotification(notificationDto);
                    return null;
                }, (e) -> {
                    saveOutbox(notificationDto);
                    return null;
                });
    }

    @Scheduled(fixedDelay = 5000)
    protected void outboxSchedule() {
        if (isOpen()) {
            return;
        }

        var notifications = notificationOutboxRepository.findReady(Instant.now(), Pageable.ofSize(outboxProperties.getBatchSize()));
        if (notifications.isEmpty()) {
            return;
        }

        notifications.forEach(notification -> {
            getCircuitBreaker()
                    .run(() -> {
                        notificationsClient.sendNotification(notification.getPayload());
                        notificationOutboxRepository.delete(notification);
                        return null;
                    }, (e) -> {
                        notification.setRetryCount(notification.getRetryCount() + 1);
                        notification.setNextRetryAt(getNextRetryAt(notification.getRetryCount()));
                        notificationOutboxRepository.save(notification);
                        return null;
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

    private CircuitBreaker getCircuitBreaker() {
        return breakerFactory.create("notifications-service");
    }

    private boolean isOpen() {
        return registry.circuitBreaker("notifications-service").getState() == io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN;
    }

}
