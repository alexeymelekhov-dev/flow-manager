package com.alexeymelekhov.flowmanager.repository;

import com.alexeymelekhov.flowmanager.model.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxRepository extends JpaRepository<Inbox, UUID> {
}
