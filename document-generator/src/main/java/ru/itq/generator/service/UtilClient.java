package ru.itq.generator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.itq.generator.api.dto.CreateDocumentRequest;

@Component
@RequiredArgsConstructor
public class UtilClient {

    private final RestClient utilRestClient;

    @Value("${generator.api.create-endpoint}")
    private String createEndpoint;

    public void create(CreateDocumentRequest request) {
        utilRestClient.post()
                .uri(createEndpoint)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
