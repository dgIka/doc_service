package ru.itq.document.api.exception;

import java.util.List;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super("NOT_FOUND", List.of(message));
    }
}
