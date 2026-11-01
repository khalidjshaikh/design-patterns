package com.artificialintelligence.patterns.apigateway;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitingFilterTest {

    @Test
    void allowsUpToLimit() {
        RateLimitingFilter f = new RateLimitingFilter(2, 60_000L);
        GatewayFilterChain chain = req -> HttpResponse.ok("ok");
        HttpRequest req = HttpRequest.of("GET", "/x", Map.of("X-Client-Id", "c1"), "");
        assertEquals(200, f.filter(req, chain).statusCode());
        assertEquals(200, f.filter(req, chain).statusCode());
        assertEquals(429, f.filter(req, chain).statusCode());
    }

    @Test
    void separateClientsHaveSeparateBuckets() {
        RateLimitingFilter f = new RateLimitingFilter(1, 60_000L);
        GatewayFilterChain chain = req -> HttpResponse.ok("ok");
        HttpRequest a = HttpRequest.of("GET", "/x", Map.of("X-Client-Id", "a"), "");
        HttpRequest b = HttpRequest.of("GET", "/x", Map.of("X-Client-Id", "b"), "");
        assertEquals(200, f.filter(a, chain).statusCode());
        assertEquals(200, f.filter(b, chain).statusCode());
        assertEquals(429, f.filter(a, chain).statusCode());
    }
}
