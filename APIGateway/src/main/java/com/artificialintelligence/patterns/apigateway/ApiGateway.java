package com.artificialintelligence.patterns.apigateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The API gateway: single entry point for clients. Applies a filter chain, then routes
 * the request to a downstream service or an aggregator.
 */
public class ApiGateway {

    private final ServiceRegistry registry;
    private final Router router;
    private final List<GatewayFilter> filters = new ArrayList<>();
    private final Map<String, Function<HttpRequest, HttpResponse>> aggregators = new HashMap<>();

    public ApiGateway(ServiceRegistry registry, Router router) {
        this.registry = registry;
        this.router = router;
    }

    public ServiceRegistry registry() {
        return registry;
    }

    public Router router() {
        return router;
    }

    public void addFilter(GatewayFilter filter) {
        filters.add(filter);
    }

    public void registerAggregator(String path, Function<HttpRequest, HttpResponse> aggregator) {
        aggregators.put(path, aggregator);
    }

    public HttpResponse handle(HttpRequest request) {
        return new Chain(0).proceed(request);
    }

    private HttpResponse route(HttpRequest request) {
        Function<HttpRequest, HttpResponse> aggregator = aggregators.get(request.path());
        if (aggregator != null) {
            return aggregator.apply(request);
        }
        String serviceName = router.resolve(request.path());
        if (serviceName == null) {
            return HttpResponse.notFound("{\"error\":\"no route for " + request.path() + "\"}");
        }
        DownstreamService service = registry.get(serviceName);
        if (service == null) {
            return HttpResponse.badGateway("{\"error\":\"unknown service " + serviceName + "\"}");
        }
        try {
            return service.handle(request);
        } catch (Exception e) {
            return HttpResponse.internalError("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private final class Chain implements GatewayFilterChain {
        private int index;

        Chain(int index) {
            this.index = index;
        }

        @Override
        public HttpResponse proceed(HttpRequest request) {
            if (index < filters.size()) {
                GatewayFilter filter = filters.get(index);
                Chain next = new Chain(index + 1);
                return filter.filter(request, next);
            }
            return route(request);
        }
    }
}
