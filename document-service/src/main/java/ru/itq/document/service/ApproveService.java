package ru.itq.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itq.document.api.exception.ConflictException;
import ru.itq.document.api.exception.NotFoundException;
import ru.itq.document.model.*;
import ru.itq.document.model.enums.ActionCode;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApproveService {

    private final DocumentRepository documentRepo;
    private final DocumentStatusRepository statusRepo;
    private final ApprovalRegistryRepository registryRepo;
    private final DocumentActionRepository actionRepo;
    private final DocumentHistoryRepository historyRepo;

    @Transactional
    public void approveOne(Long id, String initiator) {

        Document document = documentRepo.findByIdForUpdate(id)
                .orElseThrow(() -> {
                    log.warn("event=document_not_found documentId={}", id);
                    return new NotFoundException("Document not found: " + id);
                });

        if (document.getStatus().getCode() != StatusCode.SUBMITTED) {
            throw new ConflictException("Document is not in SUBMITTED status");
        }

        DocumentAction action = actionRepo.findByCode(ActionCode.APPROVE)
                .orElseThrow(() -> {
                    log.error("event=reference_missing entity=document_action code=APPROVE");
                    return new IllegalStateException("APPROVE action not found");
                });

        DocumentHistory history = new DocumentHistory();
        history.setDocument(document);
        history.setAction(action);
        history.setInitiator(initiator);
        history.setComment(null);
        history.setCreatedAt(LocalDateTime.now());
        historyRepo.save(history);

        ApprovalRegistry registry = new ApprovalRegistry();
        registry.setDocument(document);
        registry.setApprovedAt(LocalDateTime.now());
        registryRepo.save(registry);

        DocumentStatus approved = statusRepo.findByCode(StatusCode.APPROVED)
                .orElseThrow(() -> {
                    log.error("event=reference_missing entity=document_status code=APPROVED");
                    return new IllegalStateException("APPROVED status not found");
                });

        document.setStatus(approved);
        document.setUpdatedAt(LocalDateTime.now());
        documentRepo.save(document);
    }
}

