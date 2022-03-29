package com.crionuke.devstracker.api.apple;

import com.crionuke.devstracker.api.apple.dto.AppleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Component
public class AppleApi {
    private static final Logger logger = LoggerFactory.getLogger(AppleApi.class);

    private static final String BASE_URL = "https://itunes.apple.com";
    private static final int SEARCH_LIMIT = 10;
    private static final int LOOKUP_LIMIT = 200;

    public AppleApi() {
        logger.info("Initialized, url={}", BASE_URL);
    }

    public Flux<AppleResponse> searchDeveloper(List<String> countries, String term) {
        return Flux.fromIterable(countries)
                .flatMap(country -> searchDeveloperForCountry(term, country));
    }

    public Mono<AppleResponse> lookupDeveloper(long developerAppleId, String country) {
        return createWebClient().get()
                .uri(builder -> builder
                        .path("/lookup")
                        .queryParam("id", developerAppleId)
                        .queryParam("country", country)
                        .queryParam("limit", LOOKUP_LIMIT)
                        .queryParam("entity", "software")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    try {
                        return new ObjectMapper().readValue(body, AppleResponse.class);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .onErrorResume(t -> {
                    logger.warn("Lookup API request failed, id={}, country={}, error={}",
                            developerAppleId, country, t.getMessage());
                    return Mono.just(new AppleResponse());
                });
    }

    private Mono<AppleResponse> searchDeveloperForCountry(String term, String country) {
        return createWebClient().get()
                .uri(builder -> builder
                        .path("/search")
                        .queryParam("term", term)
                        .queryParam("country", country)
                        .queryParam("limit", SEARCH_LIMIT)
                        .queryParam("media", "software")
                        .queryParam("entity", "software")
                        .queryParam("attribute", "softwareDeveloper")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    try {
                        return new ObjectMapper().readValue(body, AppleResponse.class);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .onErrorResume(t -> {
                    logger.warn("Search API request failed, term={}, country={}, error={}",
                            term, country, t.getMessage());
                    return Mono.just(new AppleResponse());
                });
    }

    private WebClient createWebClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();
    }
}
