package ru.itq.document.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchGetDocumentsRequest {
    private List<Long> ids;
}
