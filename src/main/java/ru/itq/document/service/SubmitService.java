package ru.itq.document.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itq.document.model.Document;
import ru.itq.document.model.DocumentAction;
import ru.itq.document.model.DocumentHistory;
import ru.itq.document.model.DocumentStatus;
import ru.itq.document.model.enums.ActionCode;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.DocumentActionRepository;
import ru.itq.document.repository.DocumentHistoryRepository;
import ru.itq.document.repository.DocumentRepository;
import ru.itq.document.repository.DocumentStatusRepository;
import ru.itq.document.api.exception.ConflictException;
import ru.itq.document.api.exception.NotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmitService {

    private final DocumentRepository documentRepo;
    private final DocumentStatusRepository statusRepo;
    private final DocumentActionRepository actionRepo;
    private final DocumentHistoryRepository historyRepo;

    @Transactional
    public void submitOne(Long id, String initiator) {

        Document doc = documentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));

        if (doc.getStatus().getCode() != StatusCode.DRAFT) {
            throw new ConflictException("Document is not in DRAFT status");
        }

        DocumentStatus submitted =
                statusRepo.findByCode(StatusCode.SUBMITTED).orElseThrow();

        doc.setStatus(submitted);
        doc.setUpdatedAt(LocalDateTime.now());
        documentRepo.save(doc);

        DocumentAction action =
                actionRepo.findByCode(ActionCode.SUBMIT).orElseThrow();

        DocumentHistory history = new DocumentHistory();
        history.setDocument(doc);
        history.setAction(action);
        history.setInitiator(initiator);
        history.setComment(null);
        history.setCreatedAt(LocalDateTime.now());
        historyRepo.save(history);
    }
}
