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
import ru.itq.document.repository.ApprovalRegistryRepository;
import ru.itq.document.repository.DocumentRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class ApproveBatchTest {

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

    @Autowired
    ApprovalRegistryRepository approvalRegistryRepository;

    @Test
    void approve_batch_with_partial_results() {
        Long okId = documentService.create("a1", "t1", "init");
        Long conflictId = documentService.create("a2", "t2", "init");

        documentService.submit(List.of(okId), "submitter");
        // conflictId остаётся в DRAFT

        Map<Long, OperationResult> result =
                documentService.approve(
                        List.of(okId, conflictId, 999999L),
                        "approver"
                );

        assertEquals(OperationResult.SUCCESS, result.get(okId));
        assertEquals(OperationResult.CONFLICT, result.get(conflictId));
        assertEquals(OperationResult.NOT_FOUND, result.get(999999L));

        Document ok = documentRepository.findById(okId).orElseThrow();
        Document conflict = documentRepository.findById(conflictId).orElseThrow();

        assertEquals(StatusCode.APPROVED, ok.getStatus().getCode());
        assertEquals(StatusCode.DRAFT, conflict.getStatus().getCode());

        assertTrue(
                approvalRegistryRepository.findByDocumentId(okId).isPresent()
        );
    }
}