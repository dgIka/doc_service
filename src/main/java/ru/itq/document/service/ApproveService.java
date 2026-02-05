package ru.itq.document.service;

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
public class ApproveService {

    private final DocumentRepository documentRepo;
    private final DocumentStatusRepository statusRepo;
    private final ApprovalRegistryRepository registryRepo;
    private final DocumentActionRepository actionRepo;
    private final DocumentHistoryRepository historyRepo;

    public ApproveService(
            DocumentRepository documentRepo,
            DocumentStatusRepository statusRepo,
            ApprovalRegistryRepository registryRepo,
            DocumentActionRepository actionRepo,
            DocumentHistoryRepository historyRepo
    ) {
        this.documentRepo = documentRepo;
        this.statusRepo = statusRepo;
        this.registryRepo = registryRepo;
        this.actionRepo = actionRepo;
        this.historyRepo = historyRepo;
    }

    @Transactional
    public void approveOne(Long id, String initiator) {

        Document document = documentRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));

        if (document.getStatus().getCode() != StatusCode.SUBMITTED) {
            throw new ConflictException("Document is not in SUBMITTED status");
        }

        DocumentStatus approved =
                statusRepo.findByCode(StatusCode.APPROVED).orElseThrow();

        document.setStatus(approved);
        document.setUpdatedAt(LocalDateTime.now());
        documentRepo.save(document);

        DocumentAction action =
                actionRepo.findByCode(ActionCode.APPROVE).orElseThrow();

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
    }
}

