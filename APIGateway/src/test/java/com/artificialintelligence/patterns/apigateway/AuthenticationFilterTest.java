package com.artificialintelligence.patterns.apigateway;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationFilterTest {

    @Test
    void publicPathBypassesAuth() {
        AuthenticationFilter f = new AuthenticationFilter();
        // chain just returns 200
        GatewayFilterChain chain = req -> HttpResponse.ok("ok");
        HttpResponse r = f.filter(HttpRequest.of("GET", "/health"), chain);
        assertEquals(200, r.statusCode());
    }

    @Test
    void protectedPathWithoutTokenReturns401() {
        AuthenticationFilter f = new AuthenticationFilter();
        GatewayFilterChain chain = req -> HttpResponse.ok("ok");
        assertEquals(401, f.filter(HttpRequest.of("GET", "/api/x"), chain).statusCode());
    }

    @Test
    void protectedPathWithValidTokenProceeds() {
        AuthenticationFilter f = new AuthenticationFilter();
        GatewayFilterChain chain = req -> HttpResponse.ok("ok");
        HttpResponse r = f.filter(HttpRequest.of("GET", "/api/x",
                Map.of("Authorization", "Bearer " + AuthenticationFilter.VALID_TOKEN), ""), chain);
        assertEquals(200, r.statusCode());
    }
}
