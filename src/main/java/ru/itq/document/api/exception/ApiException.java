package ru.itq.document.api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class ApiException extends RuntimeException {

    private final String code;
    private final List<String> messages;

    protected ApiException(String code, List<String> messages) {
        super(String.join("; ", messages));
        this.code = code;
        this.messages = messages;
    }
}
