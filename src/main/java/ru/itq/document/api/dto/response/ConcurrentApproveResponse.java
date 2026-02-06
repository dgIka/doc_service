package ru.itq.document.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConcurrentApproveResponse {

    private int success;
    private int conflict;
    private int registryError;
    private String finalStatus;
}
