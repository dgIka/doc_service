package ru.itq.generator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UtilConfig {

    @Value("${generator.api.base-url}")
    private String baseUrl;

    @Bean
    public RestClient utilRestClient() {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

}
