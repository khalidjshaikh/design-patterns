package com.artificialintelligence.patterns.apigateway;

/**
 * Handle to the next filter / terminal routing step in the gateway pipeline.
 */
public interface GatewayFilterChain {

    HttpResponse proceed(HttpRequest request);
}
