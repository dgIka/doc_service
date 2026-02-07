package ru.itq.document.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.DocumentRepository;
import ru.itq.document.service.DocumentService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubmitWorker {

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Value("${worker.submit.batch-size}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${worker.submit.delay-ms}")
    public void run() {
        List<Long> ids = documentRepository.findIdsByStatus(
                StatusCode.DRAFT,
                PageRequest.of(0, batchSize)
        );

        if (ids.isEmpty()) {
            return;
        }

        documentService.submit(ids, "submit-worker");

    }
}
