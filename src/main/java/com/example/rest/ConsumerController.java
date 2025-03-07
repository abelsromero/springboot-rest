package com.example.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@RestController
class ConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    private final RestClient restClient;

    ConsumerController(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://backend:8080").build();
    }

    @GetMapping("/consume")
    Map<String, Object> sayHello(HttpServletRequest request) {

        var uri = restClient.get().uri("/hit");

        final String connectionHeader = request.getHeader(HttpHeaders.CONNECTION);
        if (StringUtils.hasLength(connectionHeader)) {
//            logger.info(connectionHeader);
            uri.header(HttpHeaders.CONNECTION, connectionHeader);
        }

        ResponseEntity<HashMap> entity = uri
                .retrieve()
                .toEntity(HashMap.class);

        logger.info("Response body: {}", entity.getBody());
        return Map.of("status", 200, "response", entity.getBody());
    }
}