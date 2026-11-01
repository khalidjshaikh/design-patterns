package com.artificialintelligence.patterns.apigateway;

import java.util.Map;

/**
 * Immutable representation of an HTTP response produced by the gateway or a downstream service.
 */
public record HttpResponse(int statusCode, Map<String, String> headers, String body) {

    public HttpResponse {
        headers = headers == null ? Map.of() : Map.copyOf(headers);
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(200, Map.of("Content-Type", "application/json"), body);
    }

    public static HttpResponse created(String body) {
        return new HttpResponse(201, Map.of("Content-Type", "application/json"), body);
    }

    public static HttpResponse notFound(String body) {
        return new HttpResponse(404, Map.of("Content-Type", "application/json"), body);
    }

    public static HttpResponse unauthorized(String body) {
        return new HttpResponse(401, Map.of("Content-Type", "application/json"), body);
    }

    public static HttpResponse tooManyRequests(String body) {
        return new HttpResponse(429, Map.of("Content-Type", "application/json"), body);
    }

    public static HttpResponse badGateway(String body) {
        return new HttpResponse(502, Map.of("Content-Type", "application/json"), body);
    }

    public static HttpResponse serviceUnavailable(String body) {
        return new HttpResponse(503, Map.of("Content-Type", "application/json"), body);
    }

    public static HttpResponse internalError(String body) {
        return new HttpResponse(500, Map.of("Content-Type", "application/json"), body);
    }
}
