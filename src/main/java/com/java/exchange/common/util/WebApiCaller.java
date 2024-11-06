package com.java.exchange.common.util;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebApiCaller {

    private final WebClient webClient;
    private static final String RESILIENCE4J_INSTANCE_NAME = "nbpApiCircuitBreaker";

    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = "fallback")
    public <R> R getObject(String url, Class<R> returnType) {
        return this.webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(returnType)
                .block();
    }
}
