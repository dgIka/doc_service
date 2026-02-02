package ru.itq.document.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchGetDocumentsResponse {
    private List<DocumentListItemDto> documents;
}
