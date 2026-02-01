package ru.itq.document.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchSubmitRequest {
    private List<Long> ids;
    private String initiator;
}
