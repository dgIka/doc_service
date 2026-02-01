package ru.itq.document.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DocumentResponse {

    private Long id;
    private String number;
    private String author;
    private String title;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<DocumentHistoryDto> history;
}
