package com.artificialintelligence.patterns.apigateway;

/**
 * A backend service reachable through the gateway. Implementations simulate remote services.
 */
public interface DownstreamService {

    String name();

    HttpResponse handle(HttpRequest request);
}
