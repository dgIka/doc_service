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
public class ApproveWorker {

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Value("${worker.approve.batch-size}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${worker.approve.delay-ms}")
    public void run() {
        long start = System.currentTimeMillis();

        List<Long> ids = documentRepository.findIdsByStatus(
                StatusCode.SUBMITTED,
                PageRequest.of(0, batchSize)
        );

        if (ids.isEmpty()) {
            log.info(
                    "event=approve_worker_empty_batch timeMs={}",
                    System.currentTimeMillis() - start
            );
            return;
        }

        log.info(
                "event=approve_worker_start batchSize={}",
                ids.size()
        );

        try {
            documentService.approve(ids, "approve-worker");
        } catch (Exception e) {
            log.error(
                    "event=approve_worker_failed batchSize={}",
                    ids.size(),
                    e
            );
        }

        log.info(
                "event=approve_worker_finished batchSize={} timeMs={}",
                ids.size(),
                System.currentTimeMillis() - start
        );
    }
}

