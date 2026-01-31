package ru.itq.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itq.document.model.DocumentStatus;
import ru.itq.document.model.enums.StatusCode;

import java.util.Optional;

public interface DocumentStatusRepository
        extends JpaRepository<DocumentStatus, Long> {

    Optional<DocumentStatus> findByCode(StatusCode code);
}

