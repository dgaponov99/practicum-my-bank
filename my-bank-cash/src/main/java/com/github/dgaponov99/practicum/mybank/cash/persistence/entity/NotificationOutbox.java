package com.github.dgaponov99.practicum.mybank.cash.persistence.entity;

import com.github.dgaponov99.practicum.mybank.dto.NotificationDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_outbox")
@EntityListeners(AuditingEntityListener.class)
public class NotificationOutbox {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payload", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private NotificationDto payload;
    @Column(name = "retry_count", nullable = false)
    private int retryCount;
    @Column(name = "next_retry_at", nullable = false)
    private Instant nextRetryAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
