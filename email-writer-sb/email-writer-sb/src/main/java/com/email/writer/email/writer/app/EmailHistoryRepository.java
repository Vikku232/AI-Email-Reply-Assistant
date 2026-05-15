package com.email.writer.email.writer.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailHistoryRepository
        extends JpaRepository<EmailHistory, Long> {
}