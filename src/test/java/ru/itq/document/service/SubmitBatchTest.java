package ru.itq.document.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itq.document.model.Document;
import ru.itq.document.model.enums.OperationResult;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.DocumentRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class SubmitBatchTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentRepository documentRepository;

    @Test
    void submit_batch_with_partial_results() {
        Long draftId = documentService.create("a1", "t1", "init");
        Long submittedId = documentService.create("a2", "t2", "init");

        documentService.submit(List.of(submittedId), "submitter");

        Map<Long, OperationResult> result =
                documentService.submit(
                        List.of(draftId, submittedId, 999999L),
                        "submitter"
                );

        assertEquals(OperationResult.SUCCESS, result.get(draftId));
        assertEquals(OperationResult.CONFLICT, result.get(submittedId));
        assertEquals(OperationResult.NOT_FOUND, result.get(999999L));

        Document draft = documentRepository.findById(draftId).orElseThrow();
        Document submitted = documentRepository.findById(submittedId).orElseThrow();

        assertEquals(StatusCode.SUBMITTED, draft.getStatus().getCode());
        assertEquals(StatusCode.SUBMITTED, submitted.getStatus().getCode());
    }
}
