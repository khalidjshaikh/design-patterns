package com.artificialintelligence.patterns.apigateway;

/**
 * Simple health-check backend.
 */
public final class HealthService implements DownstreamService {

    @Override
    public String name() {
        return "health-service";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.ok("{\"status\":\"UP\"}");
    }
}
