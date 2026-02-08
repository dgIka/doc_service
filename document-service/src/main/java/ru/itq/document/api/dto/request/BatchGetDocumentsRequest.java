package ru.itq.document.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchGetDocumentsRequest {

    @NotEmpty(message = "ids must not be empty")
    @Size(max = 1000, message = "ids size must be <= 1000")
    private List<@NotNull(message = "id must not be null") Long> ids;
}
