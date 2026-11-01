package com.artificialintelligence.patterns.apigateway;

/**
 * Mock order service.
 */
public final class OrderService implements DownstreamService {

    @Override
    public String name() {
        return "order-service";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String id = lastSegment(request.path());
        return HttpResponse.ok("{\"service\":\"order-service\",\"orderId\":\"" + id + "\",\"status\":\"SHIPPED\"}");
    }

    private static String lastSegment(String path) {
        String trimmed = path.endsWith("/") && path.length() > 1 ? path.substring(0, path.length() - 1) : path;
        int idx = trimmed.lastIndexOf('/');
        return idx >= 0 ? trimmed.substring(idx + 1) : trimmed;
    }
}
