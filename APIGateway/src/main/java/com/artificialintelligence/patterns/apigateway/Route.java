package com.artificialintelligence.patterns.apigateway;

/**
 * A routing rule mapping a path prefix to a downstream service name.
 */
public record Route(String pathPrefix, String serviceName) {
}
