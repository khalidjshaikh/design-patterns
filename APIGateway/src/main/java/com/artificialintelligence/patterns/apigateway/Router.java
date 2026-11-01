package com.artificialintelligence.patterns.apigateway;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Resolves an incoming request path to a downstream service name using longest-prefix matching.
 */
public class Router {

    private final List<Route> routes = new ArrayList<>();

    public void addRoute(String pathPrefix, String serviceName) {
        if (pathPrefix == null || pathPrefix.isBlank()) {
            throw new IllegalArgumentException("pathPrefix must not be blank");
        }
        routes.add(new Route(pathPrefix, serviceName));
        // longest prefix wins
        routes.sort(Comparator.comparingInt((Route r) -> r.pathPrefix().length()).reversed());
    }

    public String resolve(String path) {
        if (path == null) return null;
        for (Route route : routes) {
            if (path.startsWith(route.pathPrefix())) {
                return route.serviceName();
            }
        }
        return null;
    }

    public List<Route> routes() {
        return List.copyOf(routes);
    }
}
