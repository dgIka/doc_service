package ru.itq.document.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.itq.document.api.exception.BadRequestException;
import ru.itq.document.api.exception.ConflictException;
import ru.itq.document.api.exception.NotFoundException;
import ru.itq.document.model.*;
import ru.itq.document.model.enums.ActionCode;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.*;
import ru.itq.document.model.enums.OperationResult;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepo;
    private final DocumentStatusRepository statusRepo;
    private final DocumentActionRepository actionRepo;
    private final DocumentHistoryRepository historyRepo;
    private final ApprovalRegistryRepository registryRepo;

    public DocumentServiceImpl(
            DocumentRepository documentRepo,
            DocumentStatusRepository statusRepo,
            DocumentActionRepository actionRepo,
            DocumentHistoryRepository historyRepo,
            ApprovalRegistryRepository registryRepo
    ) {
        this.documentRepo = documentRepo;
        this.statusRepo = statusRepo;
        this.actionRepo = actionRepo;
        this.historyRepo = historyRepo;
        this.registryRepo = registryRepo;
    }

    @Override
    @Transactional
    public Long create(String author, String title, String initiator) {
        DocumentStatus draft = statusRepo.findByCode(StatusCode.DRAFT).orElseThrow();

        Document doc = new Document();
        doc.setNumber(UUID.randomUUID().toString());
        doc.setAuthor(author);
        doc.setTitle(title);
        doc.setStatus(draft);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());

        documentRepo.save(doc);
        writeHistory(doc, ActionCode.SUBMIT, initiator, "created");

        return doc.getId();
    }

    @Override
    public Document getByIdWithHistory(Long id) {
        return documentRepo.findById(id).orElseThrow();
    }

    @Override
    public List<Document> getByIds(List<Long> ids) {
        return documentRepo.findByIdIn(ids);
    }

    @Override
    public Map<Long, OperationResult> submit(List<Long> ids, String initiator) {
        return process(ids, initiator, StatusCode.DRAFT, StatusCode.SUBMITTED, ActionCode.SUBMIT);
    }

    @Override
    public Map<Long, OperationResult> approve(List<Long> ids, String initiator) {
        return process(ids, initiator, StatusCode.SUBMITTED, StatusCode.APPROVED, ActionCode.APPROVE);
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



    @Override
    public List<Document> search(
            String status,
            String author,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    ) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new BadRequestException("dateFrom must be before dateTo");
        }
        StatusCode code = null;
        if (status != null) {
            try {
                code = StatusCode.valueOf(status);
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException(
                        "status must be one of DRAFT, SUBMITTED, APPROVED"
                );
            }
        }
        return documentRepo.findAll(
                DocumentSpecification.search(code, author, dateFrom, dateTo)
        );
    }

    private Map<Long, OperationResult> process(
            Collection<Long> ids,
            String initiator,
            StatusCode from,
            StatusCode to,
            ActionCode action
    ) {
        Map<Long, OperationResult> result = new HashMap<>();
        for (Long id : ids) {
            result.put(id, processOne(id, initiator, from, to, action));
        }
        return result;
    }

    @Transactional
    protected OperationResult processOne(
            Long id,
            String initiator,
            StatusCode from,
            StatusCode to,
            ActionCode action
    ) {
        Optional<Document> opt = documentRepo.findById(id);
        if (opt.isEmpty()) return OperationResult.NOT_FOUND;

        Document doc = opt.get();
        if (doc.getStatus().getCode() != from) return OperationResult.CONFLICT;

        try {
            DocumentStatus target = statusRepo.findByCode(to).orElseThrow();
            doc.setStatus(target);
            doc.setUpdatedAt(LocalDateTime.now());
            documentRepo.save(doc);

            writeHistory(doc, action, initiator, null);

            if (to == StatusCode.APPROVED) {
                ApprovalRegistry reg = new ApprovalRegistry();
                reg.setDocument(doc);
                reg.setApprovedAt(LocalDateTime.now());
                registryRepo.save(reg);
            }

            return OperationResult.SUCCESS;

        } catch (Exception e) {
            if (to == StatusCode.APPROVED) {
                return OperationResult.REGISTRY_ERROR;
            } else {
               return OperationResult.CONFLICT;
            }

        }
    }

    private void writeHistory(
            Document doc,
            ActionCode action,
            String initiator,
            String comment
    ) {
        DocumentAction act = actionRepo.findByCode(action).orElseThrow();

        DocumentHistory h = new DocumentHistory();
        h.setDocument(doc);
        h.setAction(act);
        h.setInitiator(initiator);
        h.setComment(comment);
        h.setCreatedAt(LocalDateTime.now());

        historyRepo.save(h);
    }



}
