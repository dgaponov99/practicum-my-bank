package com.github.dgaponov99.practicum.mybank.accounts.persistence.repository;

import com.github.dgaponov99.practicum.mybank.accounts.persistence.entity.NotificationOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, UUID> {

    @Query("""
            select n from NotificationOutbox n where n.nextRetryAt < :now order by n.nextRetryAt
            """)
    List<NotificationOutbox> findReady(Instant now, Pageable pageable);

}
