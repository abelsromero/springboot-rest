package com.example.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClientConsumer.class);

    //    private static final String TARGET_URL = "http://localhost:8080/consume";
//    private static final String TARGET_URL = "http://localhost:8080/backend/hit";
    private static final String TARGET_URL = "http://localhost:8080/backend-lb/hit";
    private final static LinesWriter linesWriter = new LinesWriter(filePattern());


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        final RestTemplate restTemplate = new RestTemplate();

        final Map<String, AtomicInteger> hosts = new HashMap<>();
        int calls = 20_000;

        for (int i = 0; i < calls; i++) {
            final Response response = call(restTemplate);
            final String hostname = response.hostname;

            int countValue = 1;
            if (hosts.containsKey(hostname)) {
                countValue = hosts.get(hostname).incrementAndGet();
            } else {
                hosts.put(hostname, new AtomicInteger(1));
            }

            linesWriter.addLine("%s, %s, %s, %s".formatted(LocalDateTime.now(), hostname, response.status, countValue));
        }

        // detail
        linesWriter.writeAll();
        // summary
        hosts.forEach((k, v) -> {
            System.out.println("Host: " + k + ", Calls: " + v);
        });

        System.out.println("Done in " + ((System.currentTimeMillis() - start) / 1000) + "s");
    }

    private static Response call(RestTemplate restTemplate) {
        final HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONNECTION, "close");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, Object> res = restTemplate
                .exchange(TARGET_URL, HttpMethod.GET, entity, HashMap.class)
                .getBody();

        return processResponse(res);
    }

    private static Response processResponse(Map<String, Object> res) {

        if (res.containsKey("hostname")) {
            return new Response((String) res.get("hostname"), (Integer) res.get("status"));
        } else {
            Map<String, Object> response = (Map<String, Object>) res.get("response");
            String hostname = (String) response.get("hostname");
            Integer status = (Integer) response.get("status");
            return new Response(hostname, status);
        }
    }

    private static String filePattern() {
        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");
        return "files/log-%s.log".formatted(formatter.format(now));
    }

    record Response(String hostname, Integer status) {
    }
}
