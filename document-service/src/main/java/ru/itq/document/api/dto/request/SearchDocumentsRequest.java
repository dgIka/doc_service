package ru.itq.document.api.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SearchDocumentsRequest {

    @Pattern(
            regexp = "DRAFT|SUBMITTED|APPROVED",
            message = "status must be one of DRAFT, SUBMITTED, APPROVED"
    )
    private String status;

    @Size(max = 255, message = "author length must be <= 255")
    private String author;

    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
}
