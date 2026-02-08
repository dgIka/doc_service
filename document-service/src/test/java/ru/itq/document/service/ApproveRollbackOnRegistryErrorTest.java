package ru.itq.document.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itq.document.model.ApprovalRegistry;
import ru.itq.document.model.Document;
import ru.itq.document.model.enums.OperationResult;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.ApprovalRegistryRepository;
import ru.itq.document.repository.DocumentHistoryRepository;
import ru.itq.document.repository.DocumentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ApproveRollbackOnRegistryErrorTest {

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void approve_should_rollback_when_registry_insert_fails() {
        Long docId = documentService.create("author", "title", "init");
        documentService.submit(List.of(docId), "submitter");

        ApprovalRegistry registry = new ApprovalRegistry();
        registry.setDocument(documentRepository.findById(docId).orElseThrow());
        registry.setApprovedAt(LocalDateTime.now());
        approvalRegistryRepository.save(registry);

        Map<Long, OperationResult> result =
                documentService.approve(List.of(docId), "approver");

        assertEquals(OperationResult.REGISTRY_ERROR, result.get(docId));

        Document doc =
                documentRepository.findByIdWithHistory(docId).orElseThrow();

        assertEquals(StatusCode.SUBMITTED, doc.getStatus().getCode());
        assertTrue(
                approvalRegistryRepository.findByDocumentId(docId).isPresent()
        );
    }
}





