package ru.itq.document.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchRequest {

    @NotEmpty(message = "ids must not be empty")
    @Size(max = 1000, message = "ids size must be <= 1000")
    private List<@NotNull(message = "id must not be null") Long> ids;

    @NotBlank(message = "initiator must not be blank")
    @Size(max = 255, message = "initiator length must be <= 255")
    private String initiator;
}
