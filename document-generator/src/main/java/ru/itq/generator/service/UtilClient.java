package ru.itq.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import ru.itq.generator.api.dto.CreateDocumentRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class UtilClient {

    private final RestClient utilRestClient;

    @Value("${generator.api.create-endpoint}")
    private String createEndpoint;

    public void create(CreateDocumentRequest request) {
        try {
            utilRestClient.post()
                    .uri(createEndpoint)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            log.error(
                    "event=generator_api_error status={} body={}",
                    ex.getRawStatusCode(),
                    ex.getResponseBodyAsString()
            );
            throw ex;
        } catch (Exception ex) {
            log.error("event=generator_unexpected_error", ex);
            throw ex;
        }
    }
}
