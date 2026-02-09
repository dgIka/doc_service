package ru.itq.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itq.generator.api.dto.CreateDocumentRequest;

@Service
@RequiredArgsConstructor
@Slf4j
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
        long start = System.currentTimeMillis();

        log.info("event=generator_start count={}", count);

        for (int i = 0; i < count; i++) {
            CreateDocumentRequest request = new CreateDocumentRequest();
            request.setAuthor(author);
            request.setTitle(titlePrefix + " " + i);
            request.setInitiator(initiator);

            utilClient.create(request);

            if ((i + 1) % 100 == 0 || i + 1 == count) {
                log.info(
                        "event=generator_progress created={}/{}",
                        i + 1,
                        count
                );
            }
        }

        log.info(
                "event=generator_finished count={} timeMs={}",
                count,
                System.currentTimeMillis() - start
        );
    }
}
