package ru.itq.generator.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itq.generator.service.DocumentGenerator;

@Component
@RequiredArgsConstructor
public class GeneratorRunner implements CommandLineRunner {

    private final DocumentGenerator documentGenerator;

    @Override
    public void run(String... args) {
        documentGenerator.generate();
    }
}
