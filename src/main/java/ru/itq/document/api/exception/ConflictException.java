package ru.itq.document.api.exception;

import java.util.List;

public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super("CONFLICT", List.of(message));
    }
}
