package com.alexeymelekhov.flowmanager.repository;

import com.alexeymelekhov.flowmanager.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findByPublishedAtIsNull();
}
