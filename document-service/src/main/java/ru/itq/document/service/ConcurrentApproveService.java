package ru.itq.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itq.document.model.Document;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.DocumentRepository;
import ru.itq.document.api.dto.response.ConcurrentApproveResponse;
import ru.itq.document.api.exception.ConflictException;
import ru.itq.document.api.exception.NotFoundException;
import ru.itq.document.repository.DocumentStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcurrentApproveService {

    private final ApproveService approveService;
    private final DocumentRepository documentRepository;
    private final DocumentStatusRepository documentStatusRepository;

    public ConcurrentApproveResponse run(Long documentId, int threads, int attempts) {

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();
        AtomicInteger registryError = new AtomicInteger();

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < attempts; i++) {
            tasks.add(() -> {
                try {
                    approveService.approveOne(documentId, "concurrent-test");
                    success.incrementAndGet();
                } catch (ConflictException | NotFoundException e) {
                    conflict.incrementAndGet();
                } catch (RuntimeException e) {
                    registryError.incrementAndGet();
                    log.error(
                            "event=concurrent_approve_error documentId={}",
                            documentId,
                            e
                    );
                }
                return null;
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("event=concurrent_approve_interrupted documentId={}", documentId, e);
        } finally {
            executor.shutdown();
        }

        Document doc = documentRepository.findByIdWithHistory(documentId)
                .orElseThrow(() -> {
                    log.warn("event=document_not_found documentId={}", documentId);
                    return new NotFoundException("Document not found: " + documentId);
                });

        StatusCode finalStatus = documentStatusRepository.findStatusCodeById(doc.getId());
        if (finalStatus == null) {
            log.error("event=final_status_missing documentId={}", documentId);
            throw new IllegalStateException("Final status not found");
        }

        return new ConcurrentApproveResponse(
                success.get(),
                conflict.get(),
                registryError.get(),
                finalStatus.name()
        );
    }
}
