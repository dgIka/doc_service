package ru.itq.document.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcurrentApproveRequest {

    @NotNull
    private Long documentId;

    @Min(1)
    private int threads;

    @Min(1)
    private int attempts;
}
