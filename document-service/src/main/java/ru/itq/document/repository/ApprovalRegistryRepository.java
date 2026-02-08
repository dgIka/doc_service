package ru.itq.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itq.document.model.ApprovalRegistry;

import java.util.Optional;

public interface ApprovalRegistryRepository extends JpaRepository<ApprovalRegistry, Long> {

    Optional<ApprovalRegistry> findByDocumentId(Long documentId);
}
