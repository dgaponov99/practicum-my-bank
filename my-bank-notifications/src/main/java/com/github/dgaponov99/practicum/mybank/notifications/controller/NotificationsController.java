package com.github.dgaponov99.practicum.mybank.notifications.controller;

import com.github.dgaponov99.practicum.mybank.notifications.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationsController {

    @PostMapping()
    @PreAuthorize("hasAuthority('notifications.write')")
    public ResponseEntity<Void> notification(@RequestBody NotificationDto notificationDto) {
        log.info("Notification for {}: {}", notificationDto.username(), notificationDto.message());
        return ResponseEntity.ok().build();
    }

}
