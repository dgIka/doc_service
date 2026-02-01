package ru.itq.document.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itq.document.api.dto.*;
import ru.itq.document.model.Document;
import ru.itq.document.model.DocumentHistory;
import ru.itq.document.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public CreateDocumentResponse create(@RequestBody CreateDocumentRequest request) {
        Long id = documentService.create(
                request.getAuthor(),
                request.getTitle(),
                request.getInitiator()
        );
        return new CreateDocumentResponse(id);
    }

    @GetMapping("/{id}")
    public DocumentResponse getById(@PathVariable Long id) {
        Document doc = documentService.getByIdWithHistory(id);
        return map(doc);
    }

    @PostMapping("/submit")
    public BatchResultResponse submit(@RequestBody BatchRequest request) {
        return new BatchResultResponse(
                documentService.submit(request.getIds(), request.getInitiator())
        );
    }

    @PostMapping("/approve")
    public BatchResultResponse approve(@RequestBody BatchRequest request) {
        return new BatchResultResponse(
                documentService.approve(request.getIds(), request.getInitiator())
        );
    }



    private DocumentResponse map(Document doc) {
        DocumentResponse dto = new DocumentResponse();
        dto.setId(doc.getId());
        dto.setNumber(doc.getNumber());
        dto.setAuthor(doc.getAuthor());
        dto.setTitle(doc.getTitle());
        dto.setStatus(doc.getStatus().getCode().name());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setUpdatedAt(doc.getUpdatedAt());

        dto.setHistory(
                doc.getHistory().stream()
                        .map(this::mapHistory)
                        .toList()
        );
        return dto;
    }

    private DocumentHistoryDto mapHistory(DocumentHistory h
    ) {
        DocumentHistoryDto dto = new DocumentHistoryDto();
        dto.setAction(h.getAction().getCode().name());
        dto.setInitiator(h.getInitiator());
        dto.setComment(h.getComment());
        dto.setCreatedAt(h.getCreatedAt());
        return dto;
    }
}
