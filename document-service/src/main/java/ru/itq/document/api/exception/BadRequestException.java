package ru.itq.document.api.exception;


import java.util.List;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super("BAD_REQUEST", List.of(message));
    }

    public BadRequestException(List<String> messages) {
        super("BAD_REQUEST", messages);
    }
}
