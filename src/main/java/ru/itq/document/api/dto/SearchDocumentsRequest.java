package ru.itq.document.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SearchDocumentsRequest {
    private String status;
    private String author;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
}
