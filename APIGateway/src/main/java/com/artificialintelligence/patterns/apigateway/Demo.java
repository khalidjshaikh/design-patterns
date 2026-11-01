package com.artificialintelligence.patterns.apigateway;

import java.util.Map;

public final class Demo {

    public static void main(String[] args) {
        // --- Registry & routing ---
        ServiceRegistry registry = new ServiceRegistry();
        registry.register(new UserService());
        registry.register(new OrderService());
        registry.register(new ProductService());
        registry.register(new HealthService());

        Router router = new Router();
        router.addRoute("/health", "health-service");
        router.addRoute("/api/users", "user-service");
        router.addRoute("/api/orders", "order-service");
        router.addRoute("/api/products", "product-service");

        ApiGateway gateway = new ApiGateway(registry, router);

        // Cross-cutting filters (order matters: logging -> auth -> rate limit)
        gateway.addFilter(new LoggingFilter());
        gateway.addFilter(new AuthenticationFilter());
        gateway.addFilter(new RateLimitingFilter(100));

        // Aggregation endpoint
        DashboardAggregator dashboard = new DashboardAggregator(
                registry.get("user-service"), registry.get("order-service"), registry.get("product-service"));
        gateway.registerAggregator("/api/dashboard", dashboard::aggregate);

        String token = "Bearer " + AuthenticationFilter.VALID_TOKEN;
        Map<String, String> auth = Map.of("Authorization", token, "X-Client-Id", "demo-client");

        System.out.println("=== Public health check (no auth) ===");
        System.out.println(gateway.handle(HttpRequest.of("GET", "/health")).body());

        System.out.println("\n=== Authenticated routing ===");
        System.out.println(gateway.handle(HttpRequest.of("GET", "/api/users/1", auth, "")).body());
        System.out.println(gateway.handle(HttpRequest.of("GET", "/api/orders/42", auth, "")).body());
        System.out.println(gateway.handle(HttpRequest.of("GET", "/api/products/7", auth, "")).body());

        System.out.println("\n=== Aggregation ===");
        System.out.println(gateway.handle(HttpRequest.of("GET", "/api/dashboard", auth, "")).body());

        System.out.println("\n=== Missing/invalid auth ===");
        System.out.println(gateway.handle(HttpRequest.of("GET", "/api/users/1")).statusCode());
        System.out.println(gateway.handle(HttpRequest.of("GET", "/api/users/1",
                Map.of("Authorization", "Bearer wrong"), "")).statusCode());

        System.out.println("\n=== Rate limiting (limit 3) ===");
        ServiceRegistry r2 = new ServiceRegistry();
        r2.register(new UserService());
        Router rt2 = new Router();
        rt2.addRoute("/api/users", "user-service");
        ApiGateway gw2 = new ApiGateway(r2, rt2);
        gw2.addFilter(new AuthenticationFilter());
        gw2.addFilter(new RateLimitingFilter(3));
        for (int i = 0; i < 5; i++) {
            HttpResponse r = gw2.handle(HttpRequest.of("GET", "/api/users/1", auth, ""));
            System.out.println("request " + (i + 1) + " -> " + r.statusCode());
        }

        System.out.println("\n=== 404 routing ===");
        System.out.println(gateway.handle(HttpRequest.of("GET", "/api/nope", auth, "")).statusCode());

        // --- Load balancing demo ---
        System.out.println("\n=== Load balancing ===");
        DownstreamService a = new DownstreamService() {
            public String name() { return "inst-a"; }
            public HttpResponse handle(HttpRequest req) { return HttpResponse.ok("{\"instance\":\"A\"}"); }
        };
        DownstreamService b = new DownstreamService() {
            public String name() { return "inst-b"; }
            public HttpResponse handle(HttpRequest req) { return HttpResponse.ok("{\"instance\":\"B\"}"); }
        };
        LoadBalancedService lb = new LoadBalancedService("lb-service", java.util.List.of(a, b));
        for (int i = 0; i < 4; i++) {
            System.out.println(lb.handle(HttpRequest.of("GET", "/x")).body());
        }

        // --- Circuit breaker demo ---
        System.out.println("\n=== Circuit breaker ===");
        FlakyService flaky = new FlakyService(2);
        CircuitBreaker cb = new CircuitBreaker(2, 100);
        for (int i = 0; i < 5; i++) {
            HttpResponse r = cb.call(
                    () -> flaky.handle(HttpRequest.of("GET", "/flaky")),
                    () -> HttpResponse.serviceUnavailable("{\"error\":\"fallback\"}"));
            System.out.println("call " + (i + 1) + " -> " + r.statusCode() + " " + r.body() + " (state=" + cb.state() + ")");
            try { Thread.sleep(120); } catch (InterruptedException ignored) { }
        }
    }
}
