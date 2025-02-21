package com.example.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
class HitController {

    private static final Logger logger = LoggerFactory.getLogger(HitController.class);
    private static final String hostname;

    private final AtomicInteger hitCount = new AtomicInteger(0);

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/hit")
    Map<String, Object> sayHello() {
        int count = hitCount.incrementAndGet();
        logger.info("Hit, {}, {}", hostname, count);
        return Map.of(
                "status", 200,
                "hostname", hostname,
                "count", count
        );
    }
}