package ru.itq.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itq.document.model.DocumentHistory;

import java.util.List;

public interface DocumentHistoryRepository
        extends JpaRepository<DocumentHistory, Long> {

    List<DocumentHistory> findByDocumentIdOrderByCreatedAtAsc(Long documentId);
}
