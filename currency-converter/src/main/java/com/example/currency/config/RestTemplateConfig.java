package com.example.currency.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${currency.api.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${currency.api.read-timeout:10000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        ClientHttpRequestFactory factory = createClientHttpRequestFactory(); // Fixed method name
        return new RestTemplate(factory);
    }


    private ClientHttpRequestFactory createClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Ensure timeouts are positive
        if (connectionTimeout <= 0) connectionTimeout = 5000;
        if (readTimeout <= 0) readTimeout = 10000;

        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}