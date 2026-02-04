package ru.itq.document.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.itq.document.model.enums.OperationResult;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class BatchResultResponse {
    private Map<Long, OperationResult> results;
}
