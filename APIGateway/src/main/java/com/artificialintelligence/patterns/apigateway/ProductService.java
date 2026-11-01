package com.artificialintelligence.patterns.apigateway;

/**
 * Mock product service.
 */
public final class ProductService implements DownstreamService {

    @Override
    public String name() {
        return "product-service";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String id = lastSegment(request.path());
        return HttpResponse.ok("{\"service\":\"product-service\",\"productId\":\"" + id + "\",\"name\":\"Widget\"}");
    }

    private static String lastSegment(String path) {
        String trimmed = path.endsWith("/") && path.length() > 1 ? path.substring(0, path.length() - 1) : path;
        int idx = trimmed.lastIndexOf('/');
        return idx >= 0 ? trimmed.substring(idx + 1) : trimmed;
    }
}
