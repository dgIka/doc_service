package ru.itq.document.api.dto;

import lombok.Getter;
import lombok.Setter;
import ru.itq.document.model.enums.OperationResult;

import java.util.Map;

@Getter
@Setter
public class BatchResultResponse {
    private Map<Long, OperationResult> results;
}
