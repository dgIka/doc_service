package ru.itq.document.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itq.document.api.exception.BadRequestException;
import ru.itq.document.api.exception.ConflictException;
import ru.itq.document.api.exception.NotFoundException;
import ru.itq.document.model.*;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.*;
import ru.itq.document.model.enums.OperationResult;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepo;
    private final DocumentStatusRepository statusRepo;
    private final ApproveService approveService;
    private final SubmitService submitService;

    @Override
    @Transactional
    public Long create(String author, String title, String initiator) {
        DocumentStatus draft = statusRepo.findByCode(StatusCode.DRAFT)
                .orElseThrow(() -> {
                    log.error("event=reference_missing entity=document_status code=DRAFT");
                    return new IllegalStateException("DRAFT status not found");
                });

        Document doc = new Document();
        doc.setNumber(UUID.randomUUID().toString());
        doc.setAuthor(author);
        doc.setTitle(title);
        doc.setStatus(draft);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());

        documentRepo.save(doc);

        return doc.getId();
    }

    @Transactional(readOnly = true)
    public Document getByIdWithHistory(Long id) {
        return documentRepo.findByIdWithHistory(id)
                .orElseThrow(() -> {
                    log.warn("event=document_not_found id={}", id);
                    return new NotFoundException("Document not found: " + id);
                });
    }

    @Override
    public List<Document> getByIds(List<Long> ids) {
        return documentRepo.findByIdIn(ids);
    }

    @Override
    public Map<Long, OperationResult> submit(List<Long> ids, String initiator) {
        Map<Long, OperationResult> result = new HashMap<>();

        for (Long id : ids) {
            try {
                submitService.submitOne(id, initiator);
                result.put(id, OperationResult.SUCCESS);
            } catch (NotFoundException e) {
                result.put(id, OperationResult.NOT_FOUND);
            } catch (ConflictException e) {
                result.put(id, OperationResult.CONFLICT);
            } catch (RuntimeException e) {
                log.error("event=submit_unexpected_error documentId={}", id, e);
                result.put(id, OperationResult.CONFLICT);
            }
        }

        return result;
    }

    @Override
    public Map<Long, OperationResult> approve(List<Long> ids, String initiator) {
        Map<Long, OperationResult> result = new HashMap<>();

        for (Long id : ids) {
            try {
                approveService.approveOne(id, initiator);
                result.put(id, OperationResult.SUCCESS);
            } catch (NotFoundException e) {
                result.put(id, OperationResult.NOT_FOUND);
            } catch (ConflictException e) {
                result.put(id, OperationResult.CONFLICT);
            } catch (RuntimeException e) {
                result.put(id, OperationResult.REGISTRY_ERROR);
            }
        }

        return result;
    }

    @Override
    public List<Document> search(
            String status,
            String author,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    ) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            log.warn("event=search_validation_error dateFrom={} dateTo={}", dateFrom, dateTo);
            throw new BadRequestException("dateFrom must be before dateTo");
        }
        StatusCode code = null;
        if (status != null) {
            try {
                code = StatusCode.valueOf(status);
            } catch (IllegalArgumentException ex) {
                log.warn("event=search_validation_error invalid_status={}", status);
                throw new BadRequestException(
                        "status must be one of DRAFT, SUBMITTED, APPROVED"
                );
            }
        }
        return documentRepo.findAll(
                DocumentSpecification.search(code, author, dateFrom, dateTo)
        );
    }

}
