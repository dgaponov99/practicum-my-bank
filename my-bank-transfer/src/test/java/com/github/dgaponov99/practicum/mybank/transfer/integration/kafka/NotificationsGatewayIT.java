package com.github.dgaponov99.practicum.mybank.transfer.integration.kafka;

import com.github.dgaponov99.practicum.mybank.dto.NotificationDto;
import com.github.dgaponov99.practicum.mybank.transfer.gateway.NotificationsGateway;
import com.github.dgaponov99.practicum.mybank.transfer.integration.PostreSQLTestcontainer;
import com.github.dgaponov99.practicum.mybank.transfer.persistence.repository.NotificationOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@Slf4j
@SpringBootTest
@EmbeddedKafka(topics = "notifications")
@ActiveProfiles("test")
@Testcontainers
@ImportTestcontainers(PostreSQLTestcontainer.class)
public class NotificationsGatewayIT {

    @MockitoSpyBean(name = "kafkaTemplate")
    KafkaTemplate<?, ?> kafkaTemplate;

    @Autowired
    NotificationsGateway notificationsGateway;
    @Autowired
    NotificationOutboxRepository notificationOutboxRepository;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @BeforeEach
    public void setUp() {
        notificationOutboxRepository.deleteAll();
    }

    @Test
    public void sendNotifications_success() {
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("notifications", "true", embeddedKafkaBroker),
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer()) {
            consumerForTest.subscribe(List.of("notifications"));

            notificationsGateway.sendNotification("user1", "Test");

            var inputMessage = KafkaTestUtils.getSingleRecord(consumerForTest, "notifications", Duration.ofSeconds(5));
            assertThat(inputMessage.key()).isEqualTo("user1");
            assertEquals(0, notificationOutboxRepository.count());
        }
    }

    @Test
    public void sendNotifications_outbox_success() {
        doAnswer(invocation -> {
            var future = new CompletableFuture<SendResult<String, NotificationDto>>();
            future.completeExceptionally(new RuntimeException("Kafka down"));
            return future;
        }).when(kafkaTemplate).send(anyString(), any(), any());


        notificationsGateway.sendNotification("user1", "Test");

        assertEquals(1, notificationOutboxRepository.count());
    }

}
