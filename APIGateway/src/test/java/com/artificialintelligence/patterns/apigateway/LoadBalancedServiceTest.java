package com.artificialintelligence.patterns.apigateway;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoadBalancedServiceTest {

    @Test
    void roundRobinsAcrossInstances() {
        DownstreamService a = new DownstreamService() {
            public String name() { return "a"; }
            public HttpResponse handle(HttpRequest req) { return HttpResponse.ok("A"); }
        };
        DownstreamService b = new DownstreamService() {
            public String name() { return "b"; }
            public HttpResponse handle(HttpRequest req) { return HttpResponse.ok("B"); }
        };
        LoadBalancedService lb = new LoadBalancedService("lb", List.of(a, b));
        assertEquals("A", lb.handle(HttpRequest.of("GET", "/x")).body());
        assertEquals("B", lb.handle(HttpRequest.of("GET", "/x")).body());
        assertEquals("A", lb.handle(HttpRequest.of("GET", "/x")).body());
    }
}
