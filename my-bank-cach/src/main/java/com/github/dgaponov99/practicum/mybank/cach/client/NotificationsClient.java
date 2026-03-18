package com.github.dgaponov99.practicum.mybank.cach.client;

import com.github.dgaponov99.practicum.mybank.cach.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class NotificationsClient {

    private final RestClient serviceRestClient;

    @Value("${notifications.service.url:http://localhost:8084}")
    private String baseUrl;

    public void sendNotification(NotificationDto notificationDto) {
        serviceRestClient.post()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(notificationDto)
                .retrieve()
                .toBodilessEntity();
    }

}
