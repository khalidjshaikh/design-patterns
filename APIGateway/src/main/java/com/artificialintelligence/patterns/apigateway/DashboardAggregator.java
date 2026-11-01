package com.artificialintelligence.patterns.apigateway;

/**
 * Aggregates responses from multiple downstream services into a single composite response.
 * Demonstrates the aggregation capability of an API gateway.
 */
public final class DashboardAggregator {

    private final DownstreamService userService;
    private final DownstreamService orderService;
    private final DownstreamService productService;

    public DashboardAggregator(DownstreamService userService, DownstreamService orderService,
                               DownstreamService productService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    public HttpResponse aggregate(HttpRequest request) {
        HttpResponse user = userService.handle(HttpRequest.of("GET", "/users/1"));
        HttpResponse order = orderService.handle(HttpRequest.of("GET", "/orders/42"));
        HttpResponse product = productService.handle(HttpRequest.of("GET", "/products/7"));
        String body = "{\"dashboard\":{"
                + "\"user\":" + toJson(user.body()) + ","
                + "\"order\":" + toJson(order.body()) + ","
                + "\"product\":" + toJson(product.body())
                + "}}";
        return HttpResponse.ok(body);
    }

    private static String toJson(String raw) {
        // downstream already returns JSON objects; embed as-is
        return raw;
    }
}
