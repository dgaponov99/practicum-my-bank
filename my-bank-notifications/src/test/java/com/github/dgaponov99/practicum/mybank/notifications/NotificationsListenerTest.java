package com.github.dgaponov99.practicum.mybank.notifications;

import com.github.dgaponov99.practicum.mybank.dto.NotificationDto;
import com.github.dgaponov99.practicum.mybank.notifications.listener.NotificationsListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(topics = "notifications")
public class NotificationsListenerTest {

    @MockitoSpyBean
    NotificationsListener notificationsListener;
    @Captor
    ArgumentCaptor<NotificationDto> dtoCaptor;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ConsumerFactory<String, NotificationDto> consumerFactory;

    private Consumer<String, NotificationDto> consumer;

    @BeforeEach
    void setUp() {
        consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "notifications");
    }

    @Test
    void listen_success() {
        try (var producer = new KafkaProducer<String, NotificationDto>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ))) {
            var dto = new NotificationDto("user1", "Уведомление");
            producer.send(new ProducerRecord<>("notifications", "user1", dto));
            producer.flush();

            Awaitility.await()
                    .atMost(Duration.ofSeconds(10))
                    .untilAsserted(() -> {
                        verify(notificationsListener, times(1)).listen(dtoCaptor.capture(), any());
                        var notificationDto = dtoCaptor.getValue();
                        assertEquals("user1", notificationDto.username());
                        assertEquals("Уведомление", notificationDto.message());
                    });
        }
    }

    @Test
    void listen_retry() {
        doThrow(new RuntimeException("boom"))
                .doCallRealMethod()
                .when(notificationsListener)
                .listen(any(), any());

        try (var producer = new KafkaProducer<String, NotificationDto>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ))) {
            var dto = new NotificationDto("user1", "Уведомление");
            producer.send(new ProducerRecord<>("notifications", "user1", dto));
            producer.flush();

            Awaitility.await()
                    .atMost(Duration.ofSeconds(10))
                    .untilAsserted(() -> {
                        verify(notificationsListener, times(2)).listen(dtoCaptor.capture(), any());
                        var notificationDto = dtoCaptor.getValue();
                        assertEquals("user1", notificationDto.username());
                        assertEquals("Уведомление", notificationDto.message());
                    });
        }
    }

    @Test
    void listen_notValid() {

        try (var producer = new KafkaProducer<String, NotificationDto>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ))) {
            var dto = new NotificationDto("user1", null);
            producer.send(new ProducerRecord<>("notifications", "user1", dto));
            producer.flush();

            Awaitility.await()
                    .atMost(Duration.ofSeconds(10))
                    .untilAsserted(() -> {
                        verify(notificationsListener, never()).listen(any(), any());
                    });

            var consumerProps = KafkaTestUtils.consumerProps("notifications-service", "true", embeddedKafkaBroker);
            try (var consumer = new KafkaConsumer<>(consumerProps,
                    new StringDeserializer(),
                    new JsonDeserializer<>(NotificationDto.class))) {
                var partition = new TopicPartition("notifications", 0);
                Awaitility.await()
                        .atMost(Duration.ofSeconds(10))
                        .untilAsserted(() -> {
                            var committed = consumer.committed(Set.of(partition));
                            assertNotNull(committed);
                            assertTrue(committed.values().iterator().next().offset() >= 1);
                        });
            }
        }
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

}
