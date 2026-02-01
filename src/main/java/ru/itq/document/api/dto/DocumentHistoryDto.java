package ru.itq.document.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentHistoryDto {
    private String action;
    private String initiator;
    private String comment;
    private LocalDateTime createdAt;
}
