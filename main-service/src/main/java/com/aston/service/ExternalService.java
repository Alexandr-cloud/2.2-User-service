package com.aston.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalService {

    private final RestTemplate restTemplate;

    public ExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackResponse")
    public String callExternalApi() {
        return restTemplate.getForObject("https://jsonplaceholder.typicode.com/users/1", String.class);
    }

    private String fallbackResponse(Exception e) {
        return "Fallback response: Service is currently unavailable";
    }
}