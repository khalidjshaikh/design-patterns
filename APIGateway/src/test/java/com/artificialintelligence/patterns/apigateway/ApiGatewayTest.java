package com.artificialintelligence.patterns.apigateway;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiGatewayTest {

    private ApiGateway newGateway() {
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

        ApiGateway gw = new ApiGateway(registry, router);
        gw.addFilter(new AuthenticationFilter());
        DashboardAggregator agg = new DashboardAggregator(
                registry.get("user-service"), registry.get("order-service"), registry.get("product-service"));
        gw.registerAggregator("/api/dashboard", agg::aggregate);
        return gw;
    }

    private Map<String, String> auth() {
        return Map.of("Authorization", "Bearer " + AuthenticationFilter.VALID_TOKEN);
    }

    @Test
    void healthIsPublic() {
        ApiGateway gw = newGateway();
        assertEquals(200, gw.handle(HttpRequest.of("GET", "/health")).statusCode());
    }

    @Test
    void routesToUserService() {
        ApiGateway gw = newGateway();
        HttpResponse r = gw.handle(HttpRequest.of("GET", "/api/users/1", auth(), ""));
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("user-service"));
    }

    @Test
    void missingAuthReturns401() {
        ApiGateway gw = newGateway();
        assertEquals(401, gw.handle(HttpRequest.of("GET", "/api/users/1")).statusCode());
    }

    @Test
    void invalidAuthReturns401() {
        ApiGateway gw = newGateway();
        assertEquals(401, gw.handle(HttpRequest.of("GET", "/api/users/1",
                Map.of("Authorization", "Bearer nope"), "")).statusCode());
    }

    @Test
    void unknownRouteReturns404() {
        ApiGateway gw = newGateway();
        assertEquals(404, gw.handle(HttpRequest.of("GET", "/api/nope", auth(), "")).statusCode());
    }

    @Test
    void aggregationCombinesServices() {
        ApiGateway gw = newGateway();
        HttpResponse r = gw.handle(HttpRequest.of("GET", "/api/dashboard", auth(), ""));
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("dashboard"));
        assertTrue(r.body().contains("user-service"));
        assertTrue(r.body().contains("order-service"));
        assertTrue(r.body().contains("product-service"));
    }
}
