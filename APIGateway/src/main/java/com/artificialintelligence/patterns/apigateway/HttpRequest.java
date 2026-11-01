package com.artificialintelligence.patterns.apigateway;

import java.util.Map;

/**
 * Immutable representation of an incoming HTTP request handled by the gateway.
 */
public record HttpRequest(String method, String path, Map<String, String> headers, String body) {

    public HttpRequest {
        headers = headers == null ? Map.of() : Map.copyOf(headers);
    }

    public static HttpRequest of(String method, String path) {
        return new HttpRequest(method, path, Map.of(), "");
    }

    public static HttpRequest of(String method, String path, Map<String, String> headers, String body) {
        return new HttpRequest(method, path, headers, body);
    }

    public String header(String name) {
        return headers.get(name);
    }
}
