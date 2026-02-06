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
import ru.itq.document.model.DocumentHistory;
import ru.itq.document.model.enums.ActionCode;
import ru.itq.document.model.enums.StatusCode;
import ru.itq.document.repository.ApprovalRegistryRepository;
import ru.itq.document.repository.DocumentHistoryRepository;
import ru.itq.document.repository.DocumentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class ApproveHappyPathTest {

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
    DocumentHistoryRepository historyRepository;

    @Autowired
    ApprovalRegistryRepository approvalRegistryRepository;


    @Test
    void approve_single_document_happy_path() {
        Long docId = documentService.create(
                "author",
                "title",
                "creator"
        );

        documentService.submit(List.of(docId), "submitter");
        documentService.approve(List.of(docId), "approver");

        Document doc = documentRepository.findById(docId).orElseThrow();
        assertEquals(StatusCode.APPROVED, doc.getStatus().getCode());

        assertTrue(approvalRegistryRepository.findByDocumentId(docId).isPresent());

        List<DocumentHistory> history = historyRepository.findByDocumentIdOrderByCreatedAtAsc(docId);


        assertEquals(2, history.size());

        assertTrue(
                history.stream().anyMatch(h ->
                        h.getAction().getCode() == ActionCode.APPROVE
                )
        );
    }
}
