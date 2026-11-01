package com.artificialintelligence.patterns.apigateway;

/**
 * A cross-cutting concern applied to requests flowing through the gateway.
 * Filters may short-circuit the chain by returning a response without calling {@code chain.proceed}.
 */
public interface GatewayFilter {

    HttpResponse filter(HttpRequest request, GatewayFilterChain chain);
}
