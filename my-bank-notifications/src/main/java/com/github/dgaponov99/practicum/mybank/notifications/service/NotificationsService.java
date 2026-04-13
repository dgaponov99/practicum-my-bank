package com.github.dgaponov99.practicum.mybank.notifications.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.dgaponov99.practicum.mybank.dto.NotificationDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationsService {

    private final Tracer tracer;
    private final Cache<String, Boolean> traceIdCache;
    private final MeterRegistry meterRegistry;

    public void sendNotification(NotificationDto notificationDto) {
        log.debug("Уведомление пользователю {}", notificationDto.username());
        var traceId = tracer.currentTraceContext().context().traceId();
        if (!Boolean.TRUE.equals(traceIdCache.getIfPresent(traceId))) {
            if (ThreadLocalRandom.current().nextInt(3) == 0) {
                traceIdCache.put(traceId, true);
            }
        }
        if (Boolean.TRUE.equals(traceIdCache.getIfPresent(traceId))) {
            throw new RuntimeException("Ошибка обработки отправки уведомления");
        }

        log.info("Notification for {}: {}", notificationDto.username(), notificationDto.message());
        meterRegistry.counter("notification").increment();
    }

}
