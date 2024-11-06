package com.java.exchange.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebApiCaller {

    private final WebClient webClient;

    public <R> R getObject(String url, Class<R> returnType) {
        return this.webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(returnType)
                .block();
    }
}
