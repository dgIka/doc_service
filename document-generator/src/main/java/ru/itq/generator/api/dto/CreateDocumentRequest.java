package ru.itq.generator.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDocumentRequest {

    private String author;
    private String title;
    private String initiator;
}
