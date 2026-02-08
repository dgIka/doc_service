package ru.itq.generator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itq.generator.api.dto.CreateDocumentRequest;

@Service
@RequiredArgsConstructor
public class DocumentGenerator {

    private final UtilClient utilClient;

    @Value("${generator.documents.count}")
    private int count;

    @Value("${generator.documents.author}")
    private String author;

    @Value("${generator.documents.initiator}")
    private String initiator;

    @Value("${generator.documents.title-prefix}")
    private String titlePrefix;

    public void generate() {
        for (int i = 0; i < count; i++) {
            CreateDocumentRequest request = new CreateDocumentRequest();
            request.setAuthor(author);
            request.setTitle(titlePrefix + " " + i);
            request.setInitiator(initiator);

            utilClient.create(request);
        }
    }
}
