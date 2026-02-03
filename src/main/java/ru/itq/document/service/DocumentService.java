package ru.itq.document.service;

import org.springframework.data.domain.Pageable;
import ru.itq.document.model.Document;
import ru.itq.document.model.enums.OperationResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DocumentService {
    Long create(String author, String title, String initiator);

    Document getByIdWithHistory(Long id);

    List<Document> getByIds(List<Long> ids);

    Map<Long, OperationResult> submit(List<Long> ids, String initiator);

    Map<Long, OperationResult> approve(List<Long> ids, String initiator);

    List<Document> search(
            String status,
            String author,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    );

}
