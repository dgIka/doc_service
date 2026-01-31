package ru.itq.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itq.document.model.DocumentAction;
import ru.itq.document.model.enums.ActionCode;

import java.util.Optional;

public interface DocumentActionRepository
        extends JpaRepository<DocumentAction, Long> {

    Optional<DocumentAction> findByCode(ActionCode code);
}
