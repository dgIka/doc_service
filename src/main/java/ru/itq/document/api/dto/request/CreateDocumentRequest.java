package ru.itq.document.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDocumentRequest {

    @NotBlank(message = "author must not be blank")
    @Size(max = 255, message = "author length must be <= 255")
    private String author;

    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title length must be <= 255")
    private String title;

    @NotBlank(message = "initiator must not be blank")
    @Size(max = 255, message = "initiator length must be <= 255")
    private String initiator;
}
