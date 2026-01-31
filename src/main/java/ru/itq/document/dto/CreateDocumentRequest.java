package ru.itq.document.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDocumentRequest {
    private String author;
    private String title;
    private String initiator;
}
